package com.code.server.db.dao;

import com.code.server.db.model.Charge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by win7 on 2017/3/13.
 */
public interface IChargeDao extends PagingAndSortingRepository<Charge, Long>, JpaSpecificationExecutor<Charge> {

//    Charge getChargeByOrderId(String orderId);
//    @Query(value = "select '*' from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3 AND status = ?4 AND charge_type IN ?5", nativeQuery = true)
//    @Query(value = "select '*' from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3  AND charge_type IN ?5", nativeQuery = true)
//    List<Charge> getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(List<Long> users, Date start, Date end, int status, List<Integer> list);
//    @Query(value = "select '*' from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3 AND status = ?4 AND recharge_source = ?5", nativeQuery = true)
//    @Query(value = "select '*' from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3  AND recharge_source = ?5", nativeQuery = true)
//    List<Charge> getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndRecharge_sourceIs(List<Long> users, Date start, Date end, int status, String sourceType);

//    @Query(value = "select '*' from charge where charge_type IN ?1", nativeQuery = true)
//    List<Charge> getChargesByChargeTypeIn(List<Integer> list);

//    @Query(value = "select '*' from charge where charge_type IN ?1 AND userid = ?2", nativeQuery = true)
//    List<Charge> getChargesByChargeTypeInAndUseridIs(List<Integer> list, Long uid);

    @Query(value = "select sum(money) from charge where charge.userid IN ?1 AND createtime BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer getSumMoneyByUsersAndDate(List<Long> ids,Date start, Date end);

    @Query(value = "select coalesce(sum(c.money), 0) from Charge c where c.userid = ?1 AND c.chargeType=?2")
    double getSumMoneyByUseridAndChargeType(Long uid, int chargeType);


    @Query(value="select '*' from charge where userid=?1 and recharge_source=1", nativeQuery = true)
    List<Charge> getChargesByUserid(long userId);
}
