package hhsixhhwkhxh.bilibili;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

import hhsixhhwkhxh.bilibili.function.HomePageSimplify;
import hhsixhhwkhxh.bilibili.function.CommentOptimization;
import hhsixhhwkhxh.bilibili.function.LivePageSimplify;
import hhsixhhwkhxh.bilibili.function.ManageHomePagePush;
import hhsixhhwkhxh.bilibili.function.ManageHomePagePushV2;
import hhsixhhwkhxh.bilibili.function.ManageVideoDetailPagePush;
import hhsixhhwkhxh.bilibili.function.ShareManagement;
import hhsixhhwkhxh.bilibili.function.VideoDetailPageSimplify;
import hhsixhhwkhxh.bilibili.function.BypassSplash;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.enums.MatchType;
import org.luckypray.dexkit.query.enums.StringMatchType;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.query.matchers.FieldsMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.query.matchers.base.OpCodesMatcher;
import org.luckypray.dexkit.query.matchers.base.StringMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.ClassDataList;
import org.luckypray.dexkit.result.FieldData;
import org.luckypray.dexkit.result.FieldDataList;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.util.OpCodeUtil;

import hhsixhhwkhxh.bilibili.function.TestFunctionArea;
import hhsixhhwkhxh.bilibili.function.UserCenterOptimization;

public class Entrance implements IXposedHookLoadPackage {

    public static final String TargetPackageName = "tv.danmaku.bili";
    public static final String ModuleSettingsActivityName = "com.bilibili.lib.dblconfig.DblConfigActivity";

    private Activity MainActivityV2=null;
    public static int contrastColor=Color.BLACK;
    private ListView listView;
    private List<ListItem> ItemsList;
    SharedPreferences sharedPreferences=null;

    private boolean ModuleSetUp = false;

    public static final String TAG = "Entrance";
    private FunctionAdapter adapter;

    private boolean preInitSucceed = false;

