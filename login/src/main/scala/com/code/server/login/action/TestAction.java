package com.code.server.login.action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by sunxianping on 2017/12/20.
 */
@Controller
public class TestAction {

    protected static Logger logger= LoggerFactory.getLogger(TestAction.class);
    @RequestMapping(path = "/testjsp")
    public String  index() {
//        view.setViewName("testjsp");
//        return view;
        return "testjsp";
    }


    @RequestMapping("/tt")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "hh");
        return "welcome";
    }

    @RequestMapping("/index")
    public String mindex(){

        return "index";
    }

    @RequestMapping("/pay")
    public String pay(){

        return "pay";
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
        String keyValue="";
        String SignTemp="amount="+amount+"+datetime="+datetime+"+memberid="+memberid+"+orderid="+orderid+"+returncode="+returncode+"+key="+keyValue+"";
//        String md5sign=MD5(SignTemp,32,1);//MD5加密
//        if (sign.equals(md5sign)){
//            if(returncode.equals("00")){
//                //支付成功，写返回数据逻辑
//                PrintWriter pw=response.getWriter();
//                pw.write("ok");
//            }else{
//                PrintWriter pw=response.getWriter();
//                pw.write("支付失败");
//            }
//        }else{
//            PrintWriter pw=response.getWriter();
//            pw.write("验签失败");
//        }
        logger.info("-------------------------------");
        logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, reserved1, reserved2, sign, SignTemp);
        logger.info("-------------------------------");
        return "notify";
    }

    @RequestMapping("/Pay/callback")
    public String callback(HttpServletRequest request){

        String memberid=request.getParameter("memberid");
        String orderid=request.getParameter("orderid");
        String amount=request.getParameter("amount");
        String datetime=request.getParameter("datetime");
        String returncode=request.getParameter("returncode");
        String reserved1=request.getParameter("reserved1");
        String reserved2=request.getParameter("reserved2");
        String sign=request.getParameter("sign");
        String SignTemp="amount="+amount+"+datetime="+datetime+"+memberid="+memberid+"+orderid="+orderid+"+returncode="+returncode+"+key="+"";

        logger.info("-------------------------------");
        logger.info("memberid{}, orderid{}, amount{}, datetime{}, requestcode{}, returncode{}, reserved1{}, reserverd2{}, sign{}, tempSign{}",memberid,orderid, amount, datetime, returncode, reserved1, reserved2, sign, SignTemp);
        logger.info("-------------------------------");
        return "callback";
    }

}
