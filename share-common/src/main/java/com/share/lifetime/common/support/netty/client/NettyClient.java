package com.share.lifetime.common.support.netty.client;

import org.apache.commons.lang3.StringUtils;

import com.share.lifetime.common.support.netty.RequestData;
import com.share.lifetime.common.support.netty.RequestDataEncoder;
import com.share.lifetime.common.support.netty.ResponseData;
import com.share.lifetime.common.support.netty.ResponseDataDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private String host;

    private int port;

    private RequestData requestData;

    public NettyClient(String host, int port, RequestData requestData) {
        if (StringUtils.isBlank(host)) {
            throw new RuntimeException("host Can not be null");
        }
        if (requestData == null) {
            throw new RuntimeException("requestData Can not be null");
        }
        this.host = host;
        this.port = port;
        this.requestData = requestData;
    }

    public ResponseData sendRequest() throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ClientHandler clientHandler = new ClientHandler(requestData);

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            // 设置连接超时时间
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            // 设置是否启动心跳保活机制（长连接）
            b.option(ChannelOption.SO_KEEPALIVE, false);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestDataEncoder(), new ResponseDataDecoder(), clientHandler);
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
        return clientHandler.getResponseData();
    }

}
