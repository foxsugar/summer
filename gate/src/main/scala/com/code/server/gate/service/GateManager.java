package com.code.server.gate.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sunxianping on 2017/5/27.
 */
public class GateManager {

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
}
