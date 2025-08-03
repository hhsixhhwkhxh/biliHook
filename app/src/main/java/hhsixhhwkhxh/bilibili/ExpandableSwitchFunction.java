package hhsixhhwkhxh.bilibili;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableSwitchFunction extends SwitchFunction{

    private boolean extensible = false;

    public ExpandableSwitchFunction(String name, String description, String id) {
        super(name, description, id);
    }

    @Override
    public View getView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        ViewGroup simpleSwitchLayout = (ViewGroup) super.getView(context);

        layout.addView(simpleSwitchLayout);

        // 创建要添加在Switch左边的TextView
        TextView middleTextView = new TextView(context);
        middleTextView.setId(View.generateViewId());
        middleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        middleTextView.setText("▼"); // 设置你的文本
        middleTextView.setTextSize(14);
        //middleTextView.setTextColor(Color.BLACK);
        middleTextView.setPadding(0, 0, 8, 0); // 在文本和Switch之间添加间距

        switchContainer.addView(middleTextView,0);



        return layout;
    }

    @Override
    public void initView(Context context, View convertView) {
        super.initView(context, convertView);

    }

    private void open(){

    }

    @Override
    public int getType() {
        return ListItem.TYPE_EXPANDABLE_SWITCH;
    }
}
