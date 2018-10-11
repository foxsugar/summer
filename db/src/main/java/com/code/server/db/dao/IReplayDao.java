package com.code.server.db.dao;

import com.code.server.db.model.Replay;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/3.
 */
public interface IReplayDao extends PagingAndSortingRepository<Replay, Long>{
    Replay getReplayById(long id);

    @Query(value = "select left_count from replay where id=?1", nativeQuery = true)
    Integer getReplayCountById(long id);

    @Transactional
    @Modifying
    @Query(value = "update replay set left_count=left_count-1 where id=?1", nativeQuery = true)
    void decReplayCountById(long id);


    @Transactional
    void deleteAllByRoomUuid(long roomUuid);

    @Transactional
    void deleteAllByDateBefore(Date date);
}
