package com.code.server.gate.service;

import com.code.server.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sunxianping on 2017/5/27.
 */
public class GateManager {

    public static AttributeKey<Long> attributeKey = AttributeKey.newInstance("userId");

    private Map<Long, ChannelHandlerContext> userNettyCtx = new ConcurrentHashMap<>();

    private static GateManager ourInstance = new GateManager();

    public static GateManager getInstance() {
        return ourInstance;
    }

    private GateManager() {
    }

    public Map<Long, ChannelHandlerContext> getUserNettyCtx() {
        return userNettyCtx;
    }

    public GateManager setUserNettyCtx(Map<Long, ChannelHandlerContext> userNettyCtx) {
        this.userNettyCtx = userNettyCtx;
        return this;
    }


    public static ChannelHandlerContext getUserNettyCtxByUserId(long userId){
        return getInstance().userNettyCtx.get(userId);
    }

    public static void putUserNettyCtx(long userId, ChannelHandlerContext ctx){
        getInstance().userNettyCtx.put(userId, ctx);
    }

    public static void removeUserNettyCtx(long userId){
        getInstance().userNettyCtx.remove(userId);
    }
    public static void sendMsg(long userId,String msg){
        ChannelHandlerContext ctx = getUserNettyCtxByUserId(userId);
        ctx.writeAndFlush(msg);
    }

    public static void sendMsg(long userId, Object object) {
        String json = JsonUtil.toJson(object);
        sendMsg(userId,json);
    }
}
