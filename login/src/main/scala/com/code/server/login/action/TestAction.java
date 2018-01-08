package com.code.server.login.action;

import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.kafka.MsgSender;
import com.code.server.login.util.PayUtil;
import com.code.server.redis.service.UserRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by sunxianping on 2017/12/20.
 */
@Controller
public class TestAction {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private UserService userService;

    protected static Logger logger= LoggerFactory.getLogger(TestAction.class);

    private static final String keyValue = "xhzw2malfjk62p0g8m9by7ycx97fqahv" ;
    private UserBean userBeanRedis;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/pay")
    public void pay(@RequestParam(value = "money", required = true) Double money,  @RequestParam(value = "uid", required = true) Long uid, HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        String orderId = PayUtil.getOrderIdByUUId();
        Charge charge = new Charge();
        charge.setOrderId(orderId);
        charge.setUserid(uid);
        charge.setMoney(money);
        charge.setMoney_point(money * 10);
        charge.setStatus(0);
        chargeService.save(charge);
        logger.info(charge.getOrderId());
        request.setAttribute("Moneys", money + "");
        request.setAttribute("orderId", orderId);
        request.getRequestDispatcher("/WEB-INF/jsp/pay.jsp").forward(request, resp);
    }

    @RequestMapping("/Pay/notify")
    public String noti(HttpServletRequest request){
        String memberid=request.getParameter("memberid");
        String orderid=request.getParameter("orderid");
        String amount=request.getParameter("amount");
        String datetime=request.getParameter("datetime");
        String returncode=request.getParameter("returncode");
        String reserved1=request.getParameter("reserved1");
        String reserved2=request.getParameter("reserved2");
        String sign=request.getParameter("sign");
        String transaction_id = request.getParameter("transaction_id");
        String SignTemp="amount="+amount+"&datetime="+datetime + "&memberid="+memberid+"&orderid="+orderid+"&transaction_id="+transaction_id+"&returncode="+returncode+"&key=" + keyValue + "";
        String md5sign= null;
        try {
            md5sign = md5(SignTemp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("-------------------------------");
        logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, sign, SignTemp);
        logger.info("-------------------------------");

        logger.info("local sign：{}", md5sign);
        logger.info("sign：{}", sign);

        if (true){
            if(returncode.equals("00")){
                Charge charge = chargeService.getChargeByOrderid(orderid);
                if (charge.getStatus() == 1){
                    return "success";
                }

                charge.setStatus(1);
                chargeService.save(charge);
                logger.info("支付成功！");

                UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());
                double addMoney = charge.getMoney() * 10;

                if (UserBeanRedis != null) {
                    userRedisService.addUserMoney(charge.getUserid(), addMoney);
                } else {
                    User user = userService.getUserByUserId(charge.getUserid());
                    System.out.println("修改玩家豆豆");
                    user.setMoney(user.getMoney() + addMoney);
                    userService.save(user);
                }

                System.out.println("通知客户端刷新充值");
                Map<String, String> rs = new HashMap<>();
                MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
            }else{
                logger.info("支付失败");
                return "failed";
            }
        }else{
            logger.info("支付失败");
            return "failed";
        }

        return "success";
    }

    @RequestMapping("/Pay/callback")
    public String callback(HttpServletRequest request){

        String memberid=request.getParameter("memberid");
        String orderid=request.getParameter("orderid");
        String amount=request.getParameter("amount");
        String datetime=request.getParameter("datetime");
        String returncode=request.getParameter("returncode");
        String sign=request.getParameter("sign");

        String transaction_id = request.getParameter("transaction_id");
        logger.info("{}", transaction_id);
        String SignTemp="amount="+amount+"&datetime="+datetime + "&memberid="+memberid+"&orderid="+orderid+"&transaction_id="+transaction_id+"&returncode="+returncode+"&key=" + keyValue + "";


        String md5sign= null;
        try {
            md5sign = md5(SignTemp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("-------------------------------");
        logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, sign, SignTemp);
        logger.info("-------------------------------");

        logger.info("-local sign：{}", md5sign);
        logger.info("-sign：{}", sign);
        if (true){
            if(returncode.equals("00")){
                Charge charge = chargeService.getChargeByOrderid(orderid);
                if (charge.getStatus() == 1){
                    return "success";
                }
                charge.setStatus(1);
                chargeService.save(charge);
                logger.info("支付成功！");

                UserBean UserBeanRedis = userRedisService.getUserBean(charge.getUserid());
                double addMoney = charge.getMoney();

                if (UserBeanRedis != null) {
                    userRedisService.addUserMoney(charge.getUserid(), addMoney);
                } else {
                    User user = userService.getUserByUserId(charge.getUserid());
                    System.out.println("修改玩家豆豆");
                    user.setMoney(user.getMoney() + addMoney);
                    userService.save(user);
                }

                System.out.println("通知客户端刷新充值");
                Map<String, String> rs = new HashMap<>();
                MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), charge.getUserid());
            }else{
                logger.info("支付失败");
                return "failed";
            }
        }else{
            logger.info("支付失败");
            return "failed";
        }

        return "success";
    }

    public static String md5(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;

            //字符数组转换成字符串
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString().toUpperCase();
            // 16位的加密
            //return buf.toString().substring(8, 24).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

//        return MD5Util.getMD5(str).toUpperCase();
//        return WXMD5.MD5Encode(str).toUpperCase();
    }

}
