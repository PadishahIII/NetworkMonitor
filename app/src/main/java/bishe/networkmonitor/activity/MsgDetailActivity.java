package bishe.networkmonitor.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;

import bishe.networkmonitor.R;
import bishe.networkmonitor.dao.TextMsg;

public class MsgDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_msg_detail));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        buildDetail();
    }

    private void buildDetail() {
        Intent intent = getIntent();
        TextMsg msg = null;
        msg = (TextMsg) intent.getSerializableExtra("textmsg");

        if (msg == null) {
            msg = TextMsg.getDefault();
        }
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView textView = (TextView) findViewById(R.id.text);
        TextView ipView = (TextView) findViewById(R.id.direction);
        TextView appNameView = (TextView) findViewById(R.id.app_name);
        TextView timeView = (TextView) findViewById(R.id.time);
        ImageView imgView = (ImageView) findViewById(R.id.img);
        ImageView preImgView = (ImageView) findViewById(R.id.img_pre);
        TextView preTextView = (TextView) findViewById(R.id.pre_text);

        SpannableString text = new SpannableString(msg.primaryText);
        ForegroundColorSpan redColor = new ForegroundColorSpan(Color.RED);
        text.setSpan(redColor, msg.from, Math.min(msg.to + 1, text.length() - 1), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(text);

        titleView.setText(getTitle(msg));
        ipView.setText(msg.getIPStr());
        timeView.setText(msg.getTimeStr());
        String imgName = msg.img;
        if (imgName != null && imgName != "" && imgName.endsWith(".jpg")) {
            AssetManager assetManager = getAssets();
            try {
                InputStream inputStream = assetManager.open(imgName);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imgView.setImageDrawable(drawable);
                imgView.setAdjustViewBounds(true);
            } catch (IOException e) {
                Log.d("asset error", "not found:" + imgName);
            }
            String imgStr = imgName;
            Log.d("imgName", imgName);
            imgStr = imgStr.substring(0, imgStr.indexOf(".jpg"));
            Log.d("imgStr1", imgStr);
            if (!imgStr.endsWith("_raw")) {
                imgStr = imgStr + "_raw";
            }
            imgStr = imgStr + ".jpg";
            Log.d("imgStrFinal", imgStr);
            try {
                InputStream inputStream = assetManager.open(imgStr);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                preImgView.setImageDrawable(drawable);
                preImgView.setAdjustViewBounds(true);
                preImgView.setVisibility(View.VISIBLE);
                preTextView.setVisibility(View.VISIBLE);
                preTextView.setText(getString(R.string.msg_detail_pre_text));
            } catch (IOException e) {
                Log.d("asset error", "not found:" + imgStr);
            }
        }
        appNameView.setText("来源:" + msg.getAppName(getApplicationContext()));
    }


    private String getTitle(TextMsg textMsg) {
        if (textMsg == null) {
            return "<null>";
        }
        String type = textMsg.type;
        String tar = textMsg.getTargetStr();
        String title = type + ":" + TextMsg.trimString(tar, 15);
        return title;
    }

}
