package com.code.server.login.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sunxianping on 2018/3/14.
 */

@Controller
@RequestMapping(value = "/")
public class AgentAction {


    @GetMapping(value = "/index1")
    String charge(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("======");
//        response.sendRedirect("tt.html");
        Cookie[] cookies = request.getCookies();
        Cookie cookie = new Cookie("name","sun");
        response.addCookie(cookie);
        return "redirect:http://192.168.1.132:8080";
//        return "/ttt";
    }
}
