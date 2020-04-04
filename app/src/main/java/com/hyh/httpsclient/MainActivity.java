package com.hyh.httpsclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.PrimitiveIterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String URL_CA = "https://www.baidu.com/";
    public static final String URL_SELF = "https://192.168.0.103:8443/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCa();
        getSelf();
    }

    private void getCa(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = get(URL_CA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("ssl_test", "MainActivity: getCa: response="+response);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getSelf(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = get(URL_SELF);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("ssl_test", "MainActivity: getSelf: response="+response);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = MyApplication.getOkHttpClient().newCall(request).execute();
        return response.body().string();
    }
}
