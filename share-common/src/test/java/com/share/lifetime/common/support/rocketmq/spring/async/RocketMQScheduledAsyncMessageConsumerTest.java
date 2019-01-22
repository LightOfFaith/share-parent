package com.share.lifetime.common.support.rocketmq.spring.async;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQScheduledAsyncMessageConsumerTest {

    public static void main(String[] args) throws MQClientException {
        AtomicLong consumeTimes = new AtomicLong(0);
        StopWatch stopWatch = new StopWatch("RocketMQScheduledMessageConsumerTest");
        stopWatch.start();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("192.168.43.145:9876");
        log.info("initialize");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 消息消费失败时的最大重试次数
        consumer.setMaxReconsumeTimes(16);
        consumer.subscribe("Scheduled_Topic", "TagA");
        // consumer.registerMessageListener(new MessageListenerOrderly() {
        //
        // @Override
        // public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        // log.info(String.format("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs));
        // MessageExt messageExt = msgs.get(0);
        // byte[] body = messageExt.getBody();
        // try {
        // log.info("consumeTimes.get():{},body:{}", consumeTimes.get(), new String(body, "UTF-8"));
        // consumeTimes.incrementAndGet();
        // } catch (Exception e) {
        // log.error(ExceptionUtils.getStackTrace(e));
        // return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        // }
        // // return ConsumeOrderlyStatus.SUCCESS;
        // return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        // }
        //
        // });

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info(String.format("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs));
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();
                try {
                    log.info("consumeTimes.get():{},body:{}", consumeTimes.get(), new String(body, "UTF-8"));
                    consumeTimes.incrementAndGet();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        // 在订阅消息前，必须调用 start 方法来启动 Consumer，只需调用一次即可。
        consumer.start();
        log.info("Consumer Started.%n");
        stopWatch.stop();
        log.info("{}", stopWatch.prettyPrint());
    }

}
