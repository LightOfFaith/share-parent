package com.share.lifetime.common.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.share.lifetime.common.constant.SessionAttributeKeysConsts;

public class AbstractGUIController extends AbstractController {

    private static final String UNKNOWN = "Unknown";

    // private static final String AUTHENTICATION_FAIL_PREFIX = "认证失败：";

    private Object getAttributeInSession(String key) {
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            return null;
        }
        return request.getSession().getAttribute(key);
    }

    private String getCurrentUser(String key) {
        String user = (String)getAttributeInSession(key);
        return StringUtils.isBlank(user) ? UNKNOWN : user;
    }

    public String getCurrentLoginUserRealName() {
        return getCurrentUser(SessionAttributeKeysConsts.LOGIN_USER_REAL_NAME);
    }

    public String getCurrentLoginUserName() {
        return getCurrentUser(SessionAttributeKeysConsts.LOGIN_USER_NAME);
    }

    protected ModelAndView success(String viewName) {
        return getModelAndView(viewName, Collections.emptyMap());
    }

    protected ModelAndView success(String viewName, Map<String, Object> model) {
        return getModelAndView(viewName, model);
    }

    protected ModelAndView failure(String viewName) {
        return getModelAndView(viewName, Collections.emptyMap());
    }

    protected ModelAndView failure(String viewName, Map<String, Object> model) {
        return getModelAndView(viewName, model);
    }

    private ModelAndView getModelAndView(String viewName, Map<String, Object> model) {
        ModelAndView mv = new ModelAndView(viewName);
        Set<Entry<String, Object>> entrySet = model.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            mv.addObject(key, value);
        }
        return mv;
    }
}
