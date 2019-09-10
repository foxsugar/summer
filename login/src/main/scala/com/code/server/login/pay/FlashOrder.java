package com.code.server.login.pay;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.DateUtil;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-09-09.
 */
@RestController
public class FlashOrder {


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


    @RequestMapping(value = "/pay_flash")
    public ModelAndView order(int money, String platform, long userId, RedirectAttributes attr) throws IOException {
        String key = "JVVcedFjsNVsahmhjVfJ";
        String app_id = "12798";
        String order_no = "" + IdWorker.getDefaultInstance().nextId();
        String trade_name = "good";
        String pay_type = platform;
        float order_amount = money;
        String order_uid = "" + userId;
        String payer_name = "s";

        String signStr = "app_id=" + app_id +
                "&order_no=" + order_no +
                "&trade_name=" + trade_name +
                "&pay_type=" + pay_type +
                "&order_amount=" + order_amount +
                "&order_uid=" + order_uid + "&" + key;

        String sign = (signStr);
        sign = DigestUtils.md5DigestAsHex(signStr.getBytes());

        System.out.println(sign);

        Charge charge = new Charge();
        charge.setOrderId(order_no);
        charge.setUserid(userId);
        charge.setMoney(money);
        charge.setMoney_point(money);
        charge.setChargeType(0);
        charge.setOrigin(0);
        charge.setStatus(0);
        String charge_source = "1";
        charge.setRecharge_source(charge_source);
        charge.setCreatetime(new Date());
        chargeService.save(charge);

        attr.addAttribute("app_id", app_id);//平台应用id
        attr.addAttribute("order_no", order_no);//平台应用id
        attr.addAttribute("trade_name", trade_name);//平台应用id
        attr.addAttribute("pay_type", pay_type);//平台应用id
        attr.addAttribute("order_amount", order_amount);//平台应用id
        attr.addAttribute("order_uid", order_uid);//平台应用id
        attr.addAttribute("app_id", app_id);//平台应用id
//        attr.addAttribute("payer_name", payer_name);//平台应用id
        attr.addAttribute("sign", sign);//平台应用id

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:https://api.sdpay.cc/pay");
        return modelAndView;


    }

    @RequestMapping(value = "/flash_notify")
    public String flash_notify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String orderId = request.getParameter("order_no");

        Charge charge = chargeService.getChargeByOrderid(orderId);
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
        return "success";
    }

    @RequestMapping(value = "/flash_return")
    public String flash_return(HttpServletRequest request, HttpServletResponse response) throws IOException {


        return "success";
    }

//    12798
}
