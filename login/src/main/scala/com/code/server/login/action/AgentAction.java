package com.code.server.login.action;

import com.code.server.constant.game.AgentBean;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.Recommend;
import com.code.server.login.service.AgentService;
import com.code.server.redis.service.AgentRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

@Controller
@RequestMapping(value = "/agent")
public class AgentAction {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private GameAgentService gameAgentService;

    @Autowired
    private UserService userService;

    @Autowired
    AgentRedisService agentRedisService;

    @Autowired
    RecommendService recommendService;

    @GetMapping(value = "/index1")
    String charge(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("======");
//        response.sendRedirect("tt.html");
        Cookie[] cookies = request.getCookies();
        Cookie cookie = new Cookie("name","sun");
        Cookie cookie1 = new Cookie("Admin-Token","Admin-Token");
        response.addCookie(cookie);
        response.addCookie(cookie1);
        return "redirect:http://192.168.1.132:8080/#/index";
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
    public AgentResponse testAction(String roomId) {
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


    @RequestMapping("/becomeAgent")
    public AgentResponse becomeAgent(long userId){

        AgentResponse agentResponse = new AgentResponse();


        AgentBean agentBean = agentRedisService.getAgentBean(userId);

        if (agentBean != null) {
            //todo 之前是代理
        }




        //之前不是代理
        if (agentBean == null) {
            GameAgent gameAgent = new GameAgent();
            gameAgent.setId(userId);

            //推荐
            String openId = userService.getUserDao().getOpenIdById(userId);
            Recommend recommend = recommendService.getRecommendDao().findOne(openId);

            //有推荐
            if (recommend != null) {
                long agentId = recommend.getAgentId();

                AgentBean parent = agentRedisService.getAgentBean(agentId);
                //上级代理存在
                if (parent != null) {
                    //和上级的partner是同一个
                    gameAgent.setPartnerId(parent.getPartnerId());
                    gameAgent.setParentId(agentId);
                    gameAgent.setIsPartner(0);

                    //上级代理加一个child
                    parent.getChildList().add(userId);
                }
            }

            //保存到数据库
            gameAgentService.getGameAgentDao().save(gameAgent);
            agentBean = AgentService.gameAgent2AgentBean(gameAgent);
            //保存的reids
            agentRedisService.setAgent2Redis(agentBean);

        }
        return agentResponse;
    }

    public void errorCallback(int err){
        System.out.println("错误，失败原因是..." + err);
    }


}
