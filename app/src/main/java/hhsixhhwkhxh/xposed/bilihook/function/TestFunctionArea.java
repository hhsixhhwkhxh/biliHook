package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import android.view.View;
import hhsixhhwkhxh.xposed.bilihook.Utils;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Type;
import de.robv.android.xposed.XposedBridge;

import java.util.Arrays;
import android.content.Context;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Handler;
import android.widget.TextView;

public class TestFunctionArea extends FunctionsBase {
/*
    这里是功能成熟前的测试的地方 有许多废弃代码
    理论上不接受关于TestFunctionArea的bug报告
*/
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //XposedBridge.log("test");
        /*
        test1(lpparam);
        test3(lpparam);
        test5(lpparam);
        test7(lpparam);
        test8(lpparam);
        test9(lpparam);
        test10(lpparam);
        */

        //test11(lpparam);
        //test12(lpparam);
        //test13(lpparam);
         //test14(lpparam);
        //test15(lpparam);
        //test16(lpparam);
        //test5(lpparam);
        //test17(lpparam);
        //test18(lpparam);
        //test19(lpparam);
        //test7(lpparam);
        //test20(lpparam);
        //test21(lpparam);
        test22(lpparam);
        //test23(lpparam);
        //test24(lpparam);
        //test25(lpparam);
        //test26(lpparam);
        //test27(lpparam);
        //test28(lpparam);
        //test29(lpparam);
    }

    public void advanceRun(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //test26(lpparam);
    }


    //隐藏视频详情页的竖屏模式入口
    public void test29(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{

        //没效果
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.ControlContainerConfig", lpparam.classLoader, "setImmersiveVisibleIds", java.util.HashMap.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                HashMap<?,?> hashMap = (HashMap) param.args[0];
                if(hashMap==null||hashMap.isEmpty()){return;}
                HashSet set = (HashSet) hashMap.get(1);
                Iterator<Integer> iterator = set.iterator();
                while (iterator.hasNext()) {
                    int id = iterator.next();

                    // 遍历时安全删除
                    //0x7f090453
                    if (id==Integer.valueOf(Utils.getViewID("bbplayer_halfscreen_story"))) {
                        iterator.remove(); // 安全删除当前元素
                        log("test29 setImmersiveVisibleIds 删除");
                    }


                }
                set.add(0x0d000721);

                log("setImmersiveVisibleIds test29:"+param.args[0]);

            }

        });


        //懒加载报错
        Class<?> Function0Class = XposedHelpers.findClass("kotlin.jvm.functions.Function0",lpparam.classLoader);
        Method invokeMethod = Function0Class.getMethod("invoke");
        /*[ 2025-07-14T00:28:42.327    10338: 32160: 32160 E/LSPosed-Bridge  ] kotlin.UninitializedPropertyAccessException: lateinit property controlContainerService has not been initialized
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.ControlContainerConfig", lpparam.classLoader, "setLayoutView", "kotlin.jvm.functions.Function0", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //super.beforeHookedMethod(param);
                //param.setResult(null);
                ViewGroup viewGroup = (ViewGroup) invokeMethod.invoke(param.args[0]);
                View view = viewGroup.findViewById(0x7f090453);
                if(view==null){
                    log("找不到");
                    return;
                }
                view.setVisibility(View.GONE);
                log("已隐藏");
            }

        });
        */
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.ControlContainerConfig", lpparam.classLoader, "setInstance", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                View layoutView = (View) param.args[0];
                View view = layoutView.findViewById(0x7f090453);
                if(view==null){
                    log("setInstance找不到");
                    return;
                }
                //view.setVisibility(View.GONE);

                //log("已隐藏");
                //view.setLayoutParams(new ViewGroup.LayoutParams(0,0));
            }

        });



        // Hook l1() 视图初始化 bili 会重新让控件可见
        XposedHelpers.findAndHookMethod(
                "tv.danmaku.biliplayerimpl.controlcontainer.ControlContainer",
                lpparam.classLoader,
                "l1",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if ((boolean) param.getResult()) {
                            Object config = XposedHelpers.getObjectField(param.thisObject, "d");
                            View instance = (View) XposedHelpers.callMethod(config, "getInstance");
                            View view = instance.findViewById(0x7f090453);
                            if(view==null){
                                log("l1找不到");
                                return;
                            }
                            //view.setVisibility(View.GONE);
                            log("viewClass:"+view.getClass());
                            //[ 2025-07-14T01:48:44.324    10338: 21475: 21475 I/LSPosed-Bridge  ] TestFunctionArea viewClass:class com.bilibili.app.gemini.player.widget.story.GeminiPlayerFullStoryWidget
                            //view.setLayoutParams(new ViewGroup.LayoutParams(0,0));
                            //view.setClipBounds(new Rect(0, 0, 0, 0));
                            ///ViewGroup parent = (ViewGroup) view.getParent();
                            //parent.removeView(view); // 彻底移除（需重新添加才能显示）
                            //log("l1已隐藏");

                        }
                    }
                }
        );


        /*查找调用堆栈 谁动了我的setVisibility
        XposedHelpers.findAndHookMethod("android.widget.ImageView", lpparam.classLoader, "setVisibility", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view = (View) param.thisObject;
                if(view.getId()==0x7f090453){
                    Utils.printStackTrace("0x7f090453 setVisibility "+param.args[0]);
                }
            }

        });

         */


        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.player.widget.story.GeminiPlayerFullStoryWidget", lpparam.classLoader, "setVisibility", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = View.GONE;
            }

        });
    }


    //禁用剪切板直接跳转 及每次跳转必须询问 这部分是豆包写的 但是把跳转功能直接整废了
    public void test28(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        // 1. Hook ClipboardResult的setMode方法，强制将popupMode改为2（弹窗模式）
        // 原本popupMode=1为直接跳转，改为2后会触发弹窗逻辑（见T方法）
        Class<?> clipboardResultClass = XposedHelpers.findClass("tv.danmaku.bili.ui.clipboard.ClipboardResult", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clipboardResultClass, "setMode", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int originalMode = (int) param.args[0];
                if (originalMode == 1) { // 拦截直接跳转模式
                    param.args[0] = 2; // 改为弹窗模式
                }
            }
        });

        // 2. Hook ClipboardResult的checkPage方法，强制返回false
        // 原本checkPage()=true会直接跳转，改为false后会执行弹窗（见V方法）
        XposedHelpers.findAndHookMethod(clipboardResultClass, "checkPage", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false); // 强制不直接跳转，走弹窗
            }
        });

        // 3. Hook ClipboardChecker的x0方法，让BVNEW/COMMON_JUMP也走弹窗
        // 原本x0()=true会跳过弹窗直接跳转，改为false后触发弹窗逻辑
        Class<?> clipboardCheckerClass = XposedHelpers.findClass("tv.danmaku.bili.ui.clipboard.ClipboardChecker", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clipboardCheckerClass, "x0", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false); // 禁用BVNEW/COMMON_JUMP的直接跳转特权
            }
        });
    }

    //禁用三连弹幕等
    public void test27(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        Class<?> DirectorVersionClass = XposedHelpers.findClass("tv.danmaku.biliplayerv2.DirectorVersion",lpparam.classLoader);
        /*会崩溃 还查不到报错位置
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.PlayerConfiguration", lpparam.classLoader, "setDirectorVersion", "tv.danmaku.biliplayerv2.DirectorVersion", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = XposedHelpers.getStaticObjectField(DirectorVersionClass,"V1");
            }

        });
        */

        /*
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.community.service.dm.v1.DmViewReply", lpparam.classLoader, "getSpecialDmsList", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.callMethod(param.thisObject,"clearSpecialDms");
            }

        });*/

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


    //草 我这模块在onCreate函数之后才启动 然而这个routesBean函数早在此前就调用完了 排查了半天bug
    public void test26(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Utils.showToast("test26",0);
        final Set<String> writtenLines = Collections.synchronizedSet(new HashSet<>());
        Class<?> BuiltInKtClass = XposedHelpers.findClass("com.bilibili.lib.blrouter.internal.BuiltInKt",lpparam.classLoader);
        String filename = "/storage/emulated/0/Android/data/tv.danmaku.bili/files/rule"+ UUID.randomUUID() +".txt";
        for(Method m : BuiltInKtClass.getMethods()){
            if(m.getName()!="routesBean"){continue;}
            if(!m.getParameterTypes()[0].equals(String.class)){continue;}
            XposedBridge.hookMethod(m ,new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String line = (String) param.args[0];
                    if(writtenLines.contains(line)){return;}
                    if(!line.startsWith("bilibili://")){return;}
                    if(line.contains("_")){return;}
                    log(line);
                    writtenLines.add(line);
                    //Utils.showToast(line,0);
                    // 每次写入都打开文件（追加模式）并写入一行，然后关闭
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        // 打印异常，方便调试
                        XposedBridge.log(e);
                    }
                }
            });
        }
        /*ClassLoader classLoader = lpparam.classLoader;
        XposedHelpers.findAndHookMethod("com.bilibili.lib.blrouter.internal.BuiltInKt", classLoader, "routesBean", String.class, classLoader.loadClass("[Lcom/bilibili/lib/blrouter/model/RouteBean;"), classLoader.loadClass("com.bilibili.lib.blrouter.Ordinaler"), classLoader.loadClass("[Lkotlin/Pair;"), classLoader.loadClass("javax.inject.Provider"), classLoader.loadClass("javax.inject.Provider"), classLoader.loadClass("javax.inject.Provider"), classLoader.loadClass("com.bilibili.lib.blrouter.internal.ModuleWrapper"),  new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String line = (String) param.args[0];
                log(line);
                Utils.showToast(line,0);
                // 每次写入都打开文件（追加模式）并写入一行，然后关闭
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("/storage/emulated/0/Android/data/tv.danmaku.bili/files/rule.txt", true))) {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    // 打印异常，方便调试
                    XposedBridge.log(e);
                }
            }
        });/*
    }
    public void test25(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.p0", lpparam.classLoader, "h", "tv.danmaku.bili.ui.main2.mine.o0", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object o = param.args[0];
                XposedHelpers.setObjectField(o,"b","bilibili://user_center/favourite?version=1");
                Object c = XposedHelpers.getObjectField(o,"c");
                XposedHelpers.setObjectField(c,"uri","bilibili://user_center/favourite?version=1");

            }

        });
        */

        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.p0", lpparam.classLoader, "b", "com.bilibili.lib.homepage.mine.MenuGroup$Item", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object MenuGroup$ItemObject = param.args[0];
                String rawUri = (String) XposedHelpers.getObjectField(MenuGroup$ItemObject,"uri");
                if(!rawUri.contains("favourite")){return;}
                String newUri = rawUri.substring(0,rawUri.length()-1)+"1";
                XposedHelpers.setObjectField(MenuGroup$ItemObject,"uri",newUri);
            }

        });


    }

    public void test24(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*失败
        XposedHelpers.findAndHookMethod("com.bilibili.lib.blrouter.internal.routes.DefaultGlobalLauncher", lpparam.classLoader, "launch", android.content.Context.class, "androidx.fragment.app.Fragment", "com.bilibili.lib.blrouter.RouteRequest", android.content.Intent[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object RouteRequestObject = param.args[2];

                Uri uri = (Uri) XposedHelpers.callMethod(RouteRequestObject,"getTargetUri");
                log("原uri: "+uri.getPath());
                String path = uri.getPath();
                if(path==null||!path.contains("playlist/playpage")){return;}
                String id = path.substring(path.lastIndexOf("/")+1);

                XposedHelpers.setObjectField(RouteRequestObject,"d",Uri.parse("bilibili://united_video/"+id));
                //XposedHelpers.setObjectField(RouteRequestObject,"d",null);
                log("现uri: "+XposedHelpers.getObjectField(RouteRequestObject,"d"));
                param.args[3] = new Intent[]{new Intent(Utils.getMainActivity(),XposedHelpers.findClass("com.bilibili.ship.theseus.detail.UnitedBizDetailsActivity",lpparam.classLoader))} ;
            }

        });*/

        /*成功 但hook点位怪 代码不雅
        XposedHelpers.findAndHookMethod("com.bilibili.lib.blrouter.RouteRequest", lpparam.classLoader, "getTargetUri", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Uri uri = (Uri) param.getResult();
                String path = uri.getPath();
                if(path==null||!path.contains("playlist/playpage")){return;}
                Utils.printStackTrace("getTargetUri");
                //String id = path.substring(path.lastIndexOf("/")+1);
                Object ExtrasObject = XposedHelpers.callMethod(param.thisObject,"getExtras");
                String id = (String) XposedHelpers.callMethod(ExtrasObject,"get","sourceid");
                Bundle bundle = (Bundle) XposedHelpers.getObjectField(ExtrasObject,"a");
                bundle.putString("aid",id);
                Uri newUri = Uri.parse("bilibili://united_video/"+id);
                XposedHelpers.setObjectField(param.thisObject,"d",newUri);
                param.setResult(newUri);


            }
        });
        */
        //测试仅凭字符串重定向 成功
        /*
        XposedHelpers.findAndHookMethod("ij5.d", lpparam.classLoader, "c", String.class, android.content.Context.class, java.util.Map.class, int.class, Object.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //bilibili://music/playlist/playpage/308614634?page_type=3&oid=114753020107241&otype=2&sourceid=114753020107241&from_spmid=main.my-fav.0.0
                //param.args[0] = "bilibili://united_video/114753020107241?&aid=114753020107241";

            }

        });
        */

        //XposedHelpers.callStaticMethod(jsonClass,"parseObject",bigCoverJson,SmallCoverV2ItemClass);
        //Class<?> targetCls =XposedHelpers.findClass("pf5.e",lpparam.classLoader);
        XposedHelpers.findAndHookMethod("pf5.e", lpparam.classLoader, "getLink", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String result = (String) param.getResult();
                Uri rawUri = Uri.parse(result);
                String sourceId = rawUri.getQueryParameter("sourceid");
                String newPath = "//united_video/" + sourceId;

                Uri.Builder builder = new Uri.Builder()
                        .scheme(rawUri.getScheme())
                        .path(newPath);



                // 添加新参数aid=123
                builder.appendQueryParameter("aid", sourceId);

                // 生成最终URI
                Uri newUri = builder.build();
                String newResult = newUri.toString();
                log(newUri);
                param.setResult(newResult);
            }
        });

    }

    //禁用输入框的神人提示语 “千山万水总是情，评论两句行不行” “只是一直在等你而已，才不是想被评论呢~” 等
    public void test23(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{


        /*
        XposedHelpers.findAndHookMethod("android.widget.TextView", lpparam.classLoader, "setHint", CharSequence.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                EditText editText = (EditText)param.thisObject;
                //param.args[0]=(CharSequence)"test";
                //0x7f091d1f 没开输入法的输入框
                if(editText.getId()==0x7f091d1f){
                    Utils.printStackTrace("test23 没开 "+param.args[0]);
                }
                //0x7f090e74 开输入法的输入框
                if(editText.getId()==0x7f090e74){
                    Utils.printStackTrace("test23 开 "+param.args[0]);
                }
            }

        });*/


        /*
        //阻止kt协程启动
        XposedHelpers.findAndHookMethod("com.bilibili.app.comment3.ui.view.CommentMainLayer$bind$2", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }

        });
        //传空的字符串
        XposedHelpers.findAndHookMethod("com.bilibili.app.comm.opus.lightpublish.compose.PublishInputBoxKt", lpparam.classLoader, "l", "ov.c", "kotlinx.coroutines.CoroutineScope", CharSequence.class, "androidx.compose.runtime.State", "kotlin.jvm.functions.Function1", String.class, "pv.d", android.content.Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //CharSequence charSequence = (CharSequence) param.args[2];
                param.args[2] = (CharSequence)"";
            }

        });
        */
        //上面是最初的方法 两个hook联合绞杀 可以是可以 但是我不喜欢hook kt自动生成的类


        //Landroid/content/Context;->getString(I)Ljava/lang/String;
        /* 调查getString方法的返回值
        XposedHelpers.findAndHookMethod(Context.class, "getString", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int resId = (int) param.args[0];
                Context context = (Context) param.thisObject;
                String str = (String) param.getResult();
                if(resId==0x7f114ff7){
                    String resName = context.getResources().getResourceName(resId);
                    log("resName:"+resName+"  result:"+str);
                    //[ 2025-07-10T01:50:44.321    10338: 15057: 15057 I/LSPosed-Bridge  ] TestFunctionArea resName:tv.danmaku.bili:string/comment_global_string_200  result:说点什么吧
                }
            }
        });*/

        /*
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply$InputConfig", lpparam.classLoader, "getDisabled", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }

        });*/

        /*没效果
        Class<?> SubjectDescriptionReplyClass = XposedHelpers.findClass("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply",lpparam.classLoader);
        Method clearInputMethod = SubjectDescriptionReplyClass.getDeclaredMethod("clearInput");

        //Object NullSubjectDescriptionReplyObject = SubjectDescriptionReplyClass.newInstance();

        XposedHelpers.findAndHookMethod("com.bilibili.app.comment3.data.source.v2.a", lpparam.classLoader, "e", SubjectDescriptionReplyClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object SubjectDescriptionReplyObject = param.args[0];
                clearInputMethod.invoke(SubjectDescriptionReplyObject);

            }

        });*/
        //最终方案
        XposedHelpers.findAndHookMethod("com.bapis.bilibili.main.community.reply.v2.SubjectDescriptionReply", lpparam.classLoader, "getInput", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }

        });





    }


    //在主页推送的更多按钮页添加按钮 供复制卡片json
    //主页推送有部分fw内容没有过掉 因此 有这个功能可以查看它们的特征 更新过滤代码
    //调试用
    public void test22(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*
        *   com.bilibili.pegasus.card.base.CardClickProcessor.q1(@NotNull f<T> fVar, @NotNull View view, boolean z)
        *   com.bilibili.app.comm.list.common.widget.bottomsheet.drag.ListThreePointMenuDialogFragment.show$default(ListThreePointMenuDialogFragment listThreePointMenuDialogFragment, FragmentManager fragmentManager, List list, Function0 function0, int i, Object obj)
        * */

        Class<?> ImageTitleDataClass = XposedHelpers.findClass("com.bilibili.app.comm.list.common.widget.bottomsheet.drag.model.ImageTitleData",lpparam.classLoader);
        Constructor<?> ImageTitleDataConstructor = ImageTitleDataClass.getConstructor(String.class,String.class,String.class,android.graphics.drawable.Drawable.class,boolean.class, XposedHelpers.findClass("kotlin.jvm.functions.Function0",lpparam.classLoader));
        Class<?> OnClickListenerClass = XposedHelpers.findClass("kotlin.jvm.functions.Function0",lpparam.classLoader);
        XposedHelpers.findAndHookMethod("com.bilibili.pegasus.card.base.CardClickProcessor", lpparam.classLoader, "K", "com.bilibili.pegasus.card.base.f", boolean.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Object fVarObject = param.args[0];


                InvocationHandler handler = new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("invoke".equals(method.getName())) {
                            Object BasicIndexItemObject = XposedHelpers.callMethod(fVarObject,"g0");
                            String BasicIndexItemJson = Utils.toJSONString(lpparam,BasicIndexItemObject);
                            Utils.copyText(BasicIndexItemJson);

                        }
                        return null; // 方法返回void，返回null即可
                    }
                };

                // 创建动态代理实例
                Object OnMenuClickListenerObject = Proxy.newProxyInstance(
                        lpparam.classLoader,
                        new Class<?>[]{OnClickListenerClass},
                        handler
                );

                Object NormalMenuItemObject = ImageTitleDataConstructor.newInstance(null,null,"复制json代码",null,false,OnMenuClickListenerObject);
                List list = (List) param.getResult();
                if(list==null){list=new ArrayList<>();}
                list.add(NormalMenuItemObject);
                //log(list);
            }
        });
    }



    //禁用原本的收藏点击事件(直接收藏进入默认文件夹) 改为与长按收藏相同的选择收藏文件夹再收藏
    public void test21(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //常规方法 hook设置长按点击事件的方法 模仿它new出来一个长按点击事件 重新给收藏按钮设置点击事件为调用长按回调方法

        /*
        Class<?> jClass = XposedHelpers.findClass("com.bilibili.app.gemini.base.ui.j",lpparam.classLoader);
        Constructor<?> jConstructor = jClass.getConstructor(XposedHelpers.findClass("kotlin.jvm.functions.Function0",lpparam.classLoader),XposedHelpers.findClass("kotlinx.coroutines.channels.ProducerScope",lpparam.classLoader));
        Method onLongClickMethod = jClass.getMethod("onLongClick",View.class);
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.UIComponentExtKt$onLongClickEvent$1", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View) XposedHelpers.getObjectField(param.thisObject,"$this_onLongClickEvent");
                Object function0 = XposedHelpers.getObjectField(param.thisObject,"$condition");
                Object ProducerScope = XposedHelpers.getObjectField(param.thisObject,"L$0");
                Object onLongClickCallBackClass = jConstructor.newInstance(function0,ProducerScope);
                view.setOnClickListener(v -> {
                    try {
                        onLongClickMethod.invoke(onLongClickCallBackClass,v);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
        */



        //相同的hook点 拿到view后直接使用xposedAPI获取原先设置的长按点击事件 新设置一个点击事件并与之对接
        //反射了系统类
        //hook点不够好
        /*
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.UIComponentExtKt$onLongClickEvent$1", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Utils.printStackTrace("UIComponentExtKt$onLongClickEvent$1.invokeSuspend");
                View view = (View) XposedHelpers.getObjectField(param.thisObject,"$this_onLongClickEvent");
                Object ListenerInfo = XposedHelpers.callMethod(view,"getListenerInfo");
                Object mOnLongClickListener = XposedHelpers.getObjectField(ListenerInfo,"mOnLongClickListener");

                view.setOnClickListener(v -> {
                    try {

                        XposedHelpers.callMethod(mOnLongClickListener,"onLongClick",v);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });*/
        /*
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.UIComponentExtKt", lpparam.classLoader, "c", android.view.View.class, "kotlin.jvm.functions.Function0", int.class, Object.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Utils.printStackTrace("com.bilibili.app.gemini.base.ui.UIComponentExtKt.c");
            }

        });*/

        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.UIComponentExtKt", lpparam.classLoader, "a", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view = (View) param.args[0];
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

        });
        /*
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.UIComponentExtKt", lpparam.classLoader, "b", android.view.View.class, "kotlin.jvm.functions.Function0", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View) param.args[0];
                Object ListenerInfo = XposedHelpers.callMethod(view,"getListenerInfo");
                Object mOnLongClickListener = XposedHelpers.getObjectField(ListenerInfo,"mOnLongClickListener");
                log("mOnLongClickListener"+mOnLongClickListener);
            }
        });*/
    }


    //精确评论时间至秒
    public void test20(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("kntr.base.localization.n0", lpparam.classLoader, "h", long.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Long timestampInMillis = (Long) param.args[0];
                if(timestampInMillis==null){return;}
                // 创建日期格式化器（格式可自定义）
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss  ", Locale.getDefault());

                // 设置时区（可选，默认系统时区）
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

                param.setResult(sdf.format(new Date(timestampInMillis)));
            }

        });


    }


    //去除评论区黄色条状广告
    public void test19(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("tv.danmaku.android.util.AppBuildConfig$Companion", lpparam.classLoader, "getDebug", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(true);//没用
            }

        });

        XposedHelpers.findAndHookMethod("com.bilibili.app.comment3.ui.adapter.CommentListAdapter", lpparam.classLoader, "e1", java.util.List.class, boolean.class, boolean.class, String.class, "kotlin.coroutines.Continuation", new XC_MethodHook() {
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



    //8.51.0版本 简化顶栏 最终hook点
    public void test18(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.HomeFragmentV2", lpparam.classLoader, "up", java.util.List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                List a = (List) param.args[0];
                List b = new ArrayList<>();
                for(int i =0;i<3;i++){
                    b.add(a.get(i));
                }
                param.args[0]=b;
            }

        });

    }
    //吐出顶栏的adapter类型
    public void test17(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //log("test17");


        XposedHelpers.findAndHookMethod("com.bilibili.lib.homepage.widget.SecondaryPagerSlidingTabStrip", lpparam.classLoader, "E", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object viewPagerObject = XposedHelpers.getObjectField(param.thisObject,"g");
                Object AdapterObject = XposedHelpers.callMethod(viewPagerObject,"getAdapter");
                log("AdapterClass: "+AdapterObject.getClass());

            }

        });
    }

    //为8.51.0版本 设计的 简化顶栏 但是有bug 开屏默认直播 而非推荐
    public void test16(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        final List NeededTags = Arrays.asList(new String[]{"直播","推荐","热门"});
        XposedHelpers.findAndHookMethod("com.bilibili.lib.homepage.widget.SecondaryPagerSlidingTabStrip", lpparam.classLoader, "r", int.class, CharSequence.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String name = (String)param.args[1];
                if(!NeededTags.contains(name)){
                    param.setResult(null);
                }
            }

        });
    }

    //追查视频详情页的recyclerView的adapter是哪个类 因为不知道为什么test14不能让它自己吐出来adapter
    public void test15(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{

        XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.detail.UnitedBizDetailsActivity", lpparam.classLoader, "onKeyDown", int.class, android.view.KeyEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int keyCode = (int)param.args[0];
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    View view = (View) XposedHelpers.callMethod(param.thisObject,"findViewById",0x7f092854);
                    log("test15:"+view);
                    //[ 2025-07-04T18:31:31.449    10338: 24361: 24361 I/LSPosed-Bridge  ] TestFunctionArea test15:com.bilibili.ship.theseus.united.widget.UnitedRecyclerView{8f49583 VFE...... ........ 0,0-1080,1926 #7f092854 app:id/recycler alpha=1.0 viewInfo = }
                    log("adapter:"+XposedHelpers.callMethod(view,"getAdapter").getClass());
                    //[ 2025-07-04T18:31:31.452    10338: 24361: 24361 I/LSPosed-Bridge  ] TestFunctionArea adapter:class com.bilibili.app.gemini.base.ui.d
                }
            }

        });
    }
    //将所有recyclerView的adapter类名绘制出来
    public void test14(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Utils.copyText("test14");
        XposedHelpers.findAndHookMethod(
                "androidx.recyclerview.widget.RecyclerView", // 支持 AndroidX
                lpparam.classLoader,
                "draw",
                Canvas.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View recyclerView = (View) param.thisObject;
                        Canvas canvas = (Canvas) param.args[0];

                        // 原始绘制完成后执行自定义绘制
                        //drawCenteredText(recyclerView, canvas);
                        if (recyclerView.getVisibility() != View.VISIBLE) return;

                        // 设置文字内容
                        //String text = "RecyclerView";
                        String text = "AdapterClass:"+XposedHelpers.callMethod(param.thisObject,"getAdapter").getClass();


                        // 初始化画笔
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setTextSize(spToPx(recyclerView, 8)); // 16sp
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        paint.setAntiAlias(true);
                        paint.setTextAlign(Paint.Align.CENTER);

                        // 计算居中位置
                        float centerX = recyclerView.getWidth() / 2f;
                        float centerY = recyclerView.getHeight() / 2f;

                        // 计算基线位置（垂直居中）
                        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                        float textHeight = fontMetrics.descent - fontMetrics.ascent;
                        float baseline = centerY - (textHeight / 2f - fontMetrics.descent);

                        // 绘制文字（带半透明背景）
                        int padding = dpToPx(recyclerView, 8);
                        int cornerRadius = dpToPx(recyclerView, 4);
                        float textWidth = paint.measureText(text);

                        // 绘制圆角背景
                        paint.setColor(Color.argb(128, 0, 0, 0)); // 半透明黑色
                        canvas.drawRoundRect(
                                centerX - textWidth / 2 - padding,
                                baseline + fontMetrics.ascent - padding,
                                centerX + textWidth / 2 + padding,
                                baseline + fontMetrics.descent + padding,
                                cornerRadius,
                                cornerRadius,
                                paint
                        );

                        // 绘制文字
                        paint.setColor(Color.WHITE);
                        canvas.drawText(text, centerX, baseline, paint);
                    }
                });
        /*
        XposedHelpers.findAndHookMethod("androidx.recyclerview.widget.RecyclerView", lpparam.classLoader, "setAdapter", "androidx.recyclerview.widget.RecyclerView$Adapter", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object adapterClass = XposedHelpers.callMethod(param.thisObject,"getAdapter");
                if(adapterClass==null){return;}
                log("adapterClass:"+adapterClass.getClass());
            }

        });*/
    }
    private int dpToPx(View view, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                view.getResources().getDisplayMetrics()
        );
    }

    // sp转px
    private float spToPx(View view, float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                view.getResources().getDisplayMetrics()
        );
    }
    public void test13(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Lcom/bilibili/pegasus/promo/BaseListFragment;->getMRecyclerView()Landroidx/recyclerview/widget/RecyclerView;
        Class<?> BaseListFragmentClass = XposedHelpers.findClass("com.bilibili.pegasus.promo.BaseListFragment",lpparam.classLoader);
        Method getMRecyclerViewMethod = BaseListFragmentClass.getDeclaredMethod("getMRecyclerView");
        getMRecyclerViewMethod.setAccessible(true);

        //Class<?> RecyclerViewClass = XposedHelpers.findClass("androidx.recyclerview.widget.RecyclerView",lpparam.classLoader);
        XposedHelpers.findAndHookMethod("com.bilibili.pegasus.promo.index.IndexFeedFragmentV2", lpparam.classLoader, "Vt", java.util.List.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object recyclerView = getMRecyclerViewMethod.invoke(param.thisObject);
                if(recyclerView!=null){

                    log("Vt方法的recyclerView的id "+XposedHelpers.callMethod(recyclerView,"getId"));
                    //TestFunctionArea Vt方法的recyclerView的id 2131306595 0x7f092863
                }else{
                    log("Vt方法的recyclerView为null");
                }


            }
        });

    }


    //以下是7.69.0版本老代码

    //调查历史记录的滑动列表
    public void test12(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V
        XposedHelpers.findAndHookMethod("androidx.recyclerview.widget.RecyclerView",lpparam.classLoader,"setAdapter","androidx.recyclerview.widget.RecyclerView$Adapter",new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    View view = (View) param.thisObject;
                    if(view.getId()==0x7f092854){
                        Utils.log("setAdapter");
                        Utils.printStackTrace("setAdapterTest");
                    }
                    
                }
        });
    }
    
    //搁置 效果不佳 禁用竖屏模式的滑动切换下一个视频
    public void test11(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Landroidx/viewpager2/widget/ViewPager2;->registerOnPageChangeCallback(Landroidx/viewpager2/widget/ViewPager2$OnPageChangeCallback;)V
        XposedHelpers.findAndHookMethod("androidx.viewpager2.widget.ViewPager2",lpparam.classLoader,"registerOnPageChangeCallback","androidx.viewpager2.widget.ViewPager2$OnPageChangeCallback",new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    View view = (View) param.thisObject;
                    if(view.getId()==0x7f091461){
                        param.setResult(null);
                    }
                    Utils.log("registerOnPageChangeCallback "+view.getId());
                }
        });
    }
    
    
    //禁用视频详情页评论区发言编辑框的糖言糖语
    public void test10(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //com.bilibili.app.comment3.data.source.v2.SubjectDescriptionDataSourceV2
        final Class<?> SubjectDescriptionDataSourceV2Class = XposedHelpers.findClass("com.bilibili.app.comment3.data.source.v2.SubjectDescriptionDataSourceV2",lpparam.classLoader);
        final Class<?> SubjectIDClass = XposedHelpers.findClass("com.bilibili.app.comment3.data.model.SubjectId",lpparam.classLoader);
        final Class<?> ContinuationClass = XposedHelpers.findClass("kotlin.coroutines.Continuation",lpparam.classLoader);
        //Lcom/bilibili/app/comment3/data/source/v2/SubjectDescriptionDataSourceV2;->a(Lcom/bilibili/app/comment3/data/model/SubjectId;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;
        
        Method setHintMethod = Utils.selectMethod(SubjectDescriptionDataSourceV2Class,Object.class,SubjectIDClass,boolean.class,boolean.class,ContinuationClass);
        XposedBridge.hookMethod(setHintMethod,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
        });
    }
    
    //视频详情里显示av号 而非bv
    public void test9 (XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Lcom/bilibili/droid/BVCompat;->a(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        final Class<?> BVCompatClass = XposedHelpers.findClass("com.bilibili.droid.BVCompat",lpparam.classLoader);
        
        Method AVorBVMethod = Utils.selectMethod(BVCompatClass,String.class,String.class,String.class);
        XposedBridge.hookMethod(AVorBVMethod,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(param.args[0]);
                }
        });
    }
    
    //去除开屏广告
    public void test8(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Utils.getBLogMessage(lpparam,"Splash");
        //注释的代码 在MainActivityV2调用add时过早拦截导致广告无法展示和结束
        /*XposedHelpers.findAndHookMethod("androidx.fragment.app.FragmentTransaction",lpparam.classLoader,"add",int.class,"androidx.fragment.app.Fragment",String.class,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String str = (String) param.args[2];
                    View view =(View) param.args[1];
                    //ADSplashFragment
                    if(str.contains("Splash")){
                        //param.setResult(null);
                        
                    }
                    Utils.log("add "+str);
                }
        });*/
        //Lcom/bilibili/ship/theseus/united/page/TheseusIntroductionFragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
        /*XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.page.TheseusIntroductionFragment",lpparam.classLoader,"onViewCreated",View.class,Bundle.class,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if(param.getResult()==null){return;}
                    View view = (View) param.getResult();
                    view.setVisibility(View.GONE);
                    Utils.log("TheseusIntroductionFragment;->onViewCreated");
                }
        });*/
        //tv.danmaku.bili.ui.splash.ad.page.BaseSplash
        
        final Class<?> BaseSplashClass = XposedHelpers.findClass("tv.danmaku.bili.ui.splash.ad.page.BaseSplash",lpparam.classLoader);
        final Field handlerField = Utils.selectField(BaseSplashClass,Handler.class);
        handlerField.setAccessible(true);
        //Ltv/danmaku/bili/ui/splash/ad/page/BaseSplash;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.splash.ad.page.BaseSplash",lpparam.classLoader,"onViewCreated",View.class,Bundle.class,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Handler handler = (Handler) handlerField.get(param.thisObject);
                    handler.sendEmptyMessage(2);
                    Utils.log("message 结束开屏广告");
                }
        });
        
    }
    
    
    //简化主页顶栏
    public void test1(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable {
        final Class<?> MainResourceManager$UpdateInfoClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.resource.MainResourceManager$UpdateInfo",lpparam.classLoader);
        final Field secondaryPagesField = MainResourceManager$UpdateInfoClass.getField("secondaryPages");

        //tv.danmaku.bili.ui.main2.resource.o
        final Class<?> secondaryPagesClass =  (Class<?>) ((ParameterizedType)secondaryPagesField.getGenericType()).getActualTypeArguments()[0];
        
        final Field tabNameField=Utils.selectFieldAt(secondaryPagesClass,String.class,2);
        
        //List<o>
        Type ListOType = secondaryPagesField.getGenericType();
        
        
        final List NeededTags = Arrays.asList(new String[]{"直播","推荐","热门"});
        
        final Class<?> MainResourceManagerClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.resource.MainResourceManager",lpparam.classLoader);
        for (final Method getTabMethod :MainResourceManagerClass.getMethods()){
            if(getTabMethod.getParameterCount()==0&&getTabMethod.getGenericReturnType().equals(ListOType)){
                //Utils.log(getTabMethod.getName());
                XposedBridge.hookMethod(getTabMethod,new XC_MethodHook(){
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                             List list = (List) param.getResult();
                            for (int i = list.size()-1; i >=0 ; i--) {
                                String tabName = (String) tabNameField.get(list.get(i));
                                if(!NeededTags.contains(tabName)){
                                    list.remove(i);
                                }
                            }
                        }
                });
            }
        }
        
    }
    
    //去除私信旁边的按钮
    public void test5(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> HomeFragmentV2$HomeMenuDataProviderClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.HomeFragmentV2$HomeMenuDataProvider",lpparam.classLoader);
        Method getMenuDataList = Utils.selectMethod(HomeFragmentV2$HomeMenuDataProviderClass,List.class,Context.class);
        XposedBridge.hookMethod(getMenuDataList,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ArrayList DataList = (ArrayList) param.getResult();
                    List newDataList = new ArrayList<>();
                    newDataList.add(DataList.get(DataList.size()-1));
                    param.setResult(newDataList);
                }
        });
    }
    
    //我的主页简化
    public void test7(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //final List<Object> list = new ArrayList<>();
        //Ltv/danmaku/bili/ui/main2/mine/HomeUserCenterFragment;->ht(Landroid/content/Context;Ljava/util/List;Ltv/danmaku/bili/ui/main2/api/AccountMine;)V
        final Method HomePageMenuGroupInitMethod = Utils.selectMethod(XposedHelpers.findClass("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment",lpparam.classLoader),void.class,Context.class,List.class,XposedHelpers.findClass("tv.danmaku.bili.ui.main2.api.AccountMine",lpparam.classLoader));
        XposedBridge.hookMethod(HomePageMenuGroupInitMethod,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    /*if(list.size()==0){
                        list.add(param.args[1]);
                    }else{
                        param.setResult(list.get(0));
                    }*/
                    
                    List list = (List) param.args[1];
                    if(list.size()<=1){return;}
                    for (int i = list.size()-2; i >=1; i--) {
                        list.remove(i);
                    }
                }
        });
        
        //Ltv/danmaku/android/util/AppBuildConfig;->isInternationalApp(Landroid/content/Context;)Z
        //Ltv/danmaku/bili/ui/main2/a;->h(Landroid/content/Context;)Ljava/util/List;
    }
    
    //移除个人页的大会员广告宣传
    public void test2(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable {
        //final Class<?> MineVipEntranceViewClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.mine.widgets.MineVipEntranceView",lpparam.classLoader);
        //for (Constructor constructor : MineVipEntranceViewClass.getConstructors()){
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment", lpparam.classLoader,
            "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class,
            new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // 在方法调用之后执行的代码
                    View view = (View) param.getResult();
                    view.findViewById(Utils.getViewID("mine_vip_layout")).setVisibility(View.GONE);
                    //log("onCreateView has been called, returning: " + view);
                }
            });
    }
    
    //去除特殊弹幕
    public void test3(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable {
        //Ltv/danmaku/biliplayerv2/service/interact/core/model/DanmakuParams;->setDmViewReply(Lcom/bapis/bilibili/community/service/dm/v1/DmViewReply;)V
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.service.interact.core.model.DanmakuParams",
            lpparam.classLoader,
            "setDmViewReply",
            "com.bapis.bilibili.community.service.dm.v1.DmViewReply",
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    // 在方法执行前执行的代码
                    param.setResult(null);
                }


            });
    }
    
    //拦截OKhttp网络请求
    public void test4(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable {
        XposedHelpers.findAndHookMethod(
            "okhttp3.Request$Builder",
            lpparam.classLoader,
            "build",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Utils.log("okhttp "+param.getResult().toString());
                }


            }
        );
    }
    
    //锁定弹幕速度
    public void test6(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("tv.danmaku.biliplayerv2.service.interact.biz.y",
            lpparam.classLoader,
            "setDanmakuSpeed",
            float.class,boolean.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Float speed = 100.0f;
                    param.args[0]=speed;
                }


            });
    }
}
