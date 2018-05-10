package com.code.server.login.wechat.handler;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.login.wechat.builder.TextBuilder;
import com.code.server.redis.service.RedisManager;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MenuHandler extends AbstractHandler {

    @Autowired
    private GameAgentService gameAgentService;
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
                case "LINK":
                    return handle_link(wxMessage,weixinService);
                case "CLEAR":



            }
        }

        return WxMpXmlOutMessage.TEXT().content(msg)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
    }


    private WxMpXmlOutMessage handle_clear(WxMpXmlMessage wxMessage,WxMpService wxService){
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            String unionId = wxMpUser.getUnionId();
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

        } catch (WxErrorException e) {
            logger.error("提现出错",e);
        }
        return null;
    }

    /**
     * 处理链接
     * @param wxMessage
     * @param wxService
     * @return
     */
    private WxMpXmlOutMessage handle_link(WxMpXmlMessage wxMessage,WxMpService wxService) {

        try {
            WxMpUser wxMpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            String unionId = wxMpUser.getUnionId();
            Long agentId = gameAgentService.getGameAgentDao().getUserIdByUnionId(unionId);
            if (agentId == null || agentId == 0) {
                return new TextBuilder().build("代理不存在", wxMessage, wxService);
            }
            //gameAgent 是否已经生成ticket
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

            if(agentBean.getQrTicket() == null || "".equals(agentBean.getQrTicket())){
                //根据unionId生成二维码
                WxMpQrCodeTicket ticket = wxService.getQrcodeService().qrCodeCreateLastTicket(unionId);
                agentBean.setQrTicket(ticket.getTicket());
                RedisManager.getAgentRedisService().updateAgentBean(agentBean);

            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setTitle("您有如下专属代理链接");
        item.setDescription("点击进入专属界面");


        WxMpXmlOutNewsMessage.Item item1 = new WxMpXmlOutNewsMessage.Item();
        //todo 展示二维码 链接
        item1.setPicUrl("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
        item1.setTitle("凤凰划水");
//          item1.setDescription("点击进入专属界面");
        item.setUrl("url");

        WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .addArticle(item, item1)
                .build();
        return m;
    }

}
