package com.code.server.db.dao;

import com.code.server.db.model.ClubRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/1/29.
 */
public interface IClubRecordDao extends PagingAndSortingRepository<ClubRecord, Long> {

    ClubRecord getClubRecordById(String id);

}
