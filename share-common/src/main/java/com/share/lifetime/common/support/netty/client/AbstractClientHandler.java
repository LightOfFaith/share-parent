package com.share.lifetime.common.support.netty.client;

import org.apache.commons.lang3.exception.ExceptionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractClientHandler extends ChannelInboundHandlerAdapter {

    private final String request;

    private final String charsetName;

    private String response;

    public AbstractClientHandler(String request, String charsetName) {
        this.request = request;
        this.charsetName = charsetName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(4);
        byte[] src = request.getBytes(charsetName);
        int value = src.length;
        buffer.writeInt(value);
        buffer.writeBytes(src);
        ctx.writeAndFlush(buffer);
        log.info("请求参数:{}", request);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            response = (String)msg;
            log.info("响应参数:{}", response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        log.info("响应参数读取完成,并释放资源!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ExceptionUtils.getStackTrace(cause));
        ctx.close();
    }

    public String getResponse() {
        return response;
    }

}
