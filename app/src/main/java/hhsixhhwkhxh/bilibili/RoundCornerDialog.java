package hhsixhhwkhxh.bilibili;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RoundCornerDialog extends Dialog {

    private String Title = "title";
    private String Content = "content";

    List<View> ChildrenViews = new ArrayList<>();
    View.OnClickListener confirmButtonOnClickListener = null;

    int buttonVisibility = View.VISIBLE;
    public RoundCornerDialog(Context context,String title, List<ListItem> listItems,int buttonVisibility) {
        super(context);
        for(ListItem item:listItems){
            ChildrenViews.add(item.getView(context));
            item.initView(context,null);
        }
        this.Title=title;
        this.buttonVisibility = buttonVisibility;
    }

    public RoundCornerDialog(Context context,String title, ListItem listItem,int buttonVisibility) {
        super(context);

        ChildrenViews.add(listItem.getView(context));
        listItem.initView(context,null);

        this.Title=title;
        this.buttonVisibility = buttonVisibility;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // 设置窗口背景透明
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }

        // 创建主布局
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainLayout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        //mainLayout.setBackgroundColor(Entrance.backgroundColor);

        // 设置圆角背景
        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setColor(Color.WHITE);
        bgDrawable.setCornerRadius(dpToPx(6));
        mainLayout.setBackground(bgDrawable);

        // 添加标题
        TextView title = new TextView(getContext());
        title.setText(Title);
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dpToPx(10), 0, dpToPx(20));
        mainLayout.addView(title);

        // 添加内容
        /*
        TextView content = new TextView(getContext());
        content.setText(Content);
        content.setTextSize(16);
        content.setTextColor(Color.DKGRAY);
        content.setGravity(Gravity.CENTER);
        content.setPadding(0, 0, 0, dpToPx(30));
        mainLayout.addView(content);
        */
        for(View view: ChildrenViews){
            mainLayout.addView(view);
        }


        // 添加按钮容器
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        buttonLayout.setPadding(0, dpToPx(10), 0, 0);

        int ButtonTextColor = Color.BLACK;
        int ButtonBackgroundColor = Color.WHITE;

        if(Utils.isNightTheme()){
            ButtonTextColor = Color.WHITE;
            ButtonBackgroundColor = Color.GRAY;
            bgDrawable.setColor(Color.parseColor("#17181A"));
            title.setTextColor(ButtonTextColor);
            //content.setTextColor(ButtonTextColor);
        }


        // 创建背景色按钮（灰色）
        Button btnCancel = createButton("取消", ButtonBackgroundColor,ButtonTextColor);
        btnCancel.setOnClickListener(v -> dismiss());
        btnCancel.setVisibility(buttonVisibility);

        // 创建粉色按钮
        //Button btnConfirm = createButton("确定", Color.parseColor("#FF4081"));
        Button btnConfirm = createButton("确定", Color.parseColor("#D04D7B"),Color.WHITE);

        btnConfirm.setOnClickListener(v ->{
            if(confirmButtonOnClickListener!=null){
                confirmButtonOnClickListener.onClick(v);
            }
            dismiss();
        });
        btnConfirm.setVisibility(buttonVisibility);

        // 添加按钮到容器
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0,
                dpToPx(45),
                1.0f
        );
        btnParams.setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
        buttonLayout.addView(btnCancel, btnParams);
        buttonLayout.addView(btnConfirm, btnParams);

        // 添加按钮容器到主布局
        mainLayout.addView(buttonLayout);

        setContentView(mainLayout);


    }

    private Button createButton(String text, int bgColor,int textColor) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setTextColor(textColor);
        button.setBackground(createButtonBackground(bgColor));
        return button;
    }

    private GradientDrawable createButtonBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dpToPx(6));
        drawable.setStroke(dpToPx(1), color);
        return drawable;
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void setConfirmButtonOnClickListener(View.OnClickListener onClickListener){
        confirmButtonOnClickListener = onClickListener;
    }




}