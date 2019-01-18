package com.share.lifetime.common.exception;

import java.util.Map;

import com.share.lifetime.common.util.JsonUtils;

public class APIException extends AbstractException {

    private static final long serialVersionUID = 1L;

    private final String url;

    private final Map<String, String> requestParameter;

    private final Map<String, String> responseParameter;

    private final int status;

    private final String reason;

    public APIException(String message, String url, Map<String, String> requestParameter,
        Map<String, String> responseParameter, int status, String reason) {
        super(String.format(
            "Request to api failed, url: %s, requestParameter: %s, responseParameter: %s, status code: %d, reason: %s, message: %s",
            url, JsonUtils.toJSON(requestParameter), JsonUtils.toJSON(responseParameter), status, reason, message));
        this.url = url;
        this.requestParameter = requestParameter;
        this.responseParameter = responseParameter;
        this.status = status;
        this.reason = reason;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getRequestParameter() {
        return requestParameter;
    }

    public Map<String, String> getResponseParameter() {
        return responseParameter;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

}
