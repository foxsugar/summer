package com.code.server.db.dao;

/**
 * Created by sunxianping on 2019-06-21.
 */

import com.code.server.db.model.RebateDetail;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IRebateDetailDao extends PagingAndSortingRepository<RebateDetail, Long> {



    List<RebateDetail> findAllByAgentId(long agentId);
}
