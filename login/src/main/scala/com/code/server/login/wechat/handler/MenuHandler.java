package com.code.server.login.wechat.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MenuHandler extends AbstractHandler {

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

    if(MenuButtonType.CLICK.equalsIgnoreCase(wxMessage.getEvent())){
      switch (wxMessage.getEventKey()){
        case "LINK":
          WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
//          item.setPicUrl("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
          item.setTitle("您有如下专属代理链接");
          item.setDescription("点击进入专属界面");
//          item.setUrl("url");


          WxMpXmlOutNewsMessage.Item item1 = new WxMpXmlOutNewsMessage.Item();
          item1.setPicUrl("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
          item1.setTitle("凤凰划水");
//          item1.setDescription("点击进入专属界面");
          item.setUrl("url");

          WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
                  .fromUser(wxMessage.getToUser())
                  .toUser(wxMessage.getFromUser())
                  .addArticle(item,item1)
                  .build();
          return m;

      }
    }

    return WxMpXmlOutMessage.TEXT().content(msg)
        .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
        .build();
  }


  private void send(){
    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
    item.setDescription("描述");
    item.setPicUrl("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
    item.setTitle("标题");
    item.setUrl("url");

    WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
            .fromUser("fromUser")
            .toUser("toUser")
            .addArticle(item)
            .build();
  }

}
