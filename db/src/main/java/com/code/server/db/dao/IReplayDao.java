package com.code.server.db.dao;

import com.code.server.db.model.Replay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/7/3.
 */
public interface IReplayDao extends PagingAndSortingRepository<Replay, Long>{
    Replay getReplayById(long id);

    @Query(value = "select left_count from replay where id=?1", nativeQuery = true)
    Replay getReplayCountById(long id);

    @Query(value = "update replay set left_count=left_count-1", nativeQuery = true)
    Replay decReplayCountById(long id);
}
