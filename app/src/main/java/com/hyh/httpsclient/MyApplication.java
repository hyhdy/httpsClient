package com.hyh.httpsclient;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okio.Buffer;

/**
 * created by curdyhuang on 2020-01-29
 */
public class MyApplication extends Application {
    private static OkHttpClient mOkHttpClient;
    private HttpsSSLHelper mHttpHelper = new HttpsSSLHelper();

    //rfc格式的证书
    private String CER_HYH = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDRzCCAi+gAwIBAgIEdP3RdTANBgkqhkiG9w0BAQsFADBUMQwwCgYDVQQGEwNo\n" +
            "eWgxDDAKBgNVBAgTA2h5aDEMMAoGA1UEBxMDaHloMQwwCgYDVQQKEwNoeWgxDDAK\n" +
            "BgNVBAsTA2h5aDEMMAoGA1UEAxMDaHloMB4XDTIwMDEyODEyNDc1NFoXDTI5MTIw\n" +
            "NjEyNDc1NFowVDEMMAoGA1UEBhMDaHloMQwwCgYDVQQIEwNoeWgxDDAKBgNVBAcT\n" +
            "A2h5aDEMMAoGA1UEChMDaHloMQwwCgYDVQQLEwNoeWgxDDAKBgNVBAMTA2h5aDCC\n" +
            "ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK64XX+OdIz/7fQb7TtUTG/1\n" +
            "dDVL85h9C8zrkj+kM2EDmYq21Er+/btLVN1qXTZmlEQOoiCbNVYkpQmXJkurf2At\n" +
            "orzor6VtJAJ5WwY01SwkpO3IROWb8eH/r8vk3Kmh4KawZqPDru1V/KFjo7QHNgkc\n" +
            "ymCjit6iOUkK8OX7a6vfc6wTFaF2vXoKjkKET6D8ft8rAjMyOpnXTqLzJoRbfcJ/\n" +
            "yCJrKP1u4HjpMe7H0Y2OokFcT45i5NrpiwDv6JQvgU2pxfFdTiH1qvHopHtM+fA+\n" +
            "z0x51osOz848Bn2rZpcFKqW0pnjkmrs8lbZWS0qVWY8yYIK4NayRKPasKcFKWGcC\n" +
            "AwEAAaMhMB8wHQYDVR0OBBYEFFu2j/scKOh6PUZjjtqkwDczjNx3MA0GCSqGSIb3\n" +
            "DQEBCwUAA4IBAQBMFXy0+GIxG1pB9qGJpsmjNm5FNd2mlGAfF/yMdGxI4w3WvSXF\n" +
            "nnjaQDNBQvwLH7FiGaAp8mzoeQsXN4O7vepRofg0eB0wDMbM3uARR7vEtMuGJyUx\n" +
            "5WrY8paQovzZXYQsZdRSqK3SjZs8LObYrjeXL/y9AZqMJk8L/2B6HvSn/q8D9EBe\n" +
            "6qjfFPxDjX/yWxw745MnF9ookcBHMfQfQrMUvYtveIB2QM5xSYie6TikHlIQ23Ja\n" +
            "UpoEQ4JBhjznpTKkZKa1I8nQzZVlvmevs/cd2S6DFolnepvNzbbpE+NuxW3dcyKx\n" +
            "XWt2bQ9EkJdHacanYJVkWZ+Gwl1jzGVEn16N\n" +
            "-----END CERTIFICATE-----\n";

    @Override
    public void onCreate() {
        Log.d("hyh", "MyApplication: onCreate: ");
        super.onCreate();
        initSSLTrustAll();

        //initSSLTrustSelfAssets();

        //initSSLTrustSelfString();
    }

    public static OkHttpClient getOkHttpClient(){
        if(mOkHttpClient == null){
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    /**
     * 初始化ssl并且信任所有证书（不推荐使用）
     */
    private void initSSLTrustAll(){
        mOkHttpClient = HttpsSSLHelper.createTrushAllClient();
    }

    /**
     * 初始化ssl并且只信任自签名证书，从assets导入证书
     */
    private void initSSLTrustSelfAssets(){
        mOkHttpClient = HttpsSSLHelper.createTrustCustomClient(getInputStreamFromAsset());
    }

    /**
     * 初始化ssl并且只信任自签名证书，以rfc字符串导入证书
     */
    private void initSSLTrustSelfString(){
        mOkHttpClient = HttpsSSLHelper.createTrustCustomClient(getInputStreamFromString());
    }

    private InputStream getInputStreamFromAsset(){
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("hyh_server.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 以rfc字符串形式导入证书流（推荐使用，因为不用常见assets文件占用包大小）
     * @return
     */
    private InputStream getInputStreamFromString(){
        return new Buffer()
                .writeUtf8(CER_HYH)
                .inputStream();
    }
}
