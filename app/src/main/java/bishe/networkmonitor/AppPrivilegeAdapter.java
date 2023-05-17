package bishe.networkmonitor;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AppPrivilegeAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppPrivilegeInfo> mAppInfos;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, // 核心线程数
            5, // 最大线程数
            60L, // 非核心线程的闲置超时时间
            TimeUnit.SECONDS, // 超时单位
            new LinkedBlockingQueue<Runnable>() // 线程队列
    );

    public AppPrivilegeAdapter(Context context, List<AppPrivilegeInfo> appInfos) {
        mContext = context;
        mAppInfos = appInfos;
    }

    @Override
    public int getCount() {
        return mAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.privilege_item, null);
        ImageView appIconView = view.findViewById(R.id.icon);
        TextView appNameView = view.findViewById(R.id.app_name);

        AppPrivilegeInfo appInfo = mAppInfos.get(position);
        appIconView.setImageDrawable(appInfo.appIcon);
        appNameView.setText(appInfo.appName);
        final String appName = appInfo.appName;
        final String packageName = appInfo.packageName;
        final int uid = appInfo.uid;

        Switch switchBtn = view.findViewById(R.id.privilege_switch);
        switchBtn.setChecked(appInfo.InternetPrivilege);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //open
                    execCmd(packageName, uid, true);
                } else {
                    execCmd(packageName, uid, false);
                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AppConnDetailActivity.class);
//                intent.putExtra("appName",appName);
                intent.putExtra("packageName", packageName);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);

            }
        });
        return view;
    }

    private void execCmd(String packageName,int uid,boolean s){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                changeNetworkPermission(packageName,uid,s);
            }
        });
    }
    private void changeNetworkPermission(String packageName, int uid, boolean s) {
//        NetworkPolicyManager policyManager = (NetworkPolicyManager) getSystemService(NETWORK_POLICY_SERVICE);
//        int uid = getPackageManager().getPackageUid(packageName, 0);
//        policyManager.setUidPolicy(uid, NetworkPolicyManager.POLICY_REJECT_METERED_BACKGROUND);
//        PackageManager packageManager = (PackageManager) mContext.getPackageManager();
//        int state = s ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
//        packageManager.setApplicationEnabledSetting(packageName, state, PackageManager.DONT_KILL_APP);
        String cmd = s ? "iptables -D OUTPUT -m owner --uid-owner " + Integer.toString(uid) + " -j DROP" : "iptables -A OUTPUT -m owner --uid-owner " + Integer.toString(uid) + " -j DROP";
        try {
            Process process = new ProcessBuilder()
                    .command(cmd)
                    .redirectErrorStream(true)
                    .start();
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                return;
            }
        } catch (IOException e) {

        }
    }

}