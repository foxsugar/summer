package com.code.server.login.action;
import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.RecommendDelegateService;
import com.code.server.login.vo.RecommandUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
@RestController
@RequestMapping("/recommandDelegate")
public class RecommendDelegateAction {

    @Autowired
    private RecommendDelegateService recommendDelegateService;

    @AuthChecker
    @RequestMapping("/findUser")
    public AgentResponse findUser(long userId){

        RecommandUserVo vo =  recommendDelegateService.findRecommandUser(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", vo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/bindDelegate")
    public AgentResponse bindDelegate(long userId){

        boolean ret = recommendDelegateService.bindDelegate(userId);
        Map<String, Object> result = new HashMap<>();

        AgentResponse agentResponse = null;
        if (ret){
            result.put("msg", "绑定失败");
            agentResponse = new AgentResponse(500, result);
        }else {
            result.put("msg", "绑定成功");
            agentResponse = new AgentResponse(200, result);
        }

        return agentResponse;
    }
}
