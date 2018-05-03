package com.code.server.login.config;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInRedisConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;


@Component
public class WechatMpConfig {

    @Autowired
    private WechatConfig accountConfig;

    @Autowired
    private JedisPool redisPool;

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
}
