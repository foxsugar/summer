package com.code.server.login.service;

import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.db.model.Club;
import com.code.server.login.util.SHA256Util;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import com.code.server.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by sunxianping on 2019-10-29.
 */

@Service("duoLiaoService")
public class DuoLiaoService {

    private static final String record_url = "http://dlappapi.nbmenghai.com/index.php/Oauth/recordPushUnbind";
    private static final String bind_url = "http://dlappapi.nbmenghai.com/index.php/Oauth/binGroup";
    private static final String appId = "zPFJPA7PYPZope0H";
    private static final String appSecret = "ihvmNZlFpEQUPUfUUOwW";
    private static final String version = "1.1";
    private static final String signType = "SHA256";


//    {"appid":"xxx","appid2":"xxxx","appid3":"xxx"} 转json字符串后的格式
// /login?tid=2709363745&openid=1003Page%20not%20found%20at%20/login HTTP/1.1" 404 7116
//

    public static String sign(String date, Object apiContent) {
        String json = "appid=" + appId + "&version=1.1&timestamp=" + date + "&signType=" + signType + "&apiContent=" + JsonUtil.toJson(apiContent) + "&appsecret=" + appSecret;
        String sign = null;
        try {
            sign = SHA256Util.HMACSHA256(json, appSecret).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }


    public static void bind(String clubId, String tid) {
        String date = DateUtil.convert2String(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_SS_MI);
        Map<String, Object> apiContent = new HashMap<>();
        apiContent.put("tid", tid);
        apiContent.put("game_tid", clubId);



        Map<String, Object> result = new LinkedHashMap<>();

        result.put("appid", appId);
        result.put("version", "1.1");
        result.put("timestamp", date);
        result.put("signType", "SHA256");

        result.put("apiContent", apiContent);
        result.put("appsecret", appSecret);



        String sign = sign(date, apiContent);

        result.put("sign", sign);



        HttpClient httpClient = HttpClientBuilder.create().build();
        //设置连接超时5s
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();

        try {
            URL url = new URL(bind_url);
            List<NameValuePair> l = new ArrayList<>();
            l.add(new BasicNameValuePair("appid", appId));
            l.add(new BasicNameValuePair("version", version));
            l.add(new BasicNameValuePair("timestamp", date));
            l.add(new BasicNameValuePair("apiContent", JsonUtil.toJson(apiContent)));
            l.add(new BasicNameValuePair("signType", signType));
            l.add(new BasicNameValuePair("sign", sign));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(l, Charset.forName("utf-8"));
            // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中

            URI uri = new URIBuilder().setScheme(url.getProtocol()).setHost(url.getHost())
                    .setPath(url.getPath()).setParameters(l).build();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);
//            httpPost.setHeader("Content-Type", "application/json;charset=utf8");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setEntity(entity);//

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity1 = httpResponse.getEntity();
                String rr = EntityUtils.toString(entity1);
                System.out.println(rr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public static void sendRecord(RoomRecord roomRecord, Club club) {
//        2709363745
        String date = DateUtil.convert2String(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_SS_MI);
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> apiContent = new LinkedHashMap<>();
        result.put("appid", appId);
        result.put("version", "1.1");
        result.put("timestamp", date);
        result.put("signType", "SHA256");

        apiContent.put("gourl", "a");
        apiContent.put("recordTime", date);
        apiContent.put("recordId", roomRecord.getId());
        apiContent.put("msgContent", "战绩");
        apiContent.put("msgTitle", "战绩");
        apiContent.put("tid", club.getClubInfo().getDuoliaoTid());
        result.put("apiContent", apiContent);
        result.put("appsecret", appSecret);

        List<Map<String, Object>> list = new ArrayList<>();

        for (com.code.server.constant.game.UserRecord userRecord : roomRecord.getRecords()) {
            Map<String, Object> u = new HashMap<>();
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userRecord.getUserId());
            u.put("game_id", userBean.getId());
            u.put("g_name", userBean.getUsername());
            u.put("headstr", userBean.getImage() + "/132");
            u.put("score", userRecord.getScore());
            list.add(u);
        }
        apiContent.put("userInfo", JsonUtil.toJson(list));


        String sign = sign(date, apiContent);

        result.put("sign", sign);



        HttpClient httpClient = HttpClientBuilder.create().build();
        //设置连接超时5s
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();

        try {
            URL url = new URL(record_url);
            List<NameValuePair> l = new ArrayList<>();
            l.add(new BasicNameValuePair("appid", appId));
            l.add(new BasicNameValuePair("version", version));
            l.add(new BasicNameValuePair("timestamp", date));
            l.add(new BasicNameValuePair("apiContent", JsonUtil.toJson(apiContent)));
            l.add(new BasicNameValuePair("signType", signType));
            l.add(new BasicNameValuePair("sign", sign));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(l, Charset.forName("utf-8"));
            // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中

            URI uri = new URIBuilder().setScheme(url.getProtocol()).setHost(url.getHost())
                    .setPath(url.getPath()).setParameters(l).build();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);
//            httpPost.setHeader("Content-Type", "application/json;charset=utf8");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setEntity(entity);//

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity1 = httpResponse.getEntity();
                String rr = EntityUtils.toString(entity1);
                System.out.println(rr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {


        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(123456);
        com.code.server.constant.game.UserRecord userRecord = new UserRecord();
        userRecord.setName("a").setImage("httt").setScore(1).setUserId(1);
        roomRecord.addRecord(userRecord);

        Club club = new Club();
        club.getClubInfo().setDuoliaoTid("2709363745");

        sendRecord(roomRecord, club);
//        System.out.println(LocalDateTime.now().toString());
    }


}
