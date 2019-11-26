package com.code.server.gate.encoding;

import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Created by sunxianping on 2018-10-18.
 */
public class WsCodec extends MessageToMessageCodec<WebSocketFrame,String> {
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        String json = (String)msg;
//        byte[] data = json.getBytes("utf-8");
////        System.out.println("发送消息===  "+json);
//        logger.info("send msg : "+json);
//
//        out.writeInt(data.length);
//        out.writeBytes(data);
//    }



    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {


        byte[] data = msg.getBytes("utf-8");


        System.out.println("jieshouxiaoxi");
        ByteBuf byteBuf = Unpooled.buffer(data.length);
        byteBuf.writeBytes(data);
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);

        System.out.println("encode = "+msg);


        out.add(binaryWebSocketFrame);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {

        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;

        if (frame.isFinalFragment()) {
            JsonNode json = JsonUtil.readTree(frame.content().toString(CharsetUtil.UTF_8));
            out.add(json);
        }

    }
}
