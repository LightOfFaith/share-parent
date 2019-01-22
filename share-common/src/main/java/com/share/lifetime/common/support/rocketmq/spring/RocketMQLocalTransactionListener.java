package com.share.lifetime.common.support.rocketmq.spring;

import org.springframework.messaging.Message;

public interface RocketMQLocalTransactionListener {

    @SuppressWarnings("rawtypes")
    RocketMQLocalTransactionStateEnum executeLocalTransaction(final Message msg, final Object arg);

    @SuppressWarnings("rawtypes")
    RocketMQLocalTransactionStateEnum checkLocalTransaction(final Message msg);

}
