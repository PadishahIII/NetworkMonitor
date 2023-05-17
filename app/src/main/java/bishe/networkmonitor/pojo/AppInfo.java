package bishe.networkmonitor.pojo;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Dell on 4/27/2023.
 */

public class AppInfo implements Serializable{
    public String packageName;
    public String appName;
    public Drawable appIcon;
    public int uid;
    public Long firstInstallTime = 0L;

}

