package com.code.server.login.action;
import com.code.server.login.service.BecomeDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
@RestController
@RequestMapping("becomeDelegate")
public class BecomeDelegateAction {

    @Autowired
    private BecomeDelegateService becomeDelegateService;

    @RequestMapping("list")
    public AgentResponse delegateList(){
        Map<String, Object> result = becomeDelegateService.delegateList();
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }
}
