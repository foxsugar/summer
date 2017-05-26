package com.code.server.db.dao;

import com.code.server.db.model.Test;
import com.code.server.db.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2017/4/1.
 */
public interface ITestDao extends PagingAndSortingRepository<Test, Long> {


}
