package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;
import hhsixhhwkhxh.xposed.bilihook.Utils;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

public class RemoveNavigationBarSign extends FunctionsBase {

    @Override
    public void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        //Lcom/bilibili/lib/homepage/widget/TabHost;->G(ILandroid/view/View;)V
        //注意这个方法名字被混淆 不同版本名字可能不一样 所以我们先筛出来这个Method对象 再hook
        //导航栏5个标签(首页 动态 +号 会员购 我的)都会走这个方法设置布局 所以以下代码会执行五次 注意判断标签种类

        Method TabSetMethod = Utils.selectMethod(XposedHelpers.findClass( "com.bilibili.lib.homepage.widget.TabHost",lpparam.classLoader),void.class,int.class,View.class);
        if(TabSetMethod!=null){
            UnhooksList.add(XposedBridge.hookMethod(TabSetMethod,new XC_MethodHook(){
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                       
                        View baseView = (View) param.args[1];

                        TextView textview = baseView.findViewById(Utils.getViewID("tab_text"));

                        //textview.getText()的结果5次分别是 "首页" "动态" "" "会员购" "我的"
                        if(sharedPreferences.getBoolean("HomePageNavigationBarRemovePlusSign",false)&&textview.getText().equals("")){
                            //这里采用较为委婉的方式隐藏布局 其实直接setVisibility应该也没问题
                            baseView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                        }
                        if(sharedPreferences.getBoolean("HomePageNavigationBarRemoveVIPShopSign",false)&&textview.getText().equals("会员购")){

                            baseView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                        }
                    }
                }));
        }else{
            Utils.reportError("RemoveNavigationBarSign中TabSetMethod未找到");
        }
    }
    
    
}
