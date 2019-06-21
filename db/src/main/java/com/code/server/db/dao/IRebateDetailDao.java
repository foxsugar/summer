package com.code.server.db.dao;

/**
 * Created by sunxianping on 2019-06-21.
 */

import com.code.server.db.model.RebateDetail;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IRebateDetailDao extends PagingAndSortingRepository<RebateDetail, Long> {



}
