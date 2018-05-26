package com.code.server.login.wechat.handler;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.model.Charge;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.config.WechatConfig;
import com.code.server.login.util.Sha1Util;
import com.code.server.login.wechat.builder.TextBuilder;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.Utils;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.service.WxPayService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MenuHandler extends AbstractHandler {

    @Autowired
    private GameAgentService gameAgentService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private WxPayService payService;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private ChargeService chargeService;

    private IdWorker idWorker;


    private long createOrderId() {
        if (idWorker == null) {
            idWorker = new IdWorker(serverConfig.getServerId(), 1);
        }
        return idWorker.nextId();
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        String msg = String.format("type:%s, event:%s, key:%s",
                wxMessage.getMsgType(), wxMessage.getEvent(),
                wxMessage.getEventKey());
        if (MenuButtonType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        }


        if (MenuButtonType.CLICK.equalsIgnoreCase(wxMessage.getEvent())) {
            switch (wxMessage.getEventKey()) {
                case "LOGIN_AGENT":

                    return handle_login_agent(wxMessage, weixinService);
                case "LINK":
                    return handle_link(wxMessage, weixinService);
                case "CLEAR":
                    return handle_clear(wxMessage, weixinService);
                case "DOWNLOAD_GAME":

                case "KEFU_ONLINE":
                    return handle_kefu(wxMessage, weixinService);
            }
        }

        return WxMpXmlOutMessage.TEXT().content(msg)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
    }

    private WxMpXmlOutMessage handle_login_agent(WxMpXmlMessage wxMessage, WxMpService wxService) {
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());


            String unionId = wxMpUser.getUnionId();
            Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(wxMpUser.getUnionId());
//            if (agentId == null || agentId == 0) {
//                //不是代理
//                return new TextBuilder().build("您还不是代理", wxMessage, wxService);
//            }else{
                HttpClient httpclient = HttpClients.createDefault();

                // 创建http GET请求
                HttpGet httpGet = new HttpGet("http://fap4k2.natappfree.cc/game/wechat/authorize?returnUrl=loginAgent");
                httpclient.execute(httpGet);
                return null;
//            }
        } catch (WxErrorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private WxMpXmlOutMessage handle_clear(WxMpXmlMessage wxMessage, WxMpService wxService) {
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            String unionId = wxMpUser.getUnionId();
            String openId = wxMpUser.getOpenId();
            Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(unionId);
            if (agentId == null || agentId == 0) {
                return new TextBuilder().build("代理不存在", wxMessage, wxService);
            }
            //gameAgent 是否已经生成ticket
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
            //金额小于 100 不能提
            if (agentBean.getRebate() < 100) {
                return new TextBuilder().build("金额小于100,不能提现", wxMessage, wxService);
            }

            double rebate = agentBean.getRebate();
            //不能大于20000
            if (rebate > 20000) {

            }

            //todo 扣 7%

            double amount = rebate * 100;

            long tradeId = createOrderId();

            String ip = Utils.getLocalIp();

            EntPayRequest wxEntPayRequest = new EntPayRequest();
            wxEntPayRequest.setAppid(wechatConfig.getMpAppId());
            wxEntPayRequest.setMchId(wechatConfig.getMchId());
            wxEntPayRequest.setNonceStr(Sha1Util.getNonceStr());
            wxEntPayRequest.setPartnerTradeNo(""+tradeId);
            wxEntPayRequest.setOpenid(openId);
            wxEntPayRequest.setCheckName("NO_CHECK");
            //金额 为分
            wxEntPayRequest.setAmount((int)amount);
            wxEntPayRequest.setDescription("提现");
            wxEntPayRequest.setSpbillCreateIp(ip);

            try {
                EntPayResult wxEntPayResult = payService.getEntPayService().entPay(wxEntPayRequest);
                if ("SUCCESS".equals(wxEntPayResult.getResultCode().toUpperCase())
                        && "SUCCESS".equals(wxEntPayResult.getReturnCode().toUpperCase())) {
                    this.logger.info("企业对个人付款成功！\n付款信息：\n" + wxEntPayResult.toString());


                    RedisManager.getAgentRedisService().addRebate(agentId, -rebate);

                    //充值记录
                    Charge charge = new Charge();
                    charge.setOrderId(""+tradeId);
                    charge.setUserid(agentId);
                    charge.setMoney(rebate);
                    charge.setMoney_point(rebate);
                    charge.setStatus(1);
                    charge.setSp_ip(ip);
                    charge.setRecharge_source("11");
                    //充值类型
                    charge.setChargeType(0);
                    charge.setCreatetime(new Date());

                    chargeService.save(charge);

                    //发送消息
                    return new TextBuilder().build("提现成功,共提现"+rebate, wxMessage, wxService);

                } else {
                    this.logger.error("err_code: " + wxEntPayResult.getErrCode()
                            + "  err_code_des: " + wxEntPayResult.getErrCodeDes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



        } catch (WxErrorException e) {
            logger.error("提现出错", e);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 处理链接
     *
     * @param wxMessage
     * @param wxService
     * @return
     */
    private WxMpXmlOutMessage handle_link(WxMpXmlMessage wxMessage, WxMpService wxService) {
        Long agentId = null;
        try {
            WxMpUser wxMpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            String unionId = wxMpUser.getUnionId();
            agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(unionId);
            if (agentId == null || agentId == 0) {
                return new TextBuilder().build("代理不存在", wxMessage, wxService);
            }
            //gameAgent 是否已经生成ticket
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

            agentBean.setImage(wxMpUser.getHeadImgUrl());
            if (agentBean.getQrTicket() == null || "".equals(agentBean.getQrTicket())) {
                //根据unionId生成二维码
                WxMpQrCodeTicket ticket = wxService.getQrcodeService().qrCodeCreateLastTicket(unionId);
                agentBean.setQrTicket(ticket.getTicket());

            }
            RedisManager.getAgentRedisService().updateAgentBean(agentBean);


        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setTitle("您有如下专属代理链接");
        item.setDescription("点击进入专属界面");


        WxMpXmlOutNewsMessage.Item item1 = new WxMpXmlOutNewsMessage.Item();
        //todo 展示二维码 链接

        item1.setPicUrl("https://mmbiz.qpic.cn/mmbiz_png/wj1STzkg04h46BuribmuoJnsMQgc2m70558p3mE91j6zq4sph6RavCicfUiahTSRj4CVRSRN9ecdJKic6ysZeBCZiag/0?wx_fmt=png");
        item1.setTitle("凤凰划水");
//          item1.setDescription("点击进入专属界面");
//        String url = "http://" + serverConfig.getDomain() + "/game/wechat/clickLink";
        String sid = "" + System.currentTimeMillis() + "_" + new Random().nextInt(999999);
        String url = MessageFormat.format("http://" + serverConfig.getDomain() +"/agent/#/sharelink?id={0}&sid={1}", ""+agentId, sid);
        item1.setUrl(url);

        WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .addArticle(item, item1)
                .build();
        return m;
    }


    private WxMpXmlOutMessage handle_kefu(WxMpXmlMessage wxMessage, WxMpService wxService){

        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setTitle("客服微信号:xxxxx");
        item.setPicUrl("https://mmbiz.qpic.cn/mmbiz_png/wj1STzkg04h46BuribmuoJnsMQgc2m705YQuY91HglHYhZVzZs971Lb6HVfLHCweYc4QeddBAZVNdCAj5F86fog/0?wx_fmt=png");
//        item.setDescription("点击进入专属界面");


        WxMpXmlOutNewsMessage.Item item1 = new WxMpXmlOutNewsMessage.Item();
        //todo 展示二维码 链接

        item1.setPicUrl("https://mmbiz.qpic.cn/mmbiz_png/wj1STzkg04h46BuribmuoJnsMQgc2m70558p3mE91j6zq4sph6RavCicfUiahTSRj4CVRSRN9ecdJKic6ysZeBCZiag/0?wx_fmt=png");
        item1.setTitle("客服微信号");


        WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .addArticle(item, item1)
                .build();
        return m;
    }

}
