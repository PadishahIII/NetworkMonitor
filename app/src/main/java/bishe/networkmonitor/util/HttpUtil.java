package bishe.networkmonitor.util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import bishe.networkmonitor.dao.TextMsg;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class HttpUtil {
    static OkHttpClient httpClient = new OkHttpClient();
    static OkHttpClient httpsClient;

    static {
        try {
            httpsClient = OKHttpClientBuilder.buildOKHttpClient().build();
        } catch (NoSuchAlgorithmException e) {
            Log.d("okhttp", e.toString());
            httpsClient = new OkHttpClient();
        }
    }

    public static void Request(String url, String method, String req, Callback callback) {
        RequestBody requestBody = RequestBody.create(getBody(req), MediaType.parse("application/json; charset=utf-8"));
        Request request = method.equalsIgnoreCase("POST") ?
                new Request.Builder().url(url).post(requestBody).build() :
                new Request.Builder().url(url).get().build();
        if (url.startsWith("http:")) {
            httpClient.newCall(request).enqueue(callback);
        } else if (url.startsWith("https:")) {
            httpsClient.newCall(request).enqueue(callback);
        } else {
            Log.e("okhttp request", "Invail url");
        }
    }

    public static String getBody(String req) {
        int i = req.indexOf("\n{");
        i += 1;
        return req.substring(i);
    }

    public static URL getURL(TextMsg textMsg) throws MalformedURLException {
        int p = textMsg.remotePort;
        String schema = "http";
        if (p == 443) {
            schema = "https";
        }
        try {
            String l = new BufferedReader(new StringReader(textMsg.primaryText)).readLine();
            String path = l.trim().split(" ")[1];
            path = path.substring(1);
            return new URL(schema, textMsg.remoteHost, p, path);
        } catch (Exception e) {
            Log.d("httputil", e.toString());
            return new URL(schema, textMsg.remoteHost, p, "");
        }


    }
}

class OKHttpClientBuilder {
    public static OkHttpClient.Builder buildOKHttpClient() throws NoSuchAlgorithmException {
        try {
            TrustManager[] trustAllCerts = buildTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.d("okhttp", e.toString());
            return new OkHttpClient.Builder();
        }
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }
}
