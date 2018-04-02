package com.code.server.db.dao;

import com.code.server.db.model.Charge;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by win7 on 2017/3/13.
 */
public interface IChargeDao extends PagingAndSortingRepository<Charge, Long> {

    Charge getChargeByOrderId(String orderId);
    List<Charge> getChargesByUseridInAndCreatetimeBetween(List<Long> users, Date start, Date end);

    @Query(value = "select sum(money) from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer getSumMoneyByUsersAndDate(List<Long> ids,Date start, Date end);

}
