package bishe.networkmonitor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import bishe.networkmonitor.dao.MsgRepository;
import bishe.networkmonitor.dao.TextMsg;

public class TestChildAdapter extends BaseExpandableListAdapter {
    public String title;
    public TextMsg textMsg;
    public MsgRepository msgRepository;

    public TestChildAdapter(String title, TextMsg textmsg, MsgRepository msgRepository) {
        this.title = title;
        this.textMsg = textmsg;
        this.msgRepository = msgRepository;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return title;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return textMsg;
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
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_child_title, parent, false);
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText((String) getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_content, parent, false);
        TextView request = (TextView) convertView.findViewById(R.id.text);
        TextView response = (TextView) convertView.findViewById(R.id.response);
        Button btn = (Button) convertView.findViewById(R.id.run_btn);

        request.setText(textMsg.primaryText);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = "";
                res = Request();
                response.setText(res);
                Insert();
            }
        });

        return convertView;
    }

    private String Request() {
        return "200";
    }

    private void Insert() {
        msgRepository.insert(textMsg);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
