package com.share.lifetime.common.support.netty.server;

import com.share.lifetime.common.support.netty.RequestData;
import com.share.lifetime.common.support.netty.ResponseData;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData)msg;
        ResponseData responseData = new ResponseData();
        responseData.setValue(requestData.getValue());
        ChannelFuture future = ctx.writeAndFlush(responseData);
        future.addListener(ChannelFutureListener.CLOSE);
        log.info("netty服务端接收客户端请求参数:{}", requestData.getValue());
    }

}
