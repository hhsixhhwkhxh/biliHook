package hhsixhhwkhxh.bilibili.function;

import android.icu.text.SimpleDateFormat;

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
        Method hMethod = Utils.getDeConfusionMethod("kntr_base_localization_n0_hMethod",lpparam.classLoader);
        if(hMethod==null){
            Utils.reportError("CommentOptimization kntr_base_localization_n0_hMethod为空");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss  ", Locale.getDefault());

        UnhooksList.add(XposedBridge.hookMethod(hMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Long timestampInMillis = (Long) param.args[0];
                if(timestampInMillis==null){return;}
                param.setResult(sdf.format(new Date(timestampInMillis)));
            }

        }));
    }
}
