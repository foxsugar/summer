package com.code.server.login.pay;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.Constant;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.service.ServerManager;
import com.code.server.login.util.PaySaPi;
import com.code.server.login.util.PayUtil_paysapi;
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

//    public void order(int money, int platform) throws IOException {
//
//
//        HttpClient httpclient = HttpClients.createDefault();
//
//        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//        String url = serverConfig.getPaysapiUrl();
//        // 创建http GET请求
//        HttpPost httpPost = new HttpPost(url);
////        httpPost.
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("p1_yingyongnum", serverConfig.getPaysapiId());//平台应用id
//        String orderId = ""+IdWorker.getDefaultInstance().nextId();
//        map.put("p2_ordernumber", orderId);//
//        map.put("p3_money", money);//
//        String dateStr = getDateStr();
//        map.put("p6_ordertime", dateStr);//日期
//        map.put("p7_productcode", "WXSM");//固定
//
//
//        map.put("p14_customname", serverConfig.getPaysapiName());//签名
//        map.put("p16_customip", Utils.getLocalIp());//ip
//        map.put("p25_terminal", platform);//ip
//
//        String sign =serverConfig.getPaysapiId() + "&" +
//                orderId +"&" +
//                money + "&"+
//                dateStr  + "&" +
//                "WXSM" +"&"+
//                serverConfig.getPaysapiKey();
//
//        String md5Sign = MD5Util.getMD5(sign);
//
//        map.put("p8_sign", md5Sign);//签名
//
//        StringEntity stringEntity = new StringEntity(JsonUtil.toJson(map));
//
//        httpPost.setEntity(stringEntity);
//        httpclient.execute(httpPost);
//    }




    @RequestMapping(value = "/pay_pays")
    public ModelAndView  pay(int platform, double money,long userId,RedirectAttributes attr) throws IOException {
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
        String orderId = ""+IdWorker.getDefaultInstance().nextId();
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
        charge.setOrigin(0);
        charge.setStatus(0);
        charge.setRecharge_source(""+platform);
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
                int money = (int)charge.getMoney();
                String dayStr = DateUtil.convert2DayString(new Date());
                //更新订单结算是否已经返利
                charge.setFinishTime(dayStr);
                UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());
                ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
                int addMoney = serverConfig.getChargeMap().get(money);
                if (UserBeanRedis != null) {
                    userRedisService.addUserMoney(charge.getUserid(), addMoney);
                    charge.setUsername(UserBeanRedis.getUsername());
                } else {
                    User user = userService.getUserByUserId(charge.getUserid());
                    user.setMoney(user.getMoney() + addMoney);
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
        }
    }

    @RequestMapping("/returnPay")
    public ModelAndView returnPay(HttpServletRequest request, HttpServletResponse response, String orderid) {
        boolean isTrue = false;
        ModelAndView view = null;
        // 根据订单号查找相应的记录:根据结果跳转到不同的页面
        if (isTrue) {
            view = new ModelAndView("");
        } else {
            view = new ModelAndView("/testaaa.html");
        }
        return view;
    }

    public static void main(String[] args) {
        System.out.println(getDateStr());
    }

    public static String getDateStr(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }
}
