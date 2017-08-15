package com.code.server.admin.action;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    public Object login( HttpServletRequest request,HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);

        Map<String, Object> result = new HashMap<>();

        result.put("result","ok");
        return result;

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
