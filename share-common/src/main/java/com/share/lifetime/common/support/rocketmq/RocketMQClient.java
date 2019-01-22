package com.share.lifetime.common.support.rocketmq;

import java.util.Properties;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public interface RocketMQClient {

    /**
     * 根据自定义的属性创建一个普通的{@code DefaultMQProducer}实例，具体支持的属性详见{@link RocketMQKeyConst}
     *
     * @param properties
     *            构造{@code DefaultMQProducer}实例的属性
     * @return {@code DefaultMQProducer}实例，用于发送消息
     */
    DefaultMQProducer createProducer(final Properties properties);

    /**
     * 根据自定义的属性创建一个普通的{@code DefaultMQPushConsumer}实例，具体支持的属性详见{@link RocketMQKeyConst}
     *
     * @param properties
     *            构造{@code DefaultMQPushConsumer}实例的属性
     * @return {@code DefaultMQPushConsumer}实例，用于订阅Topic进行消息消费
     */
    DefaultMQPushConsumer createConsumer(final Properties properties);

    /**
     * 根据自定义的属性创建一个支持批量消费的{@code DefaultMQPushConsumer}实例，具体支持的属性详见{@link RocketMQKeyConst}
     *
     * @param properties
     *            构造{@code DefaultMQPushConsumer}实例的属性
     * @return {@code DefaultMQPushConsumer}实例，用于订阅Topic进行批量的消息消费
     */
    DefaultMQPushConsumer createBatchConsumer(final Properties properties);

}
