package hhsixhhwkhxh.xposed.bilihook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import android.app.Activity;
import java.lang.reflect.Constructor;
import android.view.View;

public class Untils {
    private static Activity MainActivityV2 = null;
    private static XC_LoadPackage.LoadPackageParam lpparam = null;
    
    private static Method toJSONStringMethod=null;
    private static Method getViewMethod =null;
    public static void init(Activity activity,XC_LoadPackage.LoadPackageParam mlpparam)throws Throwable{
        MainActivityV2=activity;
        lpparam=mlpparam;
        
        final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);
        toJSONStringMethod = jsonClass.getMethod("toJSONString", Object.class);
        
        final Class<?> FragmentClass = XposedHelpers.findClass("androidx.fragment.app.Fragment",lpparam.classLoader);
        getViewMethod = FragmentClass.getMethod("getView");
        
    }
    public static String toJSONString(final XC_LoadPackage.LoadPackageParam lpparam,Object o)throws Throwable{
        return (String)(toJSONStringMethod.invoke(null,o));
    }
    
    public static Activity getMainActivity(){
        return MainActivityV2;
    }

    public static void getBLogMessage(final XC_LoadPackage.LoadPackageParam lpparam,final String keyword){
        //Ltv/danmaku/android/log/BLog;->i(Ljava/lang/String;Ljava/lang/String;)V
        XposedHelpers.findAndHookMethod("tv.danmaku.android.log.BLog",lpparam.classLoader,"i",String.class,String.class,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String label = (String) param.args[0];
                    if(!label.contains(keyword)){return;}
                    XposedBridge.log("BLog "+label+" "+param.args[1]);
                }
            });
    }


    public static Field selectField(Class<?> TargetClass,Class<?> FieldTypeClass){
        for(Field field:TargetClass.getDeclaredFields()){
            if(field.getType().equals(FieldTypeClass)){
                return field;
            }
        }
        return null;
    }

    public static Method selectMethod(Class<?> TargetClass,Class<?> returnType,Class<?>... args){
        Method TargetMethod = null;
   
        for(Method method :TargetClass.getDeclaredMethods()){
            
            if (method.getReturnType().equals(returnType)) {
                Class<?>[] argt = method.getParameterTypes();
                if(argt.length!=args.length){continue;}
                
                boolean argsCheck = true;
                for (int i = 0; i < argt.length; i++) {
                    if(!argt[i].equals(args[i])){
                        
                        argsCheck=false;
                        break;
                    }
                }

                if(!argsCheck){continue;}
                
                TargetMethod=method;
            }
        }
        return TargetMethod;
    }
    
    public static int getViewID(String id){
        return MainActivityV2.getResources().getIdentifier(id,"id",XposedEntrance.TargetPackageName);
    }
    public static void log(Object content){
        if(false){return;}
        if(content==null){content="日志为空";}
        XposedBridge.log(content.toString());
    }
    public static boolean containField(Class clazz,String variableName){

        for (Field field : clazz.getFields()) {

            if (field.getName().equals(variableName)) {
                return true;

            }
        }
        return false;
    }
    
    public static Constructor<?> selectConstructor(Class<?> TargetClass,int ArgsNum){
        for(Constructor constructor :TargetClass.getConstructors()){
            if(constructor.getParameterCount()==ArgsNum){
                return constructor;
            }
        }
        return null;
    }
    
    public static View getView(Object fragment)throws Throwable{
        return (View)(getViewMethod.invoke(fragment));
    }
}
