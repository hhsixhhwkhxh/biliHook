package hhsixhhwkhxh.xposed.bilihook;
import android.content.SharedPreferences;
import android.view.View;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.view.View.OnClickListener;

public class ButtonFunction extends ListItem {
    
    private String name;
    private String description;
    private String id;
    
    
    
    TextView functionName,functionDescription;
    private FunctionOnClickListener functionOnClickListener;

    public ButtonFunction(String name, String description,String id,FunctionOnClickListener functionOnClickListener) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.functionOnClickListener = functionOnClickListener;
    }
    
    public void onFunctionClick(){
        if(functionOnClickListener!=null){
            functionOnClickListener.onClick();
        }
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
    
    
    @Override
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
        functionName.setTextColor(XposedEntrance.contrastColor);

        functionDescription = new TextView(context);
        functionDescription.setId(View.generateViewId());
        functionDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionDescription.setTextSize(10);
        functionDescription.setTextColor(Color.parseColor("#666666"));

        textLayout.addView(functionName);
        textLayout.addView(functionDescription);

        

        layout.addView(textLayout);
        
        layout.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1) {
                    onFunctionClick();
                }
                
            
        });
        
        return layout;
    }
    
    @Override
    public void initView(final Context context,View convertView) {
        
        functionName.setText(name);
        //functionDescription.setText(description);
        
    }
    
    @Override
    public int getViewKindID() {
        return 3;
    }
}
