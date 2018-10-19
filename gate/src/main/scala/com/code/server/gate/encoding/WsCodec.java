package com.code.server.gate.encoding;

import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

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
        System.out.println("jieshouxiaoxi");
        WebSocketFrame webSocketFrame = new TextWebSocketFrame(msg);

        System.out.println("encode = "+msg);


        out.add(webSocketFrame);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {

        System.out.println("jieshouxiaoxi  decode ");
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)msg;
        System.out.println("decode = " + textWebSocketFrame.text());

        JsonNode json = JsonUtil.readTree(textWebSocketFrame.text());
//        JSONObject json = JSONObject.fromObject(str);

        out.add(json);

    }
}
