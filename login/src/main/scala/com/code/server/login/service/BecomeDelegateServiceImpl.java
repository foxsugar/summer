package com.code.server.login.service;

import com.code.server.constant.game.AgentBean;
import com.code.server.login.util.CookieUtil;
import com.code.server.redis.service.RedisManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
@Service
public class BecomeDelegateServiceImpl implements BecomeDelegateService {
    @Override
    public Map<String, Object> delegateList() {

        long agentId = CookieUtil.getAgentIdByCookie();
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        String gameItem = "game name";
        //已代理
        List<String> aList = new ArrayList<>();
        //未代理
        List<String> bList = new ArrayList<>();
        if (agentBean == null){
            bList.add(gameItem);
        }else {
            aList.add(gameItem);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("alreadyDelegate", aList);
        result.put("notDelegate", bList);
        return result;

    }
}
