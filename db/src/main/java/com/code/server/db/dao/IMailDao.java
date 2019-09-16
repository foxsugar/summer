package com.code.server.db.dao;

import com.code.server.db.model.Mail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sunxianping on 2019-09-06.
 */
public interface IMailDao extends PagingAndSortingRepository<Mail, String> {

    List<Mail> getAllByUserIdOrderByMailDateDesc(long userId, Pageable pageable);


    Mail getFirstByIsReadEqualsAndUserId(int isRead, long userId);

    void deleteAllByUserId(long userId);


    @Transactional
    @Modifying
    @Query(value = "update mail set is_read = 1 where user_id=?1", nativeQuery = true)
    void readAll(long userId);


}
