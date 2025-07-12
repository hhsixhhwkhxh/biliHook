package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import android.app.Activity;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedBridge;
import java.util.List;
import java.util.ArrayList;
import android.os.Handler;
import hhsixhhwkhxh.xposed.bilihook.Utils;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class RemoveVideoDetailPageAD extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean VideoDetailPageRemoveAD =sharedPreferences.getBoolean("VideoDetailPageRemoveAD",false);
        boolean BanEditTextSBHint = sharedPreferences.getBoolean("BanEditTextSBHint",false);
        boolean BanDirectFavorite = sharedPreferences.getBoolean("BanDirectFavorite",false);

        if(VideoDetailPageRemoveAD){
            VideoDetailPageRemoveAD(lpparam);
        }
        if(BanEditTextSBHint){
            BanEditTextSBHint(lpparam);
        }

        if(BanDirectFavorite){
            BanDirectFavorite(lpparam);
        }



    }
    public void VideoDetailPageRemoveAD(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //去除视频和简介/评论区之间的广告 通常是大白条广告 也有咸鱼广告是黄色的 要按右部三个点选原因去除
        Method mj3_a_bindMethod = Utils.getDeConfusionMethod("mj3_a_bindMethod",lpparam.classLoader);
        if(mj3_a_bindMethod==null){
            Utils.reportError("RemoveVideoDetailPageAD中mj3_a_bindMethod为空");
            return;
        }
        XposedBridge.hookMethod(mj3_a_bindMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View frameLayout3 =(View) XposedHelpers.callStaticMethod(XposedHelpers.findClass("androidx.viewbinding.ViewBindings",lpparam.classLoader),"findChildViewById",(View)param.args[0],Utils.getViewID("underplayer_container"));
                if (frameLayout3.getParent() instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) frameLayout3.getParent();
                    parent.removeView(frameLayout3);
                }
                //frameLayout3.setVisibility(View.GONE);
            }

        });

        //去除评论区的顶格广告 是小黄条 右部有叉叉 按下消失

        Class CommentListAdapterClass = XposedHelpers.findClass("com.bilibili.app.comment3.ui.adapter.CommentListAdapter",lpparam.classLoader);
        Method e1Method = Utils.selectMethod(CommentListAdapterClass, Object.class, java.util.List.class, boolean.class, boolean.class, String.class,XposedHelpers.findClass("kotlin.coroutines.Continuation",lpparam.classLoader));
        //Lcom/bilibili/app/comment3/ui/adapter/CommentListAdapter;->e1(Ljava/util/List;ZZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;

        if(e1Method==null){
            Utils.reportError("RemoveVideoDetailPageAD中e1Method为空");
            return;
        }

        XposedBridge.hookMethod(e1Method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                List list = (List) param.args[0];
                if(list==null||list.isEmpty()){return;}
                if(list.get(0).toString().contains("CM")){
                    list.remove(0);
                }
            }

        });
    }
    public void BanEditTextSBHint(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply", lpparam.classLoader, "getInput", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }

        });
    }

    public void BanDirectFavorite(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> UIComponentExtKtClass = XposedHelpers.findClass("com.bilibili.app.gemini.base.ui.UIComponentExtKt",lpparam.classLoader);
        Class<?> FlowClass = XposedHelpers.findClass("kotlinx.coroutines.flow.Flow",lpparam.classLoader);
        Method aMethod = Utils.selectMethod(UIComponentExtKtClass,FlowClass,View.class);
        int favoriteId = Utils.getViewID("frame_fav");
        if(aMethod==null){
            Utils.reportError("RemoveVideoDetailPageAD中aMethod为空");
            return;
        }
        UnhooksList.add(XposedBridge.hookMethod(aMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view = (View) param.args[0];
                //log(view.getId());
                if(view.getId()!=favoriteId){return;}

                TextView proxy = new TextView(view.getContext());
                param.args[0] = proxy;//狸猫换太子 避免我下面的点击事件被覆盖

                AtomicReference<Object> mOnLongClickListenerRef = new AtomicReference<>();;
                view.setOnClickListener(view1 -> {
                    if(mOnLongClickListenerRef.get()==null){
                        Object ListenerInfo = XposedHelpers.callMethod(view,"getListenerInfo");
                        mOnLongClickListenerRef.set(XposedHelpers.getObjectField(ListenerInfo, "mOnLongClickListener"));
                    }
                    XposedHelpers.callMethod(mOnLongClickListenerRef.get(),"onLongClick",view1);
                });

            }

        }));
    }
} 
