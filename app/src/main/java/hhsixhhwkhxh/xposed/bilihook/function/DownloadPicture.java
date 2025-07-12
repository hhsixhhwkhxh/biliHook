package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import hhsixhhwkhxh.xposed.bilihook.Utils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import de.robv.android.xposed.XC_MethodHook;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnLongClickListener;
import android.app.DownloadManager;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.os.Environment;
import android.net.Uri;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class DownloadPicture extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //关键词ImageFragment@
        if(!sharedPreferences.getBoolean("CommentSectionAllowPictureLongPressSave",false)){return;}
        final Class<?> ImageFragmentClass = XposedHelpers.findClass("com.bilibili.lib.imageviewer.fragment.ImageFragment",lpparam.classLoader);
        final Class<?> ImageItemClass = XposedHelpers.findClass("com.bilibili.lib.imageviewer.data.ImageItem",lpparam.classLoader);
        final Class<?> ImageItemParentClass = ImageItemClass.getSuperclass();
        final Method getPictureUriMethod = Utils.selectMethod(ImageItemParentClass,String.class);

        final Field ImageItemField = Utils.selectField(ImageFragmentClass,ImageItemClass);
        //Lcom/bilibili/lib/imageviewer/fragment/ImageFragment;->h:Lcom/bilibili/lib/imageviewer/data/ImageItem;

        XposedHelpers.findAndHookMethod(ImageFragmentClass, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    View view = (View) param.getResult();

                    ImageView imageView = view.findViewById(Utils.getViewID("image_view"));
                    imageView.setOnLongClickListener(new OnLongClickListener(){

                            @Override
                            public boolean onLongClick(View view) {
                                try {
                                    Object ImageFragment = param.thisObject;
                                    Object ImageItem = ImageItemField.get(ImageFragment);
                                    String imageUrl = (String) getPictureUriMethod.invoke(ImageItem);

                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
                                    String DownloadPath ="BiliBili/"+imageUrl.substring(imageUrl.lastIndexOf("/"),imageUrl.length());
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, DownloadPath);

                                    DownloadManager downloadManager = (DownloadManager) Utils.getMainActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                    final long downloadId = downloadManager.enqueue(request);
                                    //log("图片开始下载");
                                    Toast.makeText(Utils.getMainActivity(), "开始下载...", Toast.LENGTH_SHORT).show();
                                    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                                    BroadcastReceiver receiver = new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {
                                            long Id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                            if(downloadId==Id){
                                                Toast.makeText(Utils.getMainActivity(), "图片保存于/sdcard/DCIM/BiliBili/", Toast.LENGTH_SHORT).show();
                                            }
                                            // 处理下载完成的逻辑
                                        }
                                    };
                                    Utils.getMainActivity().registerReceiver(receiver, filter);
                                    
                                    //Toast.makeText(Untils.getMainActivity(), "图片保存于/storage/emulated/0/DCIM/BiliBili/", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                    log("图片保存失败");
                                    log(e);
                                    Toast.makeText(Utils.getMainActivity(), "图片保存失败 请将lsp日志截图报告开发者", Toast.LENGTH_SHORT).show();
                                }
                                return false;
                            }
                        });

                }
            });
    }
    
    
    
    
}
