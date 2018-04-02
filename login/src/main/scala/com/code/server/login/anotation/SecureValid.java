package com.code.server.login.anotation;

import java.lang.annotation.*;

/**
 * Created by dajuejinxian on 2018/4/2.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SecureValid {
    String desc() default "身份和安全验证开始...";
}
