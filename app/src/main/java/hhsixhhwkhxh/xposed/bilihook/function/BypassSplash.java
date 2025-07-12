package hhsixhhwkhxh.xposed.bilihook.function;
import de.robv.android.xposed.XposedBridge;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import hhsixhhwkhxh.xposed.bilihook.Utils;

public class BypassSplash extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //关键词Splash
        if(!sharedPreferences.getBoolean("BypassSplash",false)){return;}
        //Lcom/bilibili/app/comm/restrict/RestrictedMode;->isEnable(Lcom/bilibili/app/comm/restrict/RestrictedType;Ljava/lang/String;)Z

        //Ltv/danmaku/bili/MainActivityV2;->T5(Landroid/os/Bundle;)V  this.L = (FrameLayout) findViewById(i0.e9); // Splash 容器
        //Ltv/danmaku/bili/MainActivityV2;->m6(Ltv/danmaku/bili/ui/splash/ad/model/Splash;Z)Z 显示广告页
        //阻止哔哩哔哩启动时的开屏广告
        Class<?> MainActivityV2Class = XposedHelpers.findClass("tv.danmaku.bili.MainActivityV2",lpparam.classLoader);
        Class<?> SplashClass = XposedHelpers.findClass("tv.danmaku.bili.ui.splash.ad.model.Splash",lpparam.classLoader);
        //Ltv/danmaku/bili/MainActivityV2;->m6(Ltv/danmaku/bili/ui/splash/ad/model/Splash;Z)Z
        Method m6Method = Utils.selectMethod(MainActivityV2Class,boolean.class,SplashClass,boolean.class);
        if(m6Method==null){
            Utils.reportError("BypassSplash中m6Method为空");
            return;
        }

        Field LFiled = Utils.selectField(MainActivityV2Class,android.widget.FrameLayout.class);
        if(LFiled==null){
            Utils.reportError("BypassSplash中LFiled为空");
            return;
        }
        LFiled.setAccessible(true);

        UnhooksList.add(XposedBridge.hookMethod(m6Method, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View)LFiled.get(param.thisObject);
                if(view==null){return;}
                view.setVisibility(View.GONE);
            }
        }));

        //Utils.getBLogMessage(lpparam,"Splash");
        //阻止生命周期引起的开屏广告 如从外界跳转回哔哩哔哩
        Method sh5_k_mMethod = Utils.getDeConfusionMethod("sh5_k_mMethod",lpparam.classLoader);
        if(sh5_k_mMethod==null){
            Utils.reportError("BypassSplash中sh5_k_mMethod为空");
            return;
        }else{
            UnhooksList.add(XposedBridge.hookMethod(sh5_k_mMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(null);
                }

            }));
        }

    }
    
    
    
    
}
