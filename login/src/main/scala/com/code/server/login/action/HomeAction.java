package com.code.server.login.action;

import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.HomeService;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.HomePageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/8.
 */

@RestController
@EnableAutoConfiguration
@RequestMapping("/home")
public class HomeAction {

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";

    @Autowired
    private HomeService homeService;

    @AuthChecker
    @RequestMapping("/show")
    public AgentResponse showHomePage(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        HomePageVo homePageVo = homeService.showHomePage(agentId);
        Map<String, Object> rs = new HashMap<>();
        rs.put("result", homePageVo);
        AgentResponse agentResponse = new AgentResponse(200, rs);
        return agentResponse;
    }

}
