package com.code.server.db.dao;

import com.code.server.db.model.GameAgent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sunxianping on 2018/3/13.
 */
public interface IGameAgentDao extends PagingAndSortingRepository<GameAgent, Long>, JpaSpecificationExecutor<GameAgent> {

    @Query(value = "select id from game_agent where union_id = ?1", nativeQuery = true)
    Long getUserIdByUnionId(String  unionId);

    //根据父ID找ChildId
    List<GameAgent> findGameAgentByParentId(long parentId);

}
