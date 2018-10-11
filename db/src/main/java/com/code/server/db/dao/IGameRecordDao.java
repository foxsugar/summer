package com.code.server.db.dao;

import com.code.server.db.model.GameRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by sunxianping on 2017/9/6.
 */
public interface IGameRecordDao extends PagingAndSortingRepository<GameRecord, Long> {

    List<GameRecord> getGameRecordByUuid(long uuid);

    @Query(value = "select left_count from game_record where uuid=?1 limit 1", nativeQuery = true)
    Integer getGameRecordCountByUuid(long uuid);

    @Transactional
    @Modifying
    @Query(value = "update game_record set left_count=left_count-1 where uuid=?1", nativeQuery = true)
    void decGameRecordCountByUuid(long uuid);

    @Transactional
    void deleteAllByUuid(long uuid);

    @Transactional
    void deleteAllByDateBefore(Date date);

}
