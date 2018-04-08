package com.code.server.db.dao;

import com.code.server.db.model.AgentUser;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2017/9/18.
 */
public interface IAgentUserDao extends PagingAndSortingRepository<AgentUser, Integer> {

}
