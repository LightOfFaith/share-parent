package com.share.lifetime.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RocketMQProperties {

    private Producer producer;

    /**
     * rocketMQ的名称服务器，格式为:“主机:端口;主机:端口”。
     */
    private String nameServer;

    @Getter
    @Setter
    public static class Producer {

        /**
         * 生产者名称
         */
        private String producerGroup;

        /**
         * 设置发送消息超时时间
         */
        private int sendMessageTimeout = 3000;

        /**
         * 设置压缩消息阈值,即默认压缩大于4k的消息体。
         */
        private int compressMessageBodyThreshold = 1024 * 4;

        /**
         * 在同步模式下声明发送失败之前，内部执行的最大重试次数。 这可能会导致消息重复，这取决于应用程序开发人员要解决的问题。
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 在异步模式下声明发送失败之前内部执行的最大重试次数。 这可能会导致消息重复，这取决于应用程序开发人员要解决的问题。
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * 指示是否在内部发送故障时重试另一个代理。
         * 
         */
        private boolean retryNextServer = false;

        /**
         * 允许的最大消息大小(以字节为单位)。
         */
        private int maxMessageSize = 1024 * 1024 * 4;

    }

}
