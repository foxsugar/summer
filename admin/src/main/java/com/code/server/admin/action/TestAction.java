package com.code.server.admin.action;

import com.code.server.admin.config.ServerConfig;
import com.code.server.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by sunxianping on 2017/8/11.
 */
@Controller
//@EnableAutoConfiguration
public class TestAction {


    @RequestMapping("/share")
    public String share(HttpServletRequest request, Model model, long userId) throws Exception {


//        model.addAttribute("users", "hello");
        HttpClient httpclient = HttpClients.createDefault();
        String ip = getIpAddr(request);

        // 创建http GET请求
        String local = SpringUtil.getBean(ServerConfig.class).getGameRpcHost();

        HttpGet httpGet = new HttpGet(local + "?userId=" + userId + "&ip=" + ip);
        httpclient.execute(httpGet);
        return "/show";
    }


    public static String getIpAddr(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
// 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
