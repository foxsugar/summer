package com.code.server.db.dao;

import com.code.server.db.model.AgentUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2017/9/18.
 */
public interface IAgentUserDao extends PagingAndSortingRepository<AgentUser, Integer> {


    @Query(value = "select * from agent_user where invite_code=?1 limit 1", nativeQuery = true)
    AgentUser findAgentUserByInvite_code(String invite_code);


    AgentUser findAgentUserByUsernameAndPassword(String userName, String password);
}
