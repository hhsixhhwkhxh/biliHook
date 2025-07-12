package hhsixhhwkhxh.xposed.bilihook;
import android.view.View;
import android.content.Context;

public abstract class ListItem {

    public ListItem() {
    }

    public abstract View getView(Context context);

    public abstract void initView(Context context,View convertView);
    
    public abstract int getViewKindID();
}
