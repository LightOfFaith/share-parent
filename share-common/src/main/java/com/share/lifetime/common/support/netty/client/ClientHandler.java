package com.share.lifetime.common.support.netty.client;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.share.lifetime.common.support.netty.RequestData;
import com.share.lifetime.common.support.netty.ResponseData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private RequestData requestData;

    private ResponseData responseData;

    public ClientHandler(RequestData requestData) {
        this.requestData = requestData;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {}

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("请求参数:{}", requestData.getValue());
        ctx.writeAndFlush(requestData);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        responseData = (ResponseData)msg;
        log.info("响应参数:{}", responseData.getValue());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ExceptionUtils.getStackTrace(cause));
        ctx.close();
    }

}
