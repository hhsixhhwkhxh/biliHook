package hhsixhhwkhxh.bilibili.function;

import android.icu.text.SimpleDateFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;

public class CommentOptimization extends FunctionsBase {
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(!sharedPreferences.getBoolean("ForceCommentsToShowAbsoluteTime",false)){return;}
        Method cMethod = Utils.getDeConfusionMethod("com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethod",lpparam.classLoader);
        if(cMethod==null){
            Utils.reportError("CommentOptimization com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethod为空");
            return;
        }

        Field timestampField = Utils.selectField(cMethod.getDeclaringClass(), Long.class);
        if(timestampField==null){
            Utils.reportError("CommentOptimization timestampField为空");
            return;
        }
        timestampField.setAccessible(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss  ", Locale.getDefault());

        UnhooksList.add(XposedBridge.hookMethod(cMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Long timestampInMillis = (Long) timestampField.get(param.thisObject);
                if(timestampInMillis==null){return;}
                param.setResult(sdf.format(new Date(timestampInMillis)));
            }

        }));
    }
}
