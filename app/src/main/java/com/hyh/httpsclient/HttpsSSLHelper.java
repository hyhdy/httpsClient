package com.hyh.httpsclient;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * created by curdyhuang on 2020-01-29
 */
public class HttpsSSLHelper {
    /**
     * 创建信任指定证书的OkHttpClient（可以用于信任自签名证书）
     * @return
     */
    public static OkHttpClient createTrustCustomClient(InputStream inputStream){
        return createSSLClient(createTrustCustomTrustManager(inputStream));
    }

    /**
     * 创建信任指定证书的OkHttpClient（可以用于信任自签名证书）
     * @return
     */
    public static OkHttpClient createTrustCustomClient(InputStream ... inputStreams){
        return createSSLClient(createTrustCustomTrustManager(inputStreams));
    }

    /**
     * 创建信任所有证书的OkHttpClient
     * @return
     */
    public static OkHttpClient createTrushAllClient(){
        return createSSLClient(createTrustAllTrustManager());
    }

    /**
     * 创建只信任指定证书的TrustManager
     * @param inputStreams：证书输入流
     * @return
     */
    @Nullable
    private static X509TrustManager createTrustCustomTrustManager(InputStream ... inputStreams) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            int index = 0;
            for (InputStream inputStream : inputStreams) {
                Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
                if(certificates.isEmpty()){
                    continue;
                }
                //将证书放入keystore中
                for (Certificate certificate : certificates) {
                    String certificateAlias = String.format("%s%d","ca",index++);
                    keyStore.setCertificateEntry(certificateAlias, certificate);
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.d("hyh", "HttpsSSLHelper: createTrustCustomSSLSocketFactory: io error");
                }
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建只信任指定证书的TrustManager
     * @param inputStream：证书输入流
     * @return
     */
    @Nullable
    private static X509TrustManager createTrustCustomTrustManager(InputStream inputStream) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            //将证书放入keystore中
            String certificateAlias = "ca";
            keyStore.setCertificateEntry(certificateAlias, certificate);
            if (inputStream != null) {
                inputStream.close();
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建信任所有证书的TrustManager
     * @return
     */
    private static X509TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static OkHttpClient createSSLClient(X509TrustManager x509TrustManager){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory(x509TrustManager),x509TrustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier());
        return builder.build();
    }

    private static SSLSocketFactory createSSLSocketFactory(TrustManager trustManager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    //实现信任所有域名的HostnameVerifier接口
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //域名校验，默认都通过
            return true;
        }
    }
}

