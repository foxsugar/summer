package com.code.server.login.wechat.handler;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.GameAgentWxService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgentWx;
import com.code.server.db.model.Recommend;
import com.code.server.login.wechat.builder.TextBuilder;
import com.code.server.redis.service.RedisManager;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private GameAgentWxService gameAgentWxService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private UserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        // 获取微信用户基本信息
        WxMpUser userWxInfo = weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);

        //事件KEY值，qrscene_为前缀，后面为二维码参数值

        if (userWxInfo != null) {
            // TODO 可以添加关注用户到本地
            GameAgentWx gameAgentWx = new GameAgentWx();
            gameAgentWx.setUnionId(userWxInfo.getUnionId());
            gameAgentWx.setOpenId(userWxInfo.getOpenId());
            gameAgentWxService.getGameAgentWxDao().save(gameAgentWx);
        }

        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = handleSpecial(wxMessage,userWxInfo,weixinService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        try {
            return new TextBuilder().build("感谢关注", wxMessage, weixinService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage, WxMpUser wxMpUser,WxMpService wxMpService) throws Exception {

        //如果是扫码登录
        String eventKey = wxMessage.getEventKey();
        if (eventKey == null || "".equals(eventKey)) {
            return null;
        }

        String[] s = eventKey.split("_");
        if ("qrscene".equals(s[0])) {
            String referrerUnionId = s[1];


            String unionId = wxMpUser.getUnionId();

            //这个人是否已经点过
            //todo 自己不能推荐自己
            Recommend recommend = recommendService.getRecommendDao().getByUnionId(unionId);
            long agentId = userService.getUserDao().getIdByOpenId(referrerUnionId);
            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
            if (agentBean == null) {
                return null;
            }


            boolean isSelf = referrerUnionId.equals(unionId);
            if (recommend == null && !isSelf) {
                recommend = new Recommend();
                recommend.setUnionId(unionId).setAgentId(agentId);
                //保存
                recommendService.getRecommendDao().save(recommend);

                //通知 代理 有人绑定他
                String name = wxMpUser.getNickname();

                wxMpService.getKefuService().sendKefuMessage(
                        WxMpKefuMessage
                                .TEXT()
                                .toUser(agentBean.getOpenId())
                                .content(name + "扫您的专属二维码,成功绑定")
                                .build());

            }

        }

        //TODO
        return null;
    }


//    接收到请求消息，内容：【WxMpXmlMessage[
//    toUser=gh_baceceb30e04
//            fromUser=oLCTw1PrkCjnL392PIQdJJehXBx0
//    createTime=1527561567
//    msgType=event
//            event=subscribe
//    eventKey=qrscene_outO70Z7ur3g1RV0FoHGi7UkU0Og
//            ticket=gQHL8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyLUJhXzBNSUFmTWsxMDAwME0wN1YAAgT_rwdbAwQAAAAA
//    scanCodeInfo=ScanCodeInfo[<all null values>]
//    sendPicsInfo=SendPicsInfo[
//    picList=[]
//            ]
}
