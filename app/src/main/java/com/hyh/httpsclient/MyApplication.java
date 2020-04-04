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
            "MIIDOzCCAiOgAwIBAgIEXZVqZDANBgkqhkiG9w0BAQsFADBOMQswCQYDVQQGEwJo\n" +
            "aDELMAkGA1UECBMCaGgxCzAJBgNVBAcTAmhoMQswCQYDVQQKEwJoaDELMAkGA1UE\n" +
            "CxMCaGgxCzAJBgNVBAMTAmhoMB4XDTIwMDQwNDEwMjEyNFoXDTMwMDIxMTEwMjEy\n" +
            "NFowTjELMAkGA1UEBhMCaGgxCzAJBgNVBAgTAmhoMQswCQYDVQQHEwJoaDELMAkG\n" +
            "A1UEChMCaGgxCzAJBgNVBAsTAmhoMQswCQYDVQQDEwJoaDCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBAKkCLt6IMQLDkCtKvmBkmw0sNovuiiduse64bFqW\n" +
            "o4HD9CTUNBDxKqY6TWi5CBaqIzINvveQK5h2vM5or60eVq0x5QSplOfru7yHPlpw\n" +
            "BdnpZ7ffISqUAHggpEmPf4nCKabF7D9HfTn3ZqgI/LelnxAOxzxyFfDwah6h3Xph\n" +
            "9ySC6uHJxrjbgsR8C84rnqw540D7MY4HE/rhDuRzTP1LH9/70LDHeUsCkMfRQ4A6\n" +
            "Z4CTDzYP7ATx4P35WoejihitEdBMquLZ2XYNDklRvGWJ2XkS2cteJUWzEdYVdnxV\n" +
            "fjYAiwu9mORtPwX9YiyoRdAnOSpiya/PnPjBlFOJZSsY5o0CAwEAAaMhMB8wHQYD\n" +
            "VR0OBBYEFMzRbwBzE5NrSFXRCNXgfddfLh/fMA0GCSqGSIb3DQEBCwUAA4IBAQAP\n" +
            "3/v7oZPx4++176BtEGhTy/xchMI1gqfxiM54j1bvjFN+90VQ3OdFStvMQWKP1tlm\n" +
            "EJHfL0ayLcVUsqiWRlykroWo9kEE9sOhbnZ8l9PGbPwbK5bYDXS80v+fx1e4mFxC\n" +
            "wrU/KLPqnG1GIdB8q+y+B8PgB8zUl5oLgaqN86jg+hc2kv5vD2n3RXvopZCO0/Mf\n" +
            "qODZ08gSBZeCDOXwgn1PpRijBxp7n4VG3Pb73L+A7dmA9aXGhvXDMIevDhLCc7hw\n" +
            "xBw8RmCyioDafdY7l4CVpvue1R6r4GJTPcQipJ2kEM4FagVwomtQvQRSoxr2ZTnY\n" +
            "6bIW+OcdKMaquNMgWsIA\n" +
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
        mOkHttpClient = HttpsSSLHelper.createTrustCustomClient(getInputStreamFromString(CER_HYH));
    }

    private InputStream getInputStreamFromAsset(){
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("my_server.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 以rfc字符串形式导入证书流（推荐使用，因为不用常见assets文件占用包大小）
     * @return
     */
    private InputStream getInputStreamFromString(String rfc){
        return new Buffer()
                .writeUtf8(rfc)
                .inputStream();
    }
}
