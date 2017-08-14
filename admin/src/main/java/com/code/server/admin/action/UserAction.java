package com.code.server.admin.action;

import com.code.server.util.JsonUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/8/14.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value ="/user")
public class UserAction {

    @RequestMapping(value = "/login")
    public String login(@PathVariable String username, @PathVariable String password, HttpServletRequest request, String callback,HttpServletResponse response) {
        System.out.println(username);
        System.out.println(password);
        Map<String, Object> result = new HashMap<>();
        result.put("result","ok");
        return JsonUtil.toJson(result);

    }

//    @RequestMapping(value = "/user", method = RequestMethod.GET)
//    public void user( HttpServletRequest request, HttpServletResponse response) {
////        System.out.println(username);
////        System.out.println(password);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result","ok");
//
//    }
}
