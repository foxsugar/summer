package com.code.server.login.service;

import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.db.model.Club;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2019-10-29.
 */

@Service("duoLiaoService")
public class DuoLiaoService {

    private static final String record_url = "http://dlappapi.nbmenghai.com/index.php/Oauth/recordPushUnbind";
    private static final String bind_url = "http://dlappapi.nbmenghai.com/index.php/Oauth/binGroup";
    private static final String appId = "fzh3ntwEBUr4SQRo";
    private static final String appSecret = "gjFFqgFYk0yhshhK";

    public void sendRecord() {

    }


//    {"appid":"xxx","appid2":"xxxx","appid3":"xxx"} 转json字符串后的格式
// /login?tid=2709363745&openid=1003Page%20not%20found%20at%20/login HTTP/1.1" 404 7116
//

    private static void sendLq_http(RoomRecord roomRecord, Club club) {
//        2709363745
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        LocalDate localDate = LocalDate.now();


        Map<String, Object> result = new HashMap<>();
        Map<String, Object> apiContent = new HashMap<>();
        result.put("appid", appId);
        result.put("version", "1.1");
        result.put("timestamp", localDate.toString());
        result.put("apiContent", localDate.toString());

        result.put("signType", localDate.toString());


        apiContent.put("gourl", "");
        apiContent.put("recordTime", localDate.toString());
        apiContent.put("recordId", roomRecord.getId());
        apiContent.put("msgContent", "战绩");
        apiContent.put("msgTitle", "战绩");
        apiContent.put("tid", club.getId());

        List<Map<String, Object>> list = new ArrayList<>();
        apiContent.put("userInfo", list);
        for (com.code.server.constant.game.UserRecord userRecord : roomRecord.getRecords()) {
            Map<String, Object> u = new HashMap<>();
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userRecord.getUserId());
            u.put("game_id", userBean.getId());
            u.put("g_name", userBean.getUsername());
            u.put("headstr", userBean.getImage() + "/132");
            u.put("score", userRecord.getScore());
            list.add(u);
        }






        String json = JsonUtil.toJson(apiContent);
        HttpClient httpClient = HttpClientBuilder.create().build();
        //设置连接超时5s
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();

        String url1 = serverConfig.getLq_http_url() + "?strContext=" + json;

        try {
            URL url = new URL(url1);
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
            HttpGet request = new HttpGet(uri);
            request.setConfig(requestConfig);
            try {
                httpClient.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        try {
//            lq_upScoreRecord(roomRecord.getRoomId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
