package com.code.server.login.action;

import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.DelegateRelataionService;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.login.vo.ThreeLevelInfoVo;
import com.code.server.login.vo.TwoLevelInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/30.
 */
@RestController
@RequestMapping("/delegateRel")
public class DelegateRelataionAction {

    @Autowired
    private DelegateRelataionService delegateRelataionService;

    @AuthChecker
    @RequestMapping("/fetchPlayers")
    public AgentResponse fetchPlayers(){

        List<OneLevelInfoVo> levelInfoVoList = delegateRelataionService.fetchSelfPlayerList();
        Map<String, Object> result = new HashMap<>();
        result.put("result", levelInfoVoList);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/fetch2Delegate")
    public AgentResponse fetchOneLevelDelegate(){

        List<TwoLevelInfoVo> list = delegateRelataionService.fetchTwoLevelDelegateList();
        Map<String, Object> result = new HashMap<>();
        result.put("result", list);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/fetch3Delegate")
    public AgentResponse fetchTwoLevelDelegate(){
        List<ThreeLevelInfoVo> list = delegateRelataionService.fetchThreeLevelDelegateList();
        Map<String, Object> result = new HashMap<>();
        result.put("result", list);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

}
