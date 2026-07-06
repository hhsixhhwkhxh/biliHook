package hhsixhhwkhxh.bilibili;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.view.View;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.graphics.PorterDuff;

public class SwitchFunction extends ListItem {

    private String name;
    private SpannableString description;
    private String id;
    private boolean enabled;
    public static SharedPreferences sharedPreferences=null;
    private View ExtraView = null;
    
    
    private TextView functionName,functionDescription;
    private  Switch functionSwitch;

    //为true时 由各个功能类自动将功能开关状态写入sp
    //为false 则需要外部读取功能开关状态 外部保存 这个类自己就完全不管了
    //现在没有使用这个api 因为觉得自动保存挺好的 如果是手动档 还要麻烦用户多点一次保存按钮
    //虽然我尽心设计的按钮颜色派不上用场就是了
    private boolean switchStateSaveAutomatically = true;

    private boolean isSwitchListenerEnabled = true;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = null;

    public SwitchFunction(String name, String description,String id) {
        this.name = name;
        this.description = new SpannableString(description);
        this.id = id;
        this.enabled=sharedPreferences.getBoolean(id, false);
    }


    public SwitchFunction(String name, SpannableString description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.enabled=sharedPreferences.getBoolean(id, false);
    }


    public void setEnabled(boolean isChecked) {
        enabled = isChecked;
        
    }

    public String getName() {
        return name;
    }


    public String getId(){
        return id;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public View getExtraView(){
        return ExtraView;
    }
    
    
    public View getView(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        functionName = new TextView(context);
        functionName.setId(View.generateViewId());
        functionName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionName.setTextSize(18);
        functionName.setTextColor(Entrance.contrastColor);

        functionDescription = new TextView(context);
        functionDescription.setId(View.generateViewId());
        functionDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionDescription.setTextSize(10);
        functionDescription.setTextColor(Color.parseColor("#666666"));

        textLayout.addView(functionName);
        textLayout.addView(functionDescription);

        LinearLayout switchLayout = new LinearLayout(context);
        switchLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        switchLayout.setOrientation(LinearLayout.VERTICAL);
        switchLayout.setPadding(16, 16, 16, 16);

        functionSwitch = new Switch(context);
        functionSwitch.setId(View.generateViewId());
        functionSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionSwitch.setPadding(10, 10, 10, 10);
        functionSwitch.setGravity(Gravity.CENTER_VERTICAL);
        functionSwitch.getTrackDrawable().setColorFilter(Entrance.contrastColor,PorterDuff.Mode.SRC_ATOP);

        layout.addView(textLayout);
        switchLayout.addView(functionSwitch);
        layout.addView(switchLayout);
        return layout;
    }
    
    @Override
    public void initView(final Context context,View convertView) {
        
        functionName.setText(name);
        functionDescription.setText(description);
        functionSwitch.setChecked(isEnabled());
        updateSwitchState();
        functionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enabled = isChecked;
                    if(!isSwitchListenerEnabled){return;}
                    if(!switchStateSaveAutomatically){

                        return;
                    }

                    if(onCheckedChangeListener!=null){
                        onCheckedChangeListener.onCheckedChanged(buttonView,isChecked);
                    }

                    SharedPreferences.Editor editor = context.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE).edit();
                    editor.putBoolean(id, isChecked);
                    editor.apply();
                    editor.commit();
                }
            });
    }


    public void disableSaveAutomatically(){
        switchStateSaveAutomatically = false;
    }


    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener=onCheckedChangeListener;
    }

    public void updateSwitchState(){
        if(functionSwitch==null){return;}

        this.enabled=sharedPreferences.getBoolean(id, false);
        isSwitchListenerEnabled=false;//防止下面触发onChecked回调 神秘bug打野点
        functionSwitch.setChecked(enabled);
        isSwitchListenerEnabled=true;
    }


    @Override
    public int getViewKindID() {
        return 1;
    }

    
}
