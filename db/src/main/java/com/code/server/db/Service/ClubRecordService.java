package com.code.server.db.Service;

import com.code.server.constant.game.RoomRecord;
import com.code.server.db.dao.IClubRecordDao;
import com.code.server.db.model.ClubRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/1/29.
 */
@Service("clubRecordService")
public class ClubRecordService {


    @Autowired
    private IClubRecordDao clubRecordDao;

    public IClubRecordDao getClubRecordDao() {
        return clubRecordDao;
    }

    public ClubRecordService setClubRecordDao(IClubRecordDao clubRecordDao) {
        this.clubRecordDao = clubRecordDao;
        return this;
    }


    public void addRecord(String clubId, RoomRecord roomRecord) {
        ClubRecord clubRecord = clubRecordDao.getClubRecordById(clubId);
        if (clubRecord == null) {
            clubRecord = new ClubRecord();
            clubRecord.setId(clubId);
        }
        List<RoomRecord> rc = clubRecord.getRecords();
        //有删除的战绩
        if (rc == null) {
            rc = new ArrayList<>();
//            gameRecordService.decGameRecordCount(roomUid);
        }else{
            rc.add(roomRecord);
        }
        //过长 删除第一个
        if (rc.size() >= 20) {
            rc.remove(0);
        }
        clubRecordDao.save(clubRecord);

//        return userRecordDao.save(userRecords);
    }
}
