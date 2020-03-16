package com.code.server.db.Service;

import com.code.server.db.dao.IClubRoomRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ClubRoomRecordService
 * @Description TODO
 * @Author sunxp
 * @Date 2020/3/16 10:19
 **/
@Service("clubRoomRecordService")
public class ClubRoomRecordService {

    @Autowired
    private IClubRoomRecordDao clubRoomRecordDao;

    public IClubRoomRecordDao getClubRoomRecordDao() {
        return clubRoomRecordDao;
    }

    public ClubRoomRecordService setClubRoomRecordDao(IClubRoomRecordDao clubRoomRecordDao) {
        this.clubRoomRecordDao = clubRoomRecordDao;
        return this;
    }
}
