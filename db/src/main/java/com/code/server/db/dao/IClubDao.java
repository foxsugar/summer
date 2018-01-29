package com.code.server.db.dao;

import com.code.server.db.model.Club;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/1/15.
 */
public interface IClubDao extends PagingAndSortingRepository<Club, Long> {

}
