package com.code.server.login.action;

import com.code.server.constant.db.UserInfo;
import com.code.server.constant.game.AgentBean;
import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.DelegateRelataionService;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.login.vo.ThreeLevelInfoVo;
import com.code.server.login.vo.TwoLevelInfoVo;
import com.code.server.login.vo.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/30.
 */
@RestController
@RequestMapping("/delegateRel")
public class DelegateRelataionAction implements ErrorCode{

    private static final Logger logger = LoggerFactory.getLogger(DelegateRelataionAction.class);
    @Autowired
    private DelegateRelataionService delegateRelataionService;

    @AuthChecker
    @RequestMapping("/fetchPlayers")
    public AgentResponse fetchPlayers(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        List<OneLevelInfoVo> levelInfoVoList = delegateRelataionService.fetchSelfPlayerList(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", levelInfoVoList);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/fetch2Delegate")
    public AgentResponse fetchOneLevelDelegate(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        List<TwoLevelInfoVo> list = delegateRelataionService.fetchTwoLevelDelegateList(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", list);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/fetch3Delegate")
    public AgentResponse fetchTwoLevelDelegate(int agentId){

//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        long agentId = AgentUtil.getAgentByRequest(request);

        logger.info("==============agentId={}", agentId);
        List<ThreeLevelInfoVo> list = delegateRelataionService.fetchThreeLevelDelegateList(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", list);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    //检查用户
    @AuthChecker
    @RequestMapping("/findUserInfo")
    public AgentResponse findSelfPlayerOrDelegates(long userId){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        UserInfoVo userInfo = delegateRelataionService.findUserInfo(agentId, userId);
        AgentResponse agentResponse = null;
        if (userInfo == null){
            Map<String, Object> result = new HashMap<>();
            result.put("result", userInfo);
            agentResponse = new AgentResponse(NOT_SELF_USER, result);
        }else {
            Map<String, Object> result = new HashMap<>();
            result.put("result", userInfo);
            agentResponse = new AgentResponse(200, result);
        }

        return agentResponse;
    }

}
