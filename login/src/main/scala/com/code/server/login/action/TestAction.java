package com.code.server.login.action;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.model.Charge;
import com.code.server.login.util.PayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunxianping on 2017/12/20.
 */
@Controller
public class TestAction {

    @Autowired
    private ChargeService chargeService;

    protected static Logger logger= LoggerFactory.getLogger(TestAction.class);

    private static final String keyValue = "xhzw2malfjk62p0g8m9by7ycx97fqahv" ;

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
    @ResponseBody
    public String noti(HttpServletRequest request){

        String memberid=request.getParameter("memberid");
        String orderid=request.getParameter("orderid");
        String amount=request.getParameter("amount");
        String datetime=request.getParameter("datetime");
        String returncode=request.getParameter("returncode");
        String reserved1=request.getParameter("reserved1");
        String reserved2=request.getParameter("reserved2");
        String sign=request.getParameter("sign");
        String keyValue="";
        String transaction_id = request.getParameter("transaction_id");
        String SignTemp="amount="+amount+"+datetime="+datetime+ "+key="+keyValue + "+memberid="+memberid+"+orderid="+orderid+"+returncode="+returncode+"+transaction_id="+transaction_id+"";
        String md5sign= null;
        try {
            md5sign = md5(SignTemp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("local sign：{}", md5sign);
        logger.info("sign：{}", sign);

        if (sign.equals(md5sign)){
            if(returncode.equals("00")){
                Charge charge = chargeService.getChargeByOrderid(orderid);
                charge.setStatus(1);
                chargeService.save(charge);
                logger.info("支付成功！");
            }else{
                logger.info("支付失败");
            }

            logger.info("-------------------------------");
            logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, reserved1, reserved2, sign, SignTemp);
            logger.info("-------------------------------");

        }else{
            logger.info("验证失败");
        }

        logger.info("-------------------------------");
        logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, reserved1, reserved2, sign, SignTemp);
        logger.info("-------------------------------");

        return "notify";
    }

    @RequestMapping("/Pay/callback")
    @ResponseBody
    public String callback(HttpServletRequest request){

        String memberid=request.getParameter("memberid");
        String orderid=request.getParameter("orderid");
        String amount=request.getParameter("amount");
        String datetime=request.getParameter("datetime");
        String returncode=request.getParameter("returncode");
        String reserved1=request.getParameter("reserved1");
        String reserved2=request.getParameter("reserved2");
        String sign=request.getParameter("sign");

        String transaction_id = request.getParameter("transaction_id");
        logger.info("{}", transaction_id);
        String SignTemp="amount="+amount+"+datetime="+datetime+ "+key="+keyValue + "+memberid="+memberid+"+orderid="+orderid+"+returncode="+returncode+"+transaction_id="+transaction_id+"";


        String md5sign= null;
        try {
            md5sign = md5(SignTemp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("-local sign：{}", md5sign);
        logger.info("-sign：{}", sign);
        if (sign.equals(md5sign)){
            if(returncode.equals("00")){
                Charge charge = chargeService.getChargeByOrderid(orderid);
                charge.setStatus(1);
                chargeService.save(charge);
                logger.info("支付成功！");

            }else{
                logger.info("支付失败");
            }


            logger.info("-------------------------------");
            logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{},  reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, reserved1, reserved2, sign, SignTemp);
            logger.info("-------------------------------");

        }else{
            logger.info("验证失败");
        }

        return "callback";
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
    }

}
