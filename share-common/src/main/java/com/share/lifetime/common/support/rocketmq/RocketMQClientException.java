package com.share.lifetime.common.support.rocketmq;

public class RocketMQClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认异常构造函数.
     */
    public RocketMQClientException() {}

    /**
     * 异常接口构造函数
     *
     * @param message
     *            需要向外传递的异常信息
     */
    public RocketMQClientException(String message) {
        super(message);
    }

    /**
     * 异常接口构造函数
     *
     * @param cause
     *            需要向外传递的异常
     */
    public RocketMQClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常接口构造函数
     *
     * @param message
     *            需要向外传递的异常信息
     * @param cause
     *            需要向外传递的异常
     */
    public RocketMQClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
