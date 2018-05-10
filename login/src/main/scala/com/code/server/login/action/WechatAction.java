package com.code.server.login.action;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.model.Recommend;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    protected WxMpConfigStorage configStorage;

    @Autowired
    private GameAgentService gameAgentService;

    @Autowired
    private WxMpMessageRouter router;

    @Autowired
    private RecommendService recommendService;

    @RequestMapping(value = "/jsapiparam")
    @ResponseBody
    public String wxJs(@RequestParam("url") String url) throws WxErrorException {
        return JsonUtil.toJson(wxMpService.createJsapiSignature(url));
    }

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
            WxMpXmlOutMessage outMessage = this.route(inMessage);
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
            WxMpXmlOutMessage outMessage = this.route(inMessage);
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

    @GetMapping("/authorize_base")
    public String authorize_base(@RequestParam("returnUrl") String returnUrl) {
        System.out.println("授权---------------");
        //1. 配置
        //2. 调用方法
        String url ="http://"+ serverConfig.getDomain() + "/game/wechat/clickLink";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }


    /**
     * 点击分享链接
     * @param code
     * @param state
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/clickLink")
    public void clickLink(@RequestParam("code") String code,
                         @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);

            //state 是id
            long agentId = Long.valueOf(state);
            //代理不存在 直接退出
            if( !RedisManager.getAgentRedisService().isExit(agentId)) return;


            String unionId = wxMpUser.getUnionId();
            //这个人是否已经点过
            Recommend recommend = recommendService.getRecommendDao().getByUnionId(unionId);
            if (recommend == null) {
                recommend = new Recommend();
                recommend.setUnionId(unionId).setAgentId(agentId);
                //保存
                recommendService.getRecommendDao().save(recommend);

                //通知 代理 有人绑定他
                String name = wxMpUser.getNickname();



            }

            //二维码 和 图片 放到cookie里
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

            //cookie 里的值
            Map<String, Object> info = new HashMap<>();
            info.put("icon", agentBean.getImage());
            info.put("qr", agentBean.getQrTicket());
            String sid = ""+System.currentTimeMillis() +"_"+ new Random().nextInt(999999);

            String json = JsonUtil.toJson(info);
            json = URLEncoder.encode(json);
            Cookie cookie = new Cookie("info"+sid,json);
            cookie.setDomain(serverConfig.getDomain());
            cookie.setPath("/");
            //过期时间 30s
            cookie.setMaxAge(30);
            response.addCookie(cookie);

            String url = MessageFormat.format("http://tfdg38.natappfree.cc/agent/#/test?id={0}&sid={1}",agentId,sid);
            response.sendRedirect(url);


        } catch (WxErrorException e) {
            logger.error("【clickLink】{}", e);
        }

    }

    @GetMapping("/userInfo")
    public void userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("获取信息");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String unionId = wxMpUser.getUnionId();
            WxMpUser wxMpUser1 = wxMpService.getUserService().userInfo(wxMpUser.getOpenId());
            WxMpKefuMessage wxMpKefuMessage = new WxMpKefuMessage();
            wxMpKefuMessage.setToUser(wxMpUser.getOpenId());
            wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);

        } catch (WxErrorException e) {
            logger.error("【微信网页授权】{}", e);
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();


//        9_BFOoh64g8jIPNzEczVfVZzUJrHKeidD3ihR8MEqNyOhBezx6BTOrb79e7wZlIS1LL5xWdNPSXhrW6p5KgZ0R9Q


        switch (state) {
            case "loginAgent":
//                handleLoginAgent(response);
                break;
            case "getReferralLink":

                break;
            case "charge":
                break;
            case "clear":
                break;
            case "base":
                response.sendRedirect("http://localhost:8080/#/test");
                System.out.println("hhh");
              break;
        }

//        return "redirect:" + state;
    }


    private void handleLoginAgent(String unionId,HttpServletResponse response) throws IOException {
        //todo token
        Cookie cookie = new Cookie("Admin-Token","Admin-Token");
        cookie.setDomain(serverConfig.getDomain());
        cookie.setPath("/");
        response.addCookie(cookie);
        String url = "http://ekzgev.natappfree.cc/agent/#/index";
        response.sendRedirect(url);


    }

    private void getReferralLink(String unionId,HttpServletResponse response){
        //已经有推广链接 直接跳转
        Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(unionId);
        if (agentId == null || agentId == 0) {

        }


    }



    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }



}
