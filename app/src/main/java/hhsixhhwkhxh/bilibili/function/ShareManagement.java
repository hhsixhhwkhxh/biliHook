package hhsixhhwkhxh.bilibili.function;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.bilibili.FunctionsBase;

public class ShareManagement extends FunctionsBase {
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean DisableJumpToVerticalVideoFromShare = sharedPreferences.getBoolean("DisableJumpToVerticalVideoFromShare",false);

        if(DisableJumpToVerticalVideoFromShare){
            DisableJumpToVerticalVideoFromShare(lpparam);
        }
    }

    public void DisableJumpToVerticalVideoFromShare(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.intent.IntentHandlerActivity", lpparam.classLoader, "init", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = (Intent) XposedHelpers.callMethod(param.thisObject,"getIntent");

                String dataString = intent.getDataString();
                if(dataString==null||!dataString.contains("story")){return;}

                Uri rawUri = intent.getData();
                if(rawUri==null){return;}
                String RawH5awaken = rawUri.getQueryParameter("h5awaken");
                String h5awakenUrlDecoded = URLDecoder.decode(RawH5awaken, "UTF-8");

                // Base64 解码
                byte[] h5awakenBase64Decoded = Base64.decode(h5awakenUrlDecoded, Base64.DEFAULT);
                String h5awakenString = new String(h5awakenBase64Decoded, StandardCharsets.UTF_8);

                //这个share_from键标明了视频的类型story 方便起见 直接篡改键名 同样识别不了
                h5awakenString = h5awakenString.replace("share_from","onani");

                String newH5awaken = Base64.encodeToString(h5awakenString.getBytes(StandardCharsets.UTF_8),Base64.DEFAULT);

                Uri.Builder builder = rawUri.buildUpon();
                builder.clearQuery(); // 清除原始查询参数
                // 重新添加所有原始查询参数，并替换h5awaken
                for (String key : rawUri.getQueryParameterNames()) {
                    if(key.equals("-Atype")){continue;}
                    if ("h5awaken".equals(key)) {
                        builder.appendQueryParameter(key, newH5awaken);
                    } else {
                        // 此处一个参数名只有一个值
                        String value = rawUri.getQueryParameter(key);
                        builder.appendQueryParameter(key, value);
                    }
                }
                Uri newUri = builder.build();
                intent.setData(newUri);
            }

        });
    }
}
