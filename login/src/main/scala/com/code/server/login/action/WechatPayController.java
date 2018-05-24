package com.code.server.login.action;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.config.WechatConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.util.XMLUtil;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.IdWorker;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/5/14.
 */
@RestController
@RequestMapping("/wechat/pay")
public class WechatPayController {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayController.class);

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private UserService userService;

    private IdWorker idWorker;


    private long createOrderId() {
        if (idWorker == null) {
            idWorker = new IdWorker(serverConfig.getServerId(), 1);
        }
        return idWorker.nextId();
    }

    @ResponseBody
    @RequestMapping(value = "preOrder")
    public AgentResponse pay(HttpServletRequest request) throws Exception {
        int money = Integer.valueOf(request.getParameter("money"));
        int chargeType = Integer.valueOf(request.getParameter("chargeType"));

        Map<String, String> agent = WechatAction.getAgentByToken(request);
        if(agent == null) return new AgentResponse().setCode(1000);
        //todo 实现
        String openId = agent.get("openId");
        long userId = Long.valueOf(agent.get("agentId"));
//        String ip = request.getHeader("X-Forwarded-For");
        String ip = getIpAddr(request);
        //元转成分

        AgentResponse agentResponse = new AgentResponse();
        int totalFee = money * 100;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setBody("充值");
            String orderId = "" + createOrderId();
            orderRequest.setOutTradeNo(orderId);
            orderRequest.setTotalFee(totalFee);//元转成分
            orderRequest.setOpenid(openId);
            orderRequest.setSpbillCreateIp(ip);
//            orderRequest.setTimeStart("yyyyMMddHHmmss");
//            orderRequest.setTimeExpire("yyyyMMddHHmmss");
            orderRequest.setTradeType("JSAPI");

            String url = "http://" + serverConfig.getDomain() + "/wechat/pay/pay";
            orderRequest.setNotifyUrl(url);
            wxPayService.createOrder(orderRequest);
            agentResponse.data = wxPayService.createOrder(orderRequest);

            Charge charge = new Charge();
            charge.setOrderId(orderId);
            charge.setUserid(userId);
            charge.setMoney(money);
            charge.setMoney_point(money);
//            charge.setOrigin(origin);
            charge.setStatus(0);
//            charge.setSign(paySign);
//            charge.setSp_ip(spIp);
            charge.setRecharge_source("1");
            charge.setChargeType(chargeType);
            charge.setCreatetime(new Date());
            chargeService.save(charge);


            return agentResponse;
        } catch (Exception e) {
//            log.error("微信支付失败！订单号：{},原因:{}", orderNo, e.getMessage());
            e.printStackTrace();
            agentResponse.code = 12;
            return agentResponse;
//            return Result.fail("支付失败，请稍后重试！");
        }
    }


    /**
     * 微信通知支付结果的回调地址，notify_url
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "pay")
    public void getJSSDKCallbackData(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            synchronized (this) {
                Map<String, String> kvm = XMLUtil.parseRequestXmlToMap(request);
                if (SignUtils.checkSign(kvm, null, this.wechatConfig.getMchKey())) {
                    if (kvm.get("result_code").equals("SUCCESS")) {
                        //TODO(user) 微信服务器通知此回调接口支付成功后，通知给业务系统做处理


                        String orderId = kvm.get("out_trade_no");
                        Charge charge = chargeService.getChargeByOrderid(orderId);
                        if (0 == charge.getStatus()) {
//                            System.out.println("修改订单状态");
                            //修改支付订单状态 已支付
                            charge.setStatus(1);
                            charge.setCallbacktime(new Date());
                            chargeService.save(charge);



                            UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());


                            int addMoney = Integer.valueOf(kvm.get("total_fee"));
                            //
//                            if (chargeMoney.containsKey(money_total)) {
//                                addMoney = chargeMoney.get(money_total);
//                            }

                            if (UserBeanRedis != null) {
                                if (charge.getChargeType() == 0) {
                                    userRedisService.addUserMoney(charge.getUserid(), addMoney);
                                } else {
                                    userRedisService.addUserGold(charge.getUserid(), addMoney);
                                }
                            } else {
                                //查询玩家
                                User user = userService.getUserByUserId(charge.getUserid());
                                System.out.println("修改玩家豆豆");
                                //修改玩家豆豆
                                if (charge.getChargeType() == 0) {

                                    user.setMoney(user.getMoney() + addMoney);
                                } else {
                                    user.setGold(user.getGold() + addMoney);
                                }
                                userService.save(user);
                            }


                            System.out.println("通知客户端刷新充值");
                            Map<String, String> rs = new HashMap<>();
                            MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
                        }


                        logger.info("out_trade_no: " + kvm.get("out_trade_no") + " pay SUCCESS!");
                        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[ok]]></return_msg></xml>");
                    } else {
                        this.logger.error("out_trade_no: "
                                + kvm.get("out_trade_no") + " result_code is FAIL");
                        response.getWriter().write(
                                "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[result_code is FAIL]]></return_msg></xml>");
                    }
                } else {
                    response.getWriter().write(
                            "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[check signature FAIL]]></return_msg></xml>");
                    this.logger.error("out_trade_no: " + kvm.get("out_trade_no")
                            + " check signature FAIL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取访问者IP
     *
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     *
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) throws Exception{
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
// 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    private String getOpenId() {
        return null;
    }

}
