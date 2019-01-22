package com.share.lifetime.common.support.rocketmq.spring;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.share.lifetime.common.support.rocketmq.spring.util.RocketMQConfigUtils;
import com.share.lifetime.common.support.rocketmq.spring.util.RocketMQUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQTemplate extends AbstractMessageSendingTemplate<String>
    implements InitializingBean, DisposableBean {

    private String charset = "UTF-8";

    private DefaultMQProducer producer;

    private ObjectMapper objectMapper;

    private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    private final Map<String, TransactionMQProducer> cache = new ConcurrentHashMap<>(16);

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MessageQueueSelector getMessageQueueSelector() {
        return messageQueueSelector;
    }

    public void setMessageQueueSelector(MessageQueueSelector messageQueueSelector) {
        this.messageQueueSelector = messageQueueSelector;
    }

    /**
     * 以同步模式发送消息。仅当发送过程完全完成时，此方法才会返回。 可靠的同步传输用于广泛的场景，例如重要的通知消息，SMS 通知，短信营销系统等。 此方法具有内部重试机制，即内部实现将重试 声明失败之前
     * {@link DefaultMQProducer#getRetryTimesWhenSendFailed}次。结果，多个 消息可能会传递给经纪人。应用程序开发人员可以自行解决重复问题。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param message
     *            {@link org.springframework.messaging.Message}
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, producer.getSendMsgTimeout());
    }

    /**
     * 与 {@link #syncSend(String, Message)} 相同，另外还指定了发送超时时间。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param message
     *            {@link org.springframework.messaging.Message}
     * @param timeout
     *            发送超时时间(单位:毫秒)
     * @return { @link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        return syncSend(destination, message, timeout, 0);
    }

    /**
     * 与 {@link #syncSend(String, Message, long)} 相同，另外还指定了延迟消息的级别。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param message
     *            {@link org.springframework.messaging.Message}
     * @param timeout
     *            发送超时时间(单位:毫秒)
     * @param delayLevel
     *            延迟消息的级别
     * @return { @link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            SendResult sendResult = producer.send(rocketMsg, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("syncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 与 {@link #syncSend(String, Message)}相同
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param payload
     *            用作有效载荷的对象
     * @return { @link SendResult}
     */
    public SendResult syncSend(String destination, Object payload) {
        return syncSend(destination, payload, producer.getSendMsgTimeout());
    }

    /**
     * 与 {@link #syncSend(String, Object)}相同，另外还指定了发送超时时间。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param payload
     *            用作有效载荷的对象
     * @param timeout
     *            发送超时时间(单位:毫秒)
     * @return
     */
    public SendResult syncSend(String destination, Object payload, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSend(destination, message, timeout);
    }

    /**
     * 与 {@link #syncSend(String, Message)}相同，并且指定发送有序的hashKey。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param message
     *            {@link org.springframework.messaging.Message}
     * @param hashKey
     *            使用此键选择队列。例如：orderId，productId ......
     * @return
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey) {
        return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 与 {@link #syncSendOrderly(String, Message, String)}相同，另外还指定了发送超时时间。
     * 
     * @param destination
     *            格式: `topicName:tags`
     * @param message
     *            {@link org.springframework.messaging.Message}
     * @param hashKey
     *            使用此键选择队列。例如：orderId，productId ......
     * @param timeout
     *            发送超时时间(单位:毫秒)
     * @return
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            SendResult sendResult = producer.send(rocketMsg, messageQueueSelector, hashKey, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("syncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 与 {@link #syncSendOrderly(String, Message)}相同，并且指定发送有序的hashKey。
     * 
     * @param destination
     * @param payload
     * @param hashKey
     * @return
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey) {
        return syncSendOrderly(destination, payload, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 与 {@link #syncSendOrderly(String, Message, String)}相同，另外还指定了发送超时时间。
     * 
     * @param destination
     * @param payload
     * @param hashKey
     * @param timeout
     * @return
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 
     * 与{@link #asyncSend(String, Message, SendCallback)}相同，另外还指定了发送超时时间。
     * 
     * @param destination
     * @param message
     * @param sendCallback
     * @param timeout
     */
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("asyncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            producer.send(rocketMsg, sendCallback, timeout);
        } catch (Exception e) {
            log.info("asyncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 异步发送消息到代理。异步传输通常用于响应时间敏感 业务场景。 此方法立即返回。发送完成后，将执行<code>sendCallback</code> 与{@link #syncSend(String, Object)}
     * 类似，内部实现可能会重试{@link DefaultMQProducer#getRetryTimesWhenSendAsyncFailed}次声明发送失败，可能会产生消息重复和应用程序开发人员是解决此潜在问题的人。
     * 
     * @param destination
     * @param message
     * @param sendCallback
     */
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback) {
        asyncSend(destination, message, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 
     * 与{@link #asyncSend(String, Message, SendCallback)}相同，另外还指定了发送超时时间。
     * 
     * @param destination
     * @param payload
     * @param sendCallback
     * @param timeout
     */
    public void asyncSend(String destination, Object payload, SendCallback sendCallback, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        asyncSend(destination, message, sendCallback, timeout);
    }

    /**
     * 与{@link #asyncSend(String, Message, SendCallback)}相同。
     * 
     * @param destination
     * @param payload
     * @param sendCallback
     */
    public void asyncSend(String destination, Object payload, SendCallback sendCallback) {
        asyncSend(destination, payload, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 与{@link #asyncSendOrderly(String, Message, String, SendCallback)}相同，另外并指定发送超时 。
     * 
     * @param destination
     * @param message
     * @param hashKey
     * @param sendCallback
     * @param timeout
     */
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback,
        long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("asyncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            producer.send(rocketMsg, messageQueueSelector, hashKey, sendCallback, timeout);
        } catch (Exception e) {
            log.error("asyncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 与{@link #asyncSendOrderly(String, Object, String, SendCallback)}相同。
     * 
     * @param destination
     * @param message
     * @param hashKey
     *            使用此键选择队列。例如：orderId，productId ......
     * @param sendCallback
     */
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, message, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 与 {@link #asyncSendOrderly(String, Message, SendCallback)}相同，并通过指定的hashKey顺序发送。
     * 
     * @param destination
     * @param payload
     * @param hashKey
     * @param sendCallback
     */
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, payload, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 与{@link #asyncSendOrderly(String, Object, String, SendCallback)}相同，另外并指定发送超时 。
     * 
     * @param destination
     * @param payload
     * @param hashKey
     * @param sendCallback
     * @param timeout
     */
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback,
        long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        asyncSendOrderly(destination, message, hashKey, sendCallback, timeout);
    }

    /**
     * 以类似一个<a href="https://en.wikipedia.org/wiki/User_Datagram_Protocol">UDP</a>时， 此方法将不等待
     * 返回前经纪人确认。显然，它具有最大的吞吐量但消息丢失的潜力。 单向传输用于需要中等可靠性的情况，例如日志收集。
     * 
     * @param destination
     * @param message
     */
    public void sendOneWay(String destination, Message<?> message) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWay failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            producer.sendOneway(rocketMsg);
        } catch (Exception e) {
            log.error("sendOneWay failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 与{@link #sendOneWay(String, Message)}相同。
     * 
     * @param destination
     * @param payload
     */
    public void sendOneWay(String destination, Object payload) {
        Message<?> message = this.doConvert(payload, null, null);
        sendOneWay(destination, message);
    }

    /**
     * 与{@link #sendOneWay(String, Message)}相同，使用hashKey按顺序发送。
     * 
     * @param destination
     * @param message
     * @param hashKey
     *            使用此键选择队列。例如：orderId，productId ......
     */
    public void sendOneWayOrderly(String destination, Message<?> message, String hashKey) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWayOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            producer.sendOneway(rocketMsg, messageQueueSelector, hashKey);
        } catch (Exception e) {
            log.error("sendOneWayOrderly failed. destination:{}, message:{}", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 与{@link #sendOneWay(String, Message)}相同，使用hashKey按顺序发送。
     * 
     * @param destination
     * @param payload
     * @param hashKey
     */
    public void sendOneWayOrderly(String destination, Object payload, String hashKey) {
        Message<?> message = this.doConvert(payload, null, null);
        sendOneWayOrderly(destination, message, hashKey);
    }

    @Override
    public void destroy() throws Exception {
        if (Objects.nonNull(producer)) {
            producer.shutdown();
        }

        for (Map.Entry<String, TransactionMQProducer> kv : cache.entrySet()) {
            if (Objects.nonNull(kv.getValue())) {
                kv.getValue().shutdown();
            }
        }
        cache.clear();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(producer, "Property 'producer' is required");
        producer.start();

    }

    /**
     * 与 {@link #syncSend(String, Message)} 相同
     */
    @Override
    protected void doSend(String destination, Message<?> message) {
        SendResult sendResult = syncSend(destination, message);
        log.debug("send message to `{}` finished. result:{}", destination, sendResult);
    }

    @Override
    protected Message<?> doConvert(Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) {
        String content;
        if (payload instanceof String) {
            content = (String)payload;
        } else {
            // If payload not as string, use objectMapper change it.
            try {
                content = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                log.error("convert payload to String failed. payload:{}", payload);
                throw new RuntimeException("convert to payload to String failed.", e);
            }
        }

        MessageBuilder<?> builder = MessageBuilder.withPayload(content);
        if (headers != null) {
            builder.copyHeaders(headers);
        }
        builder.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);

        Message<?> message = builder.build();
        if (postProcessor != null) {
            message = postProcessor.postProcessMessage(message);
        }
        return message;
    }

    private String getTxProducerGroupName(String name) {
        return name == null ? RocketMQConfigUtils.ROCKETMQ_TRANSACTION_DEFAULT_GLOBAL_NAME : name;
    }

    private TransactionMQProducer stageMQProducer(String name) throws MessagingException {
        name = getTxProducerGroupName(name);

        TransactionMQProducer cachedProducer = cache.get(name);
        if (cachedProducer == null) {
            throw new MessagingException(String.format(
                "Can not found MQProducer '%s' in cache! please define @RocketMQLocalTransactionListener class or invoke createOrGetStartedTransactionMQProducer() to create it firstly",
                name));
        }

        return cachedProducer;
    }

    /**
     * 在事务中发送Spring消息
     * 
     * @param txProducerGroup
     * @param destination
     *            格式：`topicName：tags`
     * @param message
     * @param arg
     * @return
     * @throws MessagingException
     */
    public TransactionSendResult sendMessageInTransaction(final String txProducerGroup, final String destination,
        final Message<?> message, final Object arg) throws MessagingException {
        try {
            TransactionMQProducer txProducer = this.stageMQProducer(txProducerGroup);
            org.apache.rocketmq.common.message.Message rocketMsg =
                RocketMQUtils.convertToRocketMessage(objectMapper, charset, destination, message);
            return txProducer.sendMessageInTransaction(rocketMsg, arg);
        } catch (MQClientException e) {
            throw RocketMQUtils.convert(e);
        }
    }

    /**
     * 通过手动从缓存中删除TransactionMQProducer。注意：RocketMQTemplate可以在bean销毁时释放所有缓存的生成器，不建议直接使用 用户使用此方法。
     * 
     * @param txProducerGroup
     * @throws MessagingException
     */
    public void removeTransactionMQProducer(String txProducerGroup) throws MessagingException {
        txProducerGroup = getTxProducerGroupName(txProducerGroup);
        if (cache.containsKey(txProducerGroup)) {
            DefaultMQProducer cachedProducer = cache.get(txProducerGroup);
            cachedProducer.shutdown();
            cache.remove(txProducerGroup);
        }
    }

    /**
     * 创建并启动事务MQProducer，这个新生成器缓存在内存中。 注意：处理{@code @RocketMQLocalTransactionListener} 时会在内部调用此方法，它不是 建议用户直接使用此方法。
     * 
     * @param txProducerGroup
     *            生产者（组）名称，每个生产者都是唯一的
     * @param transactionListener
     * @param executorService
     * @return 如果创建并启动了生产者，则返回 true; 如果命名生成器已存在于缓存中，则返回false。
     * @throws MessagingException
     */
    public boolean createAndStartTransactionMQProducer(String txProducerGroup,
        RocketMQLocalTransactionListener transactionListener, ExecutorService executorService)
        throws MessagingException {
        txProducerGroup = getTxProducerGroupName(txProducerGroup);
        if (cache.containsKey(txProducerGroup)) {
            log.info(String.format("get TransactionMQProducer '%s' from cache", txProducerGroup));
            return false;
        }

        TransactionMQProducer txProducer =
            createTransactionMQProducer(txProducerGroup, transactionListener, executorService);
        try {
            txProducer.start();
            cache.put(txProducerGroup, txProducer);
        } catch (MQClientException e) {
            throw RocketMQUtils.convert(e);
        }

        return true;
    }

    private TransactionMQProducer createTransactionMQProducer(String name,
        RocketMQLocalTransactionListener transactionListener, ExecutorService executorService) {
        Assert.notNull(producer, "Property 'producer' is required");
        Assert.notNull(transactionListener, "Parameter 'transactionListener' is required");
        TransactionMQProducer txProducer = new TransactionMQProducer(name);
        txProducer.setTransactionListener(RocketMQUtils.convert(transactionListener));

        txProducer.setNamesrvAddr(producer.getNamesrvAddr());
        if (executorService != null) {
            txProducer.setExecutorService(executorService);
        }

        txProducer.setSendMsgTimeout(producer.getSendMsgTimeout());
        txProducer.setRetryTimesWhenSendFailed(producer.getRetryTimesWhenSendFailed());
        txProducer.setRetryTimesWhenSendAsyncFailed(producer.getRetryTimesWhenSendAsyncFailed());
        txProducer.setMaxMessageSize(producer.getMaxMessageSize());
        txProducer.setCompressMsgBodyOverHowmuch(producer.getCompressMsgBodyOverHowmuch());
        txProducer.setRetryAnotherBrokerWhenNotStoreOK(producer.isRetryAnotherBrokerWhenNotStoreOK());

        return txProducer;
    }

}
