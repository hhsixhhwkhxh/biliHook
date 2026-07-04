package hhsixhhwkhxh.bilibili.function;
import android.content.LocusId;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.XposedBridge;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import de.robv.android.xposed.XposedHelpers;

import de.robv.android.xposed.XC_MethodHook;
import hhsixhhwkhxh.bilibili.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import java.util.ArrayList;

public class TestFunctionArea extends FunctionsBase {
/*
    这里是功能成熟前的测试的地方 有许多废弃代码
    测码

    在某些情况下 log方法有不同程度的漏日志 甚至在某处代码往后日志不再打印
    TestFunctionArea类因为执行优先级低 日志输出更可能在此之前就被截断 是重灾区
    可复现性差 不清楚原因 匪夷所思
*/
    ClassLoader classLoader = null;
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        classLoader = lpparam.classLoader;
        //XposedBridge.log("test");

        //test65(lpparam);
        //test66(lpparam);
        //Utils.test14(lpparam);
        //test67(lpparam);
        //test68(lpparam);
        test69(lpparam);
        //test70(lpparam);
        //test71(lpparam);
        //test72(lpparam);
        //test73(lpparam);
        //Utils.hookTextViewSetText("小时前");
    }

    public void advanceRun(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        classLoader = lpparam.classLoader;

    }
    //9.0.0
    public void test74(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{

    }
    public void test73(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XC_MethodHook test = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Utils.log( param.method.getDeclaringClass().getName()+"."+param.method.getName()+" before");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Utils.log(param.method.getDeclaringClass().getName()+"."+param.method.getName()+" after");
            }
        };
        //XposedHelpers.findAndHookMethod("E81.b", lpparam.classLoader, "a", ("D81.x"), boolean.class, ("D81.x"), ("kotlin.jvm.functions.Function2"), test);
        //XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService", classLoader, "m", classLoader.loadClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService"), classLoader.loadClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService$PauseTriggerSource"), classLoader.loadClass("kotlin.coroutines.jvm.internal.ContinuationImpl"), test);
        //XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService$showPauseBarCountdownToast$3", classLoader, "invoke", classLoader.loadClass("kotlinx.coroutines.CoroutineScope"), classLoader.loadClass("kotlin.coroutines.Continuation"), test);

        Class<?> PausedPageServiceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService",lpparam.classLoader);
        Class<?> PauseTriggerSourceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService$PauseTriggerSource",lpparam.classLoader);
        Class<?> ContinuationImplClass = XposedHelpers.findClass("kotlin.coroutines.jvm.internal.ContinuationImpl",lpparam.classLoader);
        Class<?> UnitClass = XposedHelpers.findClass("kotlin.Unit",lpparam.classLoader);
        Object INSTANCE = XposedHelpers.getStaticObjectField(UnitClass,"INSTANCE");
        Method mMethod = Utils.selectMethod(PausedPageServiceClass,Object.class,PausedPageServiceClass,PauseTriggerSourceClass,ContinuationImplClass);
        XposedBridge.hookMethod(mMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(INSTANCE);
            }
        });
        //Lcom/bilibili/ship/theseus/united/page/pausedpage/PausedPageService;->m(Lcom/bilibili/ship/theseus/united/page/pausedpage/PausedPageService;Lcom/bilibili/ship/theseus/united/page/pausedpage/PausedPageService$PauseTriggerSource;Lkotlin/coroutines/jvm/internal/ContinuationImpl;)Ljava/lang/Object;
    }
    public void test72(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        Class<?> AdRepositoryClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.ad.AdRepository",lpparam.classLoader);
        Class<?> PageAdRepositoryClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.ad.PageAdRepository",lpparam.classLoader);

        XposedHelpers.findAndHookConstructor("com.bilibili.ship.theseus.united.page.pausedpage.PausedPageService", lpparam.classLoader, ("kotlinx.coroutines.CoroutineScope"), ("androidx.fragment.app.FragmentActivity"), ("com.bilibili.ship.theseus.united.page.ad.AdRepository"), ("Dr0.a"), ("com.bilibili.playerbizcommon.gesture.IGestureService"), ("tv.danmaku.biliplayerv2.service.IPlayerCoreService"), ("tv.danmaku.biliplayerv2.service.z"), ("com.bilibili.ship.theseus.united.page.pausedpage.b"), ("com.bilibili.ship.theseus.united.page.ad.PageAdRepository"), ("com.bilibili.ship.theseus.united.page.backpress.BackActionRepository"), ("kv0.a"), ("com.bilibili.ship.theseus.united.page.playingarea.a"), ("com.bilibili.ship.theseus.united.page.playingarea.PageNestedScrollFusionRepository"), ("com.bilibili.ship.theseus.united.page.uistyle.TheseusPageUIStyleRepository"), ("com.bilibili.ship.theseus.united.page.screenstate.d"), ("com.bilibili.ship.theseus.united.page.tab.u"), ("com.bilibili.ship.theseus.united.page.toolbar.ToolbarRepository"), ("com.bapis.bilibili.app.viewunite.v1.ViewReply"), String.class, ("tv.danmaku.biliplayerv2.service.IToastService"), ("kotlinx.coroutines.flow.Flow"), ("com.bilibili.ship.theseus.united.page.floatlayer.TheseusFloatLayerService"), ("kotlinx.coroutines.flow.Flow"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                for (int i = 0;i<param.args.length;i++){
                    Object obj = param.args[i];
                    if(obj==null){
                        continue;
                    }
                    if(obj.getClass()==AdRepositoryClass||obj.getClass()==PageAdRepositoryClass){
                        param.args[i] = null;
                    }
                }
                //Utils.printStackTrace("PausedPageService <init>");
            }

        });
        //	at tv.danmaku.bili.a$C1$a.b(BL:99)
        // i = 137
        /*
        XposedHelpers.findAndHookConstructor("tv.danmaku.bili.a$C1$a", lpparam.classLoader, ("tv.danmaku.bili.a$e1"), ("tv.danmaku.bili.a$k0"), ("tv.danmaku.bili.a$C1"), int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int i = (int) param.args[param.args.length-1];
                if(i!=137){
                    return;
                }
                Utils.printStackTrace("create a$C1$a class for new PausedPageService");
            }

        });*/

        //	at tv.danmaku.bili.a$C1.<init>(BL:1441)


    }
    public void test71(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.MinePageManager$switchTo$1", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object $targetPage = XposedHelpers.getObjectField(param.thisObject,"$targetPage");
                String str = (String) XposedHelpers.getObjectField($targetPage,"b");
                Utils.log("$targetPage.b="+str);
                //[ 2026-07-04T11:46:32.855    10338: 27870: 27870 I/LSPosed-Bridge  ] $targetPage.b=bilibili://user_center/favourite?version=2
            }
        });
    }
    public void test70(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*
        XposedHelpers.findAndHookMethod("cf1.t", lpparam.classLoader, "p0", ("com.bilibili.lib.homepage.mine.MenuGroup"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
                Utils.printStackTrace("MenuGroup");
            }

        });*/

        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment", lpparam.classLoader, "onCreateView", android.view.LayoutInflater.class, android.view.ViewGroup.class, android.os.Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //Utils.log("onCreateView before");
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //XposedHelpers.setObjectField(param.thisObject,"l1",new ArrayList<>());

            }
        });
        Class<?> HomeUserCenterFragmentClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(HomeUserCenterFragmentClass, "pf", ("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment"), ("tv.danmaku.bili.ui.main2.api.AccountMine"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);


            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Utils.log("pf after");
                List<?> list = (List<?>) XposedHelpers.getObjectField(param.args[0],"l1");
                list.remove(1);
                list.remove(1);
                Utils.printfList("pf",list);

            }
        });
    }
    public void test69(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*
        Class<?> FoundationAliasClass = XposedHelpers.findClass("com.bilibili.lib.foundation.FoundationAlias",lpparam.classLoader);
        Object apps = XposedHelpers.callStaticMethod(FoundationAliasClass,"getFapps");
        String str = (String) XposedHelpers.callMethod(apps,"getChannel");
        Utils.log("apps:"+apps.getClass().getName()+",str:"+str);*/
        //[ 2026-07-03T19:25:44.699    10338: 10095: 10095 I/LSPosed-Bridge  ] apps:com.bilibili.lib.foundation.DefaultApps,str:oppo
        XposedHelpers.findAndHookMethod("com.bilibili.lib.foundation.DefaultApps", lpparam.classLoader, "getChannel", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult("master");
            }

        });
    }
    public void test68(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        /*
        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "performClick", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                boolean result = (boolean) param.getResult();
                if(!result){
                    return;
                }
                View view = (View) param.thisObject;
                Object mListenerInfo = XposedHelpers.getObjectField(param.thisObject,"mListenerInfo");
                View.OnClickListener onClickListener = (View.OnClickListener) XposedHelpers.getObjectField(mListenerInfo,"mOnClickListener");
                Utils.log("performClick view:"+param.thisObject+"("+view.getId()+"),onClickListenerClass:"+onClickListener.getClass().getName());
            }

        });*/
        //[ 2026-07-03T18:15:26.869    10338: 12666: 12666 I/LSPosed-Bridge  ] performClick view:android.widget.LinearLayout{3b47b54 VFE...CL. ...P.... 640,0-842,174 #7f09165b app:id/frame_fav alpha=1.0 viewInfo = }(2131301979),onClickListenerClass:com.bilibili.app.gemini.ui.h
        /*
        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "setOnClickListener", android.view.View.OnClickListener.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view = (View) param.thisObject;
                if(view.getId()!=2131301979){
                    return;
                }
                Utils.printStackTrace("frame_fav");
            }

        });*/
        //	at com.bilibili.app.gemini.ui.UIComponentExtKt$onClickEvent$1.invokeSuspend(BL:38)

        /*
        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.ui.UIComponentExtKt$onClickEvent$1", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Utils.log("onClick over");
            }
        });

        XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.ui.UIComponentExtKt$onLongClickEvent$1", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Utils.log("onLongClick over");
            }
        });*/
        Class<?> UIComponentExtKtClass = XposedHelpers.findClass("com.bilibili.app.gemini.ui.UIComponentExtKt$onLongClickEvent$1", lpparam.classLoader);
        Field viewField = Utils.selectField(UIComponentExtKtClass,View.class);
        if(viewField==null){
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

    public void test67(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{

        /*
        XposedHelpers.findAndHookMethod("android.widget.TextView", lpparam.classLoader, "setHintInternal", CharSequence.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                TextView textView = (TextView) param.thisObject;
                if(textView.getId()!=2131303711){
                    return;
                }
                Utils.printStackTrace("LightPublishEdit");
            }

        });*/

        Class<?> QJDClass = XposedHelpers.findClass("Qj.d",lpparam.classLoader);

    }

    public void test66(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        Class<?> CollapsableChildFrameLayoutClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.behavior.CollapsableChildFrameLayout",lpparam.classLoader);
        /*
        XposedBridge.hookAllConstructors(CollapsableChildFrameLayoutClass, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View) param.thisObject;
                view.setVisibility(View.GONE);
            }
        });*/

        XposedHelpers.findAndHookConstructor("ou0.c", lpparam.classLoader, ("com.bilibili.ship.theseus.united.widget.TheseusAncestorLayout"), android.widget.LinearLayout.class, android.widget.LinearLayout.class, ("com.google.android.material.appbar.AppBarLayout"), ("com.bilibili.ship.theseus.united.widget.LockableCollapsingToolbarLayout"), ("com.bilibili.ship.theseus.united.widget.TouchAwareConstraintLayout"), ("androidx.compose.ui.platform.ComposeView"), android.view.View.class, android.widget.ImageView.class, ("com.bilibili.ship.theseus.united.widget.PinnedBottomFrameLayout"), android.widget.FrameLayout.class, android.widget.FrameLayout.class, android.widget.FrameLayout.class, ("com.bilibili.ship.theseus.united.widget.TouchAwareToolbar"), ("com.bilibili.ship.theseus.united.widget.UnitedViewPager"), ("com.bilibili.ogv.infra.widget.RatioLayout"), ("com.bilibili.playerbizcommonv2.view.RoundFrameLayout"), android.view.View.class, android.view.View.class, ("com.bilibili.ship.theseus.united.widget.UnitedTabLayout"), android.widget.ImageView.class, android.view.View.class, android.widget.ImageView.class, ("cv0.F0"), android.widget.TextView.class, ("com.bilibili.ship.theseus.united.page.behavior.CollapsableChildFrameLayout"), android.widget.FrameLayout.class, ("com.bilibili.ogv.infra.widget.RatioLayout"), android.widget.FrameLayout.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view = (View) param.args[param.args.length-4];
                //view.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = 0;
                params.height = 0;
                view.setLayoutParams(params);
            }

        });
    }
    public void test65(XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        XposedHelpers.findAndHookConstructor("com.bilibili.ship.theseus.united.page.intro.IntroRecycleViewService", lpparam.classLoader, "kotlinx.coroutines.CoroutineScope", java.util.List.class, "com.bilibili.ship.theseus.united.page.performance.a", "com.bilibili.ship.theseus.united.page.intro.IntroContentSizeRepository", "com.bilibili.ship.theseus.united.page.color.ActivityColorRepository", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //param.args[1] = new ArrayList<>();
                //Utils.printStackTrace("intro");
            }
        });
        /*
        at LSPHooker_.constructor(Unknown Source:23)
	    at tv.danmaku.bili.a$C1$a.a(BL:116)  i=18 return (T) new IntroRecycleViewService(a.C1.b(c1), (List) c1.f1.get(), a.k0.c(k0Var), (IntroContentSizeRepository) k0Var.T1.get(), (ActivityColorRepository) k0Var.u0.get());
	    at tv.danmaku.bili.a$C1$a.get(BL:21)
	    at CV0.b.get(BL:14)
	    at CV0.a.get(BL:5)
	    at tv.danmaku.bili.a$C1$a.a(BL:139)
	    at tv.danmaku.bili.a$C1$a.get(BL:21)
	    at CV0.b.get(BL:14)
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invokeSuspend(BL:114)
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invoke(BL:2)
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invoke(BL:1)
	     */

        XposedHelpers.findAndHookConstructor("tv.danmaku.bili.a$C1$a", lpparam.classLoader, "tv.danmaku.bili.a$e1", "tv.danmaku.bili.a$k0", "tv.danmaku.bili.a$C1", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int i = (int) param.args[param.args.length-1];
                if(i!=18){
                    return;
                }
                //Utils.printStackTrace("a$C1$a");
            }

        });
        //a$C1$a(a.e1 e1Var, a.k0 k0Var, a.C1 c1, int i) 关注c1
        /*
        * at LSPHooker_.constructor(Unknown Source:24)
	    at tv.danmaku.bili.a$C1.<init>(BL:1079) c1传的自己
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invokeSuspend(BL:109)
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invoke(BL:2)
	    at com.bilibili.ship.theseus.detail.BusinessScopeDriverImpl$switchScope$2.invoke(BL:1)
	    at E81.b.a(BL:22)
	    */

        //this.f1 = w0.b(e1Var, k0Var, this, 19);

        /*
        * public final class w0 {
            public static b b(a.e1 e1Var, a.k0 k0Var, a.C1 c1, int i) {
                return b.b(new a.C1.a(e1Var, k0Var, c1, i));
            }
          }
         */

        //get方法启发

        XposedHelpers.findAndHookConstructor("CV0.b", lpparam.classLoader, "dagger.internal.Provider", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //Utils.printStackTrace("CV0.b堆栈");
            }

        });
        /*
        * LSPHooker_.constructor(Unknown Source:11)
	    at CV0.b.b(BL:13)
	    at CV0.b.c(BL:5)
	    at com.bilibili.lib.gripper.core.internal.producers.e.newRawProducer(BL:5)
	    at tv.danmaku.bili.a$r$a.b(BL:379)
	    at tv.danmaku.bili.a$r$a.get(BL:3984)
	    at CV0.b.get(BL:14)
	    at com.bilibili.lib.gripper.core.internal.a.get(BL:6)
	    at com.bilibili.lib.gripper.core.internal.c.get(BL:5)
	    at R40.a$b.get(BL:10)
	    at com.bilibili.gripper.bootstrap.BootsStrapKt.bootDynamicView(BL:9)
	    * */

        //case 19:return (T) A.a((IntroductionTab) c1.w.get(), c1.i());

        //a = gVar.a(introductionTab);
        XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.di.A", lpparam.classLoader, "a", "com.bapis.bilibili.app.viewunite.v1.IntroductionTab", "mv0.g", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                //Utils.log("gVar:"+param.args[1]+" 类型"+param.args[1].getClass().getName());
                //[ 2026-07-02T22:19:38.288    10338:  7468:  7468 I/LSPosed-Bridge  ] gVar:mv0.l@e344a20 类型mv0.l
            }

        });

        /*
        XposedHelpers.findAndHookMethod("mv0.l", lpparam.classLoader, "a", "com.bapis.bilibili.app.viewunite.v1.IntroductionTab", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //param.setResult(new ArrayList<>());
                //Utils.printfList("mv0l",(List<?>) param.getResult());

                Object introductionTabObject = param.args[0];
                List<?> ModulesList = (List) XposedHelpers.callMethod(introductionTabObject,"getModulesList");

                //Utils.printBoundaryLine();
                Object immutableMap = XposedHelpers.getObjectField(param.thisObject,"b");
                for (Object mod:ModulesList){
                    Object modType = XposedHelpers.callMethod(mod,"getType");
                    Object hVar = XposedHelpers.callMethod(immutableMap,"get",modType);
                    log(modType+" hvar:"+hVar+",类型"+hVar.getClass().getName());
                    //mv0.q
                }


            }
        });*/

        XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateRepository", lpparam.classLoader, "a", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //param.setResult(new ArrayList<>());
            }
        });

        Class<?> ModuleTypeClass = XposedHelpers.findClass("com.bapis.bilibili.app.viewunite.common.ModuleType",lpparam.classLoader);
        HashSet<Integer> blackList = new HashSet<>();
        blackList.add(XposedHelpers.getStaticIntField(ModuleTypeClass,"KING_POSITION_VALUE"));
        blackList.add(XposedHelpers.getStaticIntField(ModuleTypeClass,"OWNER_VALUE"));


        XposedHelpers.findAndHookMethod("com.bapis.bilibili.app.viewunite.v1.IntroductionTab", lpparam.classLoader, "getModulesList", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                List<?> modList = (List<?>) param.getResult();
                List<Object> newList = new ArrayList<>();
                for(Object mod:modList){
                    Object currentModType = XposedHelpers.callMethod(mod,"getType");
                    int typeNumber = (int) XposedHelpers.callMethod(currentModType,"getNumber");
                    if(!blackList.contains(typeNumber)){
                        newList.add(mod);
                    }
                }
                param.setResult(newList);
            }
        });
    }



}
