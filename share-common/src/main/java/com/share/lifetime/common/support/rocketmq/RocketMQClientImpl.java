package com.share.lifetime.common.support.rocketmq;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class RocketMQClientImpl implements RocketMQClient {

    private final static int MAX_CACHED_MESSAGE_SIZE_IN_MIB = 2048;
    private final static int MIN_CACHED_MESSAGE_SIZE_IN_MIB = 16;
    private final static int MAX_CACHED_MESSAGE_AMOUNT = 50000;
    private final static int MIN_CACHED_MESSAGE_AMOUNT = 100;
    private int maxCachedMessageSizeInMiB = 512; // 默认值限制为512MiB
    private int maxCachedMessageAmount = 5000; // 默认值限制为5000条
    private final static int MAX_BATCH_SIZE = 32;
    private final static int MIN_BATCH_SIZE = 1;

    @Override
    public DefaultMQProducer createProducer(Properties properties) {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        setDefaultMQProducerAttributes(properties, defaultMQProducer);
        return null;
    }

    @Override
    public DefaultMQPushConsumer createConsumer(Properties properties) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        setDefaultMQPushConsumerAttributes(properties, defaultMQPushConsumer);
        return null;
    }

    @Override
    public DefaultMQPushConsumer createBatchConsumer(Properties properties) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        setDefaultMQPushConsumerAttributes(properties, defaultMQPushConsumer);
        String consumeBatchSize = properties.getProperty(RocketMQKeyConst.ConsumeMessageBatchMaxSize);
        if (!StringUtils.isBlank(consumeBatchSize)) {
            int batchSize = Math.min(MAX_BATCH_SIZE, Integer.valueOf(consumeBatchSize));
            batchSize = Math.max(MIN_BATCH_SIZE, batchSize);
            defaultMQPushConsumer.setConsumeMessageBatchMaxSize(batchSize);
        }
        return defaultMQPushConsumer;
    }

    private void setDefaultMQProducerAttributes(Properties properties, DefaultMQProducer defaultMQProducer) {
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
        defaultMQProducer.setNamesrvAddr(properties.getProperty(RocketMQKeyConst.NAMESRV_ADDR, buildIntanceName()));
        // 消息最大大小4M
        defaultMQProducer.setMaxMessageSize(1024 * 1024 * 4);
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

    private void setDefaultMQPushConsumerAttributes(Properties properties,
        DefaultMQPushConsumer defaultMQPushConsumer) {
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
        defaultMQPushConsumer.setNamesrvAddr(properties.getProperty(RocketMQKeyConst.NAMESRV_ADDR, buildIntanceName()));

        String consumeThreadNums = properties.getProperty(RocketMQKeyConst.ConsumeThreadNums);
        if (!StringUtils.isBlank(consumeThreadNums)) {
            defaultMQPushConsumer.setConsumeThreadMin(Integer.valueOf(consumeThreadNums));
            defaultMQPushConsumer.setConsumeThreadMax(Integer.valueOf(consumeThreadNums));
        }

        String configuredCachedMessageAmount = properties.getProperty(RocketMQKeyConst.MaxCachedMessageAmount);
        if (!StringUtils.isBlank(configuredCachedMessageAmount)) {
            maxCachedMessageAmount =
                Math.min(MAX_CACHED_MESSAGE_AMOUNT, Integer.valueOf(configuredCachedMessageAmount));
            maxCachedMessageAmount = Math.max(MIN_CACHED_MESSAGE_AMOUNT, maxCachedMessageAmount);
            defaultMQPushConsumer.setPullThresholdForTopic(maxCachedMessageAmount);

        }

        String configuredCachedMessageSizeInMiB = properties.getProperty(RocketMQKeyConst.MaxCachedMessageSizeInMiB);
        if (!StringUtils.isBlank(configuredCachedMessageSizeInMiB)) {
            maxCachedMessageSizeInMiB =
                Math.min(MAX_CACHED_MESSAGE_SIZE_IN_MIB, Integer.valueOf(configuredCachedMessageSizeInMiB));
            maxCachedMessageSizeInMiB = Math.max(MIN_CACHED_MESSAGE_SIZE_IN_MIB, maxCachedMessageSizeInMiB);
            defaultMQPushConsumer.setPullThresholdSizeForTopic(maxCachedMessageSizeInMiB);
        }

        boolean postSubscriptionWhenPull =
            Boolean.parseBoolean(properties.getProperty(RocketMQKeyConst.PostSubscriptionWhenPull, "false"));
        defaultMQPushConsumer.setPostSubscriptionWhenPull(postSubscriptionWhenPull);

        String messageModel = properties.getProperty(RocketMQKeyConst.MessageModel, RocketMQValueConst.CLUSTERING);
        defaultMQPushConsumer.setMessageModel(MessageModel.valueOf(messageModel));
    }

    @Override
    public TransactionMQProducer createTransactionProducer(Properties properties,
        TransactionListener transactionListener) {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
        transactionMQProducer.setProducerGroup(properties.getProperty(RocketMQKeyConst.ProducerId));
        boolean isVipChannelEnabled =
            Boolean.parseBoolean(properties.getProperty(RocketMQKeyConst.isVipChannelEnabled, "false"));
        transactionMQProducer.setVipChannelEnabled(isVipChannelEnabled);
        String instanceName = properties.getProperty(RocketMQKeyConst.InstanceName, this.buildIntanceName());
        transactionMQProducer.setInstanceName(instanceName);
        transactionMQProducer.setTransactionListener(transactionListener);
        return transactionMQProducer;
    }

}
