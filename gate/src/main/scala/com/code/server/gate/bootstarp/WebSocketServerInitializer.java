package com.code.server.gate.bootstarp;

import com.code.server.gate.handle.WsGameMsgHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by sunxianping on 2018-10-17.
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理日志
//        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        //处理心跳
        pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
//        pipeline.addLast(new ChatHeartbeatHandler());

        // 获取职责链
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(64*1024));
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());

//        pipeline.addLast("encoder", new WsEncoder());
//        pipeline.addLast("decoder", new WsDecoder());
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
//        pipeline.addLast("codec", new WsEncoder());
        //websocket定义了传递数据的6中frame类型
        pipeline.addLast(new WsGameMsgHandler());
    }
}
