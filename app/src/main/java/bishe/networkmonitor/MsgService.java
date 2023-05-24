package bishe.networkmonitor;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bishe.networkmonitor.dao.MsgRepository;
import bishe.networkmonitor.dao.TextMsg;
import bishe.networkmonitor.pojo.Packet;
import bishe.networkmonitor.pojo.RegexMsg;
import bishe.networkmonitor.util.RegexUtil;

public class MsgService extends Service {
    private int uid;
    private MsgRepository msgRepository;
    private RegexUtil mRegexUtil;

    public MsgService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("service", "onUnBind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("service", "onDestroy");
        super.onDestroy();
    }

    private final IMsgInterface.Stub binder = new IMsgInterface.Stub() {
        @Override
        public void transferJsonMsg(String str) throws RemoteException {
            Log.d("service json", str);
            List<TextMsg> textMsgList = jsonToTextMsg(str);
            if (textMsgList == null) {
                return;
            } else {
                Log.d("service insert", Integer.toString(textMsgList.size()));
                insertAll(textMsgList);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }
    };
    private void insertAll(List<TextMsg> textMsgList){
        msgRepository.insertAllSync(textMsgList.stream().toArray(TextMsg[]::new));
    }

    private List<TextMsg> jsonToTextMsg(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String desHost = jsonObject.getString("desHost");
            int desPort = 0;
            try {
                desPort = Integer.parseInt(jsonObject.getString("desPort"));
            } catch (NumberFormatException e) {
                desPort = 0;
            }
            int srcPort = Integer.parseInt(jsonObject.getString("srcPort"));
            Packet packet = new Packet(jsonObject.getString("srcHost"), srcPort, desHost, desPort, jsonObject.getString("content"));
            List<TextMsg> textMsgList = buildTextMsg(packet);

            return textMsgList;
        } catch (JSONException e) {
            Log.d("jsonException", e.toString());
            return null;
        }
    }

    private List<TextMsg> buildTextMsg(Packet packet) {
        List<TextMsg> resList = new ArrayList<>();
        List<RegexMsg> list = regexIdentify(packet.content);
        for (RegexMsg regexMsg : list) {
            TextMsg textMsg = TextMsg.getDefault();
            textMsg.uid = uid;
            textMsg.remoteHost = packet.desHost;
            textMsg.remotePort = new Random().nextBoolean() ? 80 : 443;
            textMsg.localPort = packet.srcPort;
            textMsg.primaryText = packet.content;
            textMsg.from = regexMsg.from;
            textMsg.to = regexMsg.to - 1;
            textMsg.type = regexMsg.type;
            textMsg.level = regexMsg.level;
            textMsg.img = "";
            resList.add(textMsg);
        }
        return resList;
    }

    private List<RegexMsg> regexIdentify(String content) {
        List<RegexMsg> list = mRegexUtil.find(content);
        return list;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        msgRepository = new MsgRepository(getApplication(), getString(R.string.database_name), getString(R.string.database_asset_path));
        mRegexUtil = RegexUtil.getInstance();
        try {
            uid = getPackageManager().getApplicationInfo(getPackageName(), 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            uid = 0;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("server", "onBind");
        return binder;
    }
}