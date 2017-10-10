package com.code.server.db.Service;

import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.model.AgentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/9/18.
 */
@Service("agentUserService")
public class AgentUserService {

    @Autowired
    public IAgentUserDao agentUserDao;


    public Page<AgentUser> list(int page, int size) {
        List<AgentUser> list = new ArrayList<>();

        PageRequest pageRequest = new PageRequest(page, size);
        return agentUserDao.findAll(pageRequest);

    }

    public IAgentUserDao getAgentUserDao() {
        return agentUserDao;
    }

    public AgentUserService setAgentUserDao(IAgentUserDao agentUserDao) {
        this.agentUserDao = agentUserDao;
        return this;
    }
}
