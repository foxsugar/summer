package com.code.server.login.config;

import com.code.server.login.wechat.handler.LogHandler;
import com.code.server.login.wechat.handler.MenuHandler;
import com.code.server.login.wechat.handler.MsgHandler;
import com.code.server.login.wechat.handler.SubscribeHandler;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInRedisConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import static me.chanjar.weixin.common.api.WxConsts.*;


@Component
public class WechatMpConfig {

    @Autowired
    private WechatConfig accountConfig;

    @Autowired
    private JedisPool redisPool;



    @Autowired
    private MenuHandler menuHandler;
    @Autowired
    private MsgHandler msgHandler;

    @Autowired
    private LogHandler logHandler;

    @Autowired
    private SubscribeHandler subscribeHandler;

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
//        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        WxMpInRedisConfigStorage wxMpConfigStorage = new WxMpInRedisConfigStorage(redisPool);
        wxMpConfigStorage.setAppId(accountConfig.getMpAppId());
        wxMpConfigStorage.setSecret(accountConfig.getMpAppSecret());
        wxMpConfigStorage.setToken(accountConfig.getToken());
        wxMpConfigStorage.setAesKey(accountConfig.getAesKey());
        return wxMpConfigStorage;
    }



    @Bean
    public WxMpMessageRouter router(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();



        // 自定义菜单事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
                .event(MenuButtonType.CLICK).handler(this.getMenuHandler()).end();

        // 点击菜单连接事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE).handler(this.getSubscribeHandler())
                .end();


        // 扫码事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.SCAN).handler(this.getScanHandler()).end();

        // 默认
        newRouter.rule().async(false).handler(this.getMsgHandler()).end();

        return newRouter;
    }


    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public WechatMpConfig setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
        return this;
    }

    public MsgHandler getMsgHandler() {
        return msgHandler;
    }

    public WechatMpConfig setMsgHandler(MsgHandler msgHandler) {
        this.msgHandler = msgHandler;
        return this;
    }

    public SubscribeHandler getSubscribeHandler() {
        return subscribeHandler;
    }

    public WechatMpConfig setSubscribeHandler(SubscribeHandler subscribeHandler) {
        this.subscribeHandler = subscribeHandler;
        return this;
    }
}
