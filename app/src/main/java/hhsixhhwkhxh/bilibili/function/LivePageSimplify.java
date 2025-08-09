package hhsixhhwkhxh.bilibili.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;

public class LivePageSimplify extends FunctionsBase {
    //注意此类需要尽可能早加载 此时utils类尚未初始化
    public Context context = null;
    Method loadClassMethod = null;
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean BanSwitchLiveByVerticalSlide = sharedPreferences.getBoolean("BanSwitchLiveByVerticalSlide",false);
        boolean HideOthersGiftBroadcastDanmaku = sharedPreferences.getBoolean("HideOthersGiftBroadcastDanmaku",false);
        boolean HideLiveNormalBanner = sharedPreferences.getBoolean("HideLiveNormalBanner",false);


        if(!BanSwitchLiveByVerticalSlide&&!HideOthersGiftBroadcastDanmaku){
            return;
        }

        final Class<?> DefaultBundleClassLoaderWrapperClass = XposedHelpers.findClass("com.bilibili.lib.tribe.core.internal.loader.DefaultBundleClassLoaderWrapper",lpparam.classLoader);
        Constructor<?> targetConstructor = DefaultBundleClassLoaderWrapperClass.getConstructors()[0];
        XposedBridge.hookMethod(targetConstructor, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String BundleInfo = param.args[0].toString();
                if(!BundleInfo.contains("liveroom")){return;}
                //ClassLoader classLoader = (ClassLoader) param.args[1];这是宿主的私人classloader 别用
                loadClassMethod = Utils.selectMethod(DefaultBundleClassLoaderWrapperClass,Class.class, String.class);

                //ClassLoader classLoader = LiveVerticalPagerViewClass.getClassLoader();

                if(BanSwitchLiveByVerticalSlide){
                    Class<?> LiveVerticalPagerViewClass = (Class<?>) loadClassMethod.invoke(param.thisObject,"com.bilibili.bililive.room.ui.roomv3.vertical.widget.LiveVerticalPagerView");
                    if(LiveVerticalPagerViewClass!=null){
                        BanSwitchLiveByVerticalSlide(LiveVerticalPagerViewClass);
                    }else{
                        Utils.submitErrorBeforeInit("BanSwitchLiveByVerticalSlide LiveVerticalPagerViewClass为空");
                    }
                }

                if(HideOthersGiftBroadcastDanmaku){
                    Class<?> FullScreenNoticeViewClass = (Class<?>) loadClassMethod.invoke(param.thisObject,"com.bilibili.bililive.room.ui.roomv3.notice.widget.FullScreenNoticeView");
                    if(FullScreenNoticeViewClass!=null){
                        HideOthersGiftBroadcastDanmaku(FullScreenNoticeViewClass);
                    }else{
                        Utils.submitErrorBeforeInit("HideOthersGiftBroadcastDanmaku FullScreenNoticeViewClass为空");
                    }

                }


                if(HideLiveNormalBanner){
                    Class<?> LiveRoomOperationAppServiceImplClass = (Class<?>) loadClassMethod.invoke(param.thisObject,"com.bilibili.bililive.room.ui.roomv3.operating4.service.LiveRoomOperationAppServiceImpl");
                    if(LiveRoomOperationAppServiceImplClass!=null){
                        HideLiveNormalBanner(LiveRoomOperationAppServiceImplClass);
                    }else{
                        Utils.submitErrorBeforeInit("HideLiveNormalBanner LiveRoomOperationAppServiceImplClass为空");
                    }

                }
            }
        });


    }
    public void BanSwitchLiveByVerticalSlide(Class<?> LiveVerticalPagerViewClass)throws Throwable{
        //Class<?> LiveVerticalPagerViewClass = (Class<?>) loadClassMethod.invoke(param.thisObject,"com.bilibili.bililive.room.ui.roomv3.vertical.widget.LiveVerticalPagerView");
        if(LiveVerticalPagerViewClass==null){
            XposedBridge.log("LiveVerticalPagerViewClass=null");
            return;
        }
        Class<?> LiveRecyclerViewClass = null;
        for(Field field:LiveVerticalPagerViewClass.getDeclaredFields()){
            Class<?> parentClass = field.getType().getSuperclass();
            if(parentClass==null||!parentClass.getName().equals("androidx.recyclerview.widget.RecyclerView")){continue;}
            LiveRecyclerViewClass = field.getType();
            break;
        }

        if(LiveRecyclerViewClass==null){
            XposedBridge.log("LiveRecyclerViewClass=null");
            return;
        }
        //XposedBridge.log("LiveRecyclerViewClass:"+LiveRecyclerViewClass);

        XposedHelpers.findAndHookMethod(LiveRecyclerViewClass, "onInterceptTouchEvent", android.view.MotionEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }

        });

        XposedHelpers.findAndHookMethod(LiveRecyclerViewClass, "onTouchEvent", android.view.MotionEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }

        });
    }


    public void HideOthersGiftBroadcastDanmaku(Class<?> FullScreenNoticeViewClass)throws Throwable{
        if(FullScreenNoticeViewClass==null){
            XposedBridge.log("FullScreenNoticeViewClass=null");
        }else{
            XposedHelpers.findAndHookMethod(FullScreenNoticeViewClass, "show", "com.bilibili.bililive.videoliveplayer.net.beans.gateway.userinfo.LiveNotice", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(null);
                }

            });
        }
    }

    public void HideLiveNormalBanner(Class<?> LiveRoomOperationAppServiceImplClass)throws Throwable{
        XposedHelpers.findAndHookMethod(LiveRoomOperationAppServiceImplClass, "setPendant", java.util.List.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                param.args[0] = new ArrayList<>();
            }

        });
    }
}
