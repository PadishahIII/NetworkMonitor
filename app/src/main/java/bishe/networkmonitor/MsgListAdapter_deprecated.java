package bishe.networkmonitor;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import bishe.networkmonitor.dao.TextMsg;

/**
 * Created by Dell on 5/2/2023.
 */

public class MsgListAdapter_deprecated extends BaseAdapter {
    private Context mContext;
    private List<TextMsg> mMsgs;

    public MsgListAdapter_deprecated(Context context, List<TextMsg> msgs) {
        mContext = context;
        mMsgs = msgs;
    }

    @Override
    public int getCount() {
        return mMsgs.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.msg_list_item, null);
        ImageView icon = view.findViewById(R.id.msg_icon);
        TextView textSender = view.findViewById(R.id.text_sender);
        TextView textTitle = view.findViewById(R.id.text_title);
        TextView textTime = view.findViewById(R.id.text_time);

        TextMsg msg = mMsgs.get(position);
        switch (msg.level){
            case 0:
                icon.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_warning));
                break;
            case 1:
                icon.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_warning));
                break;
            case 2:
                icon.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_warning));
                break;
            default:
                icon.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_warning));
                break;
        }
        textSender.setText("Warning");
        textTitle.setText(trimString(msg.primaryText,25));
        Date date = new Date(msg.timestamp);
        textTime.setText(date.toString());
        final TextMsg mMsg = msg;
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mContext,MsgDetailActivity.class);
                intent.putExtra("textmsg",mMsg);
                mContext.startActivity(intent);

            }
        });
        return view;
    }
    private String trimString(String str,int len){
        return str.substring(0,len) + "...";
    }

}
