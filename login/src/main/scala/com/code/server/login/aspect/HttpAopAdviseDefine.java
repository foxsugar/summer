package com.code.server.login.aspect;

import com.code.server.login.action.AgentAction;
import com.code.server.login.action.AgentResponse;
import com.code.server.redis.service.RedisManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/1.
 */
@Component
@Aspect
public class HttpAopAdviseDefine {

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";

    private static final Logger logger = LoggerFactory.getLogger(HttpAopAdviseDefine.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Pointcut("@annotation(com.code.server.login.anotation.AuthChecker)")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        // 检查用户所传递的 token 是否合法
        Map<String, String> map = getAgentByToken(request);

        Object[] objects = joinPoint.getArgs();

        if (null == map) {
//            return "错误, 请登录!";
            Map<String, Object> rs = new HashMap<>();
            return new AgentResponse(1000, rs);
        };

        logger.info("----------map is -- {}", map);
        return joinPoint.proceed();
    }

    public static Map<String, String> getAgentByToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie cookie = null;
        for (Cookie c : cookies) {
            if (AGENT_COOKIE_NAME.equals(c.getName())) {
                cookie = c;
            }
        }
        if (cookie != null) {
            return RedisManager.getAgentRedisService().getAgentByToken(cookie.getValue());
        }

        return null;
    }

}
