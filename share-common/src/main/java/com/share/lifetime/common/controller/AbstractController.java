package com.share.lifetime.common.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import com.share.lifetime.common.util.WebUtils;

public abstract class AbstractController {

    protected static final String BAD_REQ_MSG_PREFIX = "Invalid request.";

    protected static final String INVALID_REQUEST_MSG = BAD_REQ_MSG_PREFIX + " Parameter: {%s} %s";

    protected static final String MISSING_REQUEST_MSG = BAD_REQ_MSG_PREFIX + " Missing parameter: {%s}";

    protected String getClientIp(HttpServletRequest request) {
        return WebUtils.getAddr(request);
    }

    protected String getStreamAsString(InputStream stream, String charset) throws IOException {
        return WebUtils.getStreamAsString(stream, charset);
    }

}
