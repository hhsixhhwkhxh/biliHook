package hhsixhhwkhxh.bilibili.function;

import android.content.Context;
import android.net.Uri;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;

public class UserCenterOptimization extends FunctionsBase {
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean UserCenterRemoveExcessiveService = sharedPreferences.getBoolean("UserCenterRemoveExcessiveService",false);
        boolean FavoritesOpenVideoRedirect = sharedPreferences.getBoolean("FavoritesOpenVideoRedirect",false);
        boolean ForceEnableV1Favorites = sharedPreferences.getBoolean("ForceEnableV1Favorites",false);

        if(UserCenterRemoveExcessiveService){
            UserCenterRemoveExcessiveService(lpparam);
        }

        if(FavoritesOpenVideoRedirect){
            FavoritesOpenVideoRedirect(lpparam);
        }

        if(ForceEnableV1Favorites){
            ForceEnableV1Favorites(lpparam);
        }

    }

    public void UserCenterRemoveExcessiveService(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        final Method HomePageMenuGroupInitMethod = Utils.selectMethod(XposedHelpers.findClass("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment",lpparam.classLoader),void.class, Context.class, List.class,XposedHelpers.findClass("tv.danmaku.bili.ui.main2.api.AccountMine",lpparam.classLoader));
        UnhooksList.add(XposedBridge.hookMethod(HomePageMenuGroupInitMethod,new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                List list = (List) param.args[1];
                if(list.size()<=1){return;}
                for (int i = list.size()-2; i >=1; i--) {
                    list.remove(i);
                }
            }
        }));
    }

    public void FavoritesOpenVideoRedirect(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        final Method getLinkMethod = Utils.getDeConfusionMethod("pf5_e_getLinkMethod",lpparam.classLoader);
        if(getLinkMethod==null){
            Utils.reportError("UserCenterOptimization pf5_e_getLinkMethod为空");
            return;
        }
        XposedBridge.hookMethod(getLinkMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String result = (String) param.getResult();
                Uri rawUri = Uri.parse(result);
                String sourceId = rawUri.getQueryParameter("sourceid");
                String newPath = "//united_video/" + sourceId;

                Uri.Builder builder = new Uri.Builder()
                        .scheme(rawUri.getScheme())                     // 保持原scheme
                        .path(newPath);
                // 添加新参数aid
                builder.appendQueryParameter("aid", sourceId);

                // 生成最终URI
                Uri newUri = builder.build();
                String newResult = newUri.toString();
                log(newUri);
                param.setResult(newResult);
            }
        });
    }

    public void ForceEnableV1Favorites(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        Method bMethod = Utils.getDeConfusionMethod("tv_danmaku_bili_ui_main2_mine_p0_bMethod",lpparam.classLoader);
        if(bMethod==null){
            Utils.reportError("UserCenterOptimization tv_danmaku_bili_ui_main2_mine_p0_bMethod为空");
            return;
        }
        final Class<?> MenuGroup$ItemClass = XposedHelpers.findClass("com.bilibili.lib.homepage.mine.MenuGroup$Item",lpparam.classLoader);
        final Field uriField = MenuGroup$ItemClass.getField("uri");
        XposedBridge.hookMethod(bMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object MenuGroup$ItemObject = param.args[0];
                String rawUri = (String) uriField.get(MenuGroup$ItemObject);
                if(rawUri==null){return;}
                if(!rawUri.contains("favourite")){return;}
                String newUri = rawUri.substring(0,rawUri.length()-1)+"1";
                uriField.set(MenuGroup$ItemObject,newUri);
                //log("uri"+newUri);
            }

        });
    }
}
