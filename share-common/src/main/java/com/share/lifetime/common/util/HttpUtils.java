package com.share.lifetime.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.google.common.io.CharStreams;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/18
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    private static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 5000;

    static class InnerStaticClass {
        private static PoolingHttpClientConnectionManager connMgr;
        private static RequestConfig requestConfig;

        static {
            // 设置连接池
            connMgr = new PoolingHttpClientConnectionManager();
            // 设置连接池大小
            connMgr.setMaxTotal(100);
            connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
            connMgr.setValidateAfterInactivity(DEFAULT_TIMEOUT_IN_MILLISECONDS);

            RequestConfig.Builder configBuilder = RequestConfig.custom();
            // 设置连接超时
            configBuilder.setConnectTimeout(DEFAULT_TIMEOUT_IN_MILLISECONDS);
            // 设置读取超时
            configBuilder.setSocketTimeout(DEFAULT_TIMEOUT_IN_MILLISECONDS);
            // 设置从连接池获取连接实例的超时
            configBuilder.setConnectionRequestTimeout(DEFAULT_TIMEOUT_IN_MILLISECONDS);
            // 在提交请求之前 测试连接是否可用
            // configBuilder.setStaleConnectionCheckEnabled(true);

            requestConfig = configBuilder.build();
        }
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl
     * @param json
     *            json对象
     * @return
     */
    public static CloseableHttpResponse doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(InnerStaticClass.requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("do post meet exception", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error("do post meet exception", e);
                }
            }
        }
        return response;
    }

    /**
     * ping the url, return true if ping ok, false otherwise
     */
    public static boolean pingUrl(String address) {
        try {
            URL urlObj = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(DEFAULT_TIMEOUT_IN_MILLISECONDS);
            connection.setReadTimeout(DEFAULT_TIMEOUT_IN_MILLISECONDS);
            int statusCode = connection.getResponseCode();
            cleanUpConnection(connection);
            return (200 <= statusCode && statusCode <= 399);
        } catch (Throwable ignore) {
        }
        return false;
    }

    /**
     * according to https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html, we should clean up
     * the connection by reading the response body so that the connection could be reused.
     */
    private static void cleanUpConnection(HttpURLConnection conn) {
        InputStreamReader isr = null;
        InputStreamReader esr = null;
        try {
            isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            CharStreams.toString(isr);
        } catch (IOException e) {
            InputStream errorStream = conn.getErrorStream();

            if (errorStream != null) {
                esr = new InputStreamReader(errorStream, StandardCharsets.UTF_8);
                try {
                    CharStreams.toString(esr);
                } catch (IOException ioe) {
                    log.error(ExceptionUtils.getStackTrace(ioe));
                }
            }
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
            }

            if (esr != null) {
                try {
                    esr.close();
                } catch (IOException ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
            }
        }
    }

}
