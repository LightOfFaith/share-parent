package com.share.lifetime.common.support.netty.server;

import org.junit.Test;

public class NettyServerTest {

    @Test
    public void testRun() throws Exception {
        NettyServer server = new NettyServer(8181);
        server.run();
    }

}
