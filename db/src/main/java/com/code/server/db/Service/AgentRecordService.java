package com.code.server.db.Service;

import com.code.server.db.dao.IAgentRecordsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/7/27.
 */
@Service("agentRecordService")
public class AgentRecordService {
    @Autowired
    public IAgentRecordsDao agentRecordsDao;

    public IAgentRecordsDao getAgentRecordsDao() {
        return agentRecordsDao;
    }

    public AgentRecordService setAgentRecordsDao(IAgentRecordsDao agentRecordsDao) {
        this.agentRecordsDao = agentRecordsDao;
        return this;
    }
}
