package com.share.lifetime.common.support.rocketmq.spring;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQConsumerTest {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("192.168.43.145:9876");
        log.info("initialize");
        consumer.subscribe("TopicTest", "TagA");
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                log.info(String.format("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs));
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();
                try {
                    log.info("body:{}", new String(body, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        consumer.start();

        log.info("Consumer Started.%n");
    }

}
