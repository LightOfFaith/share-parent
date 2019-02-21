package com.share.lifetime.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.share.lifetime.common.constant.Consts;
import com.share.lifetime.common.constant.HttpHeaderConsts;
import com.share.lifetime.common.constant.HttpMethodConsts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebUtils {

    private static boolean ignoreSSLCheck = true; // 忽略SSL检查
    private static boolean ignoreHostCheck = true; // 忽略HOST检查

    public static class TrustAllTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
    }

    public static void setIgnoreSSLCheck(boolean ignoreSSLCheck) {
        WebUtils.ignoreSSLCheck = ignoreSSLCheck;
    }

    public static void setIgnoreHostCheck(boolean ignoreHostCheck) {
        WebUtils.ignoreHostCheck = ignoreHostCheck;
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 响应字符串
     */
    public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
        throws IOException {
        return doPost(url, params, Consts.DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param charset
     *            字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     */
    public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
        int readTimeout) throws IOException {
        return doPost(url, params, charset, connectTimeout, readTimeout, null, null);
    }

    public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
        int readTimeout, Map<String, String> headerMap, Proxy proxy) throws IOException {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap, proxy);
    }

    public static String doPost(String url, String apiBody, String charset, int connectTimeout, int readTimeout,
        Map<String, String> headerMap) throws IOException {
        String ctype = "text/plain;charset=" + charset;
        byte[] content = apiBody.getBytes(charset);
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap, null);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url
     *            请求地址
     * @param ctype
     *            请求类型
     * @param content
     *            请求字节数组
     * @return 响应字符串
     */
    public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
        throws IOException {
        return _doPost(url, ctype, content, connectTimeout, readTimeout, null, null);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url
     *            请求地址
     * @param ctype
     *            请求类型
     * @param content
     *            请求字节数组
     * @param headerMap
     *            请求头部参数
     * @return 响应字符串
     */
    public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout,
        Map<String, String> headerMap, Proxy proxy) throws IOException {
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap, proxy);
    }

    private static String _doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout,
        Map<String, String> headerMap, Proxy proxy) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = getConnection(new URL(url), HttpMethodConsts.POST, ctype, headerMap, proxy);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            out = conn.getOutputStream();
            out.write(content);
            rsp = getResponseAsString(conn);
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    /**
     * 执行请求 content_type: aplication/json
     *
     * @param url
     * @param params
     * @param charset
     * @param connectTimeout
     * @param readTimeout
     * @return
     * @throws IOException
     */
    public static String doPostWithJson(String url, Map<String, Object> params, String charset, int connectTimeout,
        int readTimeout) throws IOException {
        String ctype = "application/json;charset=" + charset;
        byte[] content = {};

        String body = JsonUtils.objectToJson(params);
        if (body != null) {
            content = body.getBytes(charset);
        }
        return _doPost(url, ctype, content, connectTimeout, readTimeout, null, null);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 响应字符串
     */
    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, Consts.DEFAULT_CHARSET);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param charset
     *            字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     */
    public static String doGet(String url, Map<String, String> params, String charset) throws IOException {
        HttpURLConnection conn = null;
        String rsp = null;

        try {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(params, charset);
            conn = getConnection(buildGetUrl(url, query), HttpMethodConsts.GET, ctype, null, null);
            rsp = getResponseAsString(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap,
        Proxy proxy) throws IOException {
        HttpURLConnection conn = null;
        if (proxy == null) {
            conn = (HttpURLConnection)url.openConnection();
        } else {
            conn = (HttpURLConnection)url.openConnection(proxy);
        }
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection connHttps = (HttpsURLConnection)conn;
            if (ignoreSSLCheck) {
                try {
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null, new TrustManager[] {new TrustAllTrustManager()}, new SecureRandom());
                    connHttps.setSSLSocketFactory(ctx.getSocketFactory());
                    connHttps.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                } catch (Exception e) {
                    throw new IOException(e.toString());
                }
            } else {
                if (ignoreHostCheck) {
                    connHttps.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }
            }
            conn = connHttps;
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // Host 请求头指明了服务器的域名（对于虚拟主机来说），以及（可选的）服务器监听的TCP端口号。
        // 如果没有给定端口号，会自动使用被请求服务的默认端口（比如请求一个HTTP的URL会自动使用80端口）。
        if (headerMap != null && headerMap.get(Consts.HTTP_HOST) != null) {
            conn.setRequestProperty("Host", headerMap.get(Consts.HTTP_HOST));
        } else {
            conn.setRequestProperty("Host", url.getHost());
        }
        // Accept 请求头用来告知客户端可以处理的内容类型，这种内容类型用MIME类型来表示。借助内容协商机制,
        // 服务器可以从诸多备选项中选择一项进行应用，并使用 Content-Type 应答头通知客户端它的选择。
        // 浏览器会基于请求的上下文来为这个请求头设置合适的值，比如获取一个CSS层叠样式表时值与获取图片、视频或脚本文件时的值是不同的。
        conn.setRequestProperty(HttpHeaderConsts.ACCEPT, "text/xml");
        // User-Agent 首部包含了一个特征字符串，用来让网络协议的对端来识别发起请求的用户代理软件的应用类型、操作系统、软件开发商以及版本号。
        conn.setRequestProperty(HttpHeaderConsts.USER_AGENT, Consts.USER_AGENT);
        // Content-Type 实体头部用于指示资源的MIME类型 media type 。
        // 在响应中，Content-Type标头告诉客户端实际返回的内容的内容类型。浏览器会在某些情况下进行MIME查找，并不一定遵循此标题的值;
        // 为了防止这种行为，可以将标题 X-Content-Type-Options 设置为 nosniff。
        conn.setRequestProperty(HttpHeaderConsts.CONTENT_TYPE, ctype);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (!Consts.HTTP_HOST.equals(entry.getKey())) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        return conn;
    }

    private static URL buildGetUrl(String url, String query) throws IOException {
        if (StringUtils.isEmpty(query)) {
            return new URL(url);
        }

        return new URL(buildRequestUrl(url, query));
    }

    public static String buildRequestUrl(String url, String... queries) {
        if (queries == null || queries.length == 0) {
            return url;
        }

        StringBuilder newUrl = new StringBuilder(url);
        boolean hasQuery = url.contains("?");
        boolean hasPrepend = url.endsWith("?") || url.endsWith("&");

        for (String query : queries) {
            if (!StringUtils.isEmpty(query)) {
                if (!hasPrepend) {
                    if (hasQuery) {
                        newUrl.append("&");
                    } else {
                        newUrl.append("?");
                        hasQuery = true;
                    }
                }
                newUrl.append(query);
                hasPrepend = false;
            }
        }
        return newUrl.toString();
    }

    /**
     * 获取客户端实际ip
     * 
     * @param request
     * @return
     */
    public static String getAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");

        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (org.apache.commons.lang3.StringUtils.isNoneEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }

    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = Consts.DEFAULT_CHARSET;

        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    public static String getStreamAsString(InputStream stream) throws IOException {
        return getStreamAsString(stream, null);
    }

    public static String getStreamAsString(InputStream inputStream, String charset) throws IOException {
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        StringWriter writer = null;
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(charset)) {
                streamReader = new InputStreamReader(inputStream);
            } else {
                streamReader = new InputStreamReader(inputStream, charset);
            }
            reader = new BufferedReader(streamReader);
            writer = new StringWriter();
            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            return writer.toString();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (streamReader != null) {
                    streamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     * 
     * @param value
     *            参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value) {
        return decode(value, Consts.DEFAULT_CHARSET);
    }

    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     * 
     * @param value
     *            参数值
     * @return 编码后的参数值
     */
    public static String encode(String value) {
        return encode(value, Consts.DEFAULT_CHARSET);
    }

    /**
     * 使用指定的字符集反编码请求参数值。
     * 
     * @param value
     *            参数值
     * @param charset
     *            字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 使用指定的字符集编码请求参数值。
     * 
     * @param value
     *            参数值
     * @param charset
     *            字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 从URL中提取所有的参数。
     * 
     * @param query
     *            URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }

}
