package com.code.server.login.util;

import com.code.server.redis.service.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/4/2.
 */
public class AgentUtil {

    public static Map<String, Object> yy_Caches = null;
    static {
        yy_Caches = new HashMap<>();
    }

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";
    protected static final Logger logger = LoggerFactory.getLogger(AgentUtil.class);
    //暂时这么命名 根据token取 agentId
    public static long getAgentIdByCookie(){
        return 1;
    }

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
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

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
