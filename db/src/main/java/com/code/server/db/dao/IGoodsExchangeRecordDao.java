package com.code.server.db.dao;

import com.code.server.db.model.GoodsExchangeRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2019-01-28.
 */
public interface IGoodsExchangeRecordDao  extends PagingAndSortingRepository<GoodsExchangeRecord, Integer> {


}
