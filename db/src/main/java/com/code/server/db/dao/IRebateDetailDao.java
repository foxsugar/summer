package com.code.server.db.dao;

/**
 * Created by sunxianping on 2019-06-21.
 */

import com.code.server.db.model.RebateDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface IRebateDetailDao extends PagingAndSortingRepository<RebateDetail, Long> {



    List<RebateDetail> findAllByAgentId(long agentId);

    List<RebateDetail> findAllByAgentIdAndDateAfter(long agentId, Date date);

//    select agent_id, sum(num) money,DATE_FORMAT(date,'%Y-%m-%d') d from rebate_detail where agent_id=5 GROUP BY d ORDER BY d desc limit 10;

    @Query(value = "select sum(num) from rebate_detail where agent_id=?1 and DATE_FORMAT(date,'%Y-%m-%d')=?2", nativeQuery = true)
    Double getRebateByDate(long userId, String date);


    void deleteAllByDateBefore(Date date);
}
