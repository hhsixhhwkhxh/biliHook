package hhsixhhwkhxh.bilibili.function;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedBridge;
import java.util.List;
import java.util.ArrayList;

import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

public class VideoDetailPageSimplify extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean VideoDetailPageRemoveAD =sharedPreferences.getBoolean("VideoDetailPageRemoveAD",false);
        boolean BanEditTextSBHint = sharedPreferences.getBoolean("BanEditTextSBHint",false);
        boolean BanDirectFavorite = sharedPreferences.getBoolean("BanDirectFavorite",false);
        boolean BanBeggingDanmaku =  sharedPreferences.getBoolean("BanBeggingDanmaku",false);
        boolean HideVerticalVideoEntrance = sharedPreferences.getBoolean("HideVerticalVideoEntrance",false);

        if(VideoDetailPageRemoveAD){
            VideoDetailPageRemoveAD(lpparam);
        }
        if(BanEditTextSBHint){
            BanEditTextSBHint(lpparam);
        }

        if(BanDirectFavorite){
            BanDirectFavorite(lpparam);
        }

        if(BanBeggingDanmaku){
            BanBeggingDanmaku(lpparam);
        }

        if(HideVerticalVideoEntrance){
            HideVerticalVideoEntrance(lpparam);
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
        final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);
        final Class<?> InputConfigClass = XposedHelpers.findClass("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply$InputConfig",lpparam.classLoader);
        List<Field> TextFieldsList = new ArrayList<>();
        for(Field field : InputConfigClass.getDeclaredFields()){
            if(field.getName().contains("Text")){
                field.setAccessible(true);
                TextFieldsList.add(field);
            }
        }
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply", lpparam.classLoader, "getInput", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //param.setResult(null);

                Object InputConfigObject = param.getResult();
                if(InputConfigObject==null){
                    return;
                }
                //XposedHelpers.setObjectField(InputConfigObject,"funcButtons_",null);
                XposedHelpers.setObjectField(InputConfigObject,"editorIconConfig_",null);
                for(Field field : TextFieldsList){
                    field.set(InputConfigObject,null);
                }

                //Utils.printStackTrace("BanEditTextSBHint");

                //禁言相关
                //XposedHelpers.setBooleanField(InputConfigObject,"disabled_",false);


            }

        });


        //禁用推荐表情
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply$EmoteConfig", lpparam.classLoader, "getSuggestEmotesList", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(new ArrayList<>());
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

    public void BanBeggingDanmaku(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        /*
        final Class<?> DmViewReplyClass = XposedHelpers.findClass("com.bapis.bilibili.community.service.dm.v1.DmViewReply",lpparam.classLoader);
        //Lcom/bapis/bilibili/community/service/dm/v1/DmViewReply;->command_:Lcom/bapis/bilibili/community/service/dm/v1/Command;
        final Field command_Field = XposedHelpers.findFieldIfExists(DmViewReplyClass,"command_");


        if(command_Field==null){
            Utils.reportError("RemoveVideoDetailPageAD中command_Field为空");
            return;
        }

        command_Field.setAccessible(true);

        Class<?> CommandClass = XposedHelpers.findClass("com.bapis.bilibili.community.service.dm.v1.Command",lpparam.classLoader);
        //Lcom/bapis/bilibili/community/service/dm/v1/Command;->commandDms_:Lcom/google/protobuf/Internal$ProtobufList;
        final Field commandDms_Field = XposedHelpers.findFieldIfExists(DmViewReplyClass,"commandDms_");

        if(commandDms_Field==null){
            Utils.reportError("RemoveVideoDetailPageAD中commandDms_Field为空");
            return;
        }
        commandDms_Field.setAccessible(true);
        */
        //上面我想用java反射的api尽可能代替xposedApi 以期提高性能 但发现可读性急剧下降 询问DeepSeek得知xposed对getObjectField有缓存Field对象减少反射优化 于是作罢

        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.service.interact.core.model.DanmakuParams", lpparam.classLoader, "setDmViewReply", "com.bapis.bilibili.community.service.dm.v1.DmViewReply", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                Object commandObject = XposedHelpers.getObjectField(param.args[0],"command_");

                List<?> rawList = (List) XposedHelpers.getObjectField(commandObject,"commandDms_");
                List<?> newList = (List) XposedHelpers.callMethod(rawList,"mutableCopyWithCapacity",rawList.size());
                //Lcom/google/protobuf/Internal$ProtobufList;->mutableCopyWithCapacity(I)Lcom/google/protobuf/Internal$ProtobufList;

                for (int i = newList.size()-1; i >= 0; i--) {
                    Object CommandDmObject = newList.get(i);
                    String command = (String) XposedHelpers.getObjectField(CommandDmObject,"command_");
                    if(!command.contains("UP")){
                        newList.remove(i);
                    }
                }

                XposedHelpers.setObjectField(commandObject,"commandDms_",newList);
            }

        });
    }


    public void HideVerticalVideoEntrance(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.player.widget.story.GeminiPlayerFullStoryWidget", lpparam.classLoader, "setVisibility", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = View.GONE;
            }

        });
    }
} 
