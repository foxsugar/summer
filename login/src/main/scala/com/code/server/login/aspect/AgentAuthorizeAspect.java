package com.code.server.login.aspect;

import com.code.server.login.action.AgentAction;
import com.code.server.login.constant.CookieConstant;
import com.code.server.login.constant.RedisConstant;
import com.code.server.login.util.CookieUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dajuejinxian on 2018/4/2.
 */

@Aspect
@Component
public class AgentAuthorizeAspect
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AgentAction action;

    @Pointcut("execution(* com.code.server.login.action.AgentAction.*Action(..))")
    public void verify() {}

    @Before("verify()")
    public void doVerify() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        System.out.println("---登录验证");
        //查询cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null) {
//            log.warn("【登录校验】Cookie中查不到token");
            System.out.println("【登录校验】Cookie中查不到token");
            return;
        }

        //去redis里查询
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)) {
            System.out.println("【登录校验】Redis中查不到token");
        }
    }
}
