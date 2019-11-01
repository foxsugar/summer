package com.code.server.db.dao;

import com.code.server.db.model.Phone;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2019-11-01.
 */
public interface IPhoneDao extends PagingAndSortingRepository<Phone, String> {


}
