package com.share.lifetime.common.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.share.lifetime.common.DynamicDataSourceHolder;

/**
 * 
 * @author liaoxiang
 * @date 2019/03/11
 * 
 * 
 * 
 * 
 */
public class DataSourceAspect {

    private static final String[] DEFAULT_SLAVE_METHOD_PREFIX =
        new String[] {"query", "find", "get", "select", "count", "list"};

    public void doBefore(JoinPoint joinPoint) {
        String methodName = getMethodName(joinPoint);
        boolean isSlave = isSlave(methodName);
        if (isSlave) {
            DynamicDataSourceHolder.markDBSlave();
        } else {
            DynamicDataSourceHolder.markDBMaster();
        }
    }

    public void doAfter(JoinPoint joinPoint) {
        DynamicDataSourceHolder.markDBClear();
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method;
    }

    private String getMethodName(JoinPoint joinPoint) {
        return getMethod(joinPoint).getName();
    }

    private boolean isSlave(String methodName) {
        for (String prefix : DEFAULT_SLAVE_METHOD_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
