package hhsixhhwkhxh.bilibili;
import android.content.SharedPreferences;
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
    private String description;
    private String id;
    private boolean enabled;
    public static SharedPreferences sharedPreferences=null;
    private View ExtraView = null;
    
    
    TextView functionName,functionDescription;
    Switch functionSwitch;

    public SwitchFunction(String name, String description,String id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.enabled=sharedPreferences.getBoolean(id, false);
    }
    
    public SwitchFunction(String name, String description,String id,View ExtraView) {
        this(name,description,id);
        this.ExtraView=ExtraView;
    }

    public void setEnabled(boolean isChecked) {
        enabled = isChecked;
        
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

        functionSwitch = new Switch(context);
        functionSwitch.setId(View.generateViewId());
        functionSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionSwitch.setPadding(10, 10, 10, 10);
        functionSwitch.setGravity(Gravity.CENTER_VERTICAL);
        functionSwitch.getTrackDrawable().setColorFilter(Entrance.contrastColor,PorterDuff.Mode.SRC_ATOP);

        layout.addView(textLayout);
        layout.addView(functionSwitch);
        return layout;
    }
    
    @Override
    public void initView(final Context context,View convertView) {
        
        functionName.setText(name);
        functionDescription.setText(description);
        functionSwitch.setChecked(isEnabled());

        functionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setEnabled(isChecked);

                    SharedPreferences.Editor editor = context.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE).edit();
                    editor.putBoolean(id, isChecked);
                    editor.apply();

                }
            });
    }
    @Override
    public int getViewKindID() {
        return 1;
    }

    
}
