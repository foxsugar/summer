package com.code.server.admin.action;

import com.code.server.db.Service.AgentUserService;
import com.code.server.db.model.AgentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/9/13.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value ="/agent")
public class AgentAction {

    @Autowired
    private AgentUserService agentUserService;

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = new HashMap<>();

        result.put("code",20000);

        System.out.println(request.getParameter("page"));
        System.out.println(request.getParameter("size"));
        int page = Integer.valueOf(request.getParameter("page"));
        int size = Integer.valueOf(request.getParameter("size"));

        Map<String, Object> data = new HashMap<>();
        Page<AgentUser> pages = agentUserService.list(page, size);
        data.put("tableData",pages.getContent());

        data.put("totalPage", pages.getTotalElements());
        data.put("currentPage", 1);
        result.put("data", data);

        return result;

    }


    public AgentUserService getAgentUserService() {
        return agentUserService;
    }

    public AgentAction setAgentUserService(AgentUserService agentUserService) {
        this.agentUserService = agentUserService;
        return this;
    }
}
