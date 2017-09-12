package com.code.server.db.Service;

import com.code.server.constant.game.RoomRecord;
import com.code.server.db.dao.IUserRecordDao;
import com.code.server.db.model.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Service("userRecordService")
public class UserRecordService {

    @PersistenceContext
    public EntityManager em;

    @Autowired
    private IUserRecordDao userRecordDao;


    @Autowired
    private ReplayService replayService;

    @Autowired
    private GameRecordService gameRecordService;


    /**
     * 添加一条战绩 超过指定条数后删除第一条
     * @param userid
     * @param roomRecord
     * @return
     */
    public UserRecord addRecord(long userid, RoomRecord roomRecord) {
        UserRecord userRecords = userRecordDao.findOne(userid);
        RoomRecord rc = userRecords.getRecord().addRoomRecord(roomRecord);
        //有删除的战绩
        if (rc != null) {
            long roomUid = rc.getId();
            gameRecordService.decGameRecordCount(roomUid);
        }
        return userRecordDao.save(userRecords);
    }

    public UserRecord getUserRecordByUserId(long userId){
        UserRecord record = userRecordDao.findOne(userId);
        if (record == null) {
            return null;
        }
        return record;
    }

    public UserRecord save(UserRecord userRecord) {
        UserRecord newUser = userRecordDao.save(userRecord);
        return newUser;
    }



}
