package hhsixhhwkhxh.bilibili;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.luckypray.dexkit.wrap.DexClass;
import org.luckypray.dexkit.wrap.DexField;
import org.luckypray.dexkit.wrap.DexMethod;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Utils {
    private static Activity MainActivityV2 = null;

    private static XC_LoadPackage.LoadPackageParam lpparam = null;
    
    private static Method toJSONStringMethod=null;
    private static Method getViewMethod =null;

    private static Method showToastMethod = null;

    private static Method isNightThemeMethod = null;

    private static Method toJsonInGsonMethod = null;
    private static Method fromJsonInGsonMethod = null;

    private static Object simpleGsonObject = null;

    private static Class<Annotation> SerializedNameClass = null;
    private static final HashMap<String,Method> DeConfusionMethodCacheMap = new HashMap<>();
    private static final HashMap<String,Field> DeConfusionFieldCacheMap = new HashMap<>();

    private static final HashMap<String,Class<?>> DeConfusionClassCacheMap = new HashMap<>();

    public static SharedPreferences sharedPreferences;

    public static StringBuilder errorsBeforeInit = new StringBuilder();

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

        final Class<?> MultipleThemeUtils = XposedHelpers.findClass("com.bilibili.lib.ui.util.MultipleThemeUtils",lpparam.classLoader);
        isNightThemeMethod = MultipleThemeUtils.getMethod("isNightTheme",Context.class);

        String errorMsg = errorsBeforeInit.toString();
        if(!errorMsg.isEmpty()){
            showToast("biliHook错误(集中报告):"+errorMsg,Toast.LENGTH_LONG);

        }

        SerializedNameClass = (Class<Annotation>) XposedHelpers.findClass("com.google.gson.annotations.SerializedName",lpparam.classLoader);

        final Class<?> GsonClass = XposedHelpers.findClass("com.google.gson.Gson",lpparam.classLoader);
        toJsonInGsonMethod = GsonClass.getMethod("toJson", Object.class);
        fromJsonInGsonMethod = GsonClass.getMethod("fromJson",String.class, Class.class);
        simpleGsonObject = GsonClass.getConstructor().newInstance();
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

    public static boolean examineIfInitialize(){
        return (MainActivityV2!=null);
    }

    public static boolean isNightTheme(){
        try {
            return (boolean) isNightThemeMethod.invoke(null,getMainActivity());
        } catch (Exception e) {
            return false;
        }
    }

    public static Field selectField(Class<?> TargetClass,Class<?> FieldTypeClass){
        for(Field field:TargetClass.getDeclaredFields()){
            if(field.getType().equals(FieldTypeClass)){
                return field;
            }
        }
        return null;
    }

    public static Field selectFieldConveniently(Class<?> TargetClass,Class<?> FieldTypeClass){
        Field field = selectField(TargetClass,FieldTypeClass);
        if(field==null){
            return null;
        }
        field.setAccessible(true);
        return field;
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

    public static Field selectFieldBySerializedName(Class<?> TargetClass,String name) throws Exception {
        log(TargetClass);
        for(Field field:TargetClass.getDeclaredFields()){

            /*
            log("SerializedNameClass:"+SerializedNameClass);
            Object[] serializedName = field.getDeclaredAnnotationsByType(SerializedNameClass);
            log("fieldName:"+field.getName());
            log("serializedName:"+serializedName);
            log("Annotations_size:"+field.getAnnotations().length);
            log("serializedName_size:"+serializedName.length);
            log("size:"+field.getAnnotations().length);
            //log("type:"+field.getAnnotations()[0].toString());
            Method m = SerializedNameClass.getMethod("value");
            if (serializedName != null&&serializedName.length!=0) {
                //field.setAccessible(true);
                String value = (String) XposedHelpers.callMethod(serializedName[0],"value"); // 获取注解值
                //String value = (String)m.invoke(serializedName);

                log("value:"+value);
                if(name.equals(value)){
                    return field;
                }
            }
            */

            for (Annotation ann : field.getDeclaredAnnotations()) {
                // 通过类名匹配注解
                if (ann.annotationType().getName().equals("com.google.gson.annotations.SerializedName")) {
                    Method valueMethod = ann.annotationType().getMethod("value");
                    String value = (String) valueMethod.invoke(ann);
                    if (name.equals(value)) {
                        return field;
                    }
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
        for (Field field: clazz.getDeclaredFields()){
            field.setAccessible(true);
            result+="\n"+field.getName()+" "+field.getType()+" "+field.get(obj);
        }
        return result;
    }

    public static void printObjectFields(Object obj) {
        if (obj == null) {
            log("Object is null");
            return;
        }

        Class<?> clazz = obj.getClass();
        log("Fields of class: " + clazz.getSimpleName());

        // 遍历所有字段（包括父类）
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                // 跳过静态字段
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // 设置可访问以读取私有字段
                field.setAccessible(true);

                try {
                    Object value = field.get(obj);
                    String valueStr = formatValue(value);
                    log(field.getType().getSimpleName()+" "+field.getName()+" "+valueStr);
                } catch (IllegalAccessException e) {
                    log(" [Access failed]"+field.getName());
                }
            }
            // 继续处理父类的字段
            clazz = clazz.getSuperclass();
        }
    }

    // 格式化特殊类型的值
    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        Class<?> type = value.getClass();

        // 处理数组
        if (type.isArray()) {
            if (type == byte[].class) {
                return Arrays.toString((byte[]) value);
            } else if (type == short[].class) {
                return Arrays.toString((short[]) value);
            } else if (type == int[].class) {
                return Arrays.toString((int[]) value);
            } else if (type == long[].class) {
                return Arrays.toString((long[]) value);
            } else if (type == char[].class) {
                return Arrays.toString((char[]) value);
            } else if (type == float[].class) {
                return Arrays.toString((float[]) value);
            } else if (type == double[].class) {
                return Arrays.toString((double[]) value);
            } else if (type == boolean[].class) {
                return Arrays.toString((boolean[]) value);
            } else {
                // 对象数组
                return Arrays.deepToString((Object[]) value);
            }
        }
        return value.toString();
    }


    public static void submitErrorBeforeInit(String msg){
        errorsBeforeInit.append(msg+"\n");
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
        return MainActivityV2.getResources().getIdentifier(id,"id", Entrance.TargetPackageName);
    }
    public static void log(Object content){
        if(false){return;}
        if(content==null){content="日志为空";}

        XposedBridge.log(content.toString());
        Log.i("biliHook",content.toString());
    }

    public static void log_s(Object content){
        if(false){return;}
        if(content==null){content="日志为空";}
        String str = content.toString();
        int maxLength = 750;
        if(str.length()>maxLength){
            str = str.substring(0, maxLength) + "...";
        }
        log(str);
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
            Log.i("hhsixhhwkhxh",stackTrace);

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

    public static Class getDeConfusionClass(String name,ClassLoader classLoader) throws ClassNotFoundException {
        if(sharedPreferences==null||name==null||name.equals("")){return null;}
        if(DeConfusionClassCacheMap.containsKey(name)){
            return DeConfusionClassCacheMap.get(name);
        }else{
            String descriptor = sharedPreferences.getString(name,"");
            Class<?> targetClass = DexClass.deserialize(descriptor).getInstance(classLoader);
            DeConfusionClassCacheMap.put(name,targetClass);
            return targetClass;
        }
    }

    public static Field getDeConfusionField(String name,ClassLoader classLoader) throws NoSuchFieldException {
        if(sharedPreferences==null||name==null||name.equals("")){return null;}
        if(DeConfusionFieldCacheMap.containsKey(name)){
            return DeConfusionFieldCacheMap.get(name);
        }else{
            String descriptor = sharedPreferences.getString(name,"");
            Field targetClass = DexField.deserialize(descriptor).getFieldInstance(classLoader);
            DeConfusionFieldCacheMap.put(name,targetClass);
            return targetClass;
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
            String modulePackage = "hhsixhhwkhxh.bilibili"; // 模块包名
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

    public static Constructor<?> getConstructorWithParamCount(Class<?> targetClass,int targetParamCount,boolean NeedDeclared){
        Constructor<?>[] constructors;
        if(NeedDeclared){
            constructors = targetClass.getDeclaredConstructors();
        }else{
            constructors = targetClass.getConstructors();
        }

        for (Constructor<?> constructor:constructors){
            int paramCount = constructor.getParameterCount();
            if(paramCount==targetParamCount){
                return constructor;
            }
        }
        return null;
    }

    public static File copyFileToAndroidData(File sourceFile, String subDir) {
        // 获取应用的android/data目录下的files目录，如果subDir不为空则使用subDir
        Context context = getMainActivity();
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir == null) {
            // 外部存储可能不可用
            return null;
        }
        File targetDir;
        if (subDir != null && !subDir.trim().isEmpty()) {
            targetDir = new File(dataDir, subDir);
        } else {
            targetDir = dataDir;
        }
        // 确保目标目录存在
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            // 创建目录失败
            return null;
        }
        File targetFile = new File(targetDir, sourceFile.getName());
        try {
            copyFile(sourceFile, targetFile);
            return targetFile;
        } catch (IOException e) {
            Utils.reportError(e);
            return null;
        }
    }
    // 使用FileChannel进行文件复制，效率较高
    private static void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }

    public static String toJsonInGson(Object obj) throws InvocationTargetException, IllegalAccessException {
        if(toJsonInGsonMethod==null||simpleGsonObject==null){
            return null;
        }
        return (String) toJsonInGsonMethod.invoke(simpleGsonObject,obj);
    }

    public static Object fromJsonInGson(String str, Class<?> cls) throws InvocationTargetException, IllegalAccessException {
        if(fromJsonInGsonMethod==null||simpleGsonObject==null){
            return null;
        }
       return fromJsonInGsonMethod.invoke(simpleGsonObject,str,cls);
    }
}
