package com.code.server.db.dao;

import com.code.server.db.model.LogRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sunxianping on 2018/2/28.
 */
public interface ILogRecordDao extends PagingAndSortingRepository<LogRecord, String> {

    List<LogRecord> findByIdIn(List<String> ids);
}
