package com.code.server.login.action;

import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.GameAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by sunxianping on 2018/3/14.
 */

@RestController
@RequestMapping(value = "/agent")
public class AgentAction {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private GameAgentService gameAgentService;

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
        data.put("agentId", 1);
        data.put("charge", 1);
        AgentResponse agentResponse = new AgentResponse(0, data);

        return agentResponse;

    }


    @RequestMapping("/test")
    public AgentResponse test(String roomId) {
        List<Long> users = new ArrayList<>();
//        users.add(1L);
        users.add(2L);
        LocalDate s = LocalDate.of(2017,1,1);
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zdt = s.atStartOfDay(zone);
        Date start = Date.from(zdt.toInstant());
        Date end = new Date();
        Integer a = chargeService.chargeDao.getSumMoneyByUsersAndDate(users, start, end);
        System.out.println(a);
        AgentResponse agentResponse = new AgentResponse();

        return agentResponse;

    }


    public AgentResponse becomeAgent(String userId){

        AgentResponse agentResponse = new AgentResponse();



        return agentResponse;
    }





}
