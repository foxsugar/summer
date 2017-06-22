package com.code.server.gate.encoding;

import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GameCharsDecoder extends ByteToMessageDecoder {

    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(GameCharsDecoder.class);

    private static final int HEAD_SIZE = 4;
    private static final int DATA_SIZE_MIN = 2;
        


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        if (buf.readableBytes() < HEAD_SIZE) {
            return;
        }

        //标记
        buf.markReaderIndex();
        //读取长度
        int dataLength = buf.readInt();

        //判断长度合法
        if (dataLength < DATA_SIZE_MIN) {
            logger.error("data length error:" + dataLength);
            ctx.close();
            return;
        }

        //数据不足
        if (buf.readableBytes() < dataLength){
            //重置标记
            buf.resetReaderIndex();
            return;
        }

        //读取
        byte[] data = new byte[dataLength];
        buf.readBytes(data);

        String str = new String(data, "utf-8");
        JsonNode json = JsonUtil.readTree(str);
//        JSONObject json = JSONObject.fromObject(str);

        list.add(json);
    }


    @SuppressWarnings("unused")
	private static byte[] uncompress(byte[] b) {
        if (b == null)
            return null;
        String map;
        byte bt;
        ArrayList<Byte> buffer = new ArrayList<Byte>();
        char client = 0;
        int readCount = 0;
        while (readCount != b.length) {
            map = Integer.toBinaryString((int) b[readCount] + 128);
            readCount++;
            bt = b[readCount];
            readCount++;
            for (int i = 0; i < map.length(); ++i) {
                client = map.charAt(i);
                if (client == '1')
                    buffer.add(i, bt);
            }
        }
        byte[] out = new byte[buffer.size()];
        for (int j = 0; j < out.length; ++j)
            out[j] = buffer.get(j);
        return out;
    }

}
