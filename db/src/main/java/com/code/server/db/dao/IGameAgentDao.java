package com.code.server.db.dao;

import com.code.server.db.model.GameAgent;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/3/13.
 */
public interface IGameAgentDao extends PagingAndSortingRepository<GameAgent, Long> {

}
