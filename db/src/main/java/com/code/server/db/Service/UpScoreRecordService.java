package com.code.server.db.Service;

import com.code.server.db.dao.IUpScoreRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2019-04-29.
 */
@Service("upScoreRecordService")
public class UpScoreRecordService {

    @Autowired
    private IUpScoreRecordDao upScoreRecordDao;

    public IUpScoreRecordDao getUpScoreRecordDao() {
        return upScoreRecordDao;
    }

    public UpScoreRecordService setUpScoreRecordDao(IUpScoreRecordDao upScoreRecordDao) {
        this.upScoreRecordDao = upScoreRecordDao;
        return this;
    }
}
