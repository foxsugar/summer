package com.code.server.db.dao;


import com.code.server.db.model.UserRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/6/1.
 */
public interface IUserRecordDao extends PagingAndSortingRepository<UserRecord, Long> {
    UserRecord getUserRecordByUserId(long userId);
}
