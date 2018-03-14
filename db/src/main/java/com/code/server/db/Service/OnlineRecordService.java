package com.code.server.db.Service;

import com.code.server.db.dao.IOnlineRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/2/28.
 */
@Service("onlineRecordService")
public class OnlineRecordService {

    @Autowired
    private IOnlineRecordDao onlineRecordDao;

    public IOnlineRecordDao getOnlineRecordDao() {
        return onlineRecordDao;
    }

    public OnlineRecordService setOnlineRecordDao(IOnlineRecordDao onlineRecordDao) {
        this.onlineRecordDao = onlineRecordDao;
        return this;
    }
}
