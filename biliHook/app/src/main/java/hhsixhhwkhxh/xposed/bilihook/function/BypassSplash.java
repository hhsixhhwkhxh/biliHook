package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import android.view.View;
import hhsixhhwkhxh.xposed.bilihook.Untils;

public class BypassSplash extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //关键词Splash
        if(!sharedPreferences.getBoolean("BypassSplash",false)){return;}
        /*XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.splash.ad.c0", lpparam.classLoader,
            "B", java.util.List.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    // 在这里实现你的逻辑
                    //param.args[0]=null;
                    param.setResult(null);
                    log("阻止Ltv/danmaku/bili/ui/splash/ad/c0;->B(Ljava/util/List;)I");
                }});
        XposedHelpers.findAndHookMethod(
            "tv.danmaku.bili.ui.splash.ad.c0", // 类路径
            lpparam.classLoader,              // 类加载器
            "m",                             // 方法名
            android.app.Activity.class,       // 参数类型
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    // 在方法执行之前执行的代码
                    param.setResult(null);
                    //super.beforeHookedMethod(param);
                    // 例如，可以在这里打印日志或修改参数
                    log("阻止Ltv/danmaku/bili/ui/splash/ad/c0;->m(Landroid/app/Activity;)V");
                }});
                */
        XposedHelpers.findAndHookMethod(
            "tv.danmaku.bili.ui.splash.ad.page.FullImageSplash", // 类路径
            lpparam.classLoader, // 类加载器
            "lh", // 方法名
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    // 在方法执行之前执行的代码
                    super.beforeHookedMethod(param);
                    
                    View view = Untils.getView(param.thisObject).findViewById(0x7f090ca0);
                    view.performClick();
                    log("模拟点击");
                    // 例如，可以在这里打印日志或修改参数
                }});
    }
    
    
    
    
}
