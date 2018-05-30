package com.code.server.db.dao;


import com.code.server.db.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by win7 on 2017/3/8.
 */
public interface IUserDao extends PagingAndSortingRepository<User, Long> {

    User getUserByAccountAndPassword(String account, String password);

    User getUserByOpenId(String openId);

    User getUserById(long userId);

    @Query(value = "select open_id from users where id=?1", nativeQuery = true)
    String getOpenIdById(long userId);


    @Query(value = "select id from users where open_id=?1", nativeQuery = true)
    Long getIdByOpenId(String openId);

    List<User> findUsersByIdIn(List<Long> list);

}
