package com.code.server.login.action;

import com.code.server.login.service.HomeService;
import com.code.server.login.util.CookieUtil;
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

    @RequestMapping("/show")
    public AgentResponse showHomePage(){

        long agentId = CookieUtil.getAgentIdByCookie();
        Map<Object, Object> result = homeService.findChargeInfoByOpenId(agentId);

        AgentResponse agentResponse = new AgentResponse();

        if (result != null){
            agentResponse.code = AgentResponse.SUCCESS;
            Map<String, Object> ret = new HashMap<>();
            ret.put("result", result);
            agentResponse.data = ret;
        }else {
            agentResponse.code = AgentResponse.ERROR;
            Map<String, Object> ret = new HashMap<>();
            ret.put("result", result);
            agentResponse.data = ret;
        }
        return agentResponse;
    }
}
