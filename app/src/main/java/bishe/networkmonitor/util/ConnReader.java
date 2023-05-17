package bishe.networkmonitor.util;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bishe.networkmonitor.pojo.NetworkConnection;

/**
 * Created by Dell on 4/27/2023.
 */

public class ConnReader {
    public Context context;

    public ConnReader(Context ctx) {
        this.context = ctx;
    }

    public List<NetworkConnection> getAllNetworkConnections() {
        List<NetworkConnection> connections = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/net/tcp"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("  sl")) continue; // skip the first line
                String[] fields = line.trim().split("\\s+");
                if (fields.length < 10) continue; // invalid line
                String localAddress = hexToIpAddress(fields[1].split(":")[0]);
                int localPort = hexToPort(fields[1].split(":")[1]);
                String remoteAddress = hexToIpAddress(fields[2].split(":")[0]);
                int remotePort = hexToPort(fields[2].split(":")[1]);
                int state = Integer.parseInt(fields[3], 16);
                int uid = getAppUidFromProcNet(fields[7]);
                String appName = getAppNameFromUid(uid);
                NetworkConnection connection = new NetworkConnection(
                        localAddress, localPort, remoteAddress, remotePort, state, uid, appName);
                connections.add(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return connections;
    }

    private static String hexToIpAddress(String hex) {
        long ip = Long.parseLong(hex, 16);
//        return String.format("%d.%d.%d.%d", (ip >> 24) & 0xff, (ip >> 16) & 0xff,
//                (ip >> 8) & 0xff, ip & 0xff);
        return String.format("%d.%d.%d.%d", ip & 0xff, (ip >> 8) & 0xff,
                (ip >> 16) & 0xff, (ip >> 24) & 0xff);
    }

    private static int hexToPort(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private static int getAppUidFromProcNet(String field) {
        int uid = -1;
        try {
            uid = Integer.parseInt(field);
        } catch (NumberFormatException e) {
            // ignore
        }
        return uid;
    }

    private String getAppNameFromUid(int uid) {
        PackageManager pm = context.getPackageManager();
        String[] packages = pm.getPackagesForUid(uid);

        if (packages != null && packages.length > 0) {
            return packages[0];
        }
        return "";
    }

}
