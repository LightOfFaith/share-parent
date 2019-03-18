package com.share.lifetime.common.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import com.share.lifetime.common.DynamicDataSourceHolder;
import com.share.lifetime.common.annotation.DynamicDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/18
 */
@Slf4j
@Aspect
public class DynamicDataSourceAspect {

    @Before(value = "@annotation(dynamicDataSource)")
    public void doBefore(JoinPoint joinPoint, DynamicDataSource dynamicDataSource) {
        String value = dynamicDataSource.value();
        String methodName = getMethodName(joinPoint);
        if (value != null) {
            DynamicDataSourceHolder.putDataSourceKey(value);
        }
        log.info("{}.{} dynamic switch datasource:{}", joinPoint.getTarget().getClass().getName(), methodName, value);
    }

    @After(value = "@annotation(dynamicDataSource)")
    public void doAfter(JoinPoint joinPoint, DynamicDataSource dynamicDataSource) {
        DynamicDataSourceHolder.markDBClear();
        String value = dynamicDataSource.value();
        String methodName = getMethodName(joinPoint);
        log.info("{}.{} dynamic datasource remove datasource:{}", joinPoint.getTarget().getClass().getName(),
            methodName, value);
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method;
    }

    private String getMethodName(JoinPoint joinPoint) {
        return getMethod(joinPoint).getName();
    }

}
