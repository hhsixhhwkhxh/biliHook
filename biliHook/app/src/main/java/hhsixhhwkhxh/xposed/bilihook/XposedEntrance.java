package hhsixhhwkhxh.xposed.bilihook;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.HashMap;
import java.lang.reflect.Parameter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import android.os.Handler;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import android.graphics.drawable.BitmapDrawable;
import android.view.View.OnLongClickListener;
import android.os.Environment;
import android.app.DownloadManager;
import android.net.Uri;
import java.util.UUID;
import java.lang.reflect.InvocationTargetException;
import hhsixhhwkhxh.xposed.bilihook.function.RemoveNavigationBarSign;
import hhsixhhwkhxh.xposed.bilihook.function.ManageHomePagePush;
import hhsixhhwkhxh.xposed.bilihook.function.ManageVideoDetailPagePush;
import hhsixhhwkhxh.xposed.bilihook.function.RemoveVideoDetailPageAD;
import hhsixhhwkhxh.xposed.bilihook.function.DownloadPicture;
import hhsixhhwkhxh.xposed.bilihook.function.BypassSplash;

public class XposedEntrance implements IXposedHookLoadPackage {

    public static final String TargetPackageName = "tv.danmaku.bili";
    public static final String ModuleSettingsActivityName = "com.bilibili.lib.dblconfig.DblConfigActivity";

    private Activity MainActivityV2=null;
    public static int contrastColor=Color.BLACK;
    private ListView listView;
    private List<ListItem> ItemsList;
    SharedPreferences sharedPreferences=null;
    
   
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

    
        if(!lpparam.packageName.equals(TargetPackageName)){return;}
        
