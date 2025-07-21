package hhsixhhwkhxh.bilibili.function;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        boolean DisableAuthorSpaceBlocking = sharedPreferences.getBoolean("DisableAuthorSpaceBlocking",false);
        boolean AllowSweepGrave = sharedPreferences.getBoolean("AllowSweepGrave",false);

        if(UserCenterRemoveExcessiveService){
            UserCenterRemoveExcessiveService(lpparam);
        }

        if(FavoritesOpenVideoRedirect){
            FavoritesOpenVideoRedirect(lpparam);
        }

        if(ForceEnableV1Favorites){
            ForceEnableV1Favorites(lpparam);
        }

        if(DisableAuthorSpaceBlocking){
            DisableAuthorSpaceBlocking(lpparam);
        }

        if(AllowSweepGrave){
            AllowSweepGrave(lpparam);
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

    public void DisableAuthorSpaceBlocking(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("com.bilibili.app.authorspace.api.BiliMemberCard", lpparam.classLoader, "isDeleted", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }

        });

        XposedHelpers.findAndHookMethod("com.bilibili.app.authorspace.api.BiliSpace", lpparam.classLoader, "isSpaceHidden", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }

        });
    }

    public void AllowSweepGrave(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        final Method F9Method = Utils.getDeConfusionMethod("com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Method",lpparam.classLoader);
        if(F9Method==null){
            Utils.reportError("UserCenterOptimization com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Method为空");
            return;
        }

        final Class<?> SpaceLoadingViewClass = XposedHelpers.findClass("com.bilibili.app.authorspace.ui.widget.SpaceLoadingView",lpparam.classLoader);
        final Field FField = Utils.selectField(F9Method.getDeclaringClass(),SpaceLoadingViewClass);
        if(FField==null){
            Utils.reportError("UserCenterOptimization com_bilibili_app_authorspace_ui_AuthorSpaceActivity_FField为空");
            return;
        }
        FField.setAccessible(true);
        XposedBridge.hookMethod(F9Method, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //ViewGroup root = (ViewGroup) XposedHelpers.getObjectField(param.thisObject,"F");
                ViewGroup root = (ViewGroup) FField.get(param.thisObject);
                int id = Utils.getViewID("btn");
                Button SweepGraveButton = new Button(root.getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, id);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                long mid = (long) XposedHelpers.callMethod(param.thisObject,"mid");
                SweepGraveButton.setText("扫墓");
                SweepGraveButton.setOnClickListener(view -> {

                    try {
                        SweepGrave(lpparam,mid);
                    } catch (Throwable e) {
                        Utils.showToast("错误"+e, Toast.LENGTH_LONG);
                    }
                });
                root.addView(SweepGraveButton, layoutParams);
            }
        });
    }



    public static void SweepGrave(XC_LoadPackage.LoadPackageParam lpparam,long uid) throws Throwable{
        Class<?> UpItemTypeClass = XposedHelpers.findClass("com.bilibili.bplus.followinglist.model.UpItemType",lpparam.classLoader);
        Object UpItemTypeNORMALObject = XposedHelpers.getStaticObjectField(UpItemTypeClass,"NORMAL");

        //Class<?> b9Class = XposedHelpers.findClass("com.bilibili.bplus.followinglist.model.b9",lpparam.classLoader);
        Class<?> b9Class = Utils.getDeConfusionClass("com_bilibili_bplus_followinglist_model_b9Class",lpparam.classLoader);
        if(b9Class==null){
            Utils.reportError("Entrance 错误 com_bilibili_bplus_followinglist_model_b9Class类未找到");
            return;
        }

        Constructor<?> b9Constructor = Utils.getConstructorWithParamCount(b9Class,22,false);
        if(b9Constructor==null){
            Utils.reportError("Entrance 错误 b9Constructor类未找到");
            return;
        }
        Object b9Object = b9Constructor.newInstance(
                "https://i1.hdslb.com/bfs/face/0de87fa269a1aa3b0a05643909246f7fdeb2ef0b.jpg",
                "hhsixhhwkhxh",
                uid,
                0,
                UpItemTypeNORMALObject,
                null,
                null,
                0,
                false,
                0,
                false,
                "bilibili://space/"+uid+"?defaultTab=dynamic",
                "",
                false,
                "",
                false,
                null,
                "",
                "{\"uid_type\":1}",
                "",
                "",
                null
        );


        //com.bilibili.bplus.followinglist.model.e7
        List<Object> newList = new ArrayList<>();
        newList.add(b9Object);

        //Class<?> e7Class = XposedHelpers.findClass("com.bilibili.bplus.followinglist.model.e7",lpparam.classLoader);
        //Class<?> e7Class = Utils.getDeConfusionClass("com_bilibili_bplus_followinglist_model_e7Class",lpparam.classLoader);
        Method N0Method = Utils.getDeConfusionMethod("com_bilibili_bplus_followinglist_model_e7_N0Method",lpparam.classLoader);
        if(N0Method==null){
            Utils.reportError("Entrance 错误 N0Method方法未找到");
            return;
        }
        Class<?> e7Class = N0Method.getDeclaringClass();

        Constructor<?> e7Constructor = Utils.getConstructorWithParamCount(e7Class,14,false);
        if(e7Constructor==null){
            Utils.reportError("Entrance 错误 e7Constructor类未找到");
            return;
        }
        Object e7Object = e7Constructor.newInstance(
                "扫墓",
                newList,
                new ArrayList<>(),
                "",
                0,
                true,
                true,
                "",
                "bilibili://following/up_more_list",
                true,
                false,
                true,
                false,
                ""
        );

        Object CardVideoUpListObject = N0Method.invoke(e7Object,false);
        byte[] bytes = (byte[]) XposedHelpers.callMethod(CardVideoUpListObject,"toByteArray");

        String quickConsumeUriStr = "bilibili://following/quick_consume";

        // 创建主Intent
        Intent intent = new Intent();
        intent.setData(android.net.Uri.parse(quickConsumeUriStr));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("tv.danmaku.bili", "com.bilibili.bplus.followinglist.quick.consume.QuickConsumeActivity");

        // 创建default_extra_bundle
        Bundle defaultExtraBundle = new Bundle();

        defaultExtraBundle.putByteArray("key_video_uplist", bytes);
        defaultExtraBundle.putBoolean("key_is_dynamic", true);

        // 创建blrouter.props
        Bundle blrouterProps = new Bundle();
        blrouterProps.putString("blrouter.from", "bilibili://following/index/filter/all");

        // 设置主Bundle的Extras
        intent.putExtra("default_extra_bundle", defaultExtraBundle);
        intent.putExtra("key_selected_row", 0);
        intent.putExtra("blrouter.native.start", false);
        intent.putExtra("key_selected", 2);
        intent.putExtra("blrouter.props", blrouterProps);
        intent.putExtra("blrouter.pagename", quickConsumeUriStr);
        intent.putExtra("blrouter.targeturl", quickConsumeUriStr);
        intent.putExtra("blrouter.matchrule", quickConsumeUriStr);

        // 启动Activity
        Utils.getMainActivity().startActivityForResult(intent, -1);
    }
}
