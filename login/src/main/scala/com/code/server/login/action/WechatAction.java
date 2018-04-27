package com.code.server.login.action;

import com.code.server.login.config.ServerConfig;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * Created by sunxianping on 2018/3/22.
 */
@Controller
@RequestMapping(value = "/wechat")
public class WechatAction extends Cors{

    private static final Logger logger = LoggerFactory.getLogger(WechatAction.class);

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ServerConfig serverConfig;



    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        System.out.println("授权---------------");
        //1. 配置
        //2. 调用方法
        String url ="http://"+ serverConfig.getDomain() + "/game/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }


    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取信息");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            wxMpUser.getUnionId();
        } catch (WxErrorException e) {
            logger.error("【微信网页授权】{}", e);
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();


//        9_BFOoh64g8jIPNzEczVfVZzUJrHKeidD3ihR8MEqNyOhBezx6BTOrb79e7wZlIS1LL5xWdNPSXhrW6p5KgZ0R9Q


        Cookie cookie1 = new Cookie("Admin-Token","Admin-Token");
        cookie1.setDomain(serverConfig.getDomain());
        cookie1.setPath("/");
        response.addCookie(cookie1);

        returnUrl = "http://3348ns.natappfree.cc/wx/#/index";
        returnUrl = "http://ekzgev.natappfree.cc/agent/#/index";
        System.out.println(returnUrl);

//        return "redirect:" + returnUrl + "?openid=" + openId;
        return "redirect:" + returnUrl;
    }

}
