package com.share.lifetime.common.support.rocketmq;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class RocketMQClientImpl implements RocketMQClient {

    @Override
    public DefaultMQProducer createProducer(Properties properties) {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        String producerGroup = properties.getProperty(RocketMQKeyConst.ProducerId, "__ONS_PRODUCER_DEFAULT_GROUP");
        defaultMQProducer.setProducerGroup(producerGroup);

        boolean isVipChannelEnabled =
            Boolean.parseBoolean(properties.getProperty(RocketMQKeyConst.isVipChannelEnabled, "false"));
        defaultMQProducer.setVipChannelEnabled(isVipChannelEnabled);

        if (properties.containsKey(RocketMQKeyConst.SendMsgTimeoutMillis)) {
            defaultMQProducer
                .setSendMsgTimeout(Integer.valueOf(properties.get(RocketMQKeyConst.SendMsgTimeoutMillis).toString()));
        } else {
            defaultMQProducer.setSendMsgTimeout(5000);
        }

        String instanceName = properties.getProperty(RocketMQKeyConst.InstanceName, buildIntanceName());
        defaultMQProducer.setInstanceName(instanceName);
        defaultMQProducer.setNamesrvAddr(properties.getProperty(RocketMQKeyConst.NAMESRV_ADDR));
        // 消息最大大小4M
        defaultMQProducer.setMaxMessageSize(1024 * 1024 * 4);

        return defaultMQProducer;
    }

    @Override
    public DefaultMQPushConsumer createConsumer(Properties properties) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();

        String consumerGroup = properties.getProperty(RocketMQKeyConst.ConsumerId);
        if (null == consumerGroup) {
            throw new NullPointerException("ConsumerId property is null");
        }

        String maxReconsumeTimes = properties.getProperty(RocketMQKeyConst.MaxReconsumeTimes);
        if (!StringUtils.isBlank(maxReconsumeTimes)) {
            try {
                defaultMQPushConsumer.setMaxReconsumeTimes(Integer.parseInt(maxReconsumeTimes));
            } catch (NumberFormatException ignored) {
            }
        }

        String consumeTimeout = properties.getProperty(RocketMQKeyConst.ConsumeTimeout);
        if (!StringUtils.isBlank(consumeTimeout)) {
            try {
                defaultMQPushConsumer.setConsumeTimeout(Integer.parseInt(consumeTimeout));
            } catch (NumberFormatException ignored) {
            }
        }

        boolean isVipChannelEnabled =
            Boolean.parseBoolean(properties.getProperty(RocketMQKeyConst.isVipChannelEnabled, "false"));
        defaultMQPushConsumer.setVipChannelEnabled(isVipChannelEnabled);

        defaultMQPushConsumer.setConsumerGroup(consumerGroup);
        String instanceName = properties.getProperty(RocketMQKeyConst.InstanceName, buildIntanceName());
        defaultMQPushConsumer.setInstanceName(instanceName);
        defaultMQPushConsumer.setNamesrvAddr(properties.getProperty(RocketMQKeyConst.NAMESRV_ADDR));

        String consumeThreadNums = properties.getProperty(RocketMQKeyConst.ConsumeThreadNums);
        if (!StringUtils.isBlank(consumeThreadNums)) {
            defaultMQPushConsumer.setConsumeThreadMin(Integer.valueOf(consumeThreadNums));
            defaultMQPushConsumer.setConsumeThreadMax(Integer.valueOf(consumeThreadNums));
        }

        boolean postSubscriptionWhenPull =
            Boolean.parseBoolean(properties.getProperty(RocketMQKeyConst.PostSubscriptionWhenPull, "false"));
        defaultMQPushConsumer.setPostSubscriptionWhenPull(postSubscriptionWhenPull);

        String messageModel = properties.getProperty(RocketMQKeyConst.MessageModel, RocketMQValueConst.CLUSTERING);
        defaultMQPushConsumer.setMessageModel(MessageModel.valueOf(messageModel));

        return defaultMQPushConsumer;
    }

    private String buildIntanceName() {
        return Integer.toString(getPid())//
            + "#" + System.nanoTime();
    }

    private int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

}
