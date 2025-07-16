package hhsixhhwkhxh.bilibili;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public abstract class FunctionsBase {
    
    public static SharedPreferences sharedPreferences;
    protected List<XC_MethodHook.Unhook> UnhooksList = new ArrayList<>();
    public abstract void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;

    public void stop() throws Throwable{
        for(XC_MethodHook.Unhook mUnhook:UnhooksList){
            mUnhook.unhook();
        }
    };
    public void log(Object content){
        Utils.log(this.getClass().getSimpleName()+ " " + content);
    }
}
