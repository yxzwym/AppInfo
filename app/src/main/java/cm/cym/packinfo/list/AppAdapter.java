package cm.cym.packinfo.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cm.cym.packinfo.R;

public class AppAdapter extends ArrayAdapter {

    public AppAdapter(Context context, int resource, List<AppBean> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_app, parent, false);

        ImageView iv_app = view.findViewById(R.id.iv_app);
        TextView tv_app = view.findViewById(R.id.tv_app);
        TextView tv_pack_name = view.findViewById(R.id.tv_pack_name);

        AppBean bean = (AppBean) getItem(position);
        iv_app.setImageDrawable(bean.getAppIcon());
        tv_app.setText(bean.getAppName());
        tv_pack_name.setText(bean.getPackName());

        return view;
    }
}
