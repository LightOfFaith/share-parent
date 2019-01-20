package com.share.lifetime.common.support.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteToStringDecoder extends ByteToMessageDecoder {

    private final String charsetName;

    public ByteToStringDecoder(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            log.warn("readableBytes It has to be greater than 4");
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        ByteBuf readBytes = in.readBytes(length);
        byte[] dst = new byte[length];
        readBytes.readBytes(dst);
        String data = new String(dst, charsetName);
        out.add(data);
    }

}
