package com.share.lifetime.common.support.netty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class StringToByteEncoder extends MessageToByteEncoder<String> {

    private final String charsetName;

    public StringToByteEncoder(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        String value = msg;
        byte[] src = value.getBytes();
        int length = src.length;
        out.writeInt(length);
        out.writeCharSequence(value, Charset.forName(charsetName));
    }

}
