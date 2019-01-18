package com.share.lifetime.common.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import com.share.lifetime.common.DataSourceHolder;
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
        Method method = getMethod(joinPoint);
        String value = dynamicDataSource.value();
        String description = dynamicDataSource.description();
        if (value != null) {
            DataSourceHolder.setDataSource(value);
        }
        log.info("className:{},methodName:{},dynamic switch datasource:{},description:{}",
            joinPoint.getTarget().getClass().getName(), method.getName(), value, description);
    }

    @After(value = "@annotation(dynamicDataSource)")
    public void doAfter(JoinPoint joinPoint, DynamicDataSource dynamicDataSource) {
        Method method = getMethod(joinPoint);
        String value = dynamicDataSource.value();
        String description = dynamicDataSource.description();
        DataSourceHolder.remove();
        log.info("className:{},methodName:{},dynamic datasource remove datasource:{},description:{} ",
            joinPoint.getTarget().getClass().getName(), method.getName(), value, description);
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method;
    }

}
