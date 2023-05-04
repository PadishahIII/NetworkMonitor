package bishe.networkmonitor.dao;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Dell on 5/1/2023.
 */
@Entity
public class TextMsg implements Serializable {
    @PrimaryKey
    @NotNull
    public Integer id;

    @ColumnInfo(name = "uid")
    @NotNull
    public Integer uid;
    @ColumnInfo(name = "type")
    @NotNull
    public String type;
    @ColumnInfo(name = "remoteHost")
    @NotNull
    public String remoteHost;
    @ColumnInfo(name = "remotePort")
    @NotNull
    public Integer remotePort;
    @ColumnInfo(name = "localPort")
    @NotNull
    public Integer localPort;
    @ColumnInfo(name = "primaryText")
    @NotNull
    public String primaryText;
    @ColumnInfo(name = "from")
    @NotNull
    public Integer from;
    @ColumnInfo(name = "to")
    @NotNull
    public Integer to;
    @ColumnInfo(name = "timestamp")
    @NotNull
    public Long timestamp;
    @ColumnInfo(name = "level")
    @NotNull
    public Integer level;

    public static TextMsg getDefault() {
        TextMsg textMsg = new TextMsg();
        textMsg.id = 0;
        textMsg.uid = 0;
        textMsg.type = "unknown";
        textMsg.remoteHost = "unknown";
        textMsg.remotePort = 0;
        textMsg.localPort = 0;
        textMsg.primaryText = "unknown";
        textMsg.from = 0;
        textMsg.to = 0;
        textMsg.timestamp = new Date().getTime();
        textMsg.level = 0;
        return textMsg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextMsg)) {
            return false;
        }
        TextMsg that = (TextMsg) obj;
        return id == that.id && uid == that.uid && type.equals(that.type) && remotePort.equals(that.remotePort) && remoteHost.equals(that.remoteHost)
                && localPort.equals(that.localPort) && primaryText.equals(that.primaryText) && from.equals(that.from) && to.equals(that.to) && timestamp.equals(that.timestamp) &&
                level.equals(that.level);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("uid=" + Integer.toString(this.uid) + "\n");
        stringBuffer.append("type=" + this.type + "\n");
        stringBuffer.append("remoteHost=" + this.remoteHost + "\n");
        stringBuffer.append("remoteHost=" + this.remoteHost + "\n");
        stringBuffer.append("remotePort=" + Integer.toString(this.remotePort) + "\n");
        stringBuffer.append("localPort=" + Integer.toString(this.localPort) + "\n");
        stringBuffer.append("level=" + Integer.toString(this.level) + "\n");
        stringBuffer.append("Text=" + this.primaryText + "\n");
        stringBuffer.append("targetText=" + this.primaryText.substring(this.from, this.to + 1) + "\n");
        stringBuffer.append("date=" + (new Date(this.timestamp)).toString() + "\n");
        return stringBuffer.toString();
    }

    public static String trimString(String str, int len) {
        String s;
        if (str.length() > len) {
            s = str.substring(0, len) + "...";
        } else {
            s = str;
        }
        return s;
    }

    public String getTargetStr() {
        String tar = "<empty>";
        if (from < to) {
            try {
                tar = primaryText.substring(from, to);
            } catch (Exception e) {
                Log.d("exception", e.toString());
                tar = "<error>";
            }
        }
        return tar;

    }
    public String getIPStr() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("0.0.0.0:" + Integer.toString(localPort) + "→" + remoteHost + ":" + Integer.toString(remotePort));
        return stringBuffer.toString();
    }
    public String getAppName(Context context) {
        int uid = this.uid;
        PackageManager packageManager = context.getPackageManager();
        String[] packageNames = packageManager.getPackagesForUid(uid);
        if (packageNames == null || packageNames.length <= 0){
            return "<uid " + Integer.toString(uid) + ">";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String packageName : packageNames) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                String appName = packageManager.getApplicationLabel(applicationInfo).toString();
                stringBuffer.append(appName);
                stringBuffer.append(";");
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("exception", e.toString());
            }
        }
        if (stringBuffer.length() <= 0) {
            stringBuffer.append("<uid " + Integer.toString(uid) + ">");
        }
        return stringBuffer.toString();
    }
}
