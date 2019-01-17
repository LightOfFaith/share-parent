package com.share.lifetime.common.aspect;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.share.lifetime.common.annotation.Audit;
import com.share.lifetime.common.annotation.AuditParam;
import com.share.lifetime.common.annotation.AuditTypeEnum;
import com.share.lifetime.common.constant.SessionAttributeKeysConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect to handle the audit log logic.
 * 
 * @author liaoxiang
 * @date 2019/01/18
 */
@Slf4j
@Aspect
public class AuditLogAspect {

    private static final String UNKNOWN = "Unkown";

    private static final String GUI_AUDIT_LOG_TEMPLATE =
        "GUI API:[%s] path:[%s] is called by User:[%s] with IP:[%s], result is %s.";

    private static final String REST_AUDIT_LOG_TEMPLATE = "REST API:[%s] path:[%s] is called by IP:[%s], result is %s.";

    @Around("@annotation(audit)")
    public Object logAuditInfo(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        addAuditParamsIfPossible(joinPoint);
        String methodName = getMethodName(joinPoint, audit);
        Boolean isSuccess = Boolean.FALSE;
        try {
            Object result = joinPoint.proceed();
            isSuccess = Boolean.TRUE;
            return result;
        } finally {
            logAudit(isSuccess, audit.type(), methodName);
            AuditInfoContext.reset();
        }
    }

    private String getMethodName(ProceedingJoinPoint joinPoint, Audit audit) {
        if (StringUtils.isNotBlank(audit.name())) {
            return audit.name();
        }
        return joinPoint.getSignature().getName();
    }

    private void addAuditParamsIfPossible(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Annotation[][] annotations = signature.getMethod().getParameterAnnotations();
        int i = 0;
        for (Object arg : joinPoint.getArgs()) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType() == AuditParam.class) {
                    AuditParam auditParamAnno = (AuditParam)annotation;
                    String value = auditParamAnno.value();
                    if (value != null) {
                        AuditInfoContext.put(value, arg == null ? null : arg.toString());
                    }
                }
            }
            i++;
        }
    }

    private void logAudit(Boolean isSuccess, AuditTypeEnum auditType, String methodName) {
        String content = null;
        switch (auditType) {
            case REST:
                content = getRESTRequstContent(methodName, isSuccess);
                break;
            case WEB:
            default:
                content = getWebRequestContent(methodName, isSuccess);
        }
        log.info(content);
    }

    protected String getWebRequestContent(String name, Boolean isSuccess) {
        String apiName = StringUtils.isNotBlank(name) ? name : "";
        return buildLogContent(String.format(GUI_AUDIT_LOG_TEMPLATE, apiName, getUri(), getUserName(), getIpAddress(),
            getResultValue(isSuccess)));
    }

    protected String getRESTRequstContent(String name, Boolean isSuccess) {
        String apiName = StringUtils.isNotBlank(name) ? name : "";
        return buildLogContent(
            String.format(REST_AUDIT_LOG_TEMPLATE, apiName, getUri(), getIpAddress(), getResultValue(isSuccess)));
    }

    private String buildLogContent(String initValue) {
        StringBuilder stringBuilder = new StringBuilder(initValue);
        // append additional context info if possible
        Map<String, String> auditInfoMap = AuditInfoContext.currentAuditInfo();
        if (auditInfoMap != null && auditInfoMap.size() > 0) {
            stringBuilder.append(" Context info:").append(auditInfoMap).append(".");
        }

        return stringBuilder.toString();
    }

    private String getUri() {
        HttpServletRequest request = getRequestFromContext();
        if (request == null) {
            return StringUtils.EMPTY;
        }

        return request.getRequestURI();
    }

    private String getIpAddress() {
        HttpServletRequest request = getRequestFromContext();
        if (request == null) {
            return StringUtils.EMPTY;
        }

        String ip = request.getHeader("X-FORWARDED-FOR");
        return StringUtils.isBlank(ip) ? request.getRemoteAddr() : ip;
    }

    private String getUserName() {
        HttpServletRequest request = getRequestFromContext();
        if (request == null) {
            return UNKNOWN;
        }

        String loginUser = (String)request.getSession().getAttribute(SessionAttributeKeysConsts.LOGIN_USER_NAME);
        return StringUtils.isBlank(loginUser) ? UNKNOWN : loginUser;
    }

    private String getResultValue(boolean isSuccess) {
        return isSuccess ? "success" : "failed";
    }

    private HttpServletRequest getRequestFromContext() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
