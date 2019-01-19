package com.share.lifetime.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources(value = {@PropertySource(value = "classpath:config/mq/rocketmq-common.properties", encoding = "UTF-8"),
    @PropertySource(value = "classpath:config/mq/rocketmq-${spring.profiles.active}.properties", encoding = "UTF-8")})
public class RocketMQConfig {

    @Autowired
    private Environment env;

    @Bean("rocketMQProperties")
    public RocketMQProperties rocketMQProperties() {
        RocketMQProperties properties = new RocketMQProperties();
        RocketMQProperties.Producer producer = new RocketMQProperties.Producer();
        properties.setNameServer(env.getProperty("rocketmq.name.server", String.class));
        producer.setCompressMessageBodyThreshold(
            env.getProperty("rocketmq.compress.message.body.threshold", Integer.class));
        producer.setMaxMessageSize(env.getProperty("rocketmq.max.message.size", Integer.class));
        producer.setProducerGroup(env.getProperty("rocketmq.producer.group", String.class));
        producer.setRetryNextServer(env.getProperty("rocketmq.retry.next.server", Boolean.class));
        producer.setRetryTimesWhenSendAsyncFailed(
            env.getProperty("rocketmq.retry。times。when。send。async。failed", Integer.class));
        producer.setRetryTimesWhenSendFailed(env.getProperty("rocketmq.retry.times.when.send.failed", Integer.class));
        producer.setSendMessageTimeout(env.getProperty("rocketmq.send.message.timeout", Integer.class));
        properties.setProducer(producer);
        return properties;

    }

    @Bean("defaultMQProducer")
    @DependsOn(value = {"rocketMQProperties"})
    public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String producerGroup = producerConfig.getProducerGroup();
        String namesrvAddr = rocketMQProperties.getNameServer();
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        return producer;
    }

}
