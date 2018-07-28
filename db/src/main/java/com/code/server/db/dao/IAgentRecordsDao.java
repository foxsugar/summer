package com.code.server.db.dao;

import com.code.server.db.model.AgentRecords;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/7/27.
 */
public interface IAgentRecordsDao extends PagingAndSortingRepository<AgentRecords, String> {
}
