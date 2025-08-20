package hhsixhhwkhxh.bilibili.function;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;

public class ManageSearchPageResult extends FunctionsBase {
    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //final Class<?> searchAdItemClass = XposedHelpers.findClass("com.bilibili.search2.api.SearchAdItem",lpparam.classLoader);
        //final Class<?> searchVideoItemClass = XposedHelpers.findClass("com.bilibili.search2.api.SearchVideoItem",lpparam.classLoader);
        Method i1Method = Utils.getDeConfusionMethod("com_bilibili_search2_result_base_b0_i1Method",lpparam.classLoader);
        if(i1Method==null){
            Utils.reportError("ManageSearchPageResult 错误 com_bilibili_search2_result_base_b0_i1Method未找到");
            return;
        }
        XposedBridge.hookMethod(i1Method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                List list = (List) param.args[0];
                if(list==null||list.isEmpty()){return;}
                for(Object baseSearchItem:list){
                    Utils.log_s(baseSearchItem.toString());
                    Utils.log(baseSearchItem.getClass());


                    //com.bilibili.search2.api.SearchAdItem
                    //com.bilibili.search2.api.SearchVideoItem
                    //com.bilibili.search2.result.holder.recommend.a0(SearchRelatedSearchItem)
                }

                for (int i = list.size()-1; i >= 0; i--) {
                    if(!list.get(i).getClass().getName().contains("SearchVideoItem")){
                        list.remove(i);
                    }
                }
            }

        });
    }
}