    static {
        System.loadLibrary("dexkit");
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

    
        if(!lpparam.packageName.equals(TargetPackageName)){return;}

        if(BuildConfig.IS_DEBUG) {
            new TestFunctionArea().advanceRun(lpparam);
        }

        XC_MethodHook preInitHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context appContext;
                if(param.args.length==0){
                    appContext = (Context) param.thisObject;
                }else {
                    appContext = (Context) param.args[0];
                }

                sharedPreferences=appContext.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
                Utils.sharedPreferences=sharedPreferences;
                FunctionsBase.sharedPreferences=sharedPreferences;

                LivePageSimplify function = new LivePageSimplify();
                function.context = appContext;

                try {
                    //function.run(lpparam);
                } catch (Throwable e) {
                    Utils.log("biliHook Function crashed: " + function.getClass().getSimpleName());
                    Utils.reportError(e);
                }
                preInitSucceed = true;
            }
        };

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, preInitHook);
        /*
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, preInitHook);
        }else {
            try{
                Class<?> BiliAppClass = XposedHelpers.findClass("com.bilibili.gripper.BiliApp",lpparam.classLoader);
                XposedHelpers.findAndHookMethod(BiliAppClass, "onCreate", preInitHook);
            }catch (Exception e){
                Utils.reportError(e);
            }
        }*/



        //如果是"tv.danmaku.bili.MainActivityV2" 正常从桌面打开app biliHook可以正常启动 然而在b站被其他应用程序拉活跳转时 MainActivityV2不会启动 此时模块功能就没有了
        //所以这里选择hook的系统activity类 因为我没对其他activity做过任何适配 一开始还以为会崩溃 结果不仅没崩效果还好 就这样吧
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate",
            Bundle.class, new XC_MethodHook() {
                
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    if(ModuleSetUp){return;}
                    Utils.log("biliHook启动 "+param.thisObject.getClass());
                    ModuleSetUp = true;
                    MainActivityV2 = (Activity) param.thisObject;

                    if(sharedPreferences==null){
                        //这通常意味着上面借助Application.attach(Context)初始化失败了
                        sharedPreferences=MainActivityV2.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
                        FunctionsBase.sharedPreferences=sharedPreferences;
                        Utils.sharedPreferences=sharedPreferences;

                        preInitSucceed = false;
                    }

                    Utils.init(MainActivityV2,lpparam);

                    String apkPath = lpparam.appInfo.sourceDir;
                    int beforeVersion = sharedPreferences.getInt("CodeVersion", -1);
                    //if(beforeVersion!=Utils.getAppVersionCode(MainActivityV2)){
                        Toast.makeText(MainActivityV2, "模块反混淆初始化...", Toast.LENGTH_SHORT).show();
                        //initNeededMethods(apkPath,classLoader);
                        initResolveConfusionMethods(apkPath,lpparam.classLoader);
                        //Toast.makeText(context, "模块初始化成功", Toast.LENGTH_SHORT).show();
                    //}


                    //
                    //runFunctionSafely(new ManageHomePagePush(), lpparam);

                    runFunctionSafely(new ManageHomePagePushV2(), lpparam);

                    runFunctionSafely(new ManageVideoDetailPagePush(), lpparam);
                    runFunctionSafely(new VideoDetailPageSimplify(), lpparam);
                    runFunctionSafely(new BypassSplash(), lpparam);
                    runFunctionSafely(new HomePageSimplify(), lpparam);
                    runFunctionSafely(new CommentOptimization(), lpparam);
                    runFunctionSafely(new UserCenterOptimization(),lpparam);
                    //runFunctionSafely(new ShareManagement(),lpparam);

                    if(BuildConfig.IS_DEBUG) {
                        runFunctionSafely(new TestFunctionArea(), lpparam);
                    }

                }
            });
        
        
        XposedHelpers.findAndHookMethod(this.getClass(),"getHookAccessible",new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });
            
        //添加设置按钮
        //Ltv/danmaku/bili/ui/main2/mine/HomeUserCenterFragment;->onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment",lpparam.classLoader,"onCreateView",LayoutInflater.class,ViewGroup.class,Bundle.class,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    FrameLayout frameLayout = (FrameLayout) param.getResult();
                    ViewGroup MultipleThemeImageView = frameLayout.findViewById(Utils.getViewID("mine_top_view"));
                    
                    //View NickNameLayout = frameLayout.findViewById(Utils.getViewID("nick_name_layout"));
                    //log(NickNameLayout);
                    //NickNameLayout.setVisibility(View.INVISIBLE);
                    
                    Button button = new Button(MainActivityV2);
                    button.setText("壁虎");
                    button.setTextColor(Color.BLACK);

                    //Class<?> MaterialCheckBoxClass = XposedHelpers.findClass("com.google.android.material.checkbox.MaterialCheckBox",lpparam.classLoader);
                    //CheckBox checkBox = (CheckBox) MaterialCheckBoxClass.getConstructor(Context.class).newInstance(MainActivityV2);
                    //MultipleThemeImageView.addView(checkBox);
                    //checkBox.setText("动态创建的三态复选框");
                    //checkBox.setCheckedState(MaterialCheckBox.STATE_INDETERMINATE);
                    //以下几个都有做设置Activity的能力
                    //Lcom/bilibili/ad/adview/download/ADDownloadManagerActivity;->onCreate(Landroid/os/Bundle;)V
                    //Lcom/bilibili/adgame/AdGameDetailActivity;->onCreate(Landroid/os/Bundle;)V
                    //Lcom/bilibili/app/authorspace/ui/nft/ui/activity/NftAggregationActivity;->onCreate(Landroid/os/Bundle;)V
                    //Lcom/bilibili/app/authorspace/ui/nft/ui/activity/SpaceNftOBPActivity;->onCreate(Landroid/os/Bundle;)V

                    //Lcom/bilibili/lib/dblconfig/DblConfigActivity;->onCreate(Landroid/os/Bundle;)V
                    XposedHelpers.findAndHookMethod(ModuleSettingsActivityName,lpparam.classLoader,"onCreate",Bundle.class,new XC_MethodHook(){
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            //param.args[0]=new Bundle();
                            Activity activity = (Activity)param.thisObject;
                            if(!activity.getIntent().getBooleanExtra("hook",false)){
                                return;
                            }
                            Bundle bundle = (Bundle) param.args[0];
                            if(bundle==null){
                                param.args[0]=new Bundle();
                            }
                        }
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            //log("after onCreate");

                            Activity activity = (Activity)param.thisObject;

                            //Toast.makeText(activity,"after onCreate",Toast.LENGTH_SHORT).show();
                            //XposedBridge.log("after onCreate");

                            if(activity.getIntent().getBooleanExtra("hook",false)){
                                initSettingActivity(lpparam,activity);
                                //Log.i(TAG,"initSettingActivity");
                                //Toast.makeText(activity,"initSettingActivity",Toast.LENGTH_SHORT).show();
                                //XposedBridge.log("initSettingActivity");
                            }
                        }
                    });



                    button.setOnClickListener(new OnClickListener(){
                            @Override
                            public void onClick(View p1) {
                                //XposedBridge.log("OnClickListener");
                                Intent intent = new Intent(MainActivityV2,XposedHelpers.findClass(ModuleSettingsActivityName,lpparam.classLoader));
                                intent.putExtra("hook",true);
                                MainActivityV2.startActivity(intent);
                                
                                //NeedHandleSettingsActivityOnCreate =true;
                                

                            }
                        });
                    MultipleThemeImageView.addView(button);
                    
                }
        });
        
        
   
        
      
    }

    private void runFunctionSafely(FunctionsBase function, XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            function.run(lpparam);
        } catch (Throwable e) {
            Utils.log("biliHook Function crashed: " + function.getClass().getSimpleName());
            Utils.reportError(e);
        }
    }
    
    public void initSettingActivity(final XC_LoadPackage.LoadPackageParam lpparam,final Activity activity){
        
        RelativeLayout layout = new RelativeLayout(activity);
        listView = new ListView(activity);
        listView.setId(View.generateViewId());
        //listView.setBackgroundColor(Color.WHITE);
        
        ColorDrawable background = (ColorDrawable) activity.getWindow().getDecorView().getRootView().getBackground();
        
        
        // 假设底色为color值
        int backgroundColor = background.getColor(); 

        // 计算底色的亮度
        double brightness = 0.299 * Color.red(backgroundColor) + 0.587 * Color.green(backgroundColor) + 0.114 * Color.blue(backgroundColor);

        // 选择一个对比度高的颜色
        
        if (brightness < 128) {
            contrastColor = Color.WHITE;  // 如果底色亮度较暗，选择白色
        } else {
            contrastColor = Color.BLACK;  // 如果底色亮度较亮，选择黑色
        }
        
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(params);

        //layout.addView(toolbar);
        layout.addView(listView);
        activity.setContentView(layout);
        
        //sharedPreferences = activity.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
        SwitchFunction.sharedPreferences=sharedPreferences;
        
        ItemsList = new ArrayList<>();
        //ItemsList.add(new GroupTitle("壁虎 开源模块 适配8.51.0"));
        final Class<?> MaterialCheckBoxClass = XposedHelpers.findClass("com.google.android.material.checkbox.MaterialCheckBox",lpparam.classLoader);


        Intent GoToGithubPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hhsixhhwkhxh/biliHook/"));
        ItemsList.add(new ButtonFunction("壁虎"+BuildConfig.VERSION_NAME+" 适配9.0.0","开源模块 点击跳转github页","GoToGithubPage",new FunctionOnClickListener(){
            public void onClick(){
                try{
                    activity.startActivity(GoToGithubPageIntent);
                    activity.finish();
                }catch(Exception e){
                    Toast.makeText(activity, "错误:"+e, Toast.LENGTH_LONG).show();
                }
            }
        }));

        OnClickListener onUpdateAdapterListener = new OnClickListener(){
            @Override
            public void onClick(View view) {
                if(adapter==null){return;}
                adapter.notifyDataSetChanged();
            }
        };


        ItemsList.add(new GroupTitle("开关设置 重启b站后生效"));


        ItemsList.add(new GroupTitle("主页综合简化",true));
        List<ListItem> HomePageNavigationBarfiltrationChildrenList = new ArrayList<>();

            HomePageNavigationBarfiltrationChildrenList.add(new SwitchFunction("去除主页+号", "最简单的一集", "HomePageNavigationBarRemovePlusSign"));
            HomePageNavigationBarfiltrationChildrenList.add(new SwitchFunction("去除主页会员购", "会员go", "HomePageNavigationBarRemoveVIPShopSign"));
        ItemsList.add(new FunctionCollection("导航栏选项过滤器","黑名单机制 点击查看配置",HomePageNavigationBarfiltrationChildrenList,onUpdateAdapterListener));


        ItemsList.add(new SwitchFunction("去除游戏按钮", "私信旁边的按钮", "HomePageRemoveGameSign"));
        ItemsList.add(new SwitchFunction("简化主页顶栏", "仅保留 直播 推荐 热门", "HomePageTopBarFilter"));
        ItemsList.add(new SwitchFunction("禁用滑动切换tab", "防止误触", "HomePageDisableHorizontalScrollable"));

        ItemsList.add(new GroupTitle("主页推送",true));

        List<ListItem> HomePagePushfiltrationChildrenList = new ArrayList<>();
        HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤广告", "可能会过滤掉创作推广视频", "HomePagePushFilterAD"));

            HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤横幅", "宽身位的卡片 视频会转生小卡片", "HomePagePushFilterBanner"));
            //HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤横幅", getTextWithFunctionPreposition("宽身位的卡片 视频会转生小卡片","test","test"), "HomePagePushFilterBanner"));
            HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤直播", "不会显示推送直播", "HomePagePushFilterLive"));
            HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤游戏", "总有些游戏推荐", "HomePagePushFilterGame"));
            HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤bangumi", "哔哩哔哩国漫", "HomePagePushFilterBangumi"));
        ItemsList.add(new FunctionCollection("推送内容过滤器","黑名单机制 点击查看配置",HomePagePushfiltrationChildrenList,onUpdateAdapterListener));

        ItemsList.add(new SwitchFunction("竖屏视频转横屏", "去抖化", "HomePagePushTransformVerticalVideo"));
        ItemsList.add(new SwitchFunction("去\"x万点赞\"", "这样所有视频都有up主名字", "HomePagePushRemoveVideoLikeCount"));
        ItemsList.add(new SwitchFunction("严格模式", "所有指向非av的卡片一律丢弃\n此功能对竖屏视频不作处理", "HomePagePushStrictMode"));

        ItemsList.add(new GroupTitle("视频详情页简化",true));
        ItemsList.add(new SwitchFunction("去x万点赞", "同上", "VideoDetailPagePushRemoveVideoLikeCount"));
        ItemsList.add(new SwitchFunction("界面过滤广告", "up主推荐?大喇叭评论区黄条广播?达咩!", "VideoDetailPageRemoveAD"));
        ItemsList.add(new SwitchFunction("推送过滤非AV", "一刀切直播游戏等等特殊推送", "VideoDetailPagePushFilterNotAV"));
        ItemsList.add(new SwitchFunction("禁用输入框的神人提示语", "千山万水总是情，评论两句行不行 \uD83D\uDC48\uD83E\uDD23", "BanEditTextSBHint"));
        ItemsList.add(new SwitchFunction("禁用收藏按钮单击直接收藏", "开启后单击收藏会先选收藏夹 而不是直接进入默认收藏夹", "BanDirectFavorite"));
        ItemsList.add(new SwitchFunction("禁用高级乞讨弹幕", "屏蔽容易误触的三连和投票弹窗弹幕\n代码参考github项目FuckBilibiliVote", "BanBeggingDanmaku"));
        ItemsList.add(new SwitchFunction("隐藏竖屏视频入口", "横板视频右下角有两种全屏方式:竖屏全屏和横屏全屏 此功能隐藏了前者入口", "HideVerticalVideoEntrance"));
        ItemsList.add(new SwitchFunction("强制使用旧版评论区", "绕过云控  *笨拙地*", "ForceEnableOldComments"));
        ItemsList.add(new SwitchFunction("屏蔽暂停倒计时广告", "\"B站未来有可能会倒闭，但绝不会变质\"", "BanPauseCountdownAD"));



        ItemsList.add(new GroupTitle("开屏",true));
        ItemsList.add(new SwitchFunction("去除开屏广告", "和开屏battle了好多次 牢屏别打复活赛了", "BypassSplash"));

        ItemsList.add(new GroupTitle("评论简化",true));
        ItemsList.add(new SwitchFunction("强制评论显示绝对时间", "禁用相对时间(刚刚/x小时前/昨天)仿网页端 精确到秒", "ForceCommentsToShowAbsoluteTime"));

        //不再维护
        /*
        if(preInitSucceed){
            ItemsList.add(new GroupTitle("直播页面简化",true));
            ItemsList.add(new SwitchFunction("禁止上下滑动切换直播间", "这个功能我还出了逆向教程", "BanSwitchLiveByVerticalSlide"));
            ItemsList.add(new SwitchFunction("隐藏他人直播间礼物全局引流弹幕", "某某投喂某某n个梦幻游乐园/浪漫城堡/深海歌姬，点击前往TA的房间吧！", "HideOthersGiftBroadcastDanmaku"));
            ItemsList.add(new SwitchFunction("隐藏右下角可折叠广告挂件", "通常是一个可点击的轮播图 不知道有没有误伤", "HideLiveNormalBanner"));
        }else{
            ItemsList.add(new Sign("直播页面简化(不可用)","biliHook预初始化失败",true));
        }*/


        ItemsList.add(new GroupTitle("个人页简化",true));
        ItemsList.add(new SwitchFunction("去除创作中心和推荐服务", "高仿国际版", "UserCenterRemoveExcessiveService"));
        ItemsList.add(new SwitchFunction("去除收藏夹视频循环列表", "重定向Activity至正常视频页面", "FavoritesOpenVideoRedirect"));
        ItemsList.add(new SwitchFunction("强制使用旧版v1收藏夹", "如启用此功能 上面重定向功能就没效果了", "ForceEnableV1Favorites"));
        ItemsList.add(new SwitchFunction("禁用用户主页拉黑屏蔽", "现在不需要退出账号就能视奸别人啦", "DisableAuthorSpaceBlocking"));
        ItemsList.add(new SwitchFunction("考古", "在账号注销的个人页面可以查看动态", "AllowSweepGrave"));

        ItemsList.add(new GroupTitle("分享",true));
        ItemsList.add(new SwitchFunction("禁止跳转到竖屏视频", "会重定向到横屏", "DisableJumpToVerticalVideoFromShare"));

        ItemsList.add(new GroupTitle("杂项",true));




        ItemsList.add(new ButtonFunction("任意门","跳转到任意注册的Activity","AnywhereDoor",new FunctionOnClickListener(){
            public void onClick(){
                final EditText ClassNameEditText = new EditText(activity);
                AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("Activity的全限定类名")
                    .setView(ClassNameEditText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dia, int which) {
                            try{
                                String targetClassName = ClassNameEditText.getText().toString();
                                if(targetClassName.isEmpty()){return;}
                                activity.startActivity(new Intent(activity,XposedHelpers.findClass(targetClassName,lpparam.classLoader)));
                            }catch(Exception e){
                                Toast.makeText(activity, "错误:"+e, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
                dialog.show();
            }
        }));


        if(BuildConfig.IS_DEBUG){
            ItemsList.add(new ButtonFunction("BiliEnvActivity","测试Activity","",new FunctionOnClickListener(){
                public void onClick(){
                    activity.startActivity(new Intent(activity,XposedHelpers.findClass("com.bilibili.bilienv.BiliEnvActivity",lpparam.classLoader)));
                }
            }));
        }



        ItemsList.add(new ButtonFunction("考古","根据uid查看注销用户的动态","SweepGrave",new FunctionOnClickListener(){
            public void onClick(){
                final EditText ClassNameEditText = new EditText(activity);
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("输入用户uid")
                        .setView(ClassNameEditText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dia, int which) {
                                try{
                                    String targetUid = ClassNameEditText.getText().toString();
                                    if(targetUid.isEmpty()){return;}
                                    UserCenterOptimization.SweepGrave(lpparam,Long.parseLong(targetUid));
                                }catch(Throwable e){
                                    Toast.makeText(activity, "错误:"+e, Toast.LENGTH_LONG).show();

                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
            }
        }));


        ItemsList.add(new ButtonFunction("重新反混淆","重新使用dexkit查找方法和变量 响应速度慢 请勿多次反复猛击","REResolveConfusionMethods",new FunctionOnClickListener(){
            public void onClick(){
                String apkPath = lpparam.appInfo.sourceDir;
                try {
                    String result = initResolveConfusionMethods(apkPath,lpparam.classLoader);
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle("反混淆结果")
                            .setMessage(result)
                            .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dia, int which) {
                                    Utils.copyText(result);
                                }
                            })
                            .create();
                    dialog.show();

                } catch (Exception e) {
                    Utils.reportError(e);
                }

            }
        }));
        Class<?> AuthorSpaceActivityClass = XposedHelpers.findClass("com.bilibili.app.authorspace.ui.AuthorSpaceActivity",lpparam.classLoader);
        String id = "394924834";
        Intent intent = new Intent(MainActivityV2,AuthorSpaceActivityClass);
        intent.putExtra("from_spmid","tm.recommend.0.0");
        intent.putExtra("blrouter.native.start",false);

        Bundle bundle = new Bundle();
        bundle.putString("blrouter.from","bilibili://pegasus/promo");

        intent.putExtra("blrouter.props",bundle);
        intent.putExtra("blrouter.pagename","activity://main/authorspace/");
        intent.putExtra("mid",id);
        intent.putExtra("blrouter.targeturl","bilibili://space/"+id);
        intent.putExtra("blrouter.matchrule","bilibili://space/{mid}/");
        intent.putExtra("bili_only",1);
        ItemsList.add(new ButtonFunction("问题反馈","不要对我抱有期望 业余玩家 实在能力受限","ReportBug",new FunctionOnClickListener(){
            public void onClick(){

                activity.startActivity(intent);
                //Extras数据:Bundle[{from_spmid=tm.recommend.0.0, blrouter.native.start=false, blrouter.props=Bundle[{blrouter.from=bilibili://pegasus/promo}], blrouter.pagename=activity://main/authorspace/, mid=1883994988, blrouter.targeturl=bilibili://space/1883994988, blrouter.matchrule=bilibili://space/{mid}/, bili_only=1}]
            }
        }));
        
        
        adapter = new FunctionAdapter(activity, ItemsList);
        listView.setAdapter(adapter);
        
        //Toast.makeText(activity, "HookAccessible:"+getHookAccessible(), Toast.LENGTH_SHORT).show();
    }


        
    public boolean getHookAccessible(){
        return false;
    }
    
    
    
    public void hookViewBinding(final Class<?> ViewBindingClass,Context context)throws Throwable{
        final List<Field> ViewList = new ArrayList<>();
        for(Field field:ViewBindingClass.getDeclaredFields()){
            //if(field.getType()){}
            Class<?> TypeClass = field.getType();
            Constructor selectedConstructor=null;
            try {
                selectedConstructor = TypeClass.getDeclaredConstructor(Context.class);
            } catch (NoSuchMethodException e) {
                continue;
            } 
            if(selectedConstructor==null){continue;}
            // 使用选定的构造函数和参数列表创建对象
            Object object = selectedConstructor.newInstance(context);
            
            
            if(object instanceof View){
                field.setAccessible(true);
                ViewList.add(field);
            }
            
        }
        XposedBridge.hookAllConstructors(ViewBindingClass,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Utils.log("hookViewBinding "+ViewBindingClass);
                    for (int i = 0; i < ViewList.size(); i++) {
                        //if(param.thisObject==null){return;}
                        Field field = ViewList.get(i);
                        View view = (View) field.get(param.thisObject);
                        Utils.log("hookViewBinding Name:"+field.getName()+" Id:0x"+Integer.toHexString(view.getId())+" Type:"+view.getClass());
                    }
                }
        });
        
    }



    public String initResolveConfusionMethods(String apkPath,ClassLoader classLoader)throws Exception{
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
            SharedPreferences.Editor editor = MainActivityV2.getSharedPreferences("FunctionPrefs",MainActivityV2.MODE_PRIVATE). edit();

            editor.putInt("CodeVersion",Utils.getAppVersionCode(MainActivityV2));

            editor.putLong("BuildTime",BuildConfig.BUILD_TIME);



            List<DeConfusionResult<ClassData>> classDeConfusionResultList = new ArrayList<>();
            List<DeConfusionResult<FieldData>> fieldDeConfusionResultList = new ArrayList<>();
            List<DeConfusionResult<MethodData>> methodDeConfusionResultList = new ArrayList<>();


            //BypassSplash
            //Lsh5/k;->m(Landroid/app/Activity;)V
            List<MethodData> sh5_k_mMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().usingStrings("[Splash]SplashHelper","checkHotSplash")))
                    .findMethod(FindMethod.create().matcher(
                    MethodMatcher.create().returnType(void.class)
                            .paramTypes("android.app.Activity")));
            methodDeConfusionResultList.add(accessMethodSeekResult(editor,sh5_k_mMethods,"sh5_k_mMethod"));





            //pf5.e
            List<MethodData> pf5_e_getLinkMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().usingStrings("FavoritesMediasItem","oid","otype")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().name("getLink")));

            methodDeConfusionResultList.add(accessMethodSeekResult(editor,pf5_e_getLinkMethods,"pf5_e_getLinkMethod"));


            //Ltv/danmaku/bili/ui/main2/mine/p0;->b(Lcom/bilibili/lib/homepage/mine/MenuGroup$Item;)V
            /*
            List<MethodData> tv_danmaku_bili_ui_main2_mine_p0_bMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().usingStrings("prompt_scene")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().paramTypes("com.bilibili.lib.homepage.mine.MenuGroup$Item")
                            .returnType(void.class)
                            .opCodes(new OpCodesMatcher().opCodes(new Integer[]{
                                    OpCodeUtil.getOpCode("move-result-object"),
                                    OpCodeUtil.getOpCode("move-object"),
                                    OpCodeUtil.getOpCode("move-object"),
                                    OpCodeUtil.getOpCode("invoke-direct/range")

                            }))
                    ));
            stringBuilder.append(accessMethodSeekResult(editor,tv_danmaku_bili_ui_main2_mine_p0_bMethods,"tv_danmaku_bili_ui_main2_mine_p0_bMethod")+"\n");
            */
            //Ltv/danmaku/bili/ui/main2/mine/MinePageManager$switchTo$1;
            List<ClassData> tv_danmaku_bili_ui_main2_mine_MinePageManager$switchTo$1Classes = bridge.findClass(FindClass.create().searchPackages("tv.danmaku.bili.ui.main2.mine").matcher(new ClassMatcher().className(new StringMatcher("MinePageManager", StringMatchType.Contains)).usingStrings("prompt_scene")));
            classDeConfusionResultList.add(accessClassSeekResult(editor,tv_danmaku_bili_ui_main2_mine_MinePageManager$switchTo$1Classes,"tv_danmaku_bili_ui_main2_mine_MinePageManager$switchTo$1Class"));


            //com.bilibili.bplus.followinglist.model.b9
            List<ClassData> com_bilibili_bplus_followinglist_model_b9Classes = bridge.findClass(new FindClass().searchPackages("com.bilibili.bplus.followinglist.model").matcher(new ClassMatcher()
                    .usingStrings("UpListItem(face=")));

            classDeConfusionResultList.add(accessClassSeekResult(editor,com_bilibili_bplus_followinglist_model_b9Classes,"com_bilibili_bplus_followinglist_model_b9Class"));



            //com.bilibili.bplus.followinglist.model.e7
            //Lcom/bilibili/bplus/followinglist/model/e7;->N0(Z)Lcom/bapis/bilibili/app/dynamic/v2/CardVideoUpList;
            List<MethodData> com_bilibili_bplus_followinglist_model_e7_N0Methods = bridge.findClass(new FindClass().searchPackages("com.bilibili.bplus.followinglist.model").matcher(new ClassMatcher()
                    .usingStrings("ModuleVideoUpList(title="))).findMethod(new FindMethod().matcher(new MethodMatcher()
                     .returnType("com.bapis.bilibili.app.dynamic.v2.CardVideoUpList")
                     .paramTypes(boolean.class)));

            methodDeConfusionResultList.add(accessMethodSeekResult(editor,com_bilibili_bplus_followinglist_model_e7_N0Methods,"com_bilibili_bplus_followinglist_model_e7_N0Method"));


            //com.bilibili.app.authorspace.ui.AuthorSpaceActivity
            //Lcom/bilibili/app/authorspace/ui/AuthorSpaceActivity;->F9()V

            List<MethodData> com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Methods = bridge.getClassData("com.bilibili.app.authorspace.ui.AuthorSpaceActivity").findMethod(new FindMethod().matcher(new MethodMatcher()
                    .returnType(void.class)
                    .usingNumbers(0,8)
                    .modifiers(Modifier.PRIVATE)//jadx牛逼 反编译完给我说是public 无敌了
                    .paramCount(0)
                    .addInvoke("Ltv/danmaku/bili/widget/LoadingImageViewV2;->setRefreshError()V")
            ));

            methodDeConfusionResultList.add(accessMethodSeekResult(editor,com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Methods,"com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Method"));

            //Lcom/bilibili/app/comment3/data/model/CommentItem$e$a;->c(Landroid/content/Context;)Ljava/lang/String;
            List<MethodData> com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethods = bridge.findClass(new FindClass().searchPackages("com.bilibili.app.comment3.data.model").matcher(new ClassMatcher().usingStrings("IP属地：")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().returnType(String.class).paramTypes(Context.class).usingStrings("")));

            methodDeConfusionResultList.add(accessMethodSeekResult(editor,com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethods,"com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethod"));



            //qa3.t smallCoverV2
            ClassDataList qa3_tClasses = bridge.findClass(new FindClass().matcher(new ClassMatcher()
                    .usingStrings("rcmdReason and descText")));

            classDeConfusionResultList.add(accessClassSeekResult(editor,qa3_tClasses,"qa3_tClass"));

            //MethodDataList qa3_t_getUriMethod = qa3_tClasses.findMethod(new FindMethod().matcher(new MethodMatcher().name("getUri")));

            FieldDataList qa3_t_fFields = qa3_tClasses.findField(new FindField().matcher(new FieldMatcher().type(String.class).addReadMethod(new MethodMatcher().name("getUri"))));

            fieldDeConfusionResultList.add(accessFieldSeekResult(editor,qa3_t_fFields,"qa3_t_fField"));

            //Lcom/bilibili/search2/result/base/b0;->i1(Ljava/util/List;ZZ)V
            List<MethodData> com_bilibili_search2_result_base_b0_i1Methods = bridge.findClass(new FindClass().searchPackages("com.bilibili.search2.result.base").matcher(new ClassMatcher().usingStrings("SearchResultFooterAdapter"))).findMethod(new FindMethod().matcher(new MethodMatcher().paramTypes(List.class,boolean.class,boolean.class)));

            methodDeConfusionResultList.add(accessMethodSeekResult(editor,com_bilibili_search2_result_base_b0_i1Methods,"com_bilibili_search2_result_base_b0_i1Method"));

            //com.bilibili.ship.theseus.united.page.intro.module.relate.a
            //BadgeInfoClass
            List<ClassData> com_bilibili_ship_theseus_united_page_intro_module_relate_BadgeInfoClasses = bridge.findClass(new FindClass().searchPackages("com.bilibili.ship.theseus.united.page.intro.module.relate").matcher(new ClassMatcher().usingStrings("BadgeInfo(")));
            classDeConfusionResultList.add(accessClassSeekResult(editor,com_bilibili_ship_theseus_united_page_intro_module_relate_BadgeInfoClasses,"com_bilibili_ship_theseus_united_page_intro_module_relate_BadgeInfoClass"));

            //ou0.c
            List<ClassData> ou0_cClasses = bridge.findClass(new FindClass().matcher(new ClassMatcher().addInterface("androidx.viewbinding.ViewBinding").addFieldForType("com.bilibili.ship.theseus.united.page.behavior.CollapsableChildFrameLayout")));
            classDeConfusionResultList.add(accessClassSeekResult(editor,ou0_cClasses,"ou0_cClass"));

            //com.bilibili.app.gemini.ui.UIComponentExtKt$onLongClickEvent$1
            List<ClassData> com_bilibili_app_gemini_ui_UIComponentExtKt$onLongClickEvent$1Classes = bridge.findClass(new FindClass().searchPackages("com.bilibili.app.gemini.ui").matcher(new ClassMatcher().className(new StringMatcher("onLongClickEvent", StringMatchType.Contains))));
            classDeConfusionResultList.add(accessClassSeekResult(editor,com_bilibili_app_gemini_ui_UIComponentExtKt$onLongClickEvent$1Classes,"com_bilibili_app_gemini_ui_UIComponentExtKt$onLongClickEvent$1Class"));

            //
            //Lcom/bilibili/app/comment3/data/model/CommentItem$e$a;->a()Ljava/lang/String;
            List<MethodData> com_bilibili_app_comment3_data_model_CommentItem$e$a_aMethods = bridge.findClass(new FindClass().searchPackages("com.bilibili.app.comment3.data.model").matcher(new ClassMatcher().className(new StringMatcher("CommentItem", StringMatchType.Contains)).usingStrings("Description(createTimeMs=")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().addUsingField(new FieldMatcher().type(Long.class)).returnType(String.class).paramCount(0)));
            methodDeConfusionResultList.add(accessMethodSeekResult(editor,com_bilibili_app_comment3_data_model_CommentItem$e$a_aMethods,"com_bilibili_app_comment3_data_model_CommentItem$e$a_aMethod"));


            editor.apply();
            editor.commit();

            StringBuilder stringBuilder1 = new StringBuilder("未找到:\n\n");
            StringBuilder stringBuilder2 = new StringBuilder("同特征:\n\n");
            StringBuilder stringBuilder3 = new StringBuilder("完美匹配:\n\n");

            for (DeConfusionResult<ClassData> deConfusionResult: classDeConfusionResultList){
                switch (deConfusionResult.getResultType()){
                    case DeConfusionResult.NOT_FOUND:
                        stringBuilder1.append(deConfusionResult.getName()).append("\n\n");
                        break;
                    case DeConfusionResult.NOT_UNIQUE:
                        stringBuilder2.append("------").append(deConfusionResult.getName()).append("------\n");
                        for (int i = 0;i < Math.min(10,deConfusionResult.getContent().size());i++){
                            ClassData classData = deConfusionResult.getContent().get(i);
                            stringBuilder2.append(classData.getDescriptor()).append("\n");
                        }
                        stringBuilder2.append("------------------------\n\n");
                        break;
                    case DeConfusionResult.ONLY:
                        stringBuilder3.append(deConfusionResult.getName()).append("\n").append(deConfusionResult.getContent().get(0).getDescriptor()).append("\n\n");
                        break;
                }
            }

            for (DeConfusionResult<FieldData> deConfusionResult: fieldDeConfusionResultList){
                switch (deConfusionResult.getResultType()){
                    case DeConfusionResult.NOT_FOUND:
                        stringBuilder1.append(deConfusionResult.getName()).append("\n\n");
                        break;
                    case DeConfusionResult.NOT_UNIQUE:
                        stringBuilder2.append("------").append(deConfusionResult.getName()).append("------\n");
                        for (int i = 0;i < Math.min(10,deConfusionResult.getContent().size());i++){
                            FieldData fieldData = deConfusionResult.getContent().get(i);
                            stringBuilder2.append(fieldData.getDescriptor()).append("\n");
                        }
                        stringBuilder2.append("------------------------\n\n");
                        break;
                    case DeConfusionResult.ONLY:
                        stringBuilder3.append(deConfusionResult.getName()).append("\n").append(deConfusionResult.getContent().get(0).getDescriptor()).append("\n\n");
                        break;
                }
            }

            for (DeConfusionResult<MethodData> deConfusionResult: methodDeConfusionResultList){
                switch (deConfusionResult.getResultType()){
                    case DeConfusionResult.NOT_FOUND:
                        stringBuilder1.append(deConfusionResult.getName()).append("\n\n");
                        break;
                    case DeConfusionResult.NOT_UNIQUE:
                        stringBuilder2.append("------").append(deConfusionResult.getName()).append("------\n");
                        for (int i = 0;i < Math.min(10,deConfusionResult.getContent().size());i++){
                            MethodData methodData = deConfusionResult.getContent().get(i);
                            stringBuilder2.append(methodData.getDescriptor()).append("\n");
                        }
                        stringBuilder2.append("------------------------\n\n");
                        break;
                    case DeConfusionResult.ONLY:
                        stringBuilder3.append(deConfusionResult.getName()).append("\n").append(deConfusionResult.getContent().get(0).getDescriptor()).append("\n\n");
                        break;
                }
            }

            return String.valueOf(stringBuilder1) +
                    stringBuilder2 +
                    stringBuilder3;


        }
    }

    public DeConfusionResult<MethodData> accessMethodSeekResult(SharedPreferences.Editor editor , List<MethodData> list, String name){
        if(list.isEmpty()){
            return new DeConfusionResult<>(name);
        }else{
            editor.putString(name,list.get(0).toDexMethod().serialize());
            if(list.size()>1){
                /*
                StringBuilder stringBuilder = new StringBuilder(name+"有"+list.size()+"个\n");
                for(MethodData methodData:list){
                    stringBuilder.append("同特征方法: ").append(methodData.getClassName()).append(" -> ").append(methodData.getName());
                    stringBuilder.append("\n");
                }*/
                return new DeConfusionResult<>(name,DeConfusionResult.NOT_UNIQUE,list);
            }
        }
        return new DeConfusionResult<>(name,DeConfusionResult.ONLY,list);
    }
    public DeConfusionResult<FieldData> accessFieldSeekResult(SharedPreferences.Editor editor , List<FieldData> list, String name){
        if(list.isEmpty()){
            return new DeConfusionResult<>(name);
        }else{
            editor.putString(name,list.get(0).toDexField().serialize());
            if(list.size()>1){
                return new DeConfusionResult<>(name,DeConfusionResult.NOT_UNIQUE,list);
            }
        }
        return new DeConfusionResult<>(name,DeConfusionResult.ONLY,list);
    }

    public DeConfusionResult<ClassData> accessClassSeekResult(SharedPreferences.Editor editor , List<ClassData> list, String name){
        if(list.isEmpty()){
            return new DeConfusionResult<>(name);
        }else{
            editor.putString(name,list.get(0).toDexType().serialize());
            if(list.size()>1){
                return new DeConfusionResult<>(name,DeConfusionResult.NOT_UNIQUE,list);
            }
        }
        return new DeConfusionResult<>(name,DeConfusionResult.ONLY,list);
    }

    public class DeConfusionResult<T>{
        private String name;
        private int result;
        public static final int ONLY = 0;
        public static final int NOT_UNIQUE = 1;
        public static final int NOT_FOUND = 2;


        private List<T> content = null;

        public DeConfusionResult(String name){
            this.name=name;
            result = NOT_FOUND;
        }

        public DeConfusionResult(String name,int result,List<T> content){
            this.name=name;
            this.result = result;
            this.content = content;
        }

        public String getName(){
            return name;
        }

        public int getResultType(){
            return result;
        }
        public List<T> getContent(){
            return content;
        }
    }

    private SpannableString getTextWithFunctionPreposition(String baseDescription,String prepositionName,String prepositionId){
        String text = baseDescription + "\nⓘ 需要前置功能「"+prepositionName+"」";

        SpannableString spannableString = new SpannableString(text);

        // 创建可点击的红色文本
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Toast.makeText(MainActivity.this, "你点击了红色文本！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.YELLOW);
                ds.setUnderlineText(false);
            }
        };



        // 应用Span
        int start = text.indexOf("ⓘ");
        spannableString.setSpan(clickableSpan, start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
