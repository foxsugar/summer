package com.code.server.db.dao;

import com.code.server.db.model.UpScoreRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2019-04-29.
 */
public interface IUpScoreRecordDao extends PagingAndSortingRepository<UpScoreRecord, Long> {


}
