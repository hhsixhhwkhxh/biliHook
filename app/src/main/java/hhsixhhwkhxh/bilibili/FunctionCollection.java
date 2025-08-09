package hhsixhhwkhxh.bilibili;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;


public class FunctionCollection extends ListItem{
    private String name;
    private String description;

    public static final String TAG = "FunctionCollection";

    private boolean enabled;
    public static SharedPreferences sharedPreferences=null;

    //标志 是否启用复选框回调 防止setChecked触发
    private boolean onCheckedChangeListenerEnabled = true;

    List<ListItem> listItems = new ArrayList<>();

    List<SwitchFunction> switchFunctionItems = new ArrayList<>();
    List<String> idList = new ArrayList<>();

    TextView functionName,functionDescription;

    //原本打算使用MaterialCheckBox 听说支持半选中 然而发现不能使用模块自带的md库 会找不到资源
    //只能通过反射用哔哩哔哩的 结果爽吃了一堆try catch的史 好不容易写出来发现想要的半选中又不能正常显示
    //于是红温改用CheckBox
    CheckBox functionCheckBox;

    int CheckBoxState = MaterialCheckBox.STATE_INDETERMINATE;

    SharedPreferences.Editor editor = null;

    ColorStateList AllCheckedColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{-android.R.attr.state_checked}}, new int[]{Color.parseColor("#E66E90"),Color.parseColor("#E66E90")});
    ColorStateList IndeterminateCheckedColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}}, new int[]{Color.BLUE});//一般来说 这会被覆盖

    //复选框使用白色/黑色表示半选中时为true
    boolean isUsingIndeterminateCheckedColorStateList = false;
    View.OnClickListener onUpdateAdapterListener;
    int switchFunctionTotal = 0;
    int activatedCount = 0;
    public FunctionCollection(String name, String description, List<ListItem> listItems, View.OnClickListener onUpdateAdapterListener) {
        this.name = name;
        this.description = description;
        this.listItems=listItems;
        //this.enabled=sharedPreferences.getBoolean(id, false);
        switchFunctionTotal = listItems.size();
        this.onUpdateAdapterListener=onUpdateAdapterListener;


        //如果二级菜单变化 会调用这个 更新一级菜单的复选框
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    activatedCount++;
                }else{
                    activatedCount--;
                }
                updateCheckBoxState();
            }
        };


        for(ListItem item:listItems){
            if(item instanceof SwitchFunction){
                SwitchFunction switchFunction = (SwitchFunction) item;
                switchFunctionItems.add(switchFunction);
                switchFunction.setOnCheckedChangeListener(onCheckedChangeListener);
                idList.add(switchFunction.getId());
                //switchFunction.disableSaveAutomatically();
                if(((SwitchFunction) item).isEnabled()){
                    activatedCount++;
                }
            }else {
                switchFunctionTotal--;
            }
        }
        //Utils.showToast("activatedCount:"+activatedCount+"\nswitchFunctionTotal:"+switchFunctionTotal,Toast.LENGTH_LONG);
        //updateCheckBoxState();

        IndeterminateCheckedColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{-android.R.attr.state_checked}}, new int[]{Entrance.contrastColor,Entrance.contrastColor});


        updateCheckBoxState();
        //CheckBoxState = MaterialCheckBox.STATE_INDETERMINATE;
        //Class<?> MaterialCheckBoxClass = XposedHelpers.findClass("com.google.android.material.checkbox.MaterialCheckBox",lpparam.classLoader);
        //CheckBox checkBox = (CheckBox) MaterialCheckBoxClass.getConstructor(Context.class).newInstance(Utils.getMainActivity());

        /*
        try {
            setCheckedStateMethod = MaterialCheckBoxClass.getMethod("setCheckedState",int.class);
            getCheckedStateMethod = MaterialCheckBoxClass.getMethod("getCheckedState");
            MaterialCheckBoxConstructor = MaterialCheckBoxClass.getConstructor(Context.class);
        } catch (NoSuchMethodException e) {
            //throw new RuntimeException(e);
        }
        */
    }

    //更新一级菜单的复选框
    public void updateCheckBoxState(){
        if(activatedCount==0){
            CheckBoxState = MaterialCheckBox.STATE_UNCHECKED;
            if(functionCheckBox!=null) {
                isUsingIndeterminateCheckedColorStateList = false;
                functionCheckBox.setButtonTintList(AllCheckedColorStateList);
            }

        }else if(activatedCount==switchFunctionTotal){
            CheckBoxState = MaterialCheckBox.STATE_CHECKED;
            if(functionCheckBox!=null) {
                isUsingIndeterminateCheckedColorStateList = false;
                functionCheckBox.setButtonTintList(AllCheckedColorStateList);
            }
        }else{
            CheckBoxState = MaterialCheckBox.STATE_INDETERMINATE;
            if(functionCheckBox!=null) {
                isUsingIndeterminateCheckedColorStateList = true;
                functionCheckBox.setButtonTintList(IndeterminateCheckedColorStateList);
            }
        }
        if(onUpdateAdapterListener!=null){
            onUpdateAdapterListener.onClick(null);
        }
        //Utils.showToast("CheckBoxState:"+CheckBoxState,Toast.LENGTH_LONG);
        //Log.i(TAG,"CheckBoxState:"+CheckBoxState);
        //Log.e(TAG,"CheckBoxState",new Throwable());
    }

    @Override
    public View getView(Context context) {

        editor = context.getSharedPreferences("FunctionPrefs", Context.MODE_PRIVATE).edit();
        //return null;
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

        functionCheckBox = new CheckBox(context);
        functionCheckBox.setId(View.generateViewId());
        functionCheckBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionCheckBox.setPadding(10, 10, 10, 10);
        functionCheckBox.setGravity(Gravity.CENTER_VERTICAL);


        layout.addView(textLayout);
        switchLayout.addView(functionCheckBox);
        layout.addView(switchLayout);


        layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1) {
                RoundCornerDialog dialog = new RoundCornerDialog(context,name,listItems,View.GONE);
                /*
                dialog.setConfirmButtonOnClickListener(v->{
                    for(SwitchFunction item:switchFunctionItems){
                        editor.putBoolean(item.getId(),item.isEnabled());
                    }
                    editor.apply();
                    editor.commit();
                    Utils.showToast("已保存", Toast.LENGTH_SHORT);
                });
                */
                dialog.show();

            }


        });


        return layout;
    }

    @Override
    public void initView(Context context, View convertView) {


        functionName.setText(name);
        functionDescription.setText(description);
        //functionCheckBox.setCheckedState(CheckBoxState);
        onCheckedChangeListenerEnabled = false;
        functionCheckBox.setChecked((CheckBoxState!=MaterialCheckBox.STATE_UNCHECKED));
        onCheckedChangeListenerEnabled = true;
        if(CheckBoxState==MaterialCheckBox.STATE_INDETERMINATE){
            isUsingIndeterminateCheckedColorStateList=true;
            functionCheckBox.setButtonTintList(IndeterminateCheckedColorStateList);
        }
        /*
        try {
            setCheckedStateMethod.invoke(functionCheckBox,CheckBoxState);
        } catch (Exception e) {

        }*/

        functionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //CheckBoxState = functionCheckBox.getCheckedState();
                if(!onCheckedChangeListenerEnabled){return;}

                if(isUsingIndeterminateCheckedColorStateList){
                    isUsingIndeterminateCheckedColorStateList=false;
                    functionCheckBox.setButtonTintList(AllCheckedColorStateList);
                }

                for(SwitchFunction switchFunction:switchFunctionItems){
                    //遍历每个功能对象 获取id 把功能状态统一写入sp文件
                    editor.putBoolean(switchFunction.getId(), isChecked);
                    editor.apply();
                    //更新功能Switch
                    switchFunction.updateSwitchState();

                }


                editor.commit();
            }
        });


    }




    @Override
    public int getViewKindID() {
        return 0;
    }
}
