/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.code.server.gate.bootstarp;



import com.code.server.gate.encoding.GameCharsDecoder;
import com.code.server.gate.encoding.GameCharsEncoder;
import com.code.server.gate.handle.GameMsgHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

/**
 */
public class SocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SocketServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new IdleStateHandler(60,60,60));
//        p.addLast(new LoggingHandler(LogLevel.INFO));
        p.addLast("encoder", new GameCharsEncoder());
        p.addLast("decoder", new GameCharsDecoder());
//        p.addLast(new SocketHandler());
        p.addLast("gameHandler", new GameMsgHandler());
    }
}
