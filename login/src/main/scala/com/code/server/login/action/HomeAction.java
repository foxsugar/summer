package com.code.server.login.action;

import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.HomeService;
import com.code.server.login.util.CookieUtil;
import com.code.server.login.vo.HomePageVo;
import com.code.server.login.vo.HomeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/8.
 */

@RestController
@EnableAutoConfiguration
@RequestMapping("/home")
public class HomeAction {

    @Autowired
    private HomeService homeService;

    @AuthChecker
    @RequestMapping("/show")
    public AgentResponse showHomePage(){
        HomePageVo homePageVo = homeService.showHomePage();
        Map<String, Object> rs = new HashMap<>();
        rs.put("result", homePageVo);
        AgentResponse agentResponse = new AgentResponse(200, rs);
        return agentResponse;
    }
}
