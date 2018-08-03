package com.code.server.login.aspect;

import com.code.server.login.action.AgentResponse;
import com.code.server.login.util.AgentUtil;
import com.code.server.redis.service.RedisManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.management.Agent;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/7/9.
 */
@Component
@Aspect
public class DemoAopAdviseDefine {

    @Pointcut("@annotation(com.code.server.login.anotation.DemoChecker)")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        String token = AgentUtil.findTokenInHeader();
        if (!AgentUtil.caches.keySet().contains(token)){
            Map<String, Object> rs = new HashMap<>();
            return new AgentResponse(50008, rs);
        }
        return joinPoint.proceed();
    }

    public static Map<String, String> getAgentByToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie cookie = null;
        for (Cookie c : cookies) {
//            if (AGENT_COOKIE_NAME.equals(c.getName())) {
//                cookie = c;
//            }
            System.out.println(c.getName());
        }
        if (cookie != null) {
            return RedisManager.getAgentRedisService().getAgentByToken(cookie.getValue());
        }

        return null;
    }

}
