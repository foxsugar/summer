package com.code.server.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {

    /**
     * 公众平台id
     */
    private String mpAppId;

    /**
     * 公众平台密钥
     */
    private String mpAppSecret;

    /**
     * 开放平台id
     */
    private String openAppId;

    /**
     * 开放平台密钥
     */
    private String openAppSecret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String keyPath;

    /**
     * 微信支付异步通知地址
     */
    private String notifyUrl;


    public String getMpAppId() {
        return mpAppId;
    }

    public WechatConfig setMpAppId(String mpAppId) {
        this.mpAppId = mpAppId;
        return this;
    }

    public String getMpAppSecret() {
        return mpAppSecret;
    }

    public WechatConfig setMpAppSecret(String mpAppSecret) {
        this.mpAppSecret = mpAppSecret;
        return this;
    }

    public String getOpenAppId() {
        return openAppId;
    }

    public WechatConfig setOpenAppId(String openAppId) {
        this.openAppId = openAppId;
        return this;
    }

    public String getOpenAppSecret() {
        return openAppSecret;
    }

    public WechatConfig setOpenAppSecret(String openAppSecret) {
        this.openAppSecret = openAppSecret;
        return this;
    }

    public String getMchId() {
        return mchId;
    }

    public WechatConfig setMchId(String mchId) {
        this.mchId = mchId;
        return this;
    }

    public String getMchKey() {
        return mchKey;
    }

    public WechatConfig setMchKey(String mchKey) {
        this.mchKey = mchKey;
        return this;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public WechatConfig setKeyPath(String keyPath) {
        this.keyPath = keyPath;
        return this;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public WechatConfig setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }
}
