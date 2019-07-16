package com.code.server.login.pay;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.Constant;
import com.code.server.db.model.Recommend;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.service.ServerManager;
import com.code.server.login.util.PaySaPi;
import com.code.server.login.util.PayUtil_paysapi;
import com.code.server.login.util.Utils;
import com.code.server.login.util.WXMD5;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.DateUtil;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-03-11.
 */
@RestController
public class PaysapiOrder {

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
    private RecommendService recommendService;


    @RequestMapping(value = "/pay_cft")
    public ModelAndView order(int money, int platform, long userId, RedirectAttributes attr) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        Map<String, Object> map = new HashMap<>();
        attr.addAttribute("p1_yingyongnum", serverConfig.getCftPayId());//平台应用id
        String orderId = "" + IdWorker.getDefaultInstance().nextId();
        attr.addAttribute("p2_ordernumber", orderId);//
        attr.addAttribute("p3_money", money);//
        String dateStr = getDateStr();
        attr.addAttribute("p6_ordertime", dateStr);//日期
        attr.addAttribute("p7_productcode", "WX");//固定


        attr.addAttribute("p14_customname", userId);//
        attr.addAttribute("p16_customip", "192_168_1_132");//ip
        attr.addAttribute("p25_terminal", platform);//ip
        attr.addAttribute("p26_ext1", "1.1");//ip

        String sign = serverConfig.getCftPayId() + "&" +
                orderId + "&" +
                money + "&" +
                dateStr + "&" +
                "WX" + "&" +
                serverConfig.getCftPayKey();

        String md5Sign = WXMD5.MD5Encode(sign);

        attr.addAttribute("p8_sign", md5Sign);//签名


        Charge charge = new Charge();
        charge.setOrderId(orderId);
        charge.setUserid(userId);
        charge.setMoney(money);
        charge.setMoney_point(money);
        charge.setChargeType(0);
        charge.setOrigin(0);
        charge.setStatus(0);
        String charge_source = platform == 1?"2":"1";
        charge.setRecharge_source(charge_source);
        charge.setCreatetime(new Date());
        chargeService.save(charge);


        modelAndView.setViewName("redirect:http://tojucum.cftgame.com/jh-web-order/order/receiveOrder/");
        return modelAndView;
    }

    @RequestMapping("/notify_cft1")
    public synchronized String notifyPay1(HttpServletRequest request, HttpServletResponse response) {
        return "success";
    }


    @RequestMapping("/notify_cft")
    public synchronized String notifyPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ip = Utils.getIpAddr(request);
        if (ip.equals("47.93.41.216") || ip.equals("59.110.172.177") || ip.equals("47.93.230.132")) {


            // 保证密钥一致性
            String order = request.getParameter("p2_ordernumber");
            int state = Integer.valueOf(request.getParameter("p4_zfstate"));


            String p1_yingyongnum = request.getParameter("p1_yingyongnum");
            String p2_ordernumber = request.getParameter("p2_ordernumber");
            String p3_money = request.getParameter("p3_money");
            String p4_zfstate = request.getParameter("p4_zfstate");
            String p5_orderid = request.getParameter("p5_orderid");
            String p6_productcode = request.getParameter("p6_productcode");
            p6_productcode = "WX";
            String p7_bank_card_code = request.getParameter("p7_bank_card_code");
            String p8_charset = request.getParameter("p8_charset");
            String p9_signtype = request.getParameter("p9_signtype");
            String p11_pdesc = request.getParameter("p11_pdesc");
            String p13_zfmoney = request.getParameter("p13_zfmoney");

            String p10_sign = request.getParameter("p10_sign");


            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//        需要签名的字符串: 68019026133901&6514401188497596416&10.00&1&6802019032115443596457&WXZZPCFD&&UTF-8&1&&9.84&046809172632GPAN4nvs
//        签名值: beff9e27fa7c98656c5fd81998cabace
//        传过来的签名值: E1703E8E0CE09A46BDCE4C80D66DF132
//                签名错误


            String str = p1_yingyongnum + "&" +
                    p2_ordernumber + "&" +
                    p3_money + "&" +
                    p4_zfstate + "&" +
                    p5_orderid + "&" +
                    p6_productcode + "&" +
                    p7_bank_card_code + "&" +
                    p8_charset + "&" +
                    p9_signtype + "&" +
                    p11_pdesc + "&" +
                    p13_zfmoney + "&" +
                    serverConfig.getCftPayKey();

            String sign = WXMD5.MD5Encode(str);
            System.out.println("需要签名的字符串: " + str);
            System.out.println("签名值: " + sign);
            System.out.println("传过来的签名值: " + p10_sign);


//        if (sign.equalsIgnoreCase(p10_sign)) {
            Charge charge = chargeService.getChargeByOrderid(order);
            if (state == 1 && 0 == charge.getStatus()) {
                System.out.println("修改订单状态");
                //修改支付订单状态 已支付
                charge.setStatus(1);
                charge.setCallbacktime(new Date());
                int money = (int) charge.getMoney();
                String dayStr = DateUtil.convert2DayString(new Date());
                //更新订单结算是否已经返利
                charge.setFinishTime(dayStr);
                UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());
                int addMoney = serverConfig.getChargeMap().get(money);
                if (UserBeanRedis != null) {
                    if (charge.getChargeType() == 0) {
                        userRedisService.addUserMoney(charge.getUserid(), addMoney);
                    } else {
                        userRedisService.addUserGold(charge.getUserid(), addMoney);
                    }
                    charge.setUsername(UserBeanRedis.getUsername());
                } else {
                    User user = userService.getUserByUserId(charge.getUserid());
                    if (charge.getChargeType() == 0) {

                        user.setMoney(user.getMoney() + addMoney);
                    } else {
                        user.setGold(user.getGold() + addMoney);
                    }
                    charge.setUsername(user.getUsername());
                    userService.save(user);
                }
                //保存订单
                chargeService.save(charge);
                Map<String, String> rs = new HashMap<>();
                MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
            }
