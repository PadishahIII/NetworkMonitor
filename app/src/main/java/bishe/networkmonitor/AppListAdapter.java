package bishe.networkmonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dell on 4/27/2023.
 */

public class AppListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo> mAppInfos;

    public AppListAdapter(Context context, List<AppInfo> appInfos) {
        mContext = context;
        mAppInfos = appInfos;
    }

    @Override
    public int getCount() {
        return mAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.app_list_item, null);
        ImageView appIconView = view.findViewById(R.id.app_icon);
        TextView appNameView = view.findViewById(R.id.app_name);
        AppInfo appInfo = mAppInfos.get(position);
        appIconView.setImageDrawable(appInfo.appIcon);
        appNameView.setText(appInfo.appName);
        final String appName = appInfo.appName;
        final String packageName = appInfo.packageName;
        final int uid = appInfo.uid;
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mContext,AppConnDetailActivity.class);
//                intent.putExtra("appName",appName);
                intent.putExtra("packageName",packageName);
                intent.putExtra("uid",uid);
                mContext.startActivity(intent);

            }
        });
        return view;
    }

}

