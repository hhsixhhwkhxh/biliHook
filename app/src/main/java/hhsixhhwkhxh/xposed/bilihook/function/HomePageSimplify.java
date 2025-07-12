package hhsixhhwkhxh.xposed.bilihook.function;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import hhsixhhwkhxh.xposed.bilihook.Utils;

public class HomePageSimplify extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean HomePageRemoveGameSign = sharedPreferences.getBoolean("HomePageRemoveGameSign",false);
        boolean HomePageTopBarFilter = sharedPreferences.getBoolean("HomePageTopBarFilter",false);
        if(!HomePageRemoveGameSign&&!HomePageTopBarFilter){return;}

        if(HomePageRemoveGameSign){
            Class<?> HomeFragmentV2$HomeMenuDataProviderClass = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.HomeFragmentV2$HomeMenuDataProvider",lpparam.classLoader);
            Method getMenuDataListMethod = Utils.selectMethod(HomeFragmentV2$HomeMenuDataProviderClass, List.class, Context.class);
            if(getMenuDataListMethod!=null) {

                UnhooksList.add(XposedBridge.hookMethod(getMenuDataListMethod, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ArrayList DataList = (ArrayList) param.getResult();
                        List newDataList = new ArrayList<>();
                        newDataList.add(DataList.get(DataList.size() - 1));
                        param.setResult(newDataList);
                    }
                }));
            }else{
                Utils.reportError("HomePageSimplify中getMenuDataListMethod为空");
            }


        }

        if(HomePageTopBarFilter){
            Class<?> HomeFragmentV2Class = XposedHelpers.findClass("tv.danmaku.bili.ui.main2.HomeFragmentV2",lpparam.classLoader);
            Method upMethod = Utils.selectMethod(HomeFragmentV2Class, List.class, List.class);

            if(upMethod!=null){
                //Ltv/danmaku/bili/ui/main2/HomeFragmentV2;->up(Ljava/util/List;)Ljava/util/List;
                UnhooksList.add(XposedBridge.hookMethod(upMethod, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        List list = (List) param.args[0];
                        // 假设 list 是原始 List 对象
                        if (list.size() > 3) {
                            list.subList(3, list.size()).clear(); // 清除索引 3 之后的所有元素
                        }

                    }

                }));
            }else{
                Utils.reportError("HomePageSimplify中upMethod为空");

            }


        }
    }
}
