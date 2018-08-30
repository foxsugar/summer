package com.code.server.login.action;

import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.config.WechatConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.pay.UnifiedOrder;
import com.code.server.login.util.PayUtil;
import com.code.server.login.util.Utils;
import com.code.server.login.util.XMLUtil;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by sunxianping on 2018/5/14.
 */
@RestController
@RequestMapping("/wechat/pay")
public class WechatPayController {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayController.class);

    @Autowired
    @Qualifier("wxPayService")
    private WxPayService wxPayService;

    @Autowired
    @Qualifier("wxAppPayService")
    private WxPayService wxAppPayService;

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

    private static Map<Integer, Integer> moneyPointMap = new HashMap<>();
    private static Map<Integer, Integer> goldPointMap = new HashMap<>();

    private final Object lock = new Object();


    static {
        moneyPointMap.put(1, 1);
        moneyPointMap.put(6, 6);
        moneyPointMap.put(18, 18);
        moneyPointMap.put(30, 30);
        moneyPointMap.put(68, 68);
        moneyPointMap.put(128, 128);


        //金币
        goldPointMap.put(1, 100);
        goldPointMap.put(10, 1000);
        goldPointMap.put(50, 5000);
        goldPointMap.put(100, 10000);
        goldPointMap.put(500, 50000);
        goldPointMap.put(1000, 100000);
    }

    private long createOrderId() {
        if (idWorker == null) {
            idWorker = new IdWorker(serverConfig.getServerId(), 1);
        }
        return idWorker.nextId();
    }

    private Integer getMoneyPoint(int money, int type) {
        if (type == 0) {
            return moneyPointMap.get(money);
        }
        if (type == 1) {
            return goldPointMap.get(money);
        }
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "preOrderApp")
    public Map<String, Object> charge(HttpServletRequest request, String userId, int chargeType, int money,String spIp) throws Exception {

        Map<String, Object> result = new HashMap<>();

        SortedMap<String, String> packageParams = new TreeMap<>();

        String ip = spIp;
        //微信


        int money100 = money * 100;

        Integer moneyPoint = getMoneyPoint(money, chargeType);
        logger.info("充值金额: " + money);
        if (moneyPoint == null) {
            Map<String, Object> m = new HashMap<>();
            m.put("code", 10);
            m.put("params", "参数错误");
            return m;
        }
        logger.info("增加钱数: " + moneyPoint);

        String body = "充值";

        String bodyUTF8 = null;
        try {
            bodyUTF8 = new String(body.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String orderId = "" + createOrderId();
        packageParams.put("appid", serverConfig.getAppId());//appID       应用id
        packageParams.put("mch_id", serverConfig.getMchId());//appID       商户号
        packageParams.put("nonce_str", PayUtil.getRandomStringByLength(32));//32位随机数
        packageParams.put("body", bodyUTF8);//商品描述
        packageParams.put("out_trade_no", orderId);
        packageParams.put("total_fee", "" + money100);//充值金额
        packageParams.put("spbill_create_ip", ip);//终端IP
        packageParams.put("trade_type", "APP");//支付类型
        String url = "http://" + serverConfig.getDomain() + "/game/wechat/pay/payApp";
        packageParams.put("notify_url", url);//通知地址

        String rtn = UnifiedOrder.postCharge(packageParams);

        Element root = PayUtil.ParsingXML(rtn);//解析xmlString


        SortedMap<String, String> secondParams = new TreeMap<>();


        //成功
//        if("SUCCESS".equals(root.elementText("return_code"))){
        //业务成功
        if ("SUCCESS".equals(root.elementText("result_code"))) {
            System.out.println("业务成功");


            secondParams.put("appid", serverConfig.getAppId());
            secondParams.put("partnerid", serverConfig.getMchId());
            secondParams.put("prepayid", root.elementText("prepay_id"));
            secondParams.put("noncestr", root.elementText("nonce_str"));
            secondParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            secondParams.put("package", "Sign=WXPay");

            String paySign = PayUtil.createSign("UTF-8", serverConfig.getKey(), secondParams);

            secondParams.put("sign", paySign);


            //充值记录
            Charge charge = new Charge();

            charge.setOrderId(orderId);
            charge.setUserid(Long.valueOf(userId));
            charge.setMoney(money);
            charge.setMoney_point(moneyPoint);
//            charge.setOrigin(origin);
            charge.setStatus(0);
//            charge.setSign(paySign);
            charge.setSp_ip(ip);
            charge.setRecharge_source("1");
            //充值类型
            charge.setChargeType(chargeType);
            charge.setCreatetime(new Date());

            chargeService.save(charge);


        } else {
            System.out.println("业务失败");
            System.out.println(root.elementText("err_code"));
            //余额不足
            if ("NOTENOUGH".equals(root.elementText("err_code"))) {
                secondParams.put("err_code", root.elementText("err_code"));
                secondParams.put("err_code_des", root.elementText("err_code_des"));
                result.put("code", 10000);
                return result;
                //订单已支付
            } else if ("ORDERPAID".equals(root.elementText("err_code"))) {
                secondParams.put("err_code", root.elementText("err_code"));
                secondParams.put("err_code_des", root.elementText("err_code_des"));
                result.put("code", 11111);
                return result;
                //订单已关闭
            } else if ("ORDERCLOSED".equals(root.elementText("err_code"))) {
                secondParams.put("err_code", root.elementText("err_code"));
                secondParams.put("err_code_des", root.elementText("err_code_des"));
                result.put("code", 10001);
                return result;
            } else {
                result.put("code", 10002);
            }
        }
//        }

        result.put("params", secondParams);
        result.put("code", 0);

        return result;
    }


    @ResponseBody
    @RequestMapping(value = "preOrderApp1")
    public Map<String, Object> pay_app(HttpServletRequest request) throws Exception {
        int money = Integer.valueOf(request.getParameter("money"));
        int chargeType = Integer.valueOf(request.getParameter("chargeType"));
        long userId = Long.valueOf(request.getParameter("userId"));
        String ip = request.getParameter("spIp");


//        String ip = getIpAddr(request);
        //元转成分

        Integer moneyPoint = getMoneyPoint(money, chargeType);
        logger.info("充值金额: " + money);
//        if (moneyPoint == null) return new AgentResponse().setCode(13);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        if (moneyPoint == null) {
            result.put("code", 10);
            result.put("params", "参数错误");
            return result;
        }
        logger.info("增加钱数: " + moneyPoint);


        AgentResponse agentResponse = new AgentResponse();
        int totalFee = money * 100;
        String orderId = "" + createOrderId();
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setBody("充值");
            orderRequest.setOutTradeNo(orderId);
            orderRequest.setTotalFee(totalFee);//元转成分
//            orderRequest.setOpenid(openId);
            orderRequest.setSpbillCreateIp(ip);
//            orderRequest.setTimeStart("yyyyMMddHHmmss");
//            orderRequest.setTimeExpire("yyyyMMddHHmmss");
            orderRequest.setTradeType("APP");

            //notify 地址
            String url = "http://" + serverConfig.getDomain() + "/game/wechat/pay/payApp";

            orderRequest.setNotifyUrl(url);

            //创建订单
            Object rtn = wxAppPayService.createOrder(orderRequest);

            WxPayAppOrderResult orderResult = wxAppPayService.createOrder(orderRequest);
            Map<String, Object> o = new HashMap<>();

            o.put("appid", orderResult.getAppId());
            o.put("partnerid", orderResult.getPartnerId());
            o.put("prepayid", orderResult.getPrepayId());
            o.put("noncestr", orderResult.getNonceStr());
            o.put("timestamp", orderResult.getTimeStamp());
            o.put("package", "Sign=WXPay");
            o.put("sign",  orderRequest.getSign());

            result.put("params", o);

            //充值记录
            Charge charge = new Charge();
            charge.setOrderId(orderId);
            charge.setUserid(userId);
            charge.setMoney(money);
            charge.setMoney_point(moneyPoint);
//            charge.setOrigin(origin);
            charge.setStatus(0);
//            charge.setSign(paySign);
            charge.setSp_ip(ip);
            charge.setRecharge_source("1");
            //充值类型
            charge.setChargeType(chargeType);
            charge.setCreatetime(new Date());

            chargeService.save(charge);


            return result;
        } catch (Exception e) {
            logger.error("微信支付失败！订单号：{},原因:{}", orderId, e.getMessage());
            agentResponse.code = 12;
            result.put("code", 12);
            return result;
        }
    }


    @ResponseBody
    @RequestMapping(value = "preOrder")
    public AgentResponse pay(HttpServletRequest request) throws Exception {
        int money = Integer.valueOf(request.getParameter("money"));
        int chargeType = Integer.valueOf(request.getParameter("chargeType"));

        Map<String, String> agent = WechatAction.getAgentByToken(request);
        if (agent == null) return new AgentResponse().setCode(1000);
        //todo 实现
        String openId = agent.get("openId");
        long userId = Long.valueOf(agent.get("agentId"));
        String ip = Utils.getIpAddr(request);
        //元转成分

        Integer moneyPoint = getMoneyPoint(money, chargeType);
        logger.info("充值金额: " + money);
        if (moneyPoint == null) return new AgentResponse().setCode(13);
        logger.info("增加钱数: " + moneyPoint);


        AgentResponse agentResponse = new AgentResponse();
        int totalFee = money * 100;
        String orderId = "" + createOrderId();
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setBody("充值");
            orderRequest.setOutTradeNo(orderId);
            orderRequest.setTotalFee(totalFee);//元转成分
            orderRequest.setOpenid(openId);
            orderRequest.setSpbillCreateIp(ip);
//            orderRequest.setTimeStart("yyyyMMddHHmmss");
//            orderRequest.setTimeExpire("yyyyMMddHHmmss");
            orderRequest.setTradeType("JSAPI");

            //notify 地址
            String url = "http://" + serverConfig.getDomain() + "/game/wechat/pay/pay";

            orderRequest.setNotifyUrl(url);

            //创建订单
            agentResponse.data = wxPayService.createOrder(orderRequest);

            //充值记录
            Charge charge = new Charge();
            charge.setOrderId(orderId);
            charge.setUserid(userId);
            charge.setMoney(money);
            charge.setMoney_point(moneyPoint);
//            charge.setOrigin(origin);
            charge.setStatus(0);
//            charge.setSign(paySign);
            charge.setSp_ip(ip);
            charge.setRecharge_source("1");
            //充值类型
            charge.setChargeType(chargeType);
            charge.setCreatetime(new Date());

            chargeService.save(charge);

            return agentResponse;
        } catch (Exception e) {
            logger.error("微信支付失败！订单号：{},原因:{}", orderId, e.getMessage());
            agentResponse.code = 12;
            return agentResponse;
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
        pay(request, response, wechatConfig.getMchKey());
    }

    /**
     * 微信通知支付结果的回调地址，notify_url
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "payApp")
    public void payCallBack_app(HttpServletRequest request,
                                HttpServletResponse response) {
        pay(request, response, serverConfig.getKey());
    }


    private void pay(HttpServletRequest request,
                     HttpServletResponse response, String mchKey) {
        try {
            synchronized (lock) {
                Map<String, String> kvm = XMLUtil.parseRequestXmlToMap(request);
                if (SignUtils.checkSign(kvm, null, mchKey)) {
                    if (kvm.get("result_code").equals("SUCCESS")) {
                        String orderId = kvm.get("out_trade_no");
                        Charge charge = chargeService.getChargeByOrderid(orderId);
                        if (0 == charge.getStatus()) {
                            long userId = charge.getUserid();
//                            System.out.println("修改订单状态");
                            //修改支付订单状态 已支付
                            charge.setStatus(1);
                            charge.setCallbacktime(new Date());

                            UserBean userBeanRedis = userRedisService.getUserBean(userId);


                            double addMoney = charge.getMoney_point();

                            double before = 0;
                            double after = 0;
                            int referee = 0;
                            long a1 = 0;
                            long a2 = 0;
                            long a3 = 0;
                            if (userBeanRedis != null) {
                                referee = userBeanRedis.getReferee();
                                if (charge.getChargeType() == 0) {
                                    before = userBeanRedis.getMoney();
                                    after = userRedisService.addUserMoney(userId, addMoney);
                                } else {
                                    before = userBeanRedis.getGold();
                                    after = userRedisService.addUserGold(userId, addMoney);
                                }

                            } else {
                                //查询玩家
                                User user = userService.getUserByUserId(userId);
                                referee = user.getReferee();
                                //修改玩家货币
                                if (charge.getChargeType() == 0) {
                                    before = user.getMoney();
                                    user.setMoney(user.getMoney() + addMoney);
                                    after = user.getMoney() + addMoney;
                                } else {
                                    before = user.getGold();
                                    user.setGold(user.getGold() + addMoney);
                                    after = user.getGold() + addMoney;
                                }
                                userService.save(user);
                            }
                            AgentBean agent1 = RedisManager.getAgentRedisService().getAgentBean(userId);
                            if (agent1 == null) {
                                agent1 = RedisManager.getAgentRedisService().getAgentBean(referee);
                            }
                            if (agent1 != null) {
                                a1 = agent1.getId();
                                a2 = agent1.getParentId();
                                AgentBean agent2 = RedisManager.getAgentRedisService().getAgentBean(a2);
                                if (agent2 != null) {
                                    a3 = agent2.getParentId();
                                }
                            }
                            charge.setA1(a1);
                            charge.setA2(a2);
                            charge.setA3(a3);


                            //保存充值记录
                            charge.setCharge_before_money(before);
                            charge.setCharge_after_money(after);

                            chargeService.save(charge);

                            //记录到reids
                            RedisManager.getLogRedisService().logCharge(1,charge.getChargeType(), charge.getMoney());

                            Map<String, String> rs = new HashMap<>();
                            MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
                            //如果在游戏中 刷新
                            String roomId = RedisManager.getUserRedisService().getRoomId(userId);
                            if (roomId != null) {
                                String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
                                if (serverId != null) {

                                    KafkaMsgKey msgKey = new KafkaMsgKey();
                                    msgKey.setRoomId(roomId);
                                    int partitionId = Integer.valueOf(serverId);
                                    msgKey.setPartition(partitionId);
                                    msgKey.setUserId(userId);
                                    MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);

                                    ResponseVo responseVo = new ResponseVo();
                                    responseVo.setService("roomService");
                                    responseVo.setMethod("pushScoreChange");
                                    responseVo.setParams("inner");
                                    msgProducer.send2Partition("roomService", partitionId, msgKey, responseVo);

                                }
                            }

                            //返利情况

                            //扣6%的税
                            if (charge.getChargeType() == 0) {
//                                double num = charge.getMoney() * 94 / 100;
                                double num = charge.getMoney();
                                RedisManager.getAgentRedisService().addRebate(userId, referee, charge.getChargeType(), num,0);
                            }
                        }


                        logger.info("out_trade_no: " + kvm.get("out_trade_no") + " pay SUCCESS!");
                        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[ok]]></return_msg></xml>");
                    } else {
                        this.logger.error("out_trade_no: " + kvm.get("out_trade_no") + " result_code is FAIL");
                        response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[result_code is FAIL]]></return_msg></xml>");
                    }
                } else {
                    response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[check signature FAIL]]></return_msg></xml>");
                    this.logger.error("out_trade_no: " + kvm.get("out_trade_no") + " check signature FAIL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "testPay")
    public void testPay(HttpServletRequest request){
//
//        long userId = Long.valueOf(request.getParameter("userId"));
//        int money = Integer.valueOf(request.getParameter("money"));
//        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
//        RedisManager.getInstance().addGold(userId, money);
//
//
//
//
//        Map<String, String> rs = new HashMap<>();
//        MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), userId);
//
//        String roomId = RedisManager.getUserRedisService().getRoomId(userId);
//        if (roomId != null) {
//            String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
//            if (serverId != null) {
//
//                KafkaMsgKey msgKey = new KafkaMsgKey();
//                msgKey.setRoomId(roomId);
//                int partitionId = Integer.valueOf(serverId);
//                msgKey.setPartition(partitionId);
//                msgKey.setUserId(userId);
//                MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
//
//                ResponseVo responseVo = new ResponseVo();
//                responseVo.setService("roomService");
//                responseVo.setMethod("pushScoreChange");
//                responseVo.setParams("inner");
//                msgProducer.send2Partition("roomService", partitionId, msgKey, responseVo);
//
//            }
//        }

    }

    private void agentRebate(long userId, long parentId) {
        //自己是否是代理
        AgentBean own = RedisManager.getAgentRedisService().getAgentBean(userId);
        //自己不是代理
        if (own == null) {

        } else {//自己是代理

        }
    }



    private String getOpenId() {
        return null;
    }

}
