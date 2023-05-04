package bishe.networkmonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import bishe.networkmonitor.dao.TextMsg;

public class MsgViewHolder extends RecyclerView.ViewHolder {
//    private final ImageView icon;
//    private final TextView sender;
//    private final TextView title;
//    private final TextView time;
    private Context mContext;
    private Intent intent;
    private final ImageView icon;
    private final TextView date;
    private final TextView count;
    private final TextView title1;
    private final TextView title2;
    private final TextView preview;
//    private  final  ImageView attachment;

    private MsgViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
//        intent = new Intent(mContext,MsgDetailActivity.class);
//        icon = itemView.findViewById(R.id.msg_icon);
//        sender = itemView.findViewById(R.id.text_sender);
//        title = itemView.findViewById(R.id.text_title);
//        time = itemView.findViewById(R.id.text_time);
        icon = itemView.findViewById(R.id.icon);
        date = itemView.findViewById(R.id.date);
        count = itemView.findViewById(R.id.count);
        title1 = itemView.findViewById(R.id.title1);
        title2 = itemView.findViewById(R.id.title2);
        preview = itemView.findViewById(R.id.preview);
//        attachment = itemView.findViewById(R.id.attachment);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.startActivity(intent);
//            }
//        });
    }

    public void bind(Drawable iconDrawable, String titleText, String titleText2,String previewText,  String timeText,TextMsg textMsg) {
//        icon.setImageDrawable(iconDrawable);
//        sender.setText(senderText);
//        title.setText(titleText);
//        time.setText(timeText);
        icon.setImageDrawable(iconDrawable);
        date.setText(timeText);
        count.setText("1");
        title1.setText(titleText);
        title2.setText(titleText2);
        preview.setText(previewText);
//        attachment.setImageDrawable(iconDrawable);

//        intent.putExtra("textmsg",textMsg);
    }

    static MsgViewHolder create(ViewGroup parent) {
//        int layout = R.layout.msg_list_item;
        int layout = R.layout.msg_list_item_new;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MsgViewHolder(view);
    }
}
