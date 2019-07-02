package com.code.server.db.dao;

import com.code.server.db.model.Rank;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2019-07-02.
 */
public interface IRankDao extends PagingAndSortingRepository<Rank, String> {

}
