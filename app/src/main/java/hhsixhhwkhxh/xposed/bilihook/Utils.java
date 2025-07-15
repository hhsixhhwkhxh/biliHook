package hhsixhhwkhxh.xposed.bilihook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import android.app.Activity;
import java.lang.reflect.Constructor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import org.luckypray.dexkit.wrap.DexMethod;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    private static Activity MainActivityV2 = null;
    private static XC_LoadPackage.LoadPackageParam lpparam = null;
    
    private static Method toJSONStringMethod=null;
    private static Method getViewMethod =null;

    private static Method showToastMethod = null;

    private static final HashMap<String,Method> DeConfusionMethodCacheMap = new HashMap<>();
    private static final HashMap<String,Field> DeConfusionFieldCacheMap = new HashMap<>();
    public static SharedPreferences sharedPreferences;
    public static void init(Activity activity,XC_LoadPackage.LoadPackageParam mlpparam)throws Throwable{
        MainActivityV2=activity;
        lpparam=mlpparam;

        final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);
        toJSONStringMethod = jsonClass.getMethod("toJSONString", Object.class);
        
        final Class<?> FragmentClass = XposedHelpers.findClass("androidx.fragment.app.Fragment",lpparam.classLoader);
        getViewMethod = FragmentClass.getMethod("getView");

        final Class<?> ToastHelperClass = XposedHelpers.findClass("com.bilibili.droid.ToastHelper",lpparam.classLoader);
        //Lcom/bilibili/droid/ToastHelper;->showToast(Landroid/content/Context;Ljava/lang/String;I)V
        showToastMethod = ToastHelperClass.getMethod("showToast",Context.class,String.class,int.class);
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
    
    public static Field selectFieldAt(Class<?> TargetClass,Class<?> FieldTypeClass,int index){
        int counts=0;
        for(Field field:TargetClass.getDeclaredFields()){
            if(field.getType().equals(FieldTypeClass)){
                counts++;
                if(counts==index){
                    return field;
                }
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


    public static ArrayList<Method> selectMethods(Class<?> TargetClass, Class<?> returnType, Class<?>... args){
        ArrayList<Method> TargetMethods = new ArrayList<>();

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

                TargetMethods.add(method);
            }
        }
        return TargetMethods;
    }
    
    public static String analyseObject(Object obj)throws Throwable{
        String result="";
        Class clazz = obj.getClass();
        result+=clazz.toString();
        for (Field field: clazz.getFields()){
            result+="\n"+field.getName()+" "+field.getType()+" "+field.get(obj);
        }
        return result;
    }


    public static void showToast(String str,int i){
        try {
            int maxLength = 750;
            if(str.length()>maxLength){
                str = str.substring(0, maxLength) + "...";
            }
            showToastMethod.invoke(null,getMainActivity(),str,i);
        } catch (Exception e) {
            Toast.makeText(getMainActivity(),str,i).show();
        }
    }

    public static void reportError(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        showToast("biliHook错误\n"+stackTrace,1);
    }
    public static void reportError(String str){
        showToast("biliHook错误\n"+str,1);
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
    
    public static void printStackTrace(String str){
        try {
            throw new Exception(str);
        } catch (Exception e) {
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            log(stackTrace);

            // 关闭PrintWriter
            pw.close();
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1; // 返回-1表示获取失败
        }
    }

    public static Method getDeConfusionMethod(String name,ClassLoader classLoader)throws NoSuchMethodException{
        if(sharedPreferences==null||name==null||name.equals("")){return null;}
        if(DeConfusionMethodCacheMap.containsKey(name)){
            return DeConfusionMethodCacheMap.get(name);
        }else{
            String descriptor = sharedPreferences.getString(name,"");
            Method targetMethod = DexMethod.deserialize(descriptor).getMethodInstance(classLoader);
            DeConfusionMethodCacheMap.put(name,targetMethod);
            return targetMethod;
        }
    }

    public static void copyText(String str){
        // 获取剪切板管理器
        ClipboardManager clipboard = (ClipboardManager)MainActivityV2.getSystemService(Context.CLIPBOARD_SERVICE);

        // 创建 ClipData 对象
        ClipData clip = ClipData.newPlainText("Copied Text", str);

        // 设置到剪切板
        clipboard.setPrimaryClip(clip);
    }

    public static int dpToPx(View view, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                view.getResources().getDisplayMetrics()
        );
    }

    // sp转px
    public static float spToPx(View view, float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                view.getResources().getDisplayMetrics()
        );
    }

    public static Resources getModuleResources(Context targetContext) {
        try {
            // 获取当前模块的ApplicationInfo
            String modulePackage = "hhsixhhwkhxh.xposed.bilihook"; // 模块包名
            ApplicationInfo moduleInfo = targetContext.getPackageManager()
                    .getApplicationInfo(modulePackage, 0);

            // 创建AssetManager并添加模块APK路径
            AssetManager assets = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assets, moduleInfo.sourceDir); // 加载模块资源

            // 构建Resources对象
            Resources res = new Resources(
                    assets,
                    targetContext.getResources().getDisplayMetrics(),
                    targetContext.getResources().getConfiguration()
            );
            return res;
        } catch (Exception e) {
            log("加载模块资源失败: "+e);
            return null;
        }
    }
}
