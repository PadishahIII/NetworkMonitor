package bishe.networkmonitor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import bishe.networkmonitor.pojo.NetworkConnection;
import bishe.networkmonitor.R;
import bishe.networkmonitor.util.ConnReader;

public class AppConnDetailActivity extends AppCompatActivity {
    public Context context;
    public int uid;
    public String appName;
    public String packageName;
    public List<NetworkConnection> connListByUid;
    public int ipWidth = 360;
    public int portWidth = 160;
    public int textSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();

        setContentView(R.layout.activity_app_conn_detail);
        Button btn =  buildRefreshBtn();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.addView(btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildView();
        initConnInfo();
        buildConnTable();
    }

    @Override
    protected void onPause() {
        this.connListByUid.clear();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.connListByUid.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initConnInfo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initConnInfo();
    }
    private void refresh(){
//        this.connListByUid.clear();
//        initConnInfo();
        Toast.makeText(this,"Refresh Finish",Toast.LENGTH_LONG).show();
    }
    private Button buildRefreshBtn(){
        Button button = (Button) findViewById(R.id.refresh_button);
        final AppCompatActivity t = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.recreate();
                refresh();
            }
        });
//        button.bringToFront();
        return button;
    }

    private void buildView() {
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("packageName");
        int uid = intent.getIntExtra("uid", 0);
        PackageManager pm = getPackageManager();
        Drawable appIcon;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            this.uid = applicationInfo.uid;
            this.appName = pm.getApplicationLabel(applicationInfo).toString();
            this.packageName = applicationInfo.packageName;
            appIcon = pm.getApplicationIcon(applicationInfo);
            ImageView icon = (ImageView) findViewById(R.id.app_icon);
            TextView appnameView = (TextView) findViewById(R.id.app_name);
//            appnameView.setBackgroundColor(getResources().getColor(R.color.gray));
//            appnameView.setTextColor(getResources().getColor(R.color.black));
            icon.setImageDrawable(appIcon);
            appnameView.setText(this.appName);
        } catch (PackageManager.NameNotFoundException e) {

        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(this.appName);

    }


    private void initConnInfo() {
        ConnReader connReader = new ConnReader(this.context);
        List<NetworkConnection> l = connReader.getAllNetworkConnections();
        this.connListByUid = new ArrayList<>();
        for (NetworkConnection i : l) {
            if (i.uid == this.uid) {
                this.connListByUid.add(i);
            } else {
//                this.connListByUid.add(i);
            }
        }
    }

    private void buildConnTable() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.conn_tab);
        tableLayout.setStretchAllColumns(true);
        addTableTitle(tableLayout, "IP", "Port");
        for (NetworkConnection i : this.connListByUid) {
            addTableRow(tableLayout, i.remoteAddress, i.remotePort);
        }


    }

    private void addTableTitle(TableLayout tableLayout, String t1, String t2) {
        TableRow tableRow = new TableRow(getApplicationContext());
        tableRow.setWeightSum(10);
        TextView ipView = new TextView(getApplicationContext());
        TextView portView = new TextView(getApplicationContext());
        ipView.setText(t1);
        ipView.setBackgroundColor(getResources().getColor(R.color.gray));
        ipView.setGravity(Gravity.LEFT);
        ipView.setTextSize(this.textSize);
        ipView.setWidth(this.ipWidth);
        portView.setText(t2);
        portView.setBackgroundColor(getResources().getColor(R.color.gray));
        portView.setGravity(Gravity.LEFT);
        portView.setTextSize(this.textSize);
        portView.setWidth(this.portWidth);
        tableRow.addView(ipView);
        tableRow.addView(portView);
        tableLayout.addView(tableRow);
    }

    private void addTableRow(TableLayout tableLayout, String ip, int port) {
        TableRow tableRow = new TableRow(this);
        tableRow.setWeightSum(10);
        TextView ipView = new TextView(this);
        TextView portView = new TextView(this);
        ipView.setText(ip);
        ipView.setGravity(Gravity.LEFT);
        ipView.setWidth(this.ipWidth);
        ipView.setTextSize(this.textSize);
        portView.setText(Integer.toString(port));
        portView.setGravity(Gravity.LEFT);
        portView.setWidth(this.portWidth);
        portView.setTextSize(this.textSize);
        tableRow.addView(ipView);
        tableRow.addView(portView);
        tableLayout.addView(tableRow);

    }
}
