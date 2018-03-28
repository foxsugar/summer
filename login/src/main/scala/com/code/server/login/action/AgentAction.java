package com.code.server.login.action;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/3/14.
 */

@RestController
@RequestMapping(value = "/agent")
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


    @RequestMapping("/getAgentInfo")
    public AgentResponse getRoomUser(String roomId) {
        Map<String, Object> data = new HashMap<>();
        data.put("money", 1);
        AgentResponse agentResponse = new AgentResponse(0, data);

        return agentResponse;

    }

}
