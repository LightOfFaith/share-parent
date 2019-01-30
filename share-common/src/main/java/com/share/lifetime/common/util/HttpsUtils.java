package com.share.lifetime.common.util;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.share.lifetime.common.constant.Consts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsUtils {

    /**
     * 请求，只请求一次，不做重试
     * 
     * @param domain
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param useCert
     *            是否使用证书
     * @return
     * @throws Exception
     */
    private String requestOnce(final String domain, String urlSuffix, String data, int connectTimeoutMs,
        int readTimeoutMs, boolean useCert) throws Exception {
        BasicHttpClientConnectionManager connManager;
        if (useCert) {
            // 证书
            char[] password = null;
            InputStream certStream = null;
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, password);

            // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, new String[] {"TLSv1"}, null, new DefaultHostnameVerifier());

            connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory).build(), null, null, null);
        } else {
            connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null, null, null);
        }

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

        String url = "https://" + domain + urlSuffix;
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig =
            RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", Consts.USER_AGENT + " ");
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");

    }

    @SuppressWarnings("unused")
    private String request(String domain, String urlSuffix, String data, int connectTimeoutMs, int readTimeoutMs,
        boolean useCert) throws Exception {
        Exception exception = null;
        long elapsedTimeMillis = 0;
        long startTimestampMs = DateUtils.getCurrentTimestampMs();
        boolean firstHasDnsErr = false;
        boolean firstHasConnectTimeout = false;
        boolean firstHasReadTimeout = false;
        if (domain == null) {
            throw new Exception("domain is empty or null");
        }
        try {
            String result = requestOnce(domain, urlSuffix, data, connectTimeoutMs, readTimeoutMs, useCert);
            elapsedTimeMillis = DateUtils.getCurrentTimestampMs() - startTimestampMs;
            return result;
        } catch (UnknownHostException ex) { // dns 解析错误，或域名不存在
            exception = ex;
            firstHasDnsErr = true;
            elapsedTimeMillis = DateUtils.getCurrentTimestampMs() - startTimestampMs;
            log.warn("UnknownHostException for domain {}", domain);
        } catch (ConnectTimeoutException ex) {
            exception = ex;
            firstHasConnectTimeout = true;
            elapsedTimeMillis = DateUtils.getCurrentTimestampMs() - startTimestampMs;
            log.warn("connect timeout happened for domain {}", domain);
        } catch (SocketTimeoutException ex) {
            exception = ex;
            firstHasReadTimeout = true;
            elapsedTimeMillis = DateUtils.getCurrentTimestampMs() - startTimestampMs;
            log.warn("timeout happened for domain {}", domain);
        } catch (Exception ex) {
            exception = ex;
            elapsedTimeMillis = DateUtils.getCurrentTimestampMs() - startTimestampMs;
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        throw exception;
    }

    /**
     * 可重试的，非双向认证的请求
     * 
     * @param urlSuffix
     * @param data
     * @return
     */
    public String requestWithoutCert(String domain, String urlSuffix, String data) throws Exception {
        return this.request(domain, urlSuffix, data, Consts.HTTP_CONNECT_TIMEOUT_MS, Consts.HTTP_READ_TIMEOUT_MS,
            false);
    }

    /**
     * 可重试的，非双向认证的请求
     * 
     * @param urlSuffix
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public String requestWithoutCert(String domain, String urlSuffix, String data, int connectTimeoutMs,
        int readTimeoutMs) throws Exception {
        return this.request(domain, urlSuffix, data, connectTimeoutMs, readTimeoutMs, false);
    }

    /**
     * 可重试的，双向认证的请求
     * 
     * @param urlSuffix
     * @param data
     * @return
     */
    public String requestWithCert(String domain, String urlSuffix, String data) throws Exception {
        return this.request(domain, urlSuffix, data, Consts.HTTP_CONNECT_TIMEOUT_MS, Consts.HTTP_READ_TIMEOUT_MS, true);
    }

    /**
     * 可重试的，双向认证的请求
     * 
     * @param urlSuffix
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public String requestWithCert(String domain, String urlSuffix, String data, int connectTimeoutMs, int readTimeoutMs)
        throws Exception {
        return this.request(domain, urlSuffix, data, connectTimeoutMs, readTimeoutMs, true);
    }

    public static void main(String[] args) throws Exception {

        HttpsUtils httpsUtils = new HttpsUtils();
        System.out.println(httpsUtils.requestWithoutCert("www.baidu.com", "/", "22"));
    }

}
