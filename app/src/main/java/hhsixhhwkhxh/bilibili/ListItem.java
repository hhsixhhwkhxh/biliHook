package hhsixhhwkhxh.bilibili;
import android.view.View;
import android.content.Context;

public abstract class ListItem {
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_EXPANDABLE_SWITCH = 2;

    public static final int TYPE_GROUP_TITLE = 3;
    public ListItem() {
    }

    public abstract View getView(Context context);

    public abstract void initView(Context context,View convertView);
    
    public abstract int getType();
}