        XposedHelpers.findAndHookMethod("tv.danmaku.bili.MainActivityV2", lpparam.classLoader, "onCreate",
            Bundle.class, new XC_MethodHook() {
                
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("tv.danmaku.bili.MainActivityV2.onCreate()");
                    MainActivityV2 = (Activity) param.thisObject;
                    //Untils.MainActivityV2=MainActivityV2;
                    Untils.init(MainActivityV2,lpparam);
                    sharedPreferences=MainActivityV2.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE);
                    FunctionsBase.sharedPreferences=sharedPreferences;
                    try{
                        new RemoveNavigationBarSign().run(lpparam);

                        new ManageHomePagePush().run(lpparam);

                        new ManageVideoDetailPagePush().run(lpparam);
                        new RemoveVideoDetailPageAD().run(lpparam);

                        new DownloadPicture().run(lpparam);
                        
                        //BypassSplash(lpparam);
                        new BypassSplash().run(lpparam);
                        Untils.getBLogMessage(lpparam,"Splash");
                        testFunction(lpparam);
                    }catch(Exception e){
                        XposedBridge.log(e);
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
                    ViewGroup MultipleThemeImageView = frameLayout.findViewById(Untils.getViewID("mine_top_view"));
                    
                    View NickNameLayout = frameLayout.findViewById(Untils.getViewID("nick_name_layout"));
                    //log(NickNameLayout);
                    NickNameLayout.setVisibility(View.INVISIBLE);
                    
                    Button button = new Button(MainActivityV2);
                    button.setText("壁虎");
                    button.setTextColor(Color.BLACK);
                    
                    button.setOnClickListener(new OnClickListener(){
                            @Override
                            public void onClick(View p1) {
                                XposedBridge.log("OnClickListener");
                                Intent intent = new Intent(MainActivityV2,XposedHelpers.findClass(ModuleSettingsActivityName,lpparam.classLoader));
                                intent.putExtra("hook",true);
                                MainActivityV2.startActivity(intent);
                                
                                //NeedHandleSettingsActivityOnCreate =true;
                                
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
                                            }
                                            //XposedHelpers.findAndHookMethod("com.bilibili.studio.uperbase.router.b",lpparam.classLoader,"a",Context.class,new XC_MethodHook(){});
                                            //XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"finish",new XC_MethodHook(){});
                                        }});
                                        }
                        });
                    MultipleThemeImageView.addView(button);
                    
                }
        });
        
        
   
        
      
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
        ItemsList.add(new GroupTitle("壁虎 开源模块"));
        ItemsList.add(new GroupTitle("开关设置请重启b站"));
        ItemsList.add(new GroupTitle("主页导航栏简化",true));
        ItemsList.add(new SwitchFunction("去除主页+号", "最简单的一集", "HomePageNavigationBarRemovePlusSign"));
        ItemsList.add(new SwitchFunction("去除主页会员购", "会员go", "HomePageNavigationBarRemoveVIPShopSign"));
        //ItemsList.add(new Function("显示隐藏评论", "代码来自\"哔哩发评反诈\"", "ShowInvisibleComment"));
        ItemsList.add(new GroupTitle("主页推送",true));
        ItemsList.add(new SwitchFunction("过滤横幅", "宽身位的卡片", "HomePagePushFilterateBanner"));
        ItemsList.add(new SwitchFunction("过滤广告", "有缺陷请反馈", "HomePagePushFilterateAD"));
        ItemsList.add(new SwitchFunction("过滤直播", "不会显示推送直播", "HomePagePushFilterateLive"));
        ItemsList.add(new SwitchFunction("过滤游戏", "总有些游戏推荐", "HomePagePushFilterateGame"));
        ItemsList.add(new SwitchFunction("过滤bangumi", "哔哩哔哩国漫", "HomePagePushFilterateBangumi"));
        ItemsList.add(new SwitchFunction("竖屏视频转横屏", "去抖化", "HomePagePushTransformVerticalVideo"));
        ItemsList.add(new SwitchFunction("去\"x万点赞\"", "这样所有视频都能看见up主名字", "HomePagePushRemoveVideoLikeCount"));
        ItemsList.add(new GroupTitle("视频详情页简化",true));
        ItemsList.add(new SwitchFunction("去x万点赞", "同上", "VideoDetailPagePushRemoveVideoLikeCount"));
        ItemsList.add(new SwitchFunction("界面过滤广告", "up主推荐?大喇叭评论区黄条广播?", "VideoDetailPageRemoveAD"));
        ItemsList.add(new SwitchFunction("推送过滤非AV", "一刀切直播游戏等等特殊推送", "VideoDetailPagePushFilterateNotAV"));
        ItemsList.add(new GroupTitle("开屏",true));
        ItemsList.add(new SwitchFunction("去除开屏广告", "此功能尚未过混淆 可能无效", "BypassSplash"));
        ItemsList.add(new GroupTitle("实用性功能",true));
        ItemsList.add(new SwitchFunction("评论区图片长按保存", "需要先点一下进入全屏大图", "CommentSectionAllowPictureLongPressSave"));
        ItemsList.add(new GroupTitle("调试用",true));
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
                                
                                activity.startActivity(new Intent(activity,XposedHelpers.findClass(ClassNameEditText.getText().toString(),lpparam.classLoader)));
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
        
        FunctionAdapter adapter = new FunctionAdapter(activity, ItemsList);
        listView.setAdapter(adapter);
        
        Toast.makeText(activity, "HookAccessible:"+getHookAccessible(), Toast.LENGTH_SHORT).show();
    }

    
    
    
    
    public void testFunction(final XC_LoadPackage.LoadPackageParam lpparam)throws Throwable{
        //Class<?> RootDrawableClass = XposedHelpers.findClass("com.facebook.drawee.generic.RootDrawable",lpparam.classLoader);
        //final Field mControllerOverlayField = XposedHelpers.findField(RootDrawableClass,"mControllerOverlay");
        /*Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);
        final Method toJSONStringMethod = jsonClass.getMethod("toJSONString", Object.class);
        
        Method proxyMethod = this.getClass().getMethod("toJSONString", Object.class);
        XposedBridge.hookMethod(proxyMethod,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(toJSONStringMethod.invoke(param.args[0]));
                }
        });*/
        
        //Ltv/danmaku/bili/MainActivityV2;->isSplashShowing()Z
        
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
                    XposedBridge.log("hookViewBinding "+ViewBindingClass);
                    for (int i = 0; i < ViewList.size(); i++) {
                        //if(param.thisObject==null){return;}
                        Field field = ViewList.get(i);
                        View view = (View) field.get(param.thisObject);
                        XposedBridge.log("hookViewBinding Name:"+field.getName()+" Id:0x"+Integer.toHexString(view.getId())+" Type:"+view.getClass());
                    }
                }
        });
        
    }
    
    
}
