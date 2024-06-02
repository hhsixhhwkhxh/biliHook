package hhsixhhwkhxh.xposed.bilihook;
import android.view.View;
import android.content.Context;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.Gravity;

public class GroupTitle extends ListItem {

    private String title;
    private boolean CenterText = false;
    
    TextView GroupTitleTextView;
    public GroupTitle(String title){
        this.title=title;
    }
    public GroupTitle(String title,boolean CenterText){
        this(title);
        this.CenterText=CenterText;
    }

    @Override
    public View getView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        GroupTitleTextView = new TextView(context);
        GroupTitleTextView.setId(View.generateViewId());
        GroupTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GroupTitleTextView.setTextSize(18);
        GroupTitleTextView.setTextColor(XposedEntrance.contrastColor);
        if(CenterText){
            GroupTitleTextView.setGravity(Gravity.CENTER);
        }
        //GroupTitleTextView.setTextColor(Color.parseColor("#FF000000"));

        
        textLayout.addView(GroupTitleTextView);
        

        layout.addView(textLayout);
        
        return layout;
    }
    @Override
    public void initView(Context context,View convertView) {
        GroupTitleTextView.setText(title);
    }
    @Override
    public int getViewKindID() {
        return 2;
    }
}
