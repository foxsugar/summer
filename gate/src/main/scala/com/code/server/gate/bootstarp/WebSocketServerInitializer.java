package com.code.server.gate.bootstarp;

import com.code.server.gate.encoding.WsCodec;
import com.code.server.gate.handle.GameMsgHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Created by sunxianping on 2018-10-17.
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/websocket";

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理日志
//        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        //处理心跳
//        pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
//        pipeline.addLast(new ChatHeartbeatHandler());

//        // 获取职责链
//        pipeline.addLast("http-codec", new HttpServerCodec());
//        pipeline.addLast("aggregator", new HttpObjectAggregator(64*1024));
//        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
////        pipeline.addLast(new WebSocketServerCompressionHandler());
//
////        pipeline.addLast("encoder", new WsEncoder());
////        pipeline.addLast("decoder", new WsDecoder());
//        pipeline.addLast("handshake",new WebSocketServerProtocolHandler("/ws"));
//        pipeline.addLast(new WsCodec());
////        pipeline.addLast(new WsGameMsgHandler());
//        pipeline.addLast(new WsHandler());
////        pipeline.addLast("codec", new WsEncoder());
//        //websocket定义了传递数据的6中frame类型
////        pipeline.addLast(new GameMsgHandler());
//


        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
        pipeline.addLast(new WsCodec());
        pipeline.addLast(new GameMsgHandler());









//        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast(new HttpServerCodec());
//        pipeline.addLast(new HttpObjectAggregator(65536));
////        pipeline.addLast(new WebSocketServerCompressionHandler());
////        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
////        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
//        pipeline.addLast(new WebSocketServerHandler());
    }
}
