package com.code.server.gate.handle;

import com.code.server.gate.kafka.MsgProducer;
import com.code.server.gate.util.SpringUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.sf.json.JSONObject;

/**
 * Created by win7 on 2017/3/9.
 */
public class GameMsgHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        System.out.println(msg);
        dispatch(msg);
    }


    private void dispatch(Object msg){
        JSONObject jsonObject = (JSONObject)msg;
        String service = jsonObject.getString("service");
        String method = jsonObject.getString("method");
        JSONObject params = jsonObject.getJSONObject("params");
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send("test",0,msg.toString());

    }
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
//        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //可从内存中剔除的玩家
//        Player player = GameManager.getPlayerByCtx(ctx);
//        if (player != null) {
//            GameManager.getInstance().getKickUser().add(player);
//        }
        super.channelInactive(ctx);
    }


}
