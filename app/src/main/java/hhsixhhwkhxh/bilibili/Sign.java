package hhsixhhwkhxh.bilibili;
import android.view.Gravity;
import android.view.View;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.View.OnClickListener;

public class Sign extends ListItem {

    private String name;
    private String description;

    private boolean CenterText = false;
    TextView functionName,functionDescription;

    public Sign(String name, String description,boolean CenterText) {
        this.name = name;
        this.description = description;
        this.CenterText=CenterText;
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
        functionName.setTextColor(Entrance.contrastColor);

        functionDescription = new TextView(context);
        functionDescription.setId(View.generateViewId());
        functionDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionDescription.setTextSize(10);
        functionDescription.setTextColor(Color.parseColor("#666666"));

        textLayout.addView(functionName);
        textLayout.addView(functionDescription);

        if(CenterText){
            functionName.setGravity(Gravity.CENTER);
            functionDescription.setGravity(Gravity.CENTER);
        }

        layout.addView(textLayout);



        return layout;
    }

    @Override
    public void initView(final Context context,View convertView) {

        functionName.setText(name);
        functionDescription.setText(description);

    }

    @Override
    public int getViewKindID() {
        return 3;
    }
}
