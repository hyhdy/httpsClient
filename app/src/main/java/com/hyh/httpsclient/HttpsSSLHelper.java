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
import java.util.Collection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * created by curdyhuang on 2020-01-29
 */
public class HttpsSSLHelper {

    /**
     * 创建信任所有证书的SSLSocketFactory
     * @return
     */
    @Nullable
    public static SSLSocketFactory createTrustAllSSLSocketFactory() {
        SSLSocketFactory sslFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            sslFactory = sc.getSocketFactory();
        } catch (Exception e) {
            Log.d("hyh", "HttpsSSLHelper: createTrustAllSSLSocketFactory: error: "+e!=null?e.getMessage():"");
        }

        return sslFactory;
    }

    /**
     * 创建只信任指定证书的SSLSocketFactory（可以用于信任自签名证书）
     * @param inputStreams：证书输入流
     * @return
     */
    @Nullable
    public static SSLSocketFactory createTrustCustomSSLSocketFactory(InputStream ... inputStreams) {
        SSLContext sslContext = null;
        try
        {
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

            sslContext = SSLContext.getInstance("TLS");
            if(sslContext!=null) {
                sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
                return sslContext.getSocketFactory();
            }
        } catch (Exception e) {
            Log.d("hyh", "HttpsSSLHelper: createTrustCustomSSLSocketFactory: error: "+e!=null?e.getMessage():"");
        }

        return null;
    }
}
