package com.share.lifetime.common.support.rocketmq;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQProducerTest {

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.43.145:9876");
        producer.setSendMsgTimeout(3000);
        producer.setRetryTimesWhenSendFailed(2);
        producer.setRetryTimesWhenSendAsyncFailed(2);
        producer.setMaxMessageSize(1024 * 1024 * 4);
        producer.setCompressMsgBodyOverHowmuch(1024 * 4);
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        producer.start();
        ObjectMapper rocketMQMessageObjectMapper = new ObjectMapper();
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(producer);
        rocketMQTemplate.setObjectMapper(rocketMQMessageObjectMapper);
        log.info("initialize");
        log.info("{}", rocketMQTemplate);
        Message<String> message = new Message<String>() {

            @Override
            public String getPayload() {
                return "payload";
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> headers = new HashMap<>();
                headers.put("headers_key", "headers_value");
                return new MessageHeaders(headers);
            }
        };
        SendResult syncSend = rocketMQTemplate.syncSend("TopicTest:TagA", message);
        log.info("{}", syncSend);
        producer.shutdown();
        log.info("destroy");
    }

}
