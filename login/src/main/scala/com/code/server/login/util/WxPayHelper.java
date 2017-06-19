package com.code.server.login.util;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;


public class WxPayHelper {

    /**
     * 取出一个指定长度大小的随机正整数.
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }


    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return outFormat.format(now);
    }

    /**
     * 获取随机字符串
     */
    public static String getNonceStr() {
        // 随机数
        String currTime = getCurrTime();
        // 8位日期
        String strTime = currTime.substring(8, currTime.length());
        // 四位随机数
        String strRandom = buildRandom(4) + "";
        // 10位序列号,可以自行调整。
        return strTime + strRandom;
    }


    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public static String createSign(SortedMap<String, String> packageParams) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : packageParams.entrySet()) {

            String k = entry.getKey();
            String v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        
      //  String key="1Q2w3e4r5t6y7u8i9Oa1s2d3f4g5h6j7";
        sb.append("key="+ PayUtil.Key);
     //  商户key即密钥： 
        return WXMD5.MD5Encode(sb.toString())
                .toUpperCase();

    }

    public static String getRequestXml(SortedMap<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        String sign=WxPayHelper.createSign(parameters);
        
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
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
        return sb.toString();
    }
    

}
