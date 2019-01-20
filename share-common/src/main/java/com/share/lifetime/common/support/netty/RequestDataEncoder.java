package com.share.lifetime.common.support.netty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 请求数据编码器
 * 
 * @author liaoxiang
 * @date 2019/01/20
 */
public class RequestDataEncoder extends MessageToByteEncoder<RequestData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestData msg, ByteBuf out) throws Exception {
        // ctx.alloc().buffer(4);
        String value = msg.getValue();
        byte[] src = value.getBytes();
        int length = src.length;
        out.writeInt(length);
        out.writeCharSequence(value, charset);
    }

}
