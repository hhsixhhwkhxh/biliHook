package hhsixhhwkhxh.bilibili;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {
    //private HashMap<Integer,View> convertViewMap = new HashMap<>();
    List<ListItem> items;
    //Context context;

    public FunctionAdapter(Context context, List<ListItem> items) {
        super();
        this.items=items;
        //this.context=context;
    }

    /*
    @Override
    public View getView(final int position, View convertView,  ViewGroup parent) {
        final ListItem item = getItem(position);

        convertView = item.getView(getContext());
        item.initView(getContext(),convertView);

        return convertView;
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType){
            case ListItem.TYPE_BUTTON:
                return createButtonFunctionViewHolder(context);
            case ListItem.TYPE_SWITCH:
                return createSwitchFunctionViewHolder(context);
            case ListItem.TYPE_GROUP_TITLE:
                return createGroupTitleViewHolder(context);
            //case ListItem.TYPE_EXPANDABLE_SWITCH:
        }
        //return new ViewHolder(items.get(viewType).getView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*
        View view = holder.getItemView();
        items.get(position).initView(context,view);
        ListItem listItem = items.get(position);
        if(listItem.getType()==ListItem.TYPE_EXPANDABLE_SWITCH){
            ViewGroup viewGroup = (ViewGroup) view;
            view.setOnClickListener(v -> {
                TextView textView = new TextView(context);
                textView.setText("ciallo");
                viewGroup.addView(textView);
            });
        }*/
        
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    private ButtonFunctionViewHolder createButtonFunctionViewHolder(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        TextView functionName = new TextView(context);
        functionName.setId(View.generateViewId());
        functionName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionName.setTextSize(18);
        functionName.setTextColor(Entrance.contrastColor);

        TextView functionDescription = new TextView(context);
        functionDescription.setId(View.generateViewId());
        functionDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionDescription.setTextSize(10);
        functionDescription.setTextColor(Color.parseColor("#666666"));

        textLayout.addView(functionName);
        textLayout.addView(functionDescription);

        layout.addView(textLayout);
        return new ButtonFunctionViewHolder(layout,functionName,functionDescription,null);
    }

    private SwitchFunctionViewHolder createSwitchFunctionViewHolder(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        TextView functionName = new TextView(context);
        functionName.setId(View.generateViewId());
        functionName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionName.setTextSize(18);
        functionName.setTextColor(Entrance.contrastColor);

        TextView functionDescription = new TextView(context);
        functionDescription.setId(View.generateViewId());
        functionDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionDescription.setTextSize(10);
        functionDescription.setTextColor(Color.parseColor("#666666"));

        textLayout.addView(functionName);
        textLayout.addView(functionDescription);

        LinearLayout switchContainer = new LinearLayout(context);
        switchContainer.setOrientation(LinearLayout.HORIZONTAL);
        switchContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        switchContainer.setGravity(Gravity.CENTER_VERTICAL);

        Switch functionSwitch = new Switch(context);
        functionSwitch.setId(View.generateViewId());
        functionSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        functionSwitch.setPadding(10, 10, 10, 10);
        functionSwitch.setGravity(Gravity.CENTER_VERTICAL);
        functionSwitch.getTrackDrawable().setColorFilter(Entrance.contrastColor, PorterDuff.Mode.SRC_ATOP);

        switchContainer.addView(functionSwitch);
        layout.addView(textLayout);
        layout.addView(switchContainer);

        return new SwitchFunctionViewHolder(layout,functionName,functionDescription);
    }

    private GroupTitleViewHolder createGroupTitleViewHolder(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        TextView GroupTitleTextView = new TextView(context);
        GroupTitleTextView.setId(View.generateViewId());
        GroupTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GroupTitleTextView.setTextSize(18);
        GroupTitleTextView.setTextColor(Entrance.contrastColor);
        //if(CenterText){
            GroupTitleTextView.setGravity(Gravity.CENTER);
        //}
        //GroupTitleTextView.setTextColor(Color.parseColor("#FF000000"));


        textLayout.addView(GroupTitleTextView);


        layout.addView(textLayout);
        return new GroupTitleViewHolder(layout,GroupTitleTextView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;

            context = itemView.getContext();
        }

        public View getItemView(){
            return itemView;
        }
    }

    public static class ButtonFunctionViewHolder extends ViewHolder{
        TextView functionName,functionDescription;
        private FunctionOnClickListener functionOnClickListener;
        public ButtonFunctionViewHolder(@NonNull View itemView,TextView functionName,TextView functionDescription,FunctionOnClickListener functionOnClickListener) {
            super(itemView);

            this.functionName = functionName;
            this.functionDescription = functionDescription;
            this.functionOnClickListener = functionOnClickListener;
        }
    }


    public static class SwitchFunctionViewHolder extends ViewHolder{
        TextView functionName,functionDescription;
        public SwitchFunctionViewHolder(@NonNull View itemView,TextView functionName,TextView functionDescription) {
            super(itemView);

            this.functionName = functionName;
            this.functionDescription = functionDescription;
        }
    }

    public static class GroupTitleViewHolder extends ViewHolder{
        TextView GroupTitleTextView;
        public GroupTitleViewHolder(@NonNull View itemView,TextView GroupTitleTextView) {
            super(itemView);

            this.GroupTitleTextView = GroupTitleTextView;

        }
    }

}
