package bishe.networkmonitor.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import bishe.networkmonitor.R;
import bishe.networkmonitor.adapter.AppListAdapter;
import bishe.networkmonitor.adapter.TestTitleAdapter;
import bishe.networkmonitor.dao.MsgRepository;
import bishe.networkmonitor.dao.TextMsg;
import bishe.networkmonitor.pojo.AppInfo;
import bishe.networkmonitor.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TestViewActivity extends AppCompatActivity {
    public List<TextMsg> textList;
    public List<TextMsg> ocrList;
    public List<TextMsg> otherList;
    public List<String> titleList;
    public List<List<TextMsg>> childList;
    public MsgRepository msgRepository;
    public int uid;
    public SharedPreferences sharedPreferences;
    public String serverIP;
    public int serverPort;
    public Callback emptyCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

        }
    };

    private class RunAllTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            // execute heavy computation in background thread
            try {
                for (TextMsg textMsg : textList
                ) {
                    try {
                        HttpUtil.Request(HttpUtil.getURL(textMsg).toString(), "POST", textMsg.primaryText, emptyCallback);
                    } catch (MalformedURLException e) {
                        Log.d("request error", e.toString());
                    }
                }
                msgRepository.insertAll(textList.stream().toArray(TextMsg[]::new));
                msgRepository.insertAll(ocrList.stream().toArray(TextMsg[]::new));
                Thread.sleep(3000);
                msgRepository.insertAll(otherList.stream().toArray(TextMsg[]::new));
            } catch (Exception e) {
                Log.e("sql error", e.toString());
            }
            String s = String.format("%s:%d\n%s:%d\n%s:%d\n", titleList.get(0), textList.size(), titleList.get(1), ocrList.size(), titleList.get(2), otherList.size());
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            // update UI with results
            TextView textView = (TextView) findViewById(R.id.run_all_text);
            textView.setText(s);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_test));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        msgRepository = new MsgRepository(getApplication(), getString(R.string.database_name), getString(R.string.database_asset_path));
        sharedPreferences = getSharedPreferences(getString(R.string.sp_name), Context.MODE_PRIVATE);
        serverIP = sharedPreferences.getString(getString(R.string.server_ip), getString(R.string.server_ip_default));
        serverPort = Integer.parseInt(sharedPreferences.getString(getString(R.string.server_port), getString(R.string.server_port_default)));

        ExpandableListView textListView = (ExpandableListView) findViewById(R.id.text_list);
        uid = 0;
        try {
            uid = getPackageManager().getApplicationInfo(getPackageName(), 0).uid;
        } catch (PackageManager.NameNotFoundException e) {

        }
        buildTestData();
        titleList = new ArrayList<>();
        childList = new ArrayList<>();

        titleList.add("文本测试");
        titleList.add("OCR测试");
        titleList.add("人脸识别及场景识别测试");
        childList.add(textList);
        childList.add(ocrList);
        childList.add(otherList);


        TestTitleAdapter adapter = new TestTitleAdapter(titleList, childList, msgRepository, getApplicationContext(), this);
        textListView.setAdapter(adapter);
        //一级点击监听
        textListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            //如果你处理了并且消费了点击返回true,这是一个基本的防止onTouch事件向下或者向上传递的返回机制
            return false;
        });

        //二级点击监听
        textListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            //如果你处理了并且消费了点击返回true
            return false;
        });

        Button runAllBtn = (Button) findViewById(R.id.run_all_btn);
        TextView runAllText = (TextView) findViewById(R.id.run_all_text);

        runAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAllText.setText(String.format("Remote Server : %s:%d\nRunning test...", serverIP, serverPort));
                runAllText.setVisibility(View.VISIBLE);

                RunAllTask runAllTask = new RunAllTask();
                runAllTask.execute();
            }
        });
    }


    private void buildTestData() {
        textList = new ArrayList<>();
        ocrList = new ArrayList<>();
        otherList = new ArrayList<>();


        buildTextData("telephone number", "13222222222", 2);
        buildTextData("id card number", "130522200011116666", 1);
        buildTextData("bank card number", "6222022222221234567", 2);
        buildTextData("email", "example@gmail.com", 2);
        buildTextData("car number", "粤A12345", 2);
        buildTextData("passport number", "AB1234567", 1);
        buildTextData("medicare number", "110101199003073375", 1);
        buildTextData("ip address", "192.168.1.1", 2);
        buildTextData("GPS", "37.77,122.41", 2);

        buildImgData("Face", "person1.jpg", 3);
        buildImgData("Face", "person2.jpg", 3);
        buildImgData("Id card number", "ocr1.jpg", 3);
        buildImgData("Id card number", "ocr2.jpg", 3);
        buildImgData("scene", "yolo1.jpg", 3);
    }


    private void buildImgData(String payload, String img, int level) {
        String type = "Image";
        String text = payload + " detected";
        String remoteHost = serverIP;
        int remotePort = serverPort;
        int localPort = 24356;
        int from = text.indexOf(payload);
        int to = from + payload.length();
        TextMsg textMsg = TextMsg.getTextMsg(
                uid,
                type,
                remoteHost,
                remotePort,
                localPort,
                text,
                from,
                to,
                level,
                img);
        if (payload.contains("number") || payload.contains("Number")) {
            ocrList.add(textMsg);
        } else {
            otherList.add(textMsg);
        }
    }

    private void buildTextData(String type, String payload, int level) {
        String remoteHost = serverIP;
        int remotePort = serverPort;
        int localPort = 24356;
        String text;
        int from, to;

        String host = serverIP + (serverPort == 80 || serverPort == 443 ? "" : ":" + Integer.toString(serverPort));
        String file = getString(R.string.server_test_path);
        text = "POST /" + file + " HTTP/1.1\n" +
                "Host: " + host + "\n" +
                "Content-Length: 73\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Connection: close\n" +
                "\n" +
                "{\n" +
                "\"" + type + "\":\"" + payload + "\"\n" +
                "}";

        from = text.indexOf(payload);
        to = from + payload.length();
        textList.add(TextMsg.getTextMsg(
                uid,
                type,
                remoteHost,
                remotePort,
                localPort,
                text,
                from,
                to,
                level,
                ""));

    }


}