package bishe.networkmonitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import bishe.networkmonitor.activity.MsgDetailActivity;
import bishe.networkmonitor.R;
import bishe.networkmonitor.dao.MsgViewHolder;
import bishe.networkmonitor.dao.TextMsg;

public class MsgListAdapter extends ListAdapter<TextMsg, MsgViewHolder> {
    private Context mContext;

    public MsgListAdapter(@NonNull DiffUtil.ItemCallback<TextMsg> callback, Context context) {
        super(callback);
        mContext = context;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MsgViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(MsgViewHolder msgViewHolder, int position) {
//        Log.d("listadapter", "in onBindViewHolder");
        TextMsg textMsg = getItem(position);
        Drawable drawable;
        String titleText;
        String titleText2;
        String previewText;
        String timeText;
        switch (textMsg.level) {
            case 1:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_warning);

                break;
            case 2:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_level_2);

                break;
            case 3:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_level_3);

                break;
            case 4:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_level_4);

                break;
            default:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_level_2);

                break;
        }
        previewText = TextMsg.trimString(textMsg.primaryText, 25);
        titleText = "\"" + textMsg.type + "\"";
        titleText2 = textMsg.getIPStr();
        timeText = new SimpleDateFormat("MM-dd HH:mm").format(new Date(textMsg.timestamp * 1000
        ));

        msgViewHolder.bind(drawable, titleText, titleText2, previewText, timeText, textMsg);
        msgViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(msgViewHolder.itemView.getContext(), MsgDetailActivity.class);
                intent.putExtra("textmsg", textMsg);
                intent.putExtra("packageName","bishe.networkmonitor");
                mContext.startActivity(intent);
            }
        });
    }


    public static class MsgDiff extends DiffUtil.ItemCallback<TextMsg> {
        @Override
        public boolean areItemsTheSame(@NonNull TextMsg oldItem, @NonNull TextMsg newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull TextMsg oldItem, @NonNull TextMsg newItem) {
            return oldItem.equals(newItem);
        }
    }
}
