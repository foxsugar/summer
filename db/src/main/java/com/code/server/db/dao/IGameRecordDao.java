package com.code.server.db.dao;

import com.code.server.db.model.GameRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2017/9/6.
 */
public interface IGameRecordDao extends PagingAndSortingRepository<GameRecord, Long> {
}
