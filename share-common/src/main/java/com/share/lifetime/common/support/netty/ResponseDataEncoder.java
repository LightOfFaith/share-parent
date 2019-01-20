package com.share.lifetime.common.support.netty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 响应数据编码器
 * 
 * @author liaoxiang
 * @date 2019/01/20
 */
public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
        String value = msg.getValue();
        byte[] src = value.getBytes();
        int length = src.length;
        out.writeInt(length);
        out.writeCharSequence(value, charset);
    }

}
