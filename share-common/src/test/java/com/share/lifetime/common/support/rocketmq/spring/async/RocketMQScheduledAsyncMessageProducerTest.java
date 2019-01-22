package com.share.lifetime.common.support.rocketmq.spring.async;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.share.lifetime.common.support.rocketmq.spring.RocketMQHeaders;
import com.share.lifetime.common.support.rocketmq.spring.RocketMQTemplate;
import com.share.lifetime.common.util.DateFormatUtils;
import com.share.lifetime.common.util.RandomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQScheduledAsyncMessageProducerTest {

    // private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";

    public static void main(String[] args) throws MQClientException {

        StopWatch stopWatch = new StopWatch("RocketMQScheduledMessageProducerTest");
        stopWatch.start();
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.43.145:9876");
        // 设置发送超时时间，单位毫秒
        producer.setSendMsgTimeout(10000);
        producer.setRetryTimesWhenSendFailed(2);
        producer.setRetryTimesWhenSendAsyncFailed(2);
        producer.setMaxMessageSize(1024 * 1024 * 4);
        producer.setCompressMsgBodyOverHowmuch(1024 * 4);
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
        producer.start();
        ObjectMapper rocketMQMessageObjectMapper = new ObjectMapper();
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(producer);
        rocketMQTemplate.setObjectMapper(rocketMQMessageObjectMapper);
        log.info("initialize");
        log.info("{}", rocketMQTemplate);

        final long currentTimeMillis = System.currentTimeMillis();
        final String orderId = RandomUtils
            .randomStringFixLength(DateFormatUtils.formatDate(DateFormatUtils.PATTERN_DEFAULT_, currentTimeMillis), 15);
        Message<String> message = new Message<String>() {

            @Override
            public String getPayload() {
                return getRequestBody();
            }

            private String getRequestBody() {
                return "\r\n" + DateFormatUtils.formatDate(DateFormatUtils.PATTERN_DEFAULT, currentTimeMillis) + "{\r\n"
                    + "    \"alipay_trade_pay_response\": {\r\n" + "        \"code\": \"10000\",\r\n"
                    + "        \"msg\": \"Success\",\r\n" + "        \"trade_no\": \"" + orderId + "\",\r\n"
                    + "        \"out_trade_no\": \"6823789339978248\",\r\n"
                    + "        \"buyer_logon_id\": \"159****5620\",\r\n" + "        \"settle_amount\": \"88.88\",\r\n"
                    + "        \"pay_currency\": \"CNY\",\r\n" + "        \"pay_amount\": \"580.04\",\r\n"
                    + "        \"settle_trans_rate\": \"1\",\r\n" + "        \"trans_pay_rate\": \"6.5261\",\r\n"
                    + "        \"total_amount\": 120.88,\r\n" + "        \"trans_currency\": \"USD\",\r\n"
                    + "        \"settle_currency\": \"USD\",\r\n" + "        \"receipt_amount\": \"88.88\",\r\n"
                    + "        \"buyer_pay_amount\": 8.88,\r\n" + "        \"point_amount\": 8.12,\r\n"
                    + "        \"invoice_amount\": 12.5,\r\n" + "        \"gmt_payment\": \"2014-11-27 15:45:57\",\r\n"
                    + "        \"fund_bill_list\": [\r\n" + "            {\r\n"
                    + "                \"fund_channel\": \"ALIPAYACCOUNT\",\r\n" + "                \"amount\": 10,\r\n"
                    + "                \"real_amount\": 11.21\r\n" + "            }\r\n" + "        ],\r\n"
                    + "        \"card_balance\": 98.23,\r\n" + "        \"store_name\": \"证大五道口店\",\r\n"
                    + "        \"buyer_user_id\": \"2088101117955611\",\r\n"
                    + "        \"discount_goods_detail\": \"[{\\\"goods_id\\\":\\\"STANDARD1026181538\\\",\\\"goods_name\\\":\\\"雪碧\\\",\\\"discount_amount\\\":\\\"100.00\\\",\\\"voucher_id\\\":\\\"2015102600073002039000002D5O\\\"}]\",\r\n"
                    + "        \"voucher_detail_list\": [\r\n" + "            {\r\n"
                    + "                \"id\": \"2015102600073002039000002D5O\",\r\n"
                    + "                \"name\": \"XX超市5折优惠\",\r\n"
                    + "                \"type\": \"ALIPAY_FIX_VOUCHER\",\r\n" + "                \"amount\": 10,\r\n"
                    + "                \"merchant_contribute\": 9,\r\n" + "                \"other_contribute\": 1,\r\n"
                    + "                \"memo\": \"学生专用优惠\",\r\n"
                    + "                \"template_id\": \"20171030000730015359000EMZP0\",\r\n"
                    + "                \"purchase_buyer_contribute\": 2.01,\r\n"
                    + "                \"purchase_merchant_contribute\": 1.03,\r\n"
                    + "                \"purchase_ant_contribute\": 0.82\r\n" + "            }\r\n" + "        ],\r\n"
                    + "        \"auth_trade_pay_mode\": \"CREDIT_PREAUTH_PAY\",\r\n"
                    + "        \"charge_amount\": \"8.88\",\r\n" + "        \"charge_flags\": \"bluesea_1\",\r\n"
                    + "        \"settlement_id\": \"2018101610032004620239146945\",\r\n"
                    + "        \"business_params\": \"{\\\"data\\\":\\\"123\\\"}\",\r\n"
                    + "        \"buyer_user_type\": \"PRIVATE\",\r\n" + "        \"mdiscount_amount\": \"88.88\",\r\n"
                    + "        \"discount_amount\": \"88.88\",\r\n" + "        \"buyer_user_name\": \"菜鸟网络有限公司\"\r\n"
                    + "    },\r\n" + "    \"sign\": \"ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE\"\r\n" + "}";
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> headers = new HashMap<>();
                // 设置代表消息的业务关键属性，请尽可能全局唯一。
                // 以方便您在无法正常收到消息情况下，可通过控制台查询消息并补发。
                // 注意：不设置也不会影响消息正常收发
                headers.put(RocketMQHeaders.KEYS, orderId);
                return new MessageHeaders(headers);
            }
        };
        // Scheduled_Topic(Message 所属的 Topic)
        // TagA( Message Tag, 可理解为 Email 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在消息队列 RocketMQ 的服务器过滤)
        //// getPayload(Message Body 可以是任何二进制形式的数据， 消息队列 RocketMQ 不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式)
        String destination = "Scheduled_Topic:TagA";

        // 异步发送消息, 发送结果通过 callback 返回给客户端。
        SendCallback sendCallback = new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                // 消费发送成功
                log.info("send message success. topic={}, msgId={}", sendResult.getMessageQueue().getTopic(),
                    sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
                log.error("Send mq message failed. Topic is:{},exception:{}", ExceptionUtils.getStackTrace(e));
            }
        };
        //// 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
        // 全局顺序消息，该字段可以设置为任意非空字符串。
        String shardingKey = String.valueOf(orderId);
        rocketMQTemplate.asyncSendOrderly(destination, message, shardingKey, sendCallback);

        // 在 callback 返回之前即可取得 msgId。
        log.info("send message async. topic=" + message.getHeaders() + ", msgId=" + message.getPayload());

        // 在应用退出前，销毁 Producer 对象
        // 注意：如果不销毁也没有问题
        producer.shutdown();
        log.info("destroy");
        stopWatch.stop();
        log.info("{}", stopWatch.prettyPrint());
    }

}
