package com.share.lifetime.common.support.rocketmq;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.aliyun.openservices.ons.api.ONSFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQFactory {

    /**
     * 工厂实现类实例. 单例模式.
     */
    private static RocketMQClient rocketMQClient = null;

    static {
        try {
            // ons client 优先加载
            Class<?> factoryClass = ONSFactory.class.getClassLoader()
                .loadClass("com.share.lifetime.common.support.rocketmq.RocketMQClientImpl");
            rocketMQClient = (RocketMQClient)factoryClass.newInstance();
        } catch (Throwable e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 创建Producer
     * 
     * <p>
     * 返回创建的{@link DefaultMQProducer}实例是线程安全, 可复用, 发送各个主题. 一般情况下, 一个进程中构建一个实例足够满足发送消息的需求.
     * </p>
     *
     * <p>
     * 示例代码:
     * 
     * <pre>
     *        Properties props = ...;
     *        // 设置必要的属性
     *        Producer producer = RocketMQFactory.createProducer(props);
     *        producer.start();
     *
     *        //producer之后可以当成单例使用
     *
     *        // 发送消息
     *        Message msg = ...;
     *        SendResult result = produer.send(msg);
     *
     *        // 应用程序关闭退出时
     *        producer.shutdown();
     * </pre>
     * </p>
     *
     * @param properties
     *            Producer的配置参数
     * @return {@link DefaultMQProducer} 实例
     */
    public static DefaultMQProducer createProducer(final Properties properties) {
        return rocketMQClient.createProducer(properties);
    }

    /**
     * 创建Consumer
     * 
     * 
     * @param properties
     *            Consumer的配置参数
     * @return {@code DefaultMQPushConsumer} 实例
     */
    public static DefaultMQPushConsumer createConsumer(final Properties properties) {
        return rocketMQClient.createConsumer(properties);
    }

}
