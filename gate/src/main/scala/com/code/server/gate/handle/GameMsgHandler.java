package com.code.server.gate.handle;

import com.code.server.gate.service.GateManager;
import com.code.server.gate.service.NettyMsgDispatch;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by win7 on 2017/3/9.
 */
public class GameMsgHandler extends ChannelDuplexHandler {

    private final Logger logger = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        logger.info("接收消息 : {}",msg );
        NettyMsgDispatch.dispatch(msg,ctx);
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
        }
        super.channelInactive(ctx);
    }


}
