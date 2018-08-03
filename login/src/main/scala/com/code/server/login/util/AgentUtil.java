package com.code.server.login.util;

import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/4/2.
 */
public class AgentUtil {

    public static Map<String, Object> caches = null;
    static {
        caches = new HashMap<>();
    }

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";

    public static final String DEMO_COOKIE_NAME = "yy-token";

    protected static final Logger logger = LoggerFactory.getLogger(AgentUtil.class);
    //暂时这么命名 根据token取 agentId

    public static long getAgentByRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        for (Cookie c : cookies) {
            if (AGENT_COOKIE_NAME.equals(c.getName())) {
                cookie = c;
            }
        }
        String agentIdStr = RedisManager.getAgentRedisService().getAgentByToken(cookie.getValue()).get("agentId");
        logger.info("agentId is:{}", agentIdStr);
        return Integer.parseInt(agentIdStr);
    }

    /**
     * 设置
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void set(HttpServletResponse response,
                           String name,
                           String value,
                           int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
//        cookie.setDomain(serverConfig.getDomain());
//        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//        cookie.setDomain(serverConfig.getDomain());
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static long getUserIdByToken(String token){
        Map<String, Object> rs = (Map<String, Object>) caches.get(token);
        int uid = (Integer) rs.get("id");
        return uid;
    }

    public static boolean clearUserTokenByUserId(int uid){
        String ret = null;
        for (String key : caches.keySet()){
            Object o = caches.get(key);
            Map<String, Object> m = (Map<String, Object>) o;
            Integer id = (Integer) m.get("id");
            if (id - uid == 0){
                ret = key;
                break;
            }
        }
        if (ret != null) {
            caches.remove(ret);
        }
        return true;
    }

    public static String findTokenInHeader(){
        return findAttributeInHeaders(AgentUtil.DEMO_COOKIE_NAME);
    }

    public static String findAttributeInHeaders(String key){
        String token = "";
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        Enumeration e = request.getHeaderNames();

        boolean find = false;
        while (e.hasMoreElements()) {
            if (find) break;
            String headerName = (String) e.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String str = headerValues.nextElement();
//                System.out.println(headerName + ":" + str);
                if (headerName.equals(key)){
                    token = str;
                    find = true;
                }
            }
        }
        return token;
    }
//
//    public  void setCookies(HttpServletResponse response,
//                           String name,
//                           String value,
//                           int maxAge) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setPath("/");
//        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
//        cookie.setDomain(serverConfig.getDomain());
//        cookie.setMaxAge(maxAge);
//        response.addCookie(cookie);
//    }

    /**
     * 获取cookie
     * @param request
     * @param name
     * @return
     */
    public static Cookie get(HttpServletRequest request,
                             String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            return cookieMap.get(name);
        }else {
            return null;
        }
    }

    /**
     * 将cookie封装成Map
     * @param request
     * @return
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }



}
