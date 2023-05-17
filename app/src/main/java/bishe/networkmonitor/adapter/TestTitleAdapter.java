package bishe.networkmonitor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import bishe.networkmonitor.R;
import bishe.networkmonitor.dao.MsgRepository;
import bishe.networkmonitor.dao.TextMsg;
import bishe.networkmonitor.util.HttpUtil;
import bishe.networkmonitor.util.MsgCallback;

public class TestTitleAdapter extends BaseExpandableListAdapter {
    public List<String> titleList;
    public List<List<TextMsg>> childList;
    public MsgRepository msgRepository;
    public Context mContext;
    public Activity mActivity;

    public TestTitleAdapter(List<String> titleList, List<List<TextMsg>> childList, MsgRepository msgRepository, Context context, Activity activity) {
        this.titleList = titleList;
        this.childList = childList;
        this.msgRepository = msgRepository;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public int getGroupCount() {
        return titleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_title, parent, false);
        TextView title = convertView.findViewById(R.id.title);
        title.setText((String) getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_content, parent, false);
        TextMsg textMsg = (TextMsg) getChild(groupPosition, childPosition);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView request = (TextView) convertView.findViewById(R.id.text);
        TextView response = (TextView) convertView.findViewById(R.id.response);
        Button btn = (Button) convertView.findViewById(R.id.run_btn);
        View content = (View) convertView.findViewById(R.id.content);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);

        request.setText(textMsg.primaryText);
        title.setText(textMsg.type);
        AssetManager assetManager = mContext.getAssets();
        String imgStr = textMsg.img;
        if (imgStr != null && imgStr != "" && imgStr.endsWith(".jpg")) {
            if (textMsg.primaryText.endsWith("detected")) {
                request.setText(textMsg.primaryText.substring(0, textMsg.primaryText.indexOf("detected")));
            }
            int i = imgStr.indexOf(".jpg");
            imgStr = imgStr.substring(0, i);
            if (!imgStr.endsWith("_raw")) {
                imgStr = imgStr + "_raw";
            }
            imgStr = imgStr + ".jpg";
            try {
                InputStream inputStream = (InputStream) assetManager.open(imgStr);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.VISIBLE);
                imageView.setAdjustViewBounds(true);
            } catch (IOException e) {

            }
        }
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getVisibility() == View.VISIBLE) {
                    content.setVisibility(View.GONE);
                } else {
                    content.setVisibility(View.VISIBLE);
                }

            }
        });
        if (groupPosition == 0) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        HttpUtil.Request(HttpUtil.getURL(textMsg).toString(), "POST", textMsg.primaryText, new MsgCallback(response, mActivity));
                    } catch (MalformedURLException e) {
                        Log.d("request error", e.toString());
                    }
                    Insert(textMsg);
                }
            });
        } else {

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    response.setText(Integer.toString(200));
                    Insert(textMsg);
                }
            });
        }
        return convertView;

    }

    public void Insert(TextMsg textMsg) {
        msgRepository.insert(textMsg);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
