package com.share.lifetime.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.share.lifetime.common.constant.Consts;
import com.share.lifetime.common.constant.HttpHeaderConsts;
import com.share.lifetime.common.constant.SignTypeEnum;
import com.share.lifetime.common.constant.SystemHeaderConsts;

/**
 * 签名工具
 * 
 * @author liaoxiang
 * @date 2019/01/31
 */
public class SignUtils {
    /**
     * 计算签名
     *
     * @param secret
     *            APP密钥
     * @param method
     *            HttpMethod
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     *            自定义参与签名Header前缀
     * @return 签名后的字符串
     */
    public static String sign(String secret, String method, String path, Map<String, String> headers,
        Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList) {
        try {
            Mac hmacSha256 = Mac.getInstance(SignTypeEnum.HMACSHA256.getValue());
            byte[] keyBytes = secret.getBytes(Consts.DEFAULT_CHARSET);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, SignTypeEnum.HMACSHA256.getValue()));

            return new String(Base64.encodeBase64(
                hmacSha256.doFinal(buildStringToSign(method, path, headers, querys, bodys, signHeaderPrefixList)
                    .getBytes(Consts.DEFAULT_CHARSET))),
                Consts.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建待签名字符串
     * 
     * @param method
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @return
     */
    private static String buildStringToSign(String method, String path, Map<String, String> headers,
        Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        sb.append(method.toUpperCase()).append(Consts.LF);
        if (null != headers) {
            if (null != headers.get(HttpHeaderConsts.ACCEPT)) {
                sb.append(headers.get(HttpHeaderConsts.ACCEPT));
            }
            sb.append(Consts.LF);
            if (null != headers.get(HttpHeaderConsts.CONTENT_MD5)) {
                sb.append(headers.get(HttpHeaderConsts.CONTENT_MD5));
            }
            sb.append(Consts.LF);
            if (null != headers.get(HttpHeaderConsts.CONTENT_TYPE)) {
                sb.append(headers.get(HttpHeaderConsts.CONTENT_TYPE));
            }
            sb.append(Consts.LF);
            if (null != headers.get(HttpHeaderConsts.DATE)) {
                sb.append(headers.get(HttpHeaderConsts.DATE));
            }
        }
        sb.append(Consts.LF);
        sb.append(buildHeaders(headers, signHeaderPrefixList));
        sb.append(buildResource(path, querys, bodys));

        return sb.toString();
    }

    /**
     * 构建待签名Path+Query+BODY
     *
     * @param path
     * @param querys
     * @param bodys
     * @return 待签名
     */
    private static String buildResource(String path, Map<String, String> querys, Map<String, String> bodys) {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isBlank(path)) {
            sb.append(path);
        }
        Map<String, String> sortMap = new TreeMap<String, String>();
        if (null != querys) {
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (!StringUtils.isBlank(query.getKey())) {
                    sortMap.put(query.getKey(), query.getValue());
                }
            }
        }

        if (null != bodys) {
            for (Map.Entry<String, String> body : bodys.entrySet()) {
                if (!StringUtils.isBlank(body.getKey())) {
                    sortMap.put(body.getKey(), body.getValue());
                }
            }
        }

        StringBuilder sbParam = new StringBuilder();
        for (Map.Entry<String, String> item : sortMap.entrySet()) {
            if (!StringUtils.isBlank(item.getKey())) {
                if (0 < sbParam.length()) {
                    sbParam.append(Consts.SPE3);
                }
                sbParam.append(item.getKey());
                if (!StringUtils.isBlank(item.getValue())) {
                    sbParam.append(Consts.SPE4).append(item.getValue());
                }
            }
        }
        if (0 < sbParam.length()) {
            sb.append(Consts.SPE5);
            sb.append(sbParam);
        }

        return sb.toString();
    }

    /**
     * 构建待签名Http头
     *
     * @param headers
     *            请求中所有的Http头
     * @param signHeaderPrefixList
     *            自定义参与签名Header前缀
     * @return 待签名Http头
     */
    private static String buildHeaders(Map<String, String> headers, List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        if (null != signHeaderPrefixList) {
            signHeaderPrefixList.remove(SystemHeaderConsts.X_CA_SIGNATURE);
            signHeaderPrefixList.remove(HttpHeaderConsts.ACCEPT);
            signHeaderPrefixList.remove(HttpHeaderConsts.CONTENT_MD5);
            signHeaderPrefixList.remove(HttpHeaderConsts.CONTENT_TYPE);
            signHeaderPrefixList.remove(HttpHeaderConsts.DATE);
            Collections.sort(signHeaderPrefixList);
            if (null != headers) {
                Map<String, String> sortMap = new TreeMap<String, String>();
                sortMap.putAll(headers);
                StringBuilder signHeadersStringBuilder = new StringBuilder();
                for (Map.Entry<String, String> header : sortMap.entrySet()) {
                    if (isHeaderToSign(header.getKey(), signHeaderPrefixList)) {
                        sb.append(header.getKey());
                        sb.append(Consts.SPE2);
                        if (!StringUtils.isBlank(header.getValue())) {
                            sb.append(header.getValue());
                        }
                        sb.append(Consts.LF);
                        if (0 < signHeadersStringBuilder.length()) {
                            signHeadersStringBuilder.append(Consts.SPE1);
                        }
                        signHeadersStringBuilder.append(header.getKey());
                    }
                }
                headers.put(SystemHeaderConsts.X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
            }
        }

        return sb.toString();
    }

    /**
     * Http头是否参与签名 return
     */
    private static boolean isHeaderToSign(String headerName, List<String> signHeaderPrefixList) {
        if (StringUtils.isBlank(headerName)) {
            return false;
        }

        if (headerName.startsWith(Consts.CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
            return true;
        }

        if (null != signHeaderPrefixList) {
            for (String signHeaderPrefix : signHeaderPrefixList) {
                if (headerName.equalsIgnoreCase(signHeaderPrefix)) {
                    return true;
                }
            }
        }

        return false;
    }
}
