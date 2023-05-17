package bishe.networkmonitor.activity;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import bishe.networkmonitor.R;
import bishe.networkmonitor.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public Context context;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        sharedPreferences = getSharedPreferences(getString(R.string.sp_name), Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_main_panel);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        NavigationListener navigationListener = NavigationListener.getInstance(this);
        navigationView.setNavigationItemSelectedListener(this);

        buildComponentStatus();
        buildEditText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEditText();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshEditText();
    }

    private void refreshEditText() {
        EditText ipText = (EditText) findViewById(R.id.edit_ip);
        EditText portText = (EditText) findViewById(R.id.edit_port);

        ipText.setText(sharedPreferences.getString(getString(R.string.server_ip), getString(R.string.server_ip_default)));
        portText.setText(sharedPreferences.getString(getString(R.string.server_port), getString(R.string.server_port_default)));
    }

    private void buildEditText() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText ipText = (EditText) findViewById(R.id.edit_ip);
        EditText portText = (EditText) findViewById(R.id.edit_port);
        Button saveBtn = (Button) findViewById(R.id.save_btn);

        ipText.setText(sharedPreferences.getString(getString(R.string.server_ip), getString(R.string.server_ip_default)));
        portText.setText(sharedPreferences.getString(getString(R.string.server_port), getString(R.string.server_port_default)));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ipText.getText()) || TextUtils.isEmpty(portText.getText())) {
                    Toast.makeText(getApplicationContext(), "Invail IP or Port", Toast.LENGTH_LONG);
                } else {
                    editor.putString(getString(R.string.server_ip), ipText.getText().toString());
                    editor.putString(getString(R.string.server_port), portText.getText().toString());
                    setServerStatus(ipText.getText().toString(), Integer.parseInt(portText.getText().toString()));

                }
            }
        });

    }

    private void setServerStatus(String ip, int port) {
        TextView serverStatus = (TextView) findViewById(R.id.server_status);
        serverStatus.setText(getString(R.string.detecting));
        try {
            URL url = port == 443 ? new URL("https", ip, port, "") : new URL("http", ip, port, "");
            HttpUtil.Request(url.toString(), "GET", "", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setTextColor(getColor(R.color.red));
                            serverStatus.setText("Request Failed");
                        }
                    });

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setTextColor(getColor(R.color.green));
                            serverStatus.setText(getString(R.string.status_success) + "..." + Integer.toString(response.code()));
                        }
                    });

                }
            });
        } catch (MalformedURLException e) {
            serverStatus.setTextColor(getColor(R.color.red));
            serverStatus.setText("Invail IP or Port");
        }
    }


    private void buildComponentStatus() {
        TextView ecaptureStatus = (TextView) findViewById(R.id.ecapture_status);
        TextView nidsStatus = (TextView) findViewById(R.id.nids_status);
        TextView serverStatus = (TextView) findViewById(R.id.server_status);
        TextView hsStatus = (TextView) findViewById(R.id.hs_status);

        ecaptureStatus.setText(R.string.status_success);
        nidsStatus.setText(R.string.status_success);
        hsStatus.setText(R.string.status_success);

        String ip = sharedPreferences.getString(getString(R.string.server_ip), getString(R.string.server_ip_default));
        String port = sharedPreferences.getString(getString(R.string.server_port), getString(R.string.server_port_default));
        setServerStatus(ip, Integer.parseInt(port));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_content) {
            // Handle the camera action
        } else if (id == R.id.nav_privilege) {
            Intent intent = new Intent(this, PrivilegeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_msg) {
            Intent intent = new Intent(this, MsgActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, TestViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_app_list) {
            Intent intent = new Intent(this, AppListMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
