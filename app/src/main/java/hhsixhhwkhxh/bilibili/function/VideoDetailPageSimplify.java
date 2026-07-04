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
        boolean ForceEnableOldComments = sharedPreferences.getBoolean("ForceEnableOldComments",false);
        boolean BanPauseCountdownAD = sharedPreferences.getBoolean("BanPauseCountdownAD",false);

        if(VideoDetailPageRemoveAD){
            //VideoDetailPageRemoveAD(lpparam);
            safeRun(this::VideoDetailPageRemoveAD,lpparam,"VideoDetailPageRemoveAD");
        }
        if(BanEditTextSBHint){
            //BanEditTextSBHint(lpparam);
            safeRun(this::BanEditTextSBHint,lpparam,"BanEditTextSBHint");
        }

        if(BanDirectFavorite){
            //BanDirectFavorite(lpparam);
            safeRun(this::BanDirectFavorite,lpparam,"BanDirectFavorite");
        }

        if(BanBeggingDanmaku){
            //BanBeggingDanmaku(lpparam);
            safeRun(this::BanBeggingDanmaku,lpparam,"BanBeggingDanmaku");
        }

        if(HideVerticalVideoEntrance){
            //HideVerticalVideoEntrance(lpparam);
            safeRun(this::HideVerticalVideoEntrance,lpparam,"HideVerticalVideoEntrance");
        }

        if(ForceEnableOldComments){
            //ForceEnableOldComments(lpparam);
            safeRun(this::ForceEnableOldComments,lpparam,"ForceEnableOldComments");
        }

        if(BanPauseCountdownAD){
            //BanPauseCountdownAD(lpparam);
            safeRun(this::BanPauseCountdownAD,lpparam,"BanPauseCountdownAD");
        }

    }
    public void VideoDetailPageRemoveAD(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //去除视频和简介/评论区之间的广告 通常是大白条广告 也有咸鱼广告是黄色的 要按右部三个点选原因去除


        Class<?> cClass = Utils.getDeConfusionClass("ou0_cClass",lpparam.classLoader);
        if(cClass==null){
            Utils.reportError("RemoveVideoDetailPageAD中cClass为空");
            return;
        }
        Class<?> CollapsableChildFrameLayoutClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.behavior.CollapsableChildFrameLayout",lpparam.classLoader);
        XposedBridge.hookAllConstructors(cClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (int i = param.args.length-1;i>=0;i--){
                    if(param.args[i]==null||!param.args[i].getClass().equals(CollapsableChildFrameLayoutClass)){
                        continue;
                    }
                    View view = (View) param.args[i];
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                    view.setLayoutParams(params);
                    return;
                }
            }
        });

        //去除评论区的顶格广告 是小黄条 右部有叉叉 按下消失
        /*
        Class CommentListAdapterClass = XposedHelpers.findClass("com.bilibili.app.comment3.ui.adapter.b",lpparam.classLoader);
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

        });*/
    }
    public void BanEditTextSBHint(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);
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
        Class<?> UIComponentExtKtClass = Utils.getDeConfusionClass("com_bilibili_app_gemini_ui_UIComponentExtKt$onLongClickEvent$1Class",lpparam.classLoader);
        if(UIComponentExtKtClass==null){
            Utils.reportError("BanDirectFavorite中 UIComponentExtKtClass为空");
            return;
        }
        Field viewField = Utils.selectField(UIComponentExtKtClass,View.class);
        if(viewField==null){
            Utils.reportError("BanDirectFavorite中 viewField为空");
            return;
        }
        viewField.setAccessible(true);
        XposedHelpers.findAndHookMethod(UIComponentExtKtClass, "invokeSuspend", Object.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View view = (View) viewField.get(param.thisObject);
                if(view==null){
                    return;
                }
                Object mListenerInfo = XposedHelpers.getObjectField(view,"mListenerInfo");
                if(mListenerInfo==null){
                    return;
                }
                View.OnLongClickListener rawOnLongClickListener = (View.OnLongClickListener) XposedHelpers.getObjectField(mListenerInfo,"mOnLongClickListener");
                if(rawOnLongClickListener==null){
                    return;
                }
                view.setOnClickListener(rawOnLongClickListener::onLongClick);
                view.setOnLongClickListener(null);
            }
        });
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

    public void ForceEnableOldComments(XC_LoadPackage.LoadPackageParam lpparam){
        XposedHelpers.findAndHookMethod("com.bilibili.lib.dd.DeviceDecision", lpparam.classLoader, "getBoolean", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String ddName = (String) param.args[0];
                if(ddName!=null&&ddName.startsWith("comment.next_appearance")){
                    param.setResult(false);
                }
            }

        });
    }

    public void BanPauseCountdownAD(XC_LoadPackage.LoadPackageParam lpparam){
        final Class<?> PausedPageServiceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService",lpparam.classLoader);
        final Class<?> PauseTriggerSourceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService$PauseTriggerSource",lpparam.classLoader);
        final Class<?> ContinuationImplClass = XposedHelpers.findClass("kotlin.coroutines.jvm.internal.ContinuationImpl",lpparam.classLoader);
        final Class<?> UnitClass = XposedHelpers.findClass("kotlin.Unit",lpparam.classLoader);
        final Object INSTANCE = XposedHelpers.getStaticObjectField(UnitClass,"INSTANCE");
        final Method mMethod = Utils.selectMethod(PausedPageServiceClass,Object.class,PausedPageServiceClass,PauseTriggerSourceClass,ContinuationImplClass);
        if(mMethod==null){
            Utils.reportError("BanPauseCountdownAD中 mMethod为空");
            return;
        }
        XposedBridge.hookMethod(mMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(INSTANCE);
            }
        });
    }
} 
