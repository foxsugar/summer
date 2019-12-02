package com.code.server.login.pay;


import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.ChildCost;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.RebateDetailService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.*;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.util.ErrorCode;
import com.code.server.login.util.PayUtil;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.DateUtil;
import com.code.server.util.SpringUtil;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@RestController
public class PayCallback {

    @Autowired
    private ChargeService chargeService;
    @Autowired
    private UserService userService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private IAgentUserDao agentUserDao;

    @Autowired
    private IConstantDao constantDao;

    @Autowired
    private RebateDetailService rebateDetailService;

    private static final Map<Integer, Integer> chargeMoney = new HashMap<>();



    protected static final Logger logger = LoggerFactory.getLogger(PayCallback.class);

    static{
        chargeMoney.put(5,60);
        chargeMoney.put(10,140);
        chargeMoney.put(20,300);
        chargeMoney.put(50,800);
        chargeMoney.put(100,1800);
    }

    @RequestMapping(value = "/callback_zhanglebao")
    public String callback_zhanglebao(HttpServletRequest request, HttpServletResponse response) {
        //获取微信返回结果
        String parameterstr = PayUtil.ObtainParameterString(request);

        System.out.println("parameterstr:" + parameterstr);

        Element element = PayUtil.ParsingXML(parameterstr);//解析xmlString

        SortedMap<String, String> secondParams = new TreeMap<>();

        secondParams.put("appid", element.elementText("appid"));
        secondParams.put("bank_type", element.elementText("bank_type"));
        secondParams.put("cash_fee", element.elementText("cash_fee"));
        secondParams.put("fee_type", element.elementText("fee_type"));
        secondParams.put("is_subscribe", element.elementText("is_subscribe"));
        secondParams.put("mch_id", element.elementText("mch_id"));
        secondParams.put("nonce_str", element.elementText("nonce_str"));
        secondParams.put("openid", element.elementText("openid"));
        secondParams.put("out_trade_no", element.elementText("out_trade_no"));
        secondParams.put("result_code", element.elementText("result_code"));
        secondParams.put("return_code", element.elementText("return_code"));
        secondParams.put("time_end", element.elementText("time_end"));
        secondParams.put("total_fee", element.elementText("total_fee"));
        secondParams.put("trade_type", element.elementText("trade_type"));
        secondParams.put("transaction_id", element.elementText("transaction_id"));


        String paySign = PayUtil.createSign("UTF-8", serverConfig.getKey(), secondParams);

        System.out.println(paySign);

        //解析返回结果
        String returnXML = null;


        System.out.println("回调成功");
        //业务返回成功

        Charge charge = chargeService.getChargeByOrderid(element.elementText("out_trade_no"));
        if ("SUCCESS".equals(element.elementText("result_code"))) {
            String transaction_id = element.elementText("transaction_id");
            charge.setTransaction_id(transaction_id);
            System.out.println("业务成功");

            System.out.println(charge.getMoney());
            System.out.println(element.elementText("cash_fee"));
            System.out.println(paySign);
            System.out.println(element.elementText("sign"));

            int money_total = Integer.valueOf(element.elementText("total_fee")) / 100;
            if (paySign.equals(element.elementText("sign"))
                    ) {

                if (0 == charge.getStatus()) {
                    ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
                    //修改支付订单状态 已支付
                    charge.setStatus(1);
                    charge.setCallbacktime(new Date());
                    double money = charge.getMoney();
                    String dayStr = DateUtil.convert2DayString(new Date());

                    User u = userService.getUserByUserId(charge.getUserid());

                    logger.info("user is :{}", u);

                    AgentUser agentUser1 = agentUserDao.findAgentUserByInvite_code(u.getReferee() + "");
                    AgentInfo agentInfo1 = null;

                    logger.info("AgentUser1 is :{}", agentUser1);

                    double rebateMoney = money ;
                    if (u.getVip() != 0) {
                        rebateMoney = money * serverConfig.getDiscount().get(u.getVip()) / 100;
                    }
                    if (agentUser1 != null && agentUser1.getLevel()<2){
                        agentInfo1 = agentUser1.getAgentInfo();

                        logger.info("AgentInfo1 is :{}", agentInfo1);
                        Map<String, ChildCost> rs1 = agentInfo1.getEveryDayCost();
                        ChildCost childCost1 = rs1.get(dayStr);
                        if (childCost1 == null){
                            childCost1 = new ChildCost();
                        }

                        logger.info("childCost1  is :{}", childCost1);
                        //今日来源于玩家的收入
                        double rebate = 0;
                        if (u.getVip() == 0) {
                            rebate = rebateMoney * serverConfig.getAgentFirstRebate().get(agentUser1.getAgentType()) * 0.01;
                            childCost1.firstLevel += rebate;
                        } else {
                            rebate = rebateMoney * serverConfig.getAgentSecondRebate().get(u.getVip()) * 0.01;
                            childCost1.firstLevel += rebate;


                        }
                        rs1.put(dayStr, childCost1);
                        agentUserDao.save(agentUser1);




                        RebateDetail rebateDetail = new RebateDetail();
                        rebateDetail.setUserId(charge.getUserid());
                        rebateDetail.setAgentName(agentUser1.getUsername());
                        rebateDetail.setName(u.getUsername());
                        rebateDetail.setAgentId(agentUser1.getId());
                        rebateDetail.setNum(rebate);
                        rebateDetail.setChargeNum(money);
                        rebateDetail.setAgentLevel(agentUser1.getAgentType());
                        rebateDetail.setDate(new Date());
                        rebateDetail.setLevle(1);
                        rebateDetail.setUserLevel(u.getVip());
                        rebateDetail.setType(0);
                        rebateDetailService.rebateDetailDao.save(rebateDetail);

                    }

                    //二级返利去掉
                    AgentUser agentUser2 = null;
                    if (agentUser1 != null){
                        agentUser2 = agentUserDao.findOne(agentUser1.getParentId());
                    }

                    logger.info("AgentUser2 is :{}", agentUser2);

//                    if (agentUser2 != null && u.getVip() == 0){
//                        AgentInfo agentInfo2 = agentUser2.getAgentInfo();
//                        logger.info("AgentInfo2 is :{}", agentInfo2);
//                        Map<String, ChildCost> rs2 = agentInfo2.getEveryDayCost();
//                        ChildCost childCost2 = rs2.get(dayStr);
//                        if (childCost2 == null){
//                            childCost2 = new ChildCost();
//                        }
//                        logger.info("childCost2  is :{}", childCost2);
//                        //今日来源于代理的收入
//                        double rebate = rebateMoney * serverConfig.getAgentSecondRebate().get(agentUser1.getAgentType()) * 0.01;
//                        childCost2.secondLevel += rebate;
//                        rs2.put(dayStr, childCost2);
//                        agentUserDao.save(agentUser2);
//
//
//
//                        RebateDetail rebateDetail = new RebateDetail();
//                        rebateDetail.setUserId(charge.getUserid());
//                        rebateDetail.setAgentName(agentUser2.getUsername());
//                        rebateDetail.setName(u.getUsername());
//                        rebateDetail.setAgentId(agentUser2.getId());
//                        rebateDetail.setNum(rebate);
//                        rebateDetail.setChargeNum(money);
//                        rebateDetail.setAgentLevel(agentUser2.getAgentType());
//                        rebateDetail.setDate(new Date());
//                        rebateDetail.setLevle(2);
//                        rebateDetail.setUserLevel(u.getVip());
//                        rebateDetail.setType(0);
//                        rebateDetailService.rebateDetailDao.save(rebateDetail);
//
//                    }

                    //更新订单结算是否已经返利
                    charge.setFinishTime(dayStr);
//                    chargeDao.save(charge);
                    logger.info("Charge  is :{}", charge);



                    UserBean userBeanRedis = userRedisService.getUserBean(charge.getUserid());

                    int addMoney = Integer.valueOf(element.elementText("total_fee"))/10;


//                    serverConfig.getChargeMap()
                    //龙七分档
                    if (serverConfig.getChargeMap().containsKey((int)charge.getMoney())) {
                        addMoney = serverConfig.getChargeMap().get((int)charge.getMoney());
                    }

                    if (userBeanRedis != null) {
                        //  userRedisService.setUserMoney(charge.getUserid(),UserBeanRedis.getMoney() + Double.valueOf(element.elementText("total_fee")) / 10);
                        userRedisService.addUserMoney(charge.getUserid(), addMoney);
                        charge.setUsername(userBeanRedis.getUsername());
                        charge.setOrigin(userBeanRedis.getReferee());

                    } else {
                        //查询玩家
                        User user = userService.getUserByUserId(charge.getUserid());
                        //修改玩家豆豆
                        user.setMoney(user.getMoney() + addMoney);
                        charge.setUsername(user.getUsername());
                        charge.setOrigin(user.getReferee());
                        userService.save(user);
                    }

                    //改成真实充值
                    charge.setMoney(rebateMoney);
                    charge.setMoney_point(addMoney);

                    //保存订单
                    chargeService.save(charge);

                    Map<String, String> rs = new HashMap<>();
                    MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
                } else {
                    Map<String, String> rs = new HashMap<>();
                    rs.put("err_code", element.elementText("err_code"));
                    rs.put("err_code_des", element.elementText("err_code_des"));
                    ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                    vo.setCode(ErrorCode.ORDER_WAS_PAID);
                    MsgSender.sendMsg2Player(vo, charge.getUserid());
                }

		    			/*returnXML = "<xml>";
                        returnXML += "<return_code>![CDATA[SUCCESS]]</return_code>";
		    		    returnXML += "<return_msg>![CDATA[OK]]</return_msg>";
		    		    returnXML += "</xml>";*/

                returnXML = "SUCCESS";
            } else {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.PARAMETER_SIGN_MONEY_ERROR);
                MsgSender.sendMsg2Player(vo, charge.getUserid());

                returnXML = "SUCCESS";
            }
        } else {

            //余额不足
            if ("NOTENOUGH".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.BALANCE_INSUFFICIENT);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
                //订单已支付
            } else if ("ORDERPAID".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.ORDER_WAS_PAID);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
                //订单已关闭
            } else if ("ORDERCLOSED".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.ORDER_WAS_CLOSED);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
            }

            returnXML = "SUCCESS";
        }

        System.out.println("回调结束，返回微信成功信息");

        System.out.println(returnXML);

        try {
            response.getWriter().write(returnXML);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return returnXML;
    }


    /**
     * 接受微信回调
     *
     * @param
     */
    @RequestMapping(value = "/callback")
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("接受回调参数");
        //获取微信返回结果
        String parameterstr = PayUtil.ObtainParameterString(request);

        System.out.println("parameterstr:" + parameterstr);

        Element element = PayUtil.ParsingXML(parameterstr);//解析xmlString

        SortedMap<String, String> secondParams = new TreeMap<>();

        secondParams.put("appid", element.elementText("appid"));
        secondParams.put("bank_type", element.elementText("bank_type"));
        secondParams.put("cash_fee", element.elementText("cash_fee"));
        secondParams.put("fee_type", element.elementText("fee_type"));
        secondParams.put("is_subscribe", element.elementText("is_subscribe"));
        secondParams.put("mch_id", element.elementText("mch_id"));
        secondParams.put("nonce_str", element.elementText("nonce_str"));
        secondParams.put("openid", element.elementText("openid"));
        secondParams.put("out_trade_no", element.elementText("out_trade_no"));
        secondParams.put("result_code", element.elementText("result_code"));
        secondParams.put("return_code", element.elementText("return_code"));
        secondParams.put("time_end", element.elementText("time_end"));
        secondParams.put("total_fee", element.elementText("total_fee"));
        secondParams.put("trade_type", element.elementText("trade_type"));
        secondParams.put("transaction_id", element.elementText("transaction_id"));


        String paySign = PayUtil.createSign("UTF-8", serverConfig.getKey(), secondParams);

        System.out.println(paySign);

        //解析返回结果
        String returnXML = null;


        System.out.println("回调成功");
        //业务返回成功

        Charge charge = chargeService.getChargeByOrderid(element.elementText("out_trade_no"));
        if ("SUCCESS".equals(element.elementText("result_code"))) {
            String transaction_id = element.elementText("transaction_id");
            charge.setTransaction_id(transaction_id);
            System.out.println("业务成功");

            System.out.println(charge.getMoney());
            System.out.println(element.elementText("cash_fee"));
            System.out.println(paySign);
            System.out.println(element.elementText("sign"));

            int money_total = Integer.valueOf(element.elementText("total_fee")) / 100;
            if (paySign.equals(element.elementText("sign"))
                    && charge.getMoney() == money_total) {

                if (0 == charge.getStatus()) {
                    System.out.println("修改订单状态");
                    //修改支付订单状态 已支付
                    charge.setStatus(1);
                    charge.setCallbacktime(new Date());
                    double money = charge.getMoney();
                    String dayStr = DateUtil.convert2DayString(new Date());
                    Constant constant = constantDao.findOne(1l);

                    User u = userService.getUserByUserId(charge.getUserid());

                    logger.info("user is :{}", u);

                    AgentUser agentUser1 = agentUserDao.findAgentUserByInvite_code(u.getReferee() + "");
                    AgentInfo agentInfo1 = null;

                    logger.info("AgentUser1 is :{}", agentUser1);

                    if (agentUser1 != null){
                        agentInfo1 = agentUser1.getAgentInfo();

                        logger.info("AgentInfo1 is :{}", agentInfo1);
                        Map<String, ChildCost> rs1 = agentInfo1.getEveryDayCost();
                        ChildCost childCost1 = rs1.get(dayStr);
                        if (childCost1 == null){
                            childCost1 = new ChildCost();
                        }

                        logger.info("childCost1  is :{}", childCost1);
                        //今日来源于玩家的收入
                        childCost1.firstLevel += money * constant.getIncome1() * 0.01;
                        rs1.put(dayStr, childCost1);
                        agentUserDao.save(agentUser1);
                    }

                    AgentUser agentUser2 = null;
                    if (agentUser1 != null){
                        agentUser2 = agentUserDao.findOne(agentUser1.getParentId());
                    }

                    logger.info("AgentUser2 is :{}", agentUser2);

                    if (agentUser2 != null){
                        AgentInfo agentInfo2 = agentUser2.getAgentInfo();
                        logger.info("AgentInfo2 is :{}", agentInfo2);
                        Map<String, ChildCost> rs2 = agentInfo2.getEveryDayCost();
                        ChildCost childCost2 = rs2.get(dayStr);
                        if (childCost2 == null){
                            childCost2 = new ChildCost();
                        }
                        logger.info("childCost2  is :{}", childCost2);
                        //今日来源于代理的收入
                        childCost2.secondLevel += money * constant.getIncome2() * 0.01;
                        rs2.put(dayStr, childCost2);
                        agentUserDao.save(agentUser2);
                    }

                    //更新订单结算是否已经返利
                    charge.setFinishTime(dayStr);
//                    chargeDao.save(charge);
                    logger.info("Charge  is :{}", charge);



                    UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());

                    int addMoney = Integer.valueOf(element.elementText("total_fee"))/10;

                    ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//                    serverConfig.getChargeMap()

                    if (serverConfig.getChargeMap().containsKey(money_total)) {
                        addMoney = serverConfig.getChargeMap().get(money_total);
                    }

                    if (UserBeanRedis != null) {
                        //  userRedisService.setUserMoney(charge.getUserid(),UserBeanRedis.getMoney() + Double.valueOf(element.elementText("total_fee")) / 10);
                        userRedisService.addUserMoney(charge.getUserid(), addMoney);
                        charge.setUsername(UserBeanRedis.getUsername());
                    } else {
                        //查询玩家
                        User user = userService.getUserByUserId(charge.getUserid());
                        System.out.println("修改玩家豆豆");
                        //修改玩家豆豆
                        user.setMoney(user.getMoney() + addMoney);
                        charge.setUsername(user.getUsername());
                        userService.save(user);
                    }


                    //保存订单
                    chargeService.save(charge);

                    System.out.println("通知客户端刷新充值");
                    Map<String, String> rs = new HashMap<>();
                    MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
                } else {
                    Map<String, String> rs = new HashMap<>();
                    rs.put("err_code", element.elementText("err_code"));
                    rs.put("err_code_des", element.elementText("err_code_des"));
                    ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                    vo.setCode(ErrorCode.ORDER_WAS_PAID);
                    MsgSender.sendMsg2Player(vo, charge.getUserid());
                }

		    			/*returnXML = "<xml>";
                        returnXML += "<return_code>![CDATA[SUCCESS]]</return_code>";
		    		    returnXML += "<return_msg>![CDATA[OK]]</return_msg>";
		    		    returnXML += "</xml>";*/

                returnXML = "SUCCESS";
            } else {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.PARAMETER_SIGN_MONEY_ERROR);
                MsgSender.sendMsg2Player(vo, charge.getUserid());

                returnXML = "SUCCESS";
            }
        } else {

            //余额不足
            if ("NOTENOUGH".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.BALANCE_INSUFFICIENT);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
                //订单已支付
            } else if ("ORDERPAID".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.ORDER_WAS_PAID);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
                //订单已关闭
            } else if ("ORDERCLOSED".equals(element.elementText("err_code"))) {
                Map<String, String> rs = new HashMap<>();
                rs.put("err_code", element.elementText("err_code"));
                rs.put("err_code_des", element.elementText("err_code_des"));
                ResponseVo vo = new ResponseVo("userService", "refresh", rs);
                vo.setCode(ErrorCode.ORDER_WAS_CLOSED);
                MsgSender.sendMsg2Player(vo, charge.getUserid());
            }

            returnXML = "SUCCESS";
        }

        System.out.println("回调结束，返回微信成功信息");

        System.out.println(returnXML);

        try {
            response.getWriter().write(returnXML);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return returnXML;
    }

}
