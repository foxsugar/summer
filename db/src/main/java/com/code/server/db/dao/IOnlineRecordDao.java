package com.code.server.db.dao;

import com.code.server.db.model.OnlineRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/2/28.
 */
public interface IOnlineRecordDao extends PagingAndSortingRepository<OnlineRecord, String> {

}
