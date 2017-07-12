package com.code.server.login.pay;


import com.code.server.db.Service.ChargeService;
import com.code.server.db.model.Charge;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.util.PayUtil;
import com.code.server.login.util.TestGetPost;
import com.code.server.login.util.WxPayHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Administrator on 2017/5/23.
 */
@RestController
public class UnifiedOrder {

    @Autowired
    private ChargeService chargeService ;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 微信统一下单
     * @param userId
     * @param spIp
     * @param origin
     * @param money
     * @return
     */
    @RequestMapping(value = "/charge", method = RequestMethod.POST)
    public Map<String,Object> charge(String userId, String spIp, int origin, int money) {

        Map<String,Object> result = new HashMap<>();
        int code = 0;
//		if(user.getVip() == null || "0".equals(user.getVip())){
//			code = ErrorCode.CHARGE_NO_BIND;
//		}
        if (code != 0) {
            result.put("code", code);
            return result;
        }


        SortedMap<String,String> packageParams = new TreeMap<>();

        //微信

        int money100 = money*100;

        String body = "龙七棋牌-充值";

        String bodyUTF8 = null;
        try {
            bodyUTF8 = new String(body.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String orderId = PayUtil.getOrderIdByUUId();
        packageParams.put("appid",serverConfig.getAppId());//appID       应用id
        packageParams.put("mch_id",serverConfig.getMchId());//appID       商户号
        packageParams.put("nonce_str", PayUtil.getRandomStringByLength(32));//32位随机数
        packageParams.put("body",bodyUTF8);//商品描述
        packageParams.put("out_trade_no",orderId);
        packageParams.put("total_fee",""+money100);//充值金额
        packageParams.put("spbill_create_ip",spIp);//终端IP
        packageParams.put("trade_type","APP");//支付类型
        packageParams.put("notify_url",serverConfig.getNotifyUrl());//通知地址

        String rtn = postCharge(packageParams);

        Element root = PayUtil.ParsingXML(rtn);//解析xmlString

        SortedMap<String,String> secondParams = new TreeMap<>();

        //成功
        if("SUCCESS".equals(root.elementText("return_code"))){
            //业务成功
            if("SUCCESS".equals(root.elementText("result_code"))){



                secondParams.put("appid",serverConfig.getAppId());
                secondParams.put("partnerid", serverConfig.getMchId());
                secondParams.put("prepayid", root.elementText("prepay_id"));
                secondParams.put("noncestr", root.elementText("nonce_str"));
                secondParams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
                secondParams.put("package", "Sign=WXPay");

                String paySign = PayUtil.createSign("UTF-8",serverConfig.getKey(), secondParams);

                secondParams.put("sign", paySign);




                Charge charge = new Charge();
                charge.setOrderId(orderId);
                charge.setUserid(Long.valueOf(userId));
                charge.setMoney(money);
                charge.setMoney_point(money*10);
                charge.setOrigin(origin);
                charge.setStatus(0);
                charge.setSign(paySign);
                charge.setSp_ip(spIp);
                charge.setRecharge_source("1");
                charge.setCallbacktime(new Date());
                chargeService.save(charge);



            }else{
                //余额不足
                if("NOTENOUGH".equals(root.elementText("err_code"))){
                    secondParams.put("err_code", root.elementText("err_code"));
                    secondParams.put("err_code_des", root.elementText("err_code_des"));
                    result.put("code", 10000);
                    return result;
                    //订单已支付
                }else if("ORDERPAID".equals(root.elementText("err_code"))){
                    secondParams.put("err_code", root.elementText("err_code"));
                    secondParams.put("err_code_des", root.elementText("err_code_des"));
                    result.put("code", 11111);
                    return result;
                    //订单已关闭
                }else if("ORDERCLOSED".equals(root.elementText("err_code"))){
                    secondParams.put("err_code", root.elementText("err_code"));
                    secondParams.put("err_code_des", root.elementText("err_code_des"));
                    result.put("code", 10001);
                    return result;
                }
            }
        }

        result.put("params", secondParams);
        result.put("code", 0);

        return result;
    }

    public static String postCharge(SortedMap<String, String> params){
        StringBuilder sb = new StringBuilder();
        String sign= WxPayHelper.createSign(params);

        sb.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<").append(k).append("><![CDATA[").append(v)
                        .append("]]></").append(k).append(">");
            } else {
                sb.append("<").append(k).append(">").append(v).append("</")
                        .append(k).append(">");
            }
        }

        sb.append("<").append("sign").append("><![CDATA[").append(sign)
                .append("]]></").append("sign").append(">");
        sb.append("</xml>");
        System.out.println(sb.toString());
        //String ss = TestGetPost.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", sb.toString());

        String ss = TestGetPost.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", sb.toString());

        return ss;
    }
}
