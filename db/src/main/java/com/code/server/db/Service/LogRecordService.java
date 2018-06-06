package com.code.server.db.Service;

import com.code.server.db.dao.ILogRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/2/28.
 */
@Service("logRecordService")
public class LogRecordService {

    @Autowired
    private ILogRecordDao onlineRecordDao;

    public ILogRecordDao getOnlineRecordDao() {
        return onlineRecordDao;
    }

    public LogRecordService setOnlineRecordDao(ILogRecordDao onlineRecordDao) {
        this.onlineRecordDao = onlineRecordDao;
        return this;
    }
}
