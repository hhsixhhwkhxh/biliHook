package hhsixhhwkhxh.bilibili.function;
import de.robv.android.xposed.XposedBridge;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;

public class BypassSplash extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //关键词Splash
        if(!sharedPreferences.getBoolean("BypassSplash",false)){return;}


        //4-9 update新的屏蔽逻辑
        //阻止哔哩哔哩启动时的开屏广告
        final Class<?> MainActivitySplashComponentExtKtClass = XposedHelpers.findClass("tv.danmaku.bili.MainActivitySplashComponentExtKt",lpparam.classLoader);
        final Class<?> SplashOrderClass = XposedHelpers.findClass("tv.danmaku.bili.splash.ad.model.SplashOrder",lpparam.classLoader);
        final Class<?> MainActivityV2Class = XposedHelpers.findClass("tv.danmaku.bili.MainActivityV2",lpparam.classLoader);
        Method iMethod = Utils.selectMethod(MainActivitySplashComponentExtKtClass,boolean.class,MainActivityV2Class,SplashOrderClass,boolean.class);
        if(iMethod==null){
            Utils.reportError("BypassSplash中iMethod为空");
            return;
        }
        XposedBridge.hookMethod(iMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[1] = null;
            }
        });
        //生命周期广告
        /*
        final Class<?> SplashManagerClass = XposedHelpers.findClass("tv.danmaku.bili.splash.ad.core.SplashManager",lpparam.classLoader);
        Method aMethod = Utils.selectMethod(SplashManagerClass,void.class,Activity.class);
        //Ltv/danmaku/bili/splash/ad/core/SplashManager;->a(Landroid/app/Activity;)V
        if(aMethod==null){
            Utils.reportError("BypassSplash中aMethod为空");
            return;
        }
        XposedBridge.hookMethod(aMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });*/
        final Class<?> SplashManagerClass = XposedHelpers.findClass("tv.danmaku.bili.splash.ad.core.SplashManager",lpparam.classLoader);
        Class<?> BaseSplashClass = XposedHelpers.findClass("tv.danmaku.bili.splash.ad.page.BaseSplash",lpparam.classLoader);
        Method aMethod = Utils.selectMethod(SplashManagerClass,BaseSplashClass,SplashOrderClass);
        if(aMethod==null){
            Utils.reportError("BypassSplash中aMethod为空");
            return;
        }
        XposedBridge.hookMethod(aMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Utils.log("生命周期 BypassSplash try");
                param.setResult(null);
            }
        });
    }
    
    
    
    
}
