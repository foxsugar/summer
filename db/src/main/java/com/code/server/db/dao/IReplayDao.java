package com.code.server.db.dao;

import com.code.server.db.model.Replay;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/7/3.
 */
public interface IReplayDao extends PagingAndSortingRepository<Replay, Long>{
    Replay getReplayById(long id);
}
