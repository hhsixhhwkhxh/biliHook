package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import android.app.Activity;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedBridge;
import java.util.List;
import java.util.ArrayList;
import android.os.Handler;
import hhsixhhwkhxh.xposed.bilihook.Untils;
import android.view.ViewGroup;
import android.view.View;
import java.lang.reflect.Constructor;

public class RemoveVideoDetailPageAD extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //if(!CheckSharedPreferences()){return;}
        if(!sharedPreferences.getBoolean("VideoDetailPageRemoveAD",false)){return;}



        //Lcom/bilibili/ship/theseus/detail/UnitedBizDetailsActivity;->onCreate(Landroid/os/Bundle;)V
        XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.detail.UnitedBizDetailsActivity",lpparam.classLoader,"onCreate",Bundle.class,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity UnitedBizDetailsActivity = (Activity) param.thisObject;

                    UnitedBizDetailsActivity.findViewById(Untils.getViewID("underplayer_container")).setVisibility(View.GONE);

                    //log("隐藏up主推荐");
                }
            });
        final Class<?> CommentMainListClass = XposedHelpers.findClass("com.bilibili.app.comment3.data.model.CommentMainList",lpparam.classLoader);
        final Constructor<?> CommentMainListClassConstructor = Untils.selectConstructor(CommentMainListClass,6);
        final Class<?> modelE0Class = CommentMainListClassConstructor.getParameterTypes()[1];
        //com.bilibili.app.comment3.data.model.e0
        final Class<?> modelWClass = CommentMainListClassConstructor.getParameterTypes()[3];
        //com.bilibili.app.comment3.data.model.w
        
     
        final Class<?> CommentMetaDataTypeEnumClass = XposedHelpers.findClass("com.bilibili.app.comment3.ui.holder.CommentMetaDataTypeEnum",lpparam.classLoader);
        Field CommentSortItemField = CommentMetaDataTypeEnumClass.getField("CommentSortItem");
        //Lcom/bilibili/app/comment3/ui/holder/CommentMetaDataTypeEnum;->CommentSortItem:Lcom/bilibili/app/comment3/ui/holder/CommentMetaDataTypeEnum;
        Object CommentSortItemDataTypeEnum = CommentSortItemField.get(null);
        Method getDataClazzMethod = CommentMetaDataTypeEnumClass.getMethod("getDataClazz");
        final Class<?> CommentSortItemClass = (Class<?>) getDataClazzMethod.invoke(CommentSortItemDataTypeEnum);
        final Class<?> CommentItemClass = XposedHelpers.findClass("com.bilibili.app.comment3.data.model.CommentItem",lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(CommentMainListClass, long.class, modelE0Class, String.class, modelWClass, String.class, List.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    List list = (List) param.args[5];
                    if(list==null||list.size()==0){return;}
                    for (int i = list.size()-1; i >= 0; i--) {
                        Class<?> CommentTypeClass = list.get(i).getClass();
                        if(!CommentTypeClass.equals(CommentItemClass)&&!CommentTypeClass.equals(CommentSortItemClass)){
                            list.remove(i);
                        }
                    }
                    
                    
                }
            }
        );
    }
} 
