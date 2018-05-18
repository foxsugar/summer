package com.code.server.login.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by sunxianping on 2018/5/14.
 */
@Component
public class WechatPayConfig {

    @Autowired
    private WechatConfig accountConfig;


    @Bean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig());
        return wxPayService;
    }


    @Bean
    public WxPayConfig wxPayConfig() {
//        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        WxPayConfig wxPayConfig = new WxPayConfig();

        wxPayConfig.setAppId(accountConfig.getMpAppId());
        wxPayConfig.setMchId(accountConfig.getMchId());
        wxPayConfig.setMchKey(accountConfig.getMchKey());
//        wxPayConfig.setSubAppId(accountConfig.getsubAppId);
//        wxPayConfig.setSubMchId(this.subMchId);
        wxPayConfig.setKeyPath(accountConfig.getKeyPath());


        return wxPayConfig;
    }
}
