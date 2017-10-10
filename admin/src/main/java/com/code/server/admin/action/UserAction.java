package com.code.server.admin.action;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Map<String, Object> data = new HashMap<>();
        data.put("token", 1);

        result.put("code", 20000);

        result.put("data", data);

        result.put("result","ok");
        return result;

    }


    @RequestMapping(value = "/info")
    @ResponseBody
    public Object getInfo( HttpServletRequest request,HttpServletResponse response) {


        String token = request.getParameter("token");
        System.out.println(token);
        Map<String, Object> result = new HashMap<>();

        result.put("code", 20000);

        Map<String, Object> data = new HashMap<>();
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        data.put("role", roles);
        data.put("name", "sun");
        data.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("data", data);



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
