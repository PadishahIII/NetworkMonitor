package bishe.networkmonitor;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.ToolbarKt;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import bishe.networkmonitor.dao.MsgRepository;
import bishe.networkmonitor.dao.TextMsg;
import bishe.networkmonitor.databinding.ActivityTestViewBinding;

public class TestViewActivity extends AppCompatActivity {
    public List<TextMsg> textList;
    public List<TextMsg> ocrList;
    public List<TextMsg> otherList;
    public List<String> titleList;
    public List<List<TextMsg>> childList;
    public MsgRepository msgRepository;
    public int uid;

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

        TestTitleAdapter adapter = new TestTitleAdapter(titleList, childList, msgRepository,getApplicationContext());
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
        String remoteHost = "172.20.116.105";
        int remotePort = 80;
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
        String remoteHost = "172.20.116.105";
        int remotePort = 80;
        int localPort = 24356;
        String text;
        int from, to;


        text = "POST / HTTP/1.1\n" +
                "Host: 172.20.116.105\n" +
                "Content-Length: 73\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36\n" +
                "Origin: http://172.20.116.105\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                "Referer: http://172.20.116.105/\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Connection: close\n" +
                "\n" +
                "{\n" +
                "\"" + type + "\":\"" + payload + "\",\n" +
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