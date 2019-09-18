package com.code.server.db.dao;


import com.code.server.db.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by win7 on 2017/3/8.
 */
public interface IUserDao extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

    User getUserByAccountAndPassword(String account, String password);

    List<User> getUsersByAccount(String account);

    User getUserByOpenId(String openId);

    User getUserById(long userId);

    List<User> findUsersByRegistDateBetween(Date dateA, Date dateB);

    List<User> findUsersByLastLoginDateBetween(Date dateA, Date dateB);

    List<User> findUsersByRegistDateBetweenAndLastLoginDateBetween(Date date1, Date date2, Date data3, Date data4);

    @Query(value = "select open_id from users where id=?1", nativeQuery = true)
    String getOpenIdById(long userId);

    @Query(value = "select id from users where open_id=?1", nativeQuery = true)
    Long getIdByOpenId(String openId);

    @Query(value = "select referee from users where open_id=?1", nativeQuery = true)
    Integer getRefereeByOpenId(String openId);

    List<User> findUsersByIdIn(List<Long> list);

    List<User> findAllByVip(int robot);

    List<User> getUserByUnionId(String unionId);

}
