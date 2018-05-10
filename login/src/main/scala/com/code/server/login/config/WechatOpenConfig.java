package com.code.server.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;


@Component
public class WechatOpenConfig {

    @Autowired
    private WechatConfig accountConfig;

    @Autowired
    private JedisPool redisPool;

//    @Bean
//    public WxMpService wxOpenService() {
//        WxMpService wxOpenService = new WxMpServiceImpl();
//        wxOpenService.setWxMpConfigStorage(wxOpenConfigStorage());
//        return wxOpenService;
//    }

//    @Bean
//    public WxMpConfigStorage wxOpenConfigStorage() {
////        WxMpInMemoryConfigStorage wxMpInMemoryConfigStorage = new WxMpInMemoryConfigStorage();
//        WxMpInRedisConfigStorage wxMpInMemoryConfigStorage = new WxMpInRedisConfigStorage(redisPool);
//        wxMpInMemoryConfigStorage.setAppId(accountConfig.getOpenAppId());
//        wxMpInMemoryConfigStorage.setSecret(accountConfig.getOpenAppSecret());
//        return wxMpInMemoryConfigStorage;
//    }
}
