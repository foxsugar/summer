package com.code.server.login.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunxianping on 2019-10-30.
 */
public class SHA256Util {

    public static String getSHA256Str(String str) {

        MessageDigest messageDigest;

        String encodeStr = "";

        try {

            messageDigest = MessageDigest.getInstance("SHA-256");

            messageDigest.update(str.getBytes("UTF-8"));

            encodeStr = byte2Hex(messageDigest.digest());

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return encodeStr;

    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */

    private static String byte2Hex(byte[] bytes) {

        StringBuffer stringBuffer = new StringBuffer();

        String temp = null;

        for (int i = 0; i < bytes.length; i++) {

            temp = Integer.toHexString(bytes[i] & 0xFF);

            if (temp.length() == 1) {

//1得到一位的进行补0操作

                stringBuffer.append("0");

            }

            stringBuffer.append(temp);

        }

        return stringBuffer.toString();

    }



    public static String HMACSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");

        sha256_HMAC.init(secret_key);

        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();

        for (byte item : array) {

            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));

        }

        return sb.toString().toUpperCase();

    }
}
