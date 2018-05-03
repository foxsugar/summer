package com.code.server.login.action;

import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.WechatCoreService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    protected WechatCoreService coreService;

    @Autowired
    protected WxMpConfigStorage configStorage;

    @RequestMapping(value = "/core")
    public void wechatCore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        String oto = configStorage.getToken();
        if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echoStr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echoStr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            String echoStrOut = String.copyValueOf(echoStr.toCharArray());
            response.getWriter().println(echoStrOut);
            return;
        }

        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type"))
                ? "raw"
                : request.getParameter("encrypt_type");

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
            if (outMessage == null) {
                response.getWriter().write("");
            } else {
                response.getWriter().write(outMessage.toXml());
            }
            return;
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                    request.getInputStream(), this.configStorage, timestamp, nonce,
                    msgSignature);
            this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
            if (outMessage == null) {
                response.getWriter().write("");
            } else {
                response.getWriter().write(outMessage.toEncryptedXml(this.configStorage));
            }

            return;
        }

        response.getWriter().println("不可识别的加密类型");
    }


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
            String s = wxMpUser.getUnionId();
            WxMpUser wxMpUser1 = wxMpService.getUserService().userInfo(wxMpUser.getOpenId());
            WxMpKefuMessage wxMpKefuMessage = new WxMpKefuMessage();
            wxMpKefuMessage.setToUser(wxMpUser.getOpenId());
            wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);

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
