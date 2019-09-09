package com.code.server.login.pay;

import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Charge;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;

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
        String order_no = ""+IdWorker.getDefaultInstance().nextId();
        String trade_name = "good";
        String pay_type = platform;
        float order_amount = money;
        String order_uid = ""+userId;
        String payer_name = "s";

        String signStr = "app_id="+app_id +
                "&order_no="+order_no +
                "&trade_name="+trade_name+
                "&pay_type="+pay_type +
                "&order_amount="+order_amount+
                "&order_uid="+order_uid + "&"+key;

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


//    12798
}