//        }else{
//            System.out.println("签名错误");
//        }
        }else{
            System.out.println("ip不合法");
        }
        return "success";
    }

    public static void main(String[] args) {
        System.out.println(WXMD5.MD5Encode("68019026133901&6514401188497596416&10.00&1&6802019032115443596457&WX&&UTF-8&1&&9.84&046809172632GPAN4nvs"));
    }

    @RequestMapping(value = "/pay_pays")
    public ModelAndView pay(int platform, double money, long userId, RedirectAttributes attr, int chargeType) throws IOException {
        Constant constant = ServerManager.constant;
        Map<String, Object> map = new HashMap<>();

        ModelAndView modelAndView = new ModelAndView();
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        String uid = constant.getPayUid();
        attr.addAttribute("uid", uid);
        attr.addAttribute("price", money);
        attr.addAttribute("istype", platform);
        String notify_url = serverConfig.getPaysapiNotifyUrl();
        attr.addAttribute("notify_url", notify_url);
        String returnUrl = serverConfig.getPaysapiReturnUrl();
        attr.addAttribute("return_url", returnUrl);
        String orderId = "" + IdWorker.getDefaultInstance().nextId();
        attr.addAttribute("orderid", orderId);

        String sign = platform + notify_url + orderId + money + returnUrl + constant.getPayToken() + uid;
        String key = WXMD5.MD5Encode(sign);

        System.out.println(key);
        attr.addAttribute("key", key);


        Charge charge = new Charge();
        charge.setOrderId(orderId);
        charge.setUserid(userId);
        charge.setMoney(money);
        charge.setMoney_point(money);
        charge.setChargeType(chargeType);
        charge.setOrigin(0);
        charge.setStatus(0);
        charge.setRecharge_source("" + platform);
        charge.setCreatetime(new Date());
        chargeService.save(charge);


        modelAndView.setViewName("redirect:https://pay.sxhhjc.cn/");

        return modelAndView;
    }


    @RequestMapping("/notifyPay")
    public void notifyPay(HttpServletRequest request, HttpServletResponse response, PaySaPi paySaPi) {
        // 保证密钥一致性
        if (PayUtil_paysapi.checkPayKey(paySaPi)) {
            Charge charge = chargeService.getChargeByOrderid(paySaPi.getOrderid());
            if (0 == charge.getStatus()) {
                System.out.println("修改订单状态");
                //修改支付订单状态 已支付
                charge.setStatus(1);
                charge.setCallbacktime(new Date());
                int money = (int) charge.getMoney();
                String dayStr = DateUtil.convert2DayString(new Date());
                //更新订单结算是否已经返利
                charge.setFinishTime(dayStr);
                UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());
                ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
                int addMoney = serverConfig.getChargeMap().get(money);
                if (UserBeanRedis != null) {
                    if (charge.getChargeType() == 0) {

                        userRedisService.addUserMoney(charge.getUserid(), addMoney);
                    } else {
                        userRedisService.addUserGold(charge.getUserid(), addMoney);
                    }
                    charge.setUsername(UserBeanRedis.getUsername());
                } else {
                    User user = userService.getUserByUserId(charge.getUserid());
                    if (charge.getChargeType() == 0) {

                        user.setMoney(user.getMoney() + addMoney);
                    } else {
                        user.setGold(user.getGold() + addMoney);
                    }
                    charge.setUsername(user.getUsername());
                    userService.save(user);
                }
                //保存订单
                chargeService.save(charge);
                Map<String, String> rs = new HashMap<>();
                MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
            }
        } else {
            // TODO 该怎么做就怎么做
            System.out.println("验证失败");
        }
    }

    @RequestMapping("/returnPay")
    public String returnPay(HttpServletRequest request, HttpServletResponse response, String orderid) {
        boolean isTrue = false;
        ModelAndView view = null;
//        // 根据订单号查找相应的记录:根据结果跳转到不同的页面
//        if (isTrue) {
//            view = new ModelAndView("");
//        } else {
//            view = new ModelAndView("/success.html");
//        }
        return "支付成功";
    }



    @RequestMapping(value = "/invite")
    public String invite( long userId, RedirectAttributes attr,HttpServletRequest request) throws Exception {
        String ip = Utils.getIpAddr(request);

        Recommend recommend = new Recommend();
        recommend.setUnionId(ip);
        recommend.setAgentId(userId);
        recommendService.getRecommendDao().save(recommend);

        ModelAndView modelAndView = new ModelAndView();


        return "/success";

//        modelAndView.setViewName("redirect:https://pay.sxhhjc.cn/");

//        return modelAndView;
    }



    public static String getDateStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }
}
