package com.code.server.login.util;

import com.code.server.login.config.ServerConfig;
import com.code.server.util.SpringUtil;
import com.github.binarywang.utils.qrcode.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-05-13.
 */
public class ZXingUtil {
    private static int WIDTH=300;
    private static int HEIGHT=300;
    private static String FORMAT="png";//二维码格式
    //生成二维码
    public static void createZxingqrCode(String content,String dir){
        //定义二维码参数
        Map hints=new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置容错等级
        hints.put(EncodeHintType.MARGIN, 2);//设置边距默认是5

        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            File path = new File(dir);
            MatrixToImageWriter.writeToFile(bitMatrix, FORMAT, path);//写到指定路径下
//            MatrixToImageWriter.writeToFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void createQrCode(long userId){
        String content = "http://fir.cool/admin/"+userId;

        String dir = SpringUtil.getBean(ServerConfig.class).getQrDir() + userId + "."+FORMAT;

        createZxingqrCode(content, dir);

    }

//    public static void main(String[] args) {
//        createZxingqrCode("http://baidu.com");
//    }
}
