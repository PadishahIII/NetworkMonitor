package bishe.networkmonitor.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bishe.networkmonitor.R;
import bishe.networkmonitor.adapter.AppPrivilegeAdapter;
import bishe.networkmonitor.pojo.AppPrivilegeInfo;

public class PrivilegeActivity extends AppCompatActivity {
    private List<AppPrivilegeInfo> appInfos;

    private class InitAppInfoListTask extends AsyncTask<Void, Void, List<AppPrivilegeInfo>> {
        private AppCompatActivity mContext;
        public InitAppInfoListTask(AppCompatActivity context){
            mContext = context;
        }
        @Override
        protected List<AppPrivilegeInfo> doInBackground(Void... voids) {
            // execute heavy computation in background thread
            return initAppInfoList();
        }

        @Override
        protected void onPostExecute(List<AppPrivilegeInfo> appInfoList) {
            // update UI with results
            AppPrivilegeAdapter adapter = new AppPrivilegeAdapter(mContext, appInfoList);
            ListView listView = (ListView) findViewById(R.id.privilege_list);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privilege);

        PrivilegeActivity.InitAppInfoListTask initAppInfoListTask= new PrivilegeActivity.InitAppInfoListTask(this);
        initAppInfoListTask.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_privilege);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private List<AppPrivilegeInfo> initAppInfoList() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        appInfos = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            AppPrivilegeInfo appInfo = new AppPrivilegeInfo();
            appInfo.packageName = app.packageName;
            appInfo.appName = pm.getApplicationLabel(app).toString();
            appInfo.appIcon = pm.getApplicationIcon(app);
            appInfo.uid = app.uid;
            int hasPermission = pm.checkPermission("android.permission.INTERNET",appInfo.packageName);
            if (hasPermission == PackageManager.PERMISSION_GRANTED){
                appInfo.InternetPrivilege = true;
            }else{
                appInfo.InternetPrivilege = false;
            }
            try {
                appInfo.firstInstallTime = pm.getPackageInfo(app.packageName,0).firstInstallTime;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("exception",e.toString());
            }
            appInfos.add(appInfo);
        }
        Collections.sort(appInfos, new Comparator<AppPrivilegeInfo>() {
            @Override
            public int compare(AppPrivilegeInfo o1, AppPrivilegeInfo o2) {
                return Long.compare(o2.firstInstallTime,o1.firstInstallTime);
            }
        });
        return appInfos;

    }
}