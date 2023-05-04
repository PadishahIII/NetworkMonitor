package bishe.networkmonitor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
        textView.setText(msg.primaryText);
        titleView.setText(getTitle(msg));
        ipView.setText(msg.getIPStr());
        appNameView.setText(msg.getAppName(getApplicationContext()));
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
