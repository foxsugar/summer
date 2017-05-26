package com.code.server.db.dao;

import com.code.server.db.model.Constant;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by win7 on 2017/3/13.
 */
public interface IConstantDao extends PagingAndSortingRepository<Constant, Long> {

}
