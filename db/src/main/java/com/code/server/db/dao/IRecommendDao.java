package com.code.server.db.dao;

import com.code.server.db.model.Recommend;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/4/3.
 */
public interface IRecommendDao extends PagingAndSortingRepository<Recommend, String> {


}
