package hhsixhhwkhxh.bilibili;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.format.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogManager {
    private static final String TAG = "LogManager";
    private static LogManager instance;

    // 日志文件相关配置
    private static final String LOG_DIR = "AppLogs";
    private static final String LOG_PREFIX = "app_log_";
    private static final String LOG_EXT = ".txt";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MAX_LOG_FILES = 10; // 最多保留10个日志文件

    // 日志写入线程和队列
    private HandlerThread logThread;
    private Handler logHandler;
    private final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isWriting = new AtomicBoolean(false);

    // 文件写入相关
    private File currentLogFile;
    private BufferedWriter writer;
    private long currentFileSize = 0;

    // 日期格式
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private final SimpleDateFormat fileDateFormat =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    private File saveDir;

    private LogManager(File saveDir) {
        this.saveDir = saveDir;
        // 初始化日志线程
        logThread = new HandlerThread("LogWriter", Process.THREAD_PRIORITY_BACKGROUND);
        logThread.start();
        logHandler = new Handler(logThread.getLooper());

        // 初始化日志文件
        logHandler.post(this::initLogFile);
    }

    public static synchronized LogManager getInstance(File saveDir) {
        if (instance == null) {
            instance = new LogManager(saveDir);
        }
        return instance;
    }

    /**
     * 初始化日志文件
     */
    private void initLogFile() {
        try {
            // 创建日志目录
            File logDir = new File(saveDir, LOG_DIR);
            if (!logDir.exists() && !logDir.mkdirs()) {
                android.util.Log.e(TAG, "Failed to create log directory");
                return;
            }

            // 清理旧日志文件
            cleanupOldLogs(logDir);

            // 创建新日志文件
            String timestamp = fileDateFormat.format(new Date());
            currentLogFile = new File(logDir, LOG_PREFIX + timestamp + LOG_EXT);

            // 初始化写入器
            writer = new BufferedWriter(new FileWriter(currentLogFile, true));
            currentFileSize = currentLogFile.length();

        } catch (IOException e) {
            android.util.Log.e(TAG, "Failed to initialize log file", e);
        }
    }

    /**
     * 清理旧日志文件
     */
    private void cleanupOldLogs(File logDir) {
        File[] logFiles = logDir.listFiles((dir, name) ->
                name.startsWith(LOG_PREFIX) && name.endsWith(LOG_EXT));

        if (logFiles != null && logFiles.length > MAX_LOG_FILES) {
            // 按修改时间排序，删除最旧的文件
            Arrays.sort(logFiles, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            for (int i = 0; i < logFiles.length - MAX_LOG_FILES; i++) {
                if (!logFiles[i].delete()) {
                    android.util.Log.w(TAG, "Failed to delete old log file: " + logFiles[i].getName());
                }
            }
        }
    }

    /**
     * 写入日志
     */
    public void log(String level, String tag, String message) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("%s [%s] %s: %s\n", timestamp, level, tag, message);

        // 添加到队列
        logQueue.offer(logEntry);

        // 如果不在写入状态，触发写入
        if (isWriting.compareAndSet(false, true)) {
            logHandler.post(this::writeLogs);
        }
    }

    /**
     * 批量写入日志
     */
    private void writeLogs() {
        try {
            int batchCount = 0;
            String logEntry;

            // 批量写入，每次最多处理100条日志
            while (batchCount < 100 && (logEntry = logQueue.poll()) != null) {
                writer.write(logEntry);
                currentFileSize += logEntry.length();
                batchCount++;
            }

            // 检查文件大小，必要时滚动文件
            if (currentFileSize >= MAX_FILE_SIZE) {
                rollOverLogFile();
            }

            writer.flush();

        } catch (IOException e) {
            android.util.Log.e(TAG, "Failed to write logs", e);
        } finally {
            // 如果队列中还有日志，继续写入
            if (!logQueue.isEmpty()) {
                logHandler.post(this::writeLogs);
            } else {
                isWriting.set(false);
            }
        }
    }

    /**
     * 滚动日志文件
     */
    private void rollOverLogFile() {
        try {
            if (writer != null) {
                writer.close();
            }

            // 重命名当前文件
            String timestamp = fileDateFormat.format(new Date());
            File newFile = new File(currentLogFile.getParent(),
                    LOG_PREFIX + timestamp + LOG_EXT);

            if (currentLogFile.renameTo(newFile)) {
                android.util.Log.i(TAG, "Rolled over log file: " + newFile.getName());
            }

            // 创建新文件
            initLogFile();

        } catch (IOException e) {
            android.util.Log.e(TAG, "Failed to roll over log file", e);
        }
    }

    /**
     * 关闭日志系统
     */
    public void shutdown() {
        logHandler.post(() -> {
            try {
                // 写入队列中剩余的日志
                while (!logQueue.isEmpty()) {
                    writeLogs();
                }

                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                android.util.Log.e(TAG, "Failed to shutdown log manager", e);
            } finally {
                logThread.quit();
            }
        });
    }

    // 便捷方法
    public void d(String tag, String message) {
        log("DEBUG", tag, message);
    }

    public void i(String tag, String message) {
        log("INFO", tag, message);
    }

    public void w(String tag, String message) {
        log("WARN", tag, message);
    }

    public void e(String tag, String message) {
        log("ERROR", tag, message);
    }

    public void e(String tag, String message, Throwable tr) {
        log("ERROR", tag, message + "\n" + android.util.Log.getStackTraceString(tr));
    }
}