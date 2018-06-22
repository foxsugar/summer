package com.code.server.login.action;

import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.*;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.GameAgentWx;
import com.code.server.db.model.Recommend;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.AgentService;
import com.code.server.login.service.GameUserService;
import com.code.server.redis.service.AgentRedisService;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
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
public class WechatAction extends Cors {

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
    private UserService userService;

    @Autowired
    private WxMpMessageRouter router;

    @Autowired
    private AgentRedisService agentRedisService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private GameAgentWxService gameAgentWxService;

    @Autowired
    private AgentService agentService;

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";


    @RequestMapping(value = "/jsapiparam")
    @ResponseBody
    public AgentResponse wxJs(@RequestParam("url") String url) throws WxErrorException {
        return new AgentResponse().setData(wxMpService.createJsapiSignature(url));
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
        String url = "http://" + serverConfig.getDomain() + "/game/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/authorize_base")
    public String authorize_base(@RequestParam("returnUrl") String returnUrl) {
        System.out.println("授权---------------");
        //1. 配置
        //2. 调用方法
        String url = "http://" + serverConfig.getDomain() + "/game/wechat/clickLink";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }


    /**
     * 点击分享链接
     *
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
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
            if (agentBean == null) return;


            String unionId = wxMpUser.getUnionId();
            //这个人是否已经点过

            //通知 代理 有人绑定他
            boolean isSelf = agentBean.getUnionId().equals(unionId);
            String name = wxMpUser.getNickname();
            if (!isSelf) {

                wxMpService.getKefuService().sendKefuMessage(
                        WxMpKefuMessage
                                .TEXT()
                                .toUser(agentBean.getOpenId())
                                .content(name+"已点击您的专属链接")
                                .build());
            }

            //这个人如果已经是玩家 并且玩家没有上级 那么成为这个人的下级
            Integer refereeId = userService.getUserDao().getRefereeByOpenId(unionId);
            if(refereeId != null){

                long userId = 0;
                String uid = RedisManager.getUserRedisService().getUserIdByOpenId(unionId);
                //玩家是不是代理
                boolean userIsAgnet = true;
                if (uid != null) {//玩家在内存里
                    userId = Long.valueOf(uid);
                    if (!RedisManager.getAgentRedisService().isExit(userId)) {
                        userIsAgnet = false;
                        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
                        //绑定代理
                        userBean.setReferee((int)agentId);
                        GameUserService.saveUserBean(userId);
                    }

                }else{

                    //玩家在数据库
                    User user = userService.getUserByOpenId(unionId);
                    if (!RedisManager.getAgentRedisService().isExit(user.getId())) {
                        userIsAgnet = false;
                        user.setReferee((int) agentId);
                    }
                }
                //玩家不是代理
                if (!userIsAgnet) {
                    //代理添加下级
                    agentBean.getChildList().add(userId);
                    //加入保存队列
                    RedisManager.getAgentRedisService().updateAgentBean(agentBean);
                }

            }else{
                Recommend recommend = recommendService.getRecommendDao().getByUnionId(unionId);



                if (recommend == null && !isSelf) {
                    recommend = new Recommend();
                    recommend.setUnionId(unionId).setAgentId(agentId);
                    //保存
                    recommendService.getRecommendDao().save(recommend);

                    wxMpService.getKefuService().sendKefuMessage(
                            WxMpKefuMessage
                                    .TEXT()
                                    .toUser(agentBean.getOpenId())
                                    .content(name+" 成功绑定")
                                    .build());

                }
            }


            //处理跳转
            handle_link_redirect(agentId, response);


        } catch (WxErrorException e) {
            logger.error("【clickLink】{}", e);
        }

    }


    private void handle_link_redirect(long agentId, HttpServletResponse response) throws IOException {

        //二维码 和 图片 放到cookie里
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        //cookie 里的值
        Map<String, Object> info = new HashMap<>();
        info.put("icon", agentBean.getImage());
        info.put("qr", agentBean.getQrTicket());
        String sid = "" + System.currentTimeMillis() + "_" + new Random().nextInt(999999);

        String json = JsonUtil.toJson(info);
        json = URLEncoder.encode(json);
        Cookie cookie = new Cookie("info" + sid, json);
        cookie.setDomain(serverConfig.getDomain());
        cookie.setPath("/");
        //过期时间 30s
        cookie.setMaxAge(300);
        response.addCookie(cookie);

        String url = MessageFormat.format("http://" + serverConfig.getDomain() + "/agent/#/sharelink?id={0}&sid={1}", ""+agentId, sid);

        response.sendRedirect(url);


    }

    @GetMapping("/userInfo")
    public void userInfo(@RequestParam("code") String code,
                         @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("获取信息");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
//            String unionId = wxMpUser.getUnionId();
//            String openId = wxMpUser.getOpenId();


//        9_BFOoh64g8jIPNzEczVfVZzUJrHKeidD3ihR8MEqNyOhBezx6BTOrb79e7wZlIS1LL5xWdNPSXhrW6p5KgZ0R9Q


            switch (state) {
                case "loginAgent":
                    handleLoginAgent(wxMpUser, request, response);
                    break;
                case "getReferralLink":
                    getReferralLink(wxMpUser, request, response);
                    break;
                case "charge":
                    handle_charge(wxMpUser, request, response);
                    break;

            }

        } catch (WxErrorException e) {
            logger.error("【微信网页授权】{}", e);
        }
    }


    private void handleLoginAgent(WxMpUser wxMpUser, HttpServletRequest request, HttpServletResponse response) throws IOException, WxErrorException {

        Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(wxMpUser.getUnionId());
        if (agentId == null || agentId == 0) {
            //不是代理
            wxMpService.getKefuService().sendKefuMessage(
                    WxMpKefuMessage
                            .TEXT()
                            .toUser(wxMpUser.getOpenId())
                            .content("您还不是代理")
                            .build());
            System.out.println(request.getRequestURL());
            response.getOutputStream().write("您不是代理".getBytes());
            return;
        }


        //设置cookie
        Map<String, String> agent = getAgentByToken(request);
        if (agent == null) {
            setTokenCookie2Redis(wxMpUser, response, agentId);
        }

        String url = "http://" + serverConfig.getDomain() + "/agent/#/";
        response.sendRedirect(url);
    }

    private void getReferralLink(WxMpUser wxMpUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //已经有推广链接 直接跳转
        Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(wxMpUser.getUnionId());
        if (agentId != null && agentId != 0) {

            handle_link_redirect(agentId, response);
        }


    }

    private void handle_charge(WxMpUser wxMpUser, HttpServletRequest request, HttpServletResponse response) throws IOException, WxErrorException {
        Long userId = userService.getUserDao().getIdByOpenId(wxMpUser.getUnionId());

        if (userId == null || userId == 0) {
            //不是代理
            wxMpService.getKefuService().sendKefuMessage(
                    WxMpKefuMessage
                            .TEXT()
                            .toUser(wxMpUser.getOpenId())
                            .content("您还不是玩家")
                            .build());
            response.getOutputStream().write("您还不是玩家".getBytes("utf-8"));
            return;
        }

        //设置cookie
        Map<String, String> agent = getAgentByToken(request);
        if (agent == null) {
            setTokenCookie2Redis(wxMpUser, response, userId);
        }

        long time = System.currentTimeMillis();
        String url = "http://" + serverConfig.getDomain() + "/agent/#/charge?time="+time;
        response.sendRedirect(url);
    }

    public void setTokenCookie2Redis(WxMpUser wxMpUser, HttpServletResponse response, long agentId) {
        Map<String, String> rd = new HashMap<>();
        rd.put("agentId", "" + agentId);
        rd.put("openId", wxMpUser.getOpenId());
        rd.put("unionId", wxMpUser.getUnionId());

        int timeout = 1800;//30分钟
        //设置redis
        String token = "" + IdWorker.getDefaultInstance().nextId();
        RedisManager.getAgentRedisService().setAgentToken(token, rd, timeout);

        //todo token
        //todo 过期时间
        Cookie cookie = new Cookie(AGENT_COOKIE_NAME, token);
        cookie.setDomain(serverConfig.getDomain());
        cookie.setPath("/");
        cookie.setMaxAge(timeout);
        response.addCookie(cookie);
    }

    public static Map<String, String> getAgentByToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie cookie = null;
        for (Cookie c : cookies) {
            if (AGENT_COOKIE_NAME.equals(c.getName())) {
                cookie = c;
            }
        }
        if (cookie != null) {
            return RedisManager.getAgentRedisService().getAgentByToken(cookie.getValue());
        }

        return null;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }


    @RequestMapping("/toAgent")
    @ResponseBody
    public AgentResponse toAgent(long userId) {

        AgentResponse agentResponse = new AgentResponse();


        agentService.change2Agent(userId);

        return agentResponse;
    }

    @RequestMapping("/toUser")
    @ResponseBody
    public AgentResponse toUser(long userId) {

        AgentResponse agentResponse = new AgentResponse();


        agentService.change2Player(userId);

        return agentResponse;
    }

    @RequestMapping("/toPartner")
    @ResponseBody
    public AgentResponse toPartner(long userId) {

        AgentResponse agentResponse = new AgentResponse();


        agentService.change2Partner(userId);

        return agentResponse;
    }

    @RequestMapping("/changeAgent")
    @ResponseBody
    public AgentResponse changeAgent(long agentId, long newAgentId) {

        AgentResponse agentResponse = new AgentResponse();


        agentService.changeAgent(agentId, newAgentId);

        return agentResponse;
    }


    @RequestMapping("/addChild")
    @ResponseBody
    public AgentResponse addChild(long agentId, long userId) {

        AgentResponse agentResponse = new AgentResponse();

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        if (agentBean == null) {
            return new AgentResponse().setData("不是代理");
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setReferee((int)agentId);
        }else{
            User user = userService.getUserByUserId(userId);
            user.setReferee((int) agentId);

        }
        AgentBean userAgentBean = RedisManager.getAgentRedisService().getAgentBean(userId);
        //自己也是代理
        if (userAgentBean != null) {
            userAgentBean.setParentId(agentId);
            userAgentBean.setPartnerId(agentBean.getPartnerId());
            RedisManager.getAgentRedisService().updateAgentBean(userAgentBean);
        }


        agentBean.getChildList().add(userId);

        RedisManager.getAgentRedisService().updateAgentBean(agentBean);



        return agentResponse;
    }



    @RequestMapping("/becomeAgent")
    @ResponseBody
    public AgentResponse becomeAgent(long userId) {

        AgentResponse agentResponse = new AgentResponse();


        AgentBean agentBean = agentRedisService.getAgentBean(userId);

        //todo 之前是代理
        if (agentBean != null) {
            return new AgentResponse().setCode(ErrorCode.ALREADY_AGENT);
        }


        //之前不是代理

        GameAgent gameAgent = new GameAgent();
        gameAgent.setId(userId);

        //推荐
        String unionId = userService.getUserDao().getOpenIdById(userId);
        Recommend recommend = recommendService.getRecommendDao().findOne(unionId);

        GameAgentWx gameAgentWx = gameAgentWxService.getGameAgentWxDao().findOne(unionId);

        if (gameAgentWx == null) {
            return new AgentResponse().setCode(ErrorCode.NOT_WX_USER);
        }

        String openId= gameAgentWx.getOpenId();

        gameAgent.setOpenId(openId);
        gameAgent.setId(userId);
        gameAgent.setUnionId(unionId);
        //有推荐
        if (recommend != null) {
            long parentId = recommend.getAgentId();

            AgentBean parent = agentRedisService.getAgentBean(parentId);
            //上级代理存在
            if (parent != null) {
                //和上级的partner是同一个
                gameAgent.setPartnerId(parent.getPartnerId());
                gameAgent.setParentId(parentId);
                gameAgent.setIsPartner(0);
                //上级代理加一个child
                parent.getChildList().add(userId);
            }
        }

        //保存到数据库
        gameAgentService.getGameAgentDao().save(gameAgent);
        agentBean = AgentService.gameAgent2AgentBean(gameAgent);
        //保存的reids
        agentRedisService.setAgent2Redis(agentBean);


        return agentResponse;
    }


    @RequestMapping("/getUserInfo")
    @ResponseBody
    public AgentResponse getUserInfo(HttpServletRequest request) {
        Map<String, String> map = getAgentByToken(request);
        AgentResponse agentResponse = new AgentResponse();
        if(map == null) return agentResponse.setCode(ErrorCode.NOT_LOGIN);
        long userId = Long.valueOf(map.get("agentId"));
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        Map<String, Object> result = new HashMap<>();
        if (userBean != null) {
            result.put("id", userBean.getId());
            result.put("name", userBean.getUsername());
            result.put("money", userBean.getMoney());
            result.put("gold", userBean.getGold());
        }else{
            User user = userService.getUserByUserId(userId);
            if (user != null) {
                result.put("id", user.getId());
                result.put("name", user.getUsername());
                result.put("money", user.getMoney());
                result.put("gold", user.getGold());
            }
        }

        return agentResponse.setData(result);

    }


    @RequestMapping("/getAgentQr")
    @ResponseBody
    public AgentResponse getAgentQr(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        AgentResponse agentResponse = new AgentResponse();
        long agentId = Long.valueOf(request.getParameter("agentId"));
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        map.put("qr", agentBean.getQrTicket());
        map.put("icon", agentBean.getImage());
        return agentResponse.setData(map);
    }
}
