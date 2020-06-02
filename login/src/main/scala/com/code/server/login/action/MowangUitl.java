package com.code.server.login.action;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MowangUitl
 * @Description TODO
 * @Author sunxp
 * @Date 2020/6/1 15:00
 **/
public class MowangUitl {


    private static String toHexValue(byte[] messageDigest) {
        if (messageDigest == null)
            return "";
        StringBuilder hexValue = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            int val = 0xFF & aMessageDigest;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * @param params
     * @return
     */


    public static String sign(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        int count = keys.size();
        for (String s : keys) {
            sb.append(s);
            sb.append("=");
            if (!hasNullStr(params.get(s))) {
                sb.append(params.get(s));
            }
            if (count > 1) {
                sb.append("&");
            }
            count--;
        }
        String sign = "";
        try {
            sign = toHexValue(encryptMD5(sb.toString().getBytes(Charset.forName("utf-8"))));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("md5 error");
        }
        return sign;
    }


    private static byte[] encryptMD5(byte[] data) throws

            Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    private static boolean hasNullStr(String arg) {
        return arg == null || arg.trim().equals("")
                || arg.trim().equalsIgnoreCase("null");
    }


}
