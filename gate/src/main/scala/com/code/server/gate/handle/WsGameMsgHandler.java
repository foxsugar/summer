package com.code.server.gate.handle;

import com.code.server.constant.response.ResponseVo;
import com.code.server.gate.service.GateManager;
import com.code.server.gate.service.NettyMsgDispatch;
import com.code.server.redis.service.RedisManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by win7 on 2017/3/9.
 */
public class WsGameMsgHandler extends ChannelDuplexHandler {

    private final Logger logger = LoggerFactory.getLogger(GameMsgHandler.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {//读
                ctx.channel().close();

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {//写
//                System.out.println("WRITER_IDLE");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {//总
//                System.out.println("ALL_IDLE");
                // 发送心跳
//                ctx.channel().write("ping\n");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        logger.info("接收消息 : {}",msg );
//        NettyMsgDispatch.dispatch(msg,ctx);
        Map<String, Object> r = new HashMap<>();
        System.out.println("返回消息");
        r.put("code", 1);
        ResponseVo responseVo = new ResponseVo("gateService", "heart", 0);
//        ctx.writeAndFlush(JsonUtil.toJson(responseVo));
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
        //移除ctx
        if(ctx.channel().hasAttr(GateManager.attributeKey) && ctx.channel().attr(GateManager.attributeKey)!=null){
            long userId = ctx.channel().attr(GateManager.attributeKey).get();
            GateManager.removeUserNettyCtx(userId);
            //删掉user-gate
            RedisManager.getUserRedisService().removeGate(userId);
            //下线通知
            NettyMsgDispatch.noticeOffline2Other(userId);
        }
//        super.channelInactive(ctx);
    }


}
