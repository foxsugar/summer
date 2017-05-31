package com.code.server.gate.encoding;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameCharsEncoder extends MessageToByteEncoder<Object> {
    private Logger logger = LoggerFactory.getLogger(GameCharsEncoder.class);
    private Gson gson = new Gson();

    protected void encode(ChannelHandlerContext ctx, Object object, ByteBuf out) throws Exception {
//        String json = gson.toJson(object);
        String json = (String)object;
        byte[] data = json.getBytes("utf-8");
//        System.out.println("发送消息===  "+json);
        logger.info("发送消息===  "+json);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
