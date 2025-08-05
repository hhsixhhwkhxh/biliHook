package hhsixhhwkhxh.bilibili;
import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;

public class FunctionAdapter extends ArrayAdapter<ListItem> {
    //private HashMap<Integer,View> convertViewMap = new HashMap<>();
    public FunctionAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);
    }

    
    @Override
    public View getView(final int position, View convertView,  ViewGroup parent) {
        final ListItem item = getItem(position);

        convertView = item.getView(getContext());
        item.initView(getContext(),convertView);

        return convertView;
    }
    
}
