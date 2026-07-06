package hhsixhhwkhxh.bilibili;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegexFilterItem extends ListItem{
    EditText editText;

    String filterModeKey,regularExpressionKey;

    String defaultExpression;

    int filterMode,defaultFilterMode;

    SharedPreferences sharedPreferences;

    Button modeButton, resetButton,clearButton;

    public static final int DISABLED = 0;
    public static final int BLACKLIST = 1;
    public static final int WHITELIST = 2;

    public RegexFilterItem(String id,String defaultExpression,int filterMode){
        filterModeKey = id+"_FilterMode";
        regularExpressionKey = id+"_RegularExpression";

        this.defaultExpression = defaultExpression;
        this.defaultFilterMode = filterMode;
    }
    @Override
    public View getView(Context context) {

        sharedPreferences = Utils.getFunctionSettingSP(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        editText = new EditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView textView = new TextView(context);
        textView.setText("正则匹配的字符串对象不是简单的\"推荐\"或是\"直播\"这样的短标题字符串\n而是客户端中对应的类的toString()方法的返回值，因此尽量避免使用^与$");

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        resetButton = new Button(context);
        resetButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        resetButton.setText("恢复默认");

        clearButton = new Button(context);
        clearButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        clearButton.setText("清空");

        modeButton = new Button(context);
        modeButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        buttonLayout.addView(resetButton);
        buttonLayout.addView(clearButton);
        buttonLayout.addView(modeButton);

        layout.addView(editText);
        layout.addView(buttonLayout);
        layout.addView(textView);
        
        return layout;
    }

    @Override
    public void initView(Context context, View convertView) {
        filterMode = sharedPreferences.getInt(filterModeKey,defaultFilterMode);
        updateModeButtonText();
        modeButton.setOnClickListener(v->{
            filterMode=(filterMode+1)%3;
            updateModeButtonText();
        });
        String regularExpression = sharedPreferences.getString(regularExpressionKey,defaultExpression);
        editText.setText(regularExpression);

        resetButton.setOnClickListener(v->{
            editText.setText(defaultExpression);
            filterMode = defaultFilterMode;
            updateModeButtonText();
        });

        clearButton.setOnClickListener(v->{
            editText.setText("");

        });
    }

    private void updateModeButtonText(){
        switch (filterMode){
            case DISABLED:
                modeButton.setText("已禁用");
                break;
            case BLACKLIST:
                modeButton.setText("黑名单");
                break;
            case WHITELIST:
                modeButton.setText("白名单");
                break;
        }

    }

    public String getRegularExpression(){
        return editText.getText().toString();
    }

    public int getFilterMode(){
        return filterMode;
    }

    public String getFilterModeKey(){
        return filterModeKey;
    }

    public String getRegularExpressionKey(){
        return regularExpressionKey;
    }

    @Override
    public int getViewKindID() {
        return 0;
    }
}
