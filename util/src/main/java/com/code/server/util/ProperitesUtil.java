package com.code.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by sun on 2015/8/26.
 */
public class ProperitesUtil {
    public static void main(String[] args) throws Exception {

        Properties p = loadProperties("application.properties");
        System.out.println(p.getProperty("url"));

    }

    public static Properties loadProperties(String name) throws Exception {
        Properties p = new Properties();
        System.out.println(ProperitesUtil.class.getResource("/" + name));
        URL url = ProperitesUtil.class.getResource("/" + name);
//        System.out.println(url.getAuthority());
        if(url==null){
            throw new Exception("找不到文件");
        }
        String path = url.getPath();
        p.load(new FileInputStream(new File(path)));
        return p;
    }
}
