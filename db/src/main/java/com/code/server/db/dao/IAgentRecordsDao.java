package com.code.server.db.dao;

import com.code.server.db.model.AgentRecords;
import com.code.server.db.model.Charge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/7/27.
 */
public interface IAgentRecordsDao extends PagingAndSortingRepository<AgentRecords, String>,  JpaSpecificationExecutor<AgentRecords> {
}
