package bishe.networkmonitor.util;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import bishe.networkmonitor.dao.MsgRepository;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MsgCallback implements Callback {
    TextView responseView;
    Activity context;

    public MsgCallback(TextView view, Activity context) {
        responseView = view;
        this.context = context;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseView.setText(e.toString());
            }
        });

    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String s = Integer.toString(response.code());
        s += "\n";
        s += response.body().string();
        String finalS = s;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseView.setText(finalS);
            }
        });

    }
}
