package com.share.lifetime.common.support.rocketmq.util;

import java.nio.charset.Charset;
import java.util.Objects;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.share.lifetime.common.support.rocketmq.RocketMQHeaders;
import com.share.lifetime.common.support.rocketmq.RocketMQLocalTransactionListener;
import com.share.lifetime.common.support.rocketmq.RocketMQLocalTransactionStateEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RocketMQUtils {

    public static TransactionListener convert(RocketMQLocalTransactionListener listener) {
        return new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object obj) {
                RocketMQLocalTransactionStateEnum state =
                    listener.executeLocalTransaction(convertToSpringMessage(message), obj);
                return convertLocalTransactionState(state);
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                RocketMQLocalTransactionStateEnum state =
                    listener.checkLocalTransaction(convertToSpringMessage(messageExt));
                return convertLocalTransactionState(state);
            }
        };
    }

    private static LocalTransactionState convertLocalTransactionState(RocketMQLocalTransactionStateEnum state) {
        switch (state) {
            case UNKNOWN:
                return LocalTransactionState.UNKNOW;
            case COMMIT:
                return LocalTransactionState.COMMIT_MESSAGE;
            case ROLLBACK:
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        // Never happen
        log.warn("Failed to covert enum type RocketMQLocalTransactionState.%s", state);
        return LocalTransactionState.UNKNOW;
    }

    public static MessagingException convert(MQClientException e) {
        return new MessagingException(e.getErrorMessage(), e);
    }

    @SuppressWarnings("rawtypes")
    public static org.springframework.messaging.Message
        convertToSpringMessage(org.apache.rocketmq.common.message.MessageExt message) {
        org.springframework.messaging.Message retMessage =
            MessageBuilder.withPayload(message.getBody()).setHeader(RocketMQHeaders.KEYS, message.getKeys())
                .setHeader(RocketMQHeaders.TAGS, message.getTags()).setHeader(RocketMQHeaders.TOPIC, message.getTopic())
                .setHeader(RocketMQHeaders.MESSAGE_ID, message.getMsgId())
                .setHeader(RocketMQHeaders.BORN_TIMESTAMP, message.getBornTimestamp())
                .setHeader(RocketMQHeaders.BORN_HOST, message.getBornHostString())
                .setHeader(RocketMQHeaders.FLAG, message.getFlag())
                .setHeader(RocketMQHeaders.QUEUE_ID, message.getQueueId())
                .setHeader(RocketMQHeaders.SYS_FLAG, message.getSysFlag())
                .setHeader(RocketMQHeaders.TRANSACTION_ID, message.getTransactionId())
                .setHeader(RocketMQHeaders.PROPERTIES, message.getProperties()).build();

        return retMessage;
    }

    @SuppressWarnings("rawtypes")
    public static org.springframework.messaging.Message
        convertToSpringMessage(org.apache.rocketmq.common.message.Message message) {
        org.springframework.messaging.Message retMessage = MessageBuilder.withPayload(message.getBody())
            .setHeader(RocketMQHeaders.KEYS, message.getKeys()).setHeader(RocketMQHeaders.TAGS, message.getTags())
            .setHeader(RocketMQHeaders.TOPIC, message.getTopic()).setHeader(RocketMQHeaders.FLAG, message.getFlag())
            .setHeader(RocketMQHeaders.TRANSACTION_ID, message.getTransactionId())
            .setHeader(RocketMQHeaders.PROPERTIES, message.getProperties()).build();
        return retMessage;
    }

    public static org.apache.rocketmq.common.message.Message convertToRocketMessage(ObjectMapper objectMapper,
        String charset, String destination, org.springframework.messaging.Message<?> message) {
        Object payloadObj = message.getPayload();
        byte[] payloads;

        if (payloadObj instanceof String) {
            payloads = ((String)payloadObj).getBytes(Charset.forName(charset));
        } else {
            try {
                String jsonObj = objectMapper.writeValueAsString(payloadObj);
                payloads = jsonObj.getBytes(Charset.forName(charset));
            } catch (Exception e) {
                throw new RuntimeException("convert to RocketMQ message failed.", e);
            }
        }

        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = "";
        if (tempArr.length > 1) {
            tags = tempArr[1];
        }

        org.apache.rocketmq.common.message.Message rocketMsg =
            new org.apache.rocketmq.common.message.Message(topic, tags, payloads);

        MessageHeaders headers = message.getHeaders();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            Object keys = headers.get(RocketMQHeaders.KEYS);
            if (!org.springframework.util.StringUtils.isEmpty(keys)) { // if headers has 'KEYS', set rocketMQ message
                                                                       // key
                rocketMsg.setKeys(keys.toString());
            }

            Object flagObj = headers.getOrDefault("FLAG", "0");
            int flag = 0;
            try {
                flag = Integer.parseInt(flagObj.toString());
            } catch (NumberFormatException e) {
                // Ignore it
                log.info("flag must be integer, flagObj:{}", flagObj);
            }
            rocketMsg.setFlag(flag);

            Object waitStoreMsgOkObj = headers.getOrDefault("WAIT_STORE_MSG_OK", "true");
            boolean waitStoreMsgOK = Boolean.TRUE.equals(waitStoreMsgOkObj);
            rocketMsg.setWaitStoreMsgOK(waitStoreMsgOK);

            headers.entrySet().stream()
                .filter(entry -> !Objects.equals(entry.getKey(), RocketMQHeaders.KEYS)
                    && !Objects.equals(entry.getKey(), "FLAG") && !Objects.equals(entry.getKey(), "WAIT_STORE_MSG_OK")) // exclude
                                                                                                                        // "KEYS",
                                                                                                                        // "FLAG",
                                                                                                                        // "WAIT_STORE_MSG_OK"
                .forEach(entry -> {
                    rocketMsg.putUserProperty("USERS_" + entry.getKey(), String.valueOf(entry.getValue())); // add other
                                                                                                            // properties
                                                                                                            // with
                                                                                                            // prefix
                                                                                                            // "USERS_"
                });

        }

        return rocketMsg;
    }

}
