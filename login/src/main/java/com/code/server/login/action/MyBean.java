package com.code.server.login.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by sunxianping on 2017/5/24.
 */
@Component
public class MyBean {
    @Autowired
    private StringRedisTemplate template;

    @Autowired
    public MyBean(StringRedisTemplate template) {
        this.template = template;

    }

    // ...

}