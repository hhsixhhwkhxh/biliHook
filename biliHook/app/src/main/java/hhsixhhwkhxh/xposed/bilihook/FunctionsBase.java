package hhsixhhwkhxh.xposed.bilihook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import android.content.SharedPreferences;

public abstract class FunctionsBase {
    
    public static SharedPreferences sharedPreferences;
    public abstract void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
    public void log(Object content){
        Untils.log(this.getClass().getSimpleName()+ " " + content);
    }
}
