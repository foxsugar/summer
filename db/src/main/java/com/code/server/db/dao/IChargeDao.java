package com.code.server.db.dao;

import com.code.server.db.model.Charge;
import com.code.server.db.model.Constant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by win7 on 2017/3/13.
 */
public interface IChargeDao extends PagingAndSortingRepository<Charge, Long> {

    Charge getChargeByOrderId(String orderId);

    @Modifying
    @Query("update charge  set status = ? where order_id = ?")
    int update(int status , String order_id);

}
