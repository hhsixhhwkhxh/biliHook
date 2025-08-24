package hhsixhhwkhxh.bilibili;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.XModuleResources;
import android.content.res.XResForwarder;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
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

import androidx.core.content.ContextCompat;

import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

import hhsixhhwkhxh.bilibili.function.HomePageSimplify;
import hhsixhhwkhxh.bilibili.function.CommentOptimization;
import hhsixhhwkhxh.bilibili.function.LivePageSimplify;
import hhsixhhwkhxh.bilibili.function.ManageHomePagePush;
import hhsixhhwkhxh.bilibili.function.ManageVideoDetailPagePush;
import hhsixhhwkhxh.bilibili.function.ShareManagement;
import hhsixhhwkhxh.bilibili.function.VideoDetailPageSimplify;
import hhsixhhwkhxh.bilibili.function.BypassSplash;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.query.matchers.FieldsMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.query.matchers.base.OpCodesMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.ClassDataList;
import org.luckypray.dexkit.result.FieldData;
import org.luckypray.dexkit.result.FieldDataList;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.result.MethodDataList;
import org.luckypray.dexkit.util.OpCodeUtil;

import hhsixhhwkhxh.bilibili.function.TestFunctionArea;
import hhsixhhwkhxh.bilibili.function.UserCenterOptimization;
import hhsixhhwkhxh.bilibili.BuildConfig;

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

    static {
        System.loadLibrary("dexkit");
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

    
        if(!lpparam.packageName.equals(TargetPackageName)){return;}

        if(BuildConfig.IS_DEBUG) {
            new TestFunctionArea().advanceRun(lpparam);
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context appContext = (Context) param.args[0];
                sharedPreferences=appContext.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
                Utils.sharedPreferences=sharedPreferences;
                FunctionsBase.sharedPreferences=sharedPreferences;
                /*
                sharedPreferences=appContext.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
                if(!sharedPreferences.getBoolean("BanSwitchLiveByVerticalSlide",false)){
                    return;
                }*/
                LivePageSimplify function = new LivePageSimplify();
                function.context = appContext;

                try {
                    function.run(lpparam);
                } catch (Throwable e) {
                    Utils.log("biliHook Function crashed: " + function.getClass().getSimpleName());
                    Utils.reportError(e);
                }
            }
        });


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

                    Utils.init(MainActivityV2,lpparam);


                    String apkPath = lpparam.appInfo.sourceDir;
                    int beforeVersion = sharedPreferences.getInt("CodeVersion", -1);
                    if(beforeVersion!=Utils.getAppVersionCode(MainActivityV2)){
                        Toast.makeText(MainActivityV2, "模块反混淆初始化...", Toast.LENGTH_SHORT).show();
                        //initNeededMethods(apkPath,classLoader);
                        initResolveConfusionMethods(apkPath,lpparam.classLoader);
                        //Toast.makeText(context, "模块初始化成功", Toast.LENGTH_SHORT).show();
                    }


                    //runFunctionSafely(new RemoveNavigationBarSign(), lpparam);
                    runFunctionSafely(new ManageHomePagePush(), lpparam);
                    runFunctionSafely(new ManageVideoDetailPagePush(), lpparam);
                    runFunctionSafely(new VideoDetailPageSimplify(), lpparam);
                    runFunctionSafely(new BypassSplash(), lpparam);
                    runFunctionSafely(new HomePageSimplify(), lpparam);
                    runFunctionSafely(new CommentOptimization(), lpparam);
                    runFunctionSafely(new UserCenterOptimization(),lpparam);
                    runFunctionSafely(new ShareManagement(),lpparam);
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
                            param.args[0]=new Bundle();
                        }
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            //log("after onCreate");
                            Activity activity = (Activity)param.thisObject;
                            if(activity.getIntent().getBooleanExtra("hook",false)){
                                initSettingActivity(lpparam,activity);
                                //Log.i(TAG,"initSettingActivity");
                            }
                            //XposedHelpers.findAndHookMethod("com.bilibili.studio.uperbase.router.b",lpparam.classLoader,"a",Context.class,new XC_MethodHook(){});
                            //XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"finish",new XC_MethodHook(){});
                        }});
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
        ItemsList.add(new ButtonFunction("壁虎"+BuildConfig.VERSION_NAME+" 适配8.56.0","开源模块 点击跳转github页","GoToGithubPage",new FunctionOnClickListener(){
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

            //HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤横幅", "宽身位的卡片 视频会转生小卡片", "HomePagePushFilterBanner"));
            HomePagePushfiltrationChildrenList.add(new SwitchFunction("过滤横幅", getTextWithFunctionPreposition("宽身位的卡片 视频会转生小卡片","test","test"), "HomePagePushFilterBanner"));
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

        ItemsList.add(new GroupTitle("开屏",true));
        ItemsList.add(new SwitchFunction("去除开屏广告", "和开屏battle了好多次 牢屏别打复活赛了", "BypassSplash"));

        ItemsList.add(new GroupTitle("评论简化",true));
        ItemsList.add(new SwitchFunction("强制评论显示绝对时间", "禁用相对时间(刚刚/x小时前/昨天)仿网页端 精确到秒", "ForceCommentsToShowAbsoluteTime"));

        ItemsList.add(new GroupTitle("直播页面简化",true));
        ItemsList.add(new SwitchFunction("禁止上下滑动切换直播间", "这个功能我还出了逆向教程", "BanSwitchLiveByVerticalSlide"));
        ItemsList.add(new SwitchFunction("隐藏他人直播间礼物全局引流弹幕", "某某投喂某某n个梦幻游乐园/浪漫城堡/深海歌姬，点击前往TA的房间吧！", "HideOthersGiftBroadcastDanmaku"));
        ItemsList.add(new SwitchFunction("隐藏右下角可折叠广告挂件", "通常是一个可点击的轮播图 不知道有没有误伤", "HideLiveNormalBanner"));


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

            StringBuilder stringBuilder = new StringBuilder();

            /*
            这是BypassSplash所用到的部分方法(m6)和变量(L) 这两个虽然被混淆 但是可以通过简单的Utils提供的筛选方法反混淆 所以这里不通过dexkit查找它们
            List<ClassData> MainActivityV2Classes =  bridge.findClass(FindClass.create().matcher(new ClassMatcher(XposedHelpers.findClass("tv.danmaku.bili.MainActivityV2",classLoader))));
            if(MainActivityV2Classes.isEmpty()){
                XposedBridge.log("biliHook: 严重错误MainActivityV2Classes为空");
                return;
            } else if (MainActivityV2Classes.size()>1) {
                XposedBridge.log("biliHook: 警告MainActivityV2Classes不唯一:"+MainActivityV2Classes);
            }
            ClassData MainActivityV2Class = MainActivityV2Classes.get(0);

            //Ltv/danmaku/bili/MainActivityV2;->m6(Ltv/danmaku/bili/ui/splash/ad/model/Splash;Z)Z
            List<MethodData> tv_danmaku_bili_MainActivityV2_m6Methods = MainActivityV2Class.findMethod(new FindMethod().matcher(
                    new MethodMatcher().returnType(boolean.class)
                            .paramTypes("tv.danmaku.bili.ui.splash.ad.model.Splash","boolean")
            ));
            accessMethodSeekResult(editor,tv_danmaku_bili_MainActivityV2_m6Methods,"tv_danmaku_bili_MainActivityV2_m6Method");

            //Ltv/danmaku/bili/MainActivityV2;->L:Landroid/widget/FrameLayout;
            List<FieldData> tv_danmaku_bili_MainActivityV2_LFields = MainActivityV2Class.findField(new FindField().matcher(new FieldMatcher().type("android.widget.FrameLayout")));
            accessFieldSeekResult(editor,tv_danmaku_bili_MainActivityV2_LFields,"tv_danmaku_bili_MainActivityV2_LField");
            */

            //BypassSplash
            //Lsh5/k;->m(Landroid/app/Activity;)V
            List<MethodData> sh5_k_mMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().usingStrings("[Splash]SplashHelper","checkHotSplash")))
                    .findMethod(FindMethod.create().matcher(
                    MethodMatcher.create().returnType(void.class)
                            .paramTypes("android.app.Activity")));
            stringBuilder.append(accessMethodSeekResult(editor,sh5_k_mMethods,"sh5_k_mMethod")+"\n");

            //Lmj3/a;->bind(Landroid/view/View;)Lmj3/a;
            List<MethodData> mj3_a_bindMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().addInterface("androidx.viewbinding.ViewBinding")
                    .usingStrings("Missing required view with ID: ")
                    .modifiers(Modifier.PUBLIC)

                            .fields(new FieldsMatcher().addForType("com.bilibili.ship.theseus.united.widget.TheseusAncestorLayout")
                                .addForType("com.bilibili.ship.theseus.united.widget.LockableCollapsingToolbarLayout")
                                .addForType("com.bilibili.ogv.infra.widget.RatioLayout")
                                .addForType("com.bilibili.ship.theseus.united.widget.UnitedViewPager")
                                    .count(24)))).findMethod(new FindMethod().matcher(new MethodMatcher().name("bind")));

            stringBuilder.append(accessMethodSeekResult(editor,mj3_a_bindMethods,"mj3_a_bindMethod")+"\n");

            //Lkntr/base/localization/n0;->h(J)Ljava/lang/String;
            /* 精确时间的旧hook点
            List<MethodData> kntr_base_localization_n0_hMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().fieldCount(2)
                    .fields(new FieldsMatcher().addForType(long.class)).methodCount(17)
                    .modifiers(Modifier.PUBLIC))).findMethod(new FindMethod().matcher(new MethodMatcher().paramTypes(long.class)
                    .returnType(String.class)
                    .annotationCount(1)
                    .usingNumbers(14)));

            stringBuilder.append(accessMethodSeekResult(editor,kntr_base_localization_n0_hMethods,"kntr_base_localization_n0_hMethod")+"\n");
            */

            //pf5.e
            List<MethodData> pf5_e_getLinkMethods = bridge.findClass(FindClass.create().matcher(new ClassMatcher().usingStrings("FavoritesMediasItem","oid","otype")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().name("getLink")));

            stringBuilder.append(accessMethodSeekResult(editor,pf5_e_getLinkMethods,"pf5_e_getLinkMethod")+"\n");


            //Ltv/danmaku/bili/ui/main2/mine/p0;->b(Lcom/bilibili/lib/homepage/mine/MenuGroup$Item;)V
            //Method putMethod = Map.class.getMethod("put", Objects.class, Objects.class);
            //List<MethodData> list = new ArrayList<>();
            //list.add(new MethodData(putMethod))


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


            //com.bilibili.bplus.followinglist.model.b9
            List<ClassData> com_bilibili_bplus_followinglist_model_b9Classes = bridge.findClass(new FindClass().searchPackages("com.bilibili.bplus.followinglist.model").matcher(new ClassMatcher()
                    .usingStrings("UpListItem(face=")));

            stringBuilder.append(accessClassSeekResult(editor,com_bilibili_bplus_followinglist_model_b9Classes,"com_bilibili_bplus_followinglist_model_b9Class")+"\n");



            //com.bilibili.bplus.followinglist.model.e7
            //Lcom/bilibili/bplus/followinglist/model/e7;->N0(Z)Lcom/bapis/bilibili/app/dynamic/v2/CardVideoUpList;
            List<MethodData> com_bilibili_bplus_followinglist_model_e7_N0Methods = bridge.findClass(new FindClass().searchPackages("com.bilibili.bplus.followinglist.model").matcher(new ClassMatcher()
                    .usingStrings("ModuleVideoUpList(title="))).findMethod(new FindMethod().matcher(new MethodMatcher()
                     .returnType("com.bapis.bilibili.app.dynamic.v2.CardVideoUpList")
                     .paramTypes(boolean.class)));

            stringBuilder.append(accessMethodSeekResult(editor,com_bilibili_bplus_followinglist_model_e7_N0Methods,"com_bilibili_bplus_followinglist_model_e7_N0Method")+"\n");


            //com.bilibili.app.authorspace.ui.AuthorSpaceActivity
            //Lcom/bilibili/app/authorspace/ui/AuthorSpaceActivity;->F9()V

            List<MethodData> com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Methods = bridge.getClassData("com.bilibili.app.authorspace.ui.AuthorSpaceActivity").findMethod(new FindMethod().matcher(new MethodMatcher()
                    .returnType(void.class)
                    .usingNumbers(0,8)
                    .modifiers(Modifier.PRIVATE)//jadx牛逼 反编译完给我说是public 无敌了
                    .paramCount(0)
                    .addInvoke("Ltv/danmaku/bili/widget/LoadingImageViewV2;->setRefreshError()V")
            ));

            stringBuilder.append(accessMethodSeekResult(editor,com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Methods,"com_bilibili_app_authorspace_ui_AuthorSpaceActivity_F9Method")+"\n");

            //Lcom/bilibili/app/comment3/data/model/CommentItem$e$a;->c(Landroid/content/Context;)Ljava/lang/String;
            List<MethodData> com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethods = bridge.findClass(new FindClass().searchPackages("com.bilibili.app.comment3.data.model").matcher(new ClassMatcher().usingStrings("IP属地：")))
                    .findMethod(new FindMethod().matcher(new MethodMatcher().returnType(String.class).paramTypes(Context.class).usingStrings("")));

            stringBuilder.append(accessMethodSeekResult(editor,com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethods,"com_bilibili_app_comment3_data_model_CommentItem$e$a_cMethod")+"\n");

            //ua3.e
            /*
            List<ClassData> ua3_eClasses = bridge.findClass(new FindClass().matcher(new ClassMatcher()
                    .usingStrings("TagData(text=")));

            stringBuilder.append(accessClassSeekResult(editor,ua3_eClasses,"ua3_eClass")+"\n");

             */

            //qa3.t smallCoverV2
            ClassDataList qa3_tClasses = bridge.findClass(new FindClass().matcher(new ClassMatcher()
                    .usingStrings("rcmdReason and descText")));

            stringBuilder.append(accessClassSeekResult(editor,qa3_tClasses,"qa3_tClass")+"\n");

            //MethodDataList qa3_t_getUriMethod = qa3_tClasses.findMethod(new FindMethod().matcher(new MethodMatcher().name("getUri")));

            FieldDataList qa3_t_fFields = qa3_tClasses.findField(new FindField().matcher(new FieldMatcher().type(String.class).addReadMethod(new MethodMatcher().name("getUri"))));

            stringBuilder.append(accessFieldSeekResult(editor,qa3_t_fFields,"qa3_t_fField")+"\n");

            //Lcom/bilibili/search2/result/base/b0;->i1(Ljava/util/List;ZZ)V
            List<MethodData> com_bilibili_search2_result_base_b0_i1Methods = bridge.findClass(new FindClass().searchPackages("com.bilibili.search2.result.base").matcher(new ClassMatcher().usingStrings("SearchResultFooterAdapter"))).findMethod(new FindMethod().matcher(new MethodMatcher().paramTypes(List.class,boolean.class,boolean.class)));

            stringBuilder.append(accessMethodSeekResult(editor,com_bilibili_search2_result_base_b0_i1Methods,"com_bilibili_search2_result_base_b0_i1Method")+"\n");

            editor.apply();
            editor.commit();

            return stringBuilder.toString();


        }
    }

    public String accessMethodSeekResult(SharedPreferences.Editor editor , List<MethodData> list, String name){
        if(list.isEmpty()){
            return (name+"未找到");
        }else{
            editor.putString(name,list.get(0).toDexMethod().serialize());
            if(list.size()>1){
                StringBuilder stringBuilder = new StringBuilder(name+"有"+list.size()+"个\n");
                for(MethodData methodData:list){
                    stringBuilder.append("同特征方法: ").append(methodData.getClassName()).append(" -> ").append(methodData.getName());
                    stringBuilder.append("\n");
                }
                return stringBuilder.toString();
            }
        }
        return(name+"->"+list.get(0).toString());
    }
    public String accessFieldSeekResult(SharedPreferences.Editor editor , List<FieldData> list, String name){
        if(list.isEmpty()){
            return (name+"未找到");
        }else{
            editor.putString(name,list.get(0).toDexField().serialize());
            if(list.size()>1){
                StringBuilder stringBuilder = new StringBuilder(name+"有"+list.size()+"个\n");
                for(FieldData fieldData:list){
                    stringBuilder.append("同特征变量: ").append(fieldData.getClassName()).append(" -> ").append(fieldData.getName());
                    stringBuilder.append("\n");
                }
                return stringBuilder.toString();
            }
        }
        return (name+"->"+list.get(0).toString());
    }

    public String accessClassSeekResult(SharedPreferences.Editor editor , List<ClassData> list, String name){
        if(list.isEmpty()){
            return (name+"未找到");
        }else{
            editor.putString(name,list.get(0).toDexType().serialize());
            if(list.size()>1){
                StringBuilder stringBuilder = new StringBuilder(name+"有"+list.size()+"个\n");
                for(ClassData classData:list){
                    stringBuilder.append("同特征类: ").append(classData.getName());
                    stringBuilder.append("\n");
                }
                return stringBuilder.toString();
            }
        }
        return (name+"->"+list.get(0).toString());
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
