package bishe.networkmonitor.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bishe.networkmonitor.R;
import bishe.networkmonitor.adapter.AppListAdapter;
import bishe.networkmonitor.pojo.AppInfo;

public class AppListMainActivity extends AppCompatActivity {
    public List<AppInfo> appInfos;
    private class InitAppInfoListTask extends AsyncTask<Void, Void, List<AppInfo>> {
        private AppCompatActivity mContext;
        public InitAppInfoListTask(AppCompatActivity context){
            mContext = context;
        }
        @Override
        protected List<AppInfo> doInBackground(Void... voids) {
            // execute heavy computation in background thread
            return initAppInfoList();
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfoList) {
            // update UI with results
            AppListAdapter adapter = new AppListAdapter(mContext, appInfoList);
            ListView listView = (ListView) findViewById(R.id.applist);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_main);

//        initAppInfoList();
        InitAppInfoListTask initAppInfoListTask= new InitAppInfoListTask(this);
        initAppInfoListTask.execute();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_applist);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private List<AppInfo> initAppInfoList() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        appInfos = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = app.packageName;
            appInfo.appName = pm.getApplicationLabel(app).toString();
            appInfo.appIcon = pm.getApplicationIcon(app);
            appInfo.uid = app.uid;
            try {
                appInfo.firstInstallTime = pm.getPackageInfo(app.packageName,0).firstInstallTime;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("exception",e.toString());
            }
            appInfos.add(appInfo);
        }
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return Long.compare(o2.firstInstallTime,o1.firstInstallTime);
            }
        });
        return appInfos;

    }


}
