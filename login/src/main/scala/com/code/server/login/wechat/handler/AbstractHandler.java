package com.code.server.login.wechat.handler;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息处理Handler的父类
 *

 */
public abstract class AbstractHandler implements WxMpMessageHandler {
    public Logger logger = LoggerFactory.getLogger(getClass());
}
