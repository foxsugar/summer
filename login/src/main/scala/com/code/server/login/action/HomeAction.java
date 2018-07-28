package com.code.server.login.action;

import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.HomeService;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.HomePageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected static final Logger logger = LoggerFactory.getLogger(HomeAction.class);

    @Autowired
    private HomeService homeService;

    @AuthChecker
    @RequestMapping("/show")
    public AgentResponse showHomePage(){
        logger.info("==========/home/show/");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);
        logger.info("/home/show/ ===============================agent Id:是{}", agentId);

        HomePageVo homePageVo = homeService.showHomePage(agentId);
        Map<String, Object> rs = new HashMap<>();
        rs.put("result", homePageVo);
        AgentResponse agentResponse = new AgentResponse(200, rs);
        return agentResponse;
    }

}
