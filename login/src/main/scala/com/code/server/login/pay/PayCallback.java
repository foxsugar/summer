package com.code.server.login.pay;


import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.ChildCost;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.AgentUser;
import com.code.server.db.model.Charge;
import com.code.server.db.model.Constant;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.util.ErrorCode;
import com.code.server.login.util.PayUtil;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.DateUtil;
import com.code.server.util.SpringUtil;
import org.dom4j.Element;
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

    private static final Map<Integer, Integer> chargeMoney = new HashMap<>();

    static{
        chargeMoney.put(5,60);
        chargeMoney.put(10,140);
        chargeMoney.put(20,300);
        chargeMoney.put(50,800);
        chargeMoney.put(100,1800);
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

                    User u = userService.getUserByOpenId(element.elementText("openid"));
                    AgentUser agentUser1 = agentUserDao.findAgentUserByInvite_code(u.getReferee() + "");
                    AgentInfo agentInfo1 = agentUser1.getAgentInfo();

                    if (agentUser1 != null){
                        Map<String, ChildCost> rs1 = agentInfo1.getEveryDayCost();
                        ChildCost childCost1 = rs1.get(dayStr);
                        if (childCost1 == null){
                            childCost1 = new ChildCost();
                        }
                        //今日来源于玩家的收入
                        childCost1.firstLevel += money * constant.getIncome1();
                        //暂时用来充当今日有没有结算完
                        childCost1.setPartner(0);
                        rs1.put(dayStr, childCost1);
                        agentUserDao.save(agentUser1);
                    }

                    AgentUser agentUser2 = null;
                    if (agentUser1 != null){
                        agentUserDao.findOne(agentUser1.getParentId());
                    }

                    if (agentUser2 != null){
                        AgentInfo agentInfo2 = agentUser2.getAgentInfo();
                        Map<String, ChildCost> rs2 = agentInfo2.getEveryDayCost();
                        ChildCost childCost2 = rs2.get(dayStr);
                        if (childCost2 == null){
                            childCost2 = new ChildCost();
                        }

                        //今日来源于代理的收入
                        childCost2.secondLevel += money * constant.getIncome2();
                        //暂时用来充当今日有没有结算完
                        childCost2.setPartner(0);
                        rs2.put(dayStr, childCost2);
                        agentUserDao.save(agentUser2);
                    }

                    //更新订单结算是否已经返利
                    charge.setFinishTime(dayStr);
//                    chargeDao.save(charge);

                    chargeService.save(charge);


                    UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());

                    int addMoney = Integer.valueOf(element.elementText("total_fee"))/10;

                    ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//                    serverConfig.getChargeMap()
                    //龙七分档
                    if (serverConfig.getChargeMap().containsKey(money_total)) {
                        addMoney = serverConfig.getChargeMap().get(money_total);
                    }

                    if (UserBeanRedis != null) {
                        //  userRedisService.setUserMoney(charge.getUserid(),UserBeanRedis.getMoney() + Double.valueOf(element.elementText("total_fee")) / 10);
                        userRedisService.addUserMoney(charge.getUserid(), addMoney);
                    } else {
                        //查询玩家
                        User user = userService.getUserByUserId(charge.getUserid());
                        System.out.println("修改玩家豆豆");
                        //修改玩家豆豆
                        user.setMoney(user.getMoney() + addMoney);
                        userService.save(user);
                    }


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
