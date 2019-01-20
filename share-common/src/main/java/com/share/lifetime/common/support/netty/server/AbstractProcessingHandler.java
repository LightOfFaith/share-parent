package com.share.lifetime.common.support.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractProcessingHandler extends ChannelInboundHandlerAdapter {

    private String request;

    private final String charsetName;

    private String response;

    public AbstractProcessingHandler(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("响应参数:{}", response);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(4);
        request = (String)msg;
        try {
            log.info("请求参数:{}", request);
            response = request;
            byte[] src = response.getBytes(charsetName);
            int value = src.length;
            buffer.writeInt(value);
            buffer.writeBytes(src);
            ctx.writeAndFlush(buffer);
            log.info("响应参数:{}", request);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        log.info("请求参数读取完成,并释放资源!");
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

}
