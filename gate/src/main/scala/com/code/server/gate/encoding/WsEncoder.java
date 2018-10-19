package com.code.server.gate.encoding;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by sunxianping on 2018-10-19.
 */
public class WsEncoder extends MessageToMessageEncoder<Object> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        String json = (String)msg;

        System.out.println(json);

//        byte[] data = json.getBytes("utf-8");
////        System.out.println("发送消息===  "+json);
//        logger.info("send msg : "+json);
//        out.writeInt(data.length);
//        out.writeBytes(data);
    }


}
