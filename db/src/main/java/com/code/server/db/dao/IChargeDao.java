package com.code.server.db.dao;

import com.code.server.db.model.Charge;
import com.code.server.db.model.Constant;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by win7 on 2017/3/13.
 */
public interface IChargeDao extends PagingAndSortingRepository<Charge, Long> {

    Charge getChargeByOrderId(String orderId);

}
