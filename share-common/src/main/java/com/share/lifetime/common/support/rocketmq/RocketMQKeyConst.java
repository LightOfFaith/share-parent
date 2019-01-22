package com.share.lifetime.common.support.rocketmq;

public class RocketMQKeyConst {

    /**
     * 消费模式，包括集群模式、广播模式
     */
    public static final String MessageModel = "MessageModel";

    /**
     * Producer的标识
     */
    public static final String ProducerId = "ProducerId";

    /**
     * Consumer的标识
     */
    public static final String ConsumerId = "ConsumerId";
    /**
     * 消息发送超时时间，如果服务端在配置的对应时间内未ACK，则发送客户端认为该消息发送失败。
     */
    public static final String SendMsgTimeoutMillis = "SendMsgTimeoutMillis";

    /**
     * Name Server地址(格式为:主机:端口;主机:端口)
     */
    public static final String NAMESRV_ADDR = "rocketmq.namesrv.addr";

    /**
     * 消费线程数量
     */
    public static final String ConsumeThreadNums = "ConsumeThreadNums";

    /**
     * 是否启动vip channel
     */
    public static final String isVipChannelEnabled = "isVipChannelEnabled";

    /**
     * 顺序消息消费失败进行重试前的等待时间 单位(毫秒)
     */
    public static final String SuspendTimeMillis = "suspendTimeMillis";

    /**
     * 消息消费失败时的最大重试次数
     */
    public static final String MaxReconsumeTimes = "maxReconsumeTimes";

    /**
     * 设置每条消息消费的最大超时时间,超过这个时间,这条消息将会被视为消费失败,等下次重新投递再次消费. 每个业务需要设置一个合理的值. 单位(分钟)
     */
    public static final String ConsumeTimeout = "consumeTimeout";
    /**
     * 设置事务消息的第一次回查延迟时间
     */
    public static final String CheckImmunityTimeInSeconds = "CheckImmunityTimeInSeconds";

    /**
     * 是否每次请求都带上最新的订阅关系
     */
    public static final String PostSubscriptionWhenPull = "PostSubscriptionWhenPull";

    /**
     * BatchConsumer每次批量消费的最大消息数量, 默认值为1, 允许自定义范围为[1, 32], 实际消费数量可能小于该值.
     */
    public static final String ConsumeMessageBatchMaxSize = "ConsumeMessageBatchMaxSize";

    /**
     * Consumer允许在客户端中缓存的最大消息数量，默认值为5000，设置过大可能会引起客户端OOM，取值范围为[100, 50000]
     * <p>
     * 考虑到批量拉取，实际最大缓存量会少量超过限定值
     * <p>
     * 该限制在客户端级别生效，限定额会平均分配到订阅的Topic上，比如限制为1000条，订阅2个Topic，每个Topic将限制缓存500条
     */
    public static final String MaxCachedMessageAmount = "maxCachedMessageAmount";

    /**
     * Consumer允许在客户端中缓存的最大消息容量，默认值为512 MiB，设置过大可能会引起客户端OOM，取值范围为[16, 2048]
     * <p>
     * 考虑到批量拉取，实际最大缓存量会少量超过限定值
     * <p>
     * 该限制在客户端级别生效，限定额会平均分配到订阅的Topic上，比如限制为1000MiB，订阅2个Topic，每个Topic将限制缓存500MiB
     */
    public static final String MaxCachedMessageSizeInMiB = "maxCachedMessageSizeInMiB";

    /**
     * 设置实例名，注意：如果在一个进程中将多个Producer或者是多个Consumer设置相同的InstanceName，底层会共享连接。
     */
    public static final String InstanceName = "rocketmq.client.name";

}
