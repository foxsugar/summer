package com.code.server.db.Service;

import com.code.server.constant.game.Record;
import com.code.server.db.dao.IUserRecordDao;
import com.code.server.db.model.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service("userRecordService")
public class UserRecordService {

    @PersistenceContext
    public EntityManager em;

    @Autowired
    private IUserRecordDao userRecordDao;



    public List<Record.RoomRecord> getUserByUserIDAndType(long userid, int type) {
        UserRecord user = userRecordDao.findOne(userid);
        if (user == null) {
            return null;
        }
        return user.getRecord().getRoomRecords().get(type);
    }

    public UserRecord addRecord(long userid, Record.RoomRecord roomRecord) {
        UserRecord user = userRecordDao.findOne(userid);

        user.getRecord().addRoomRecord(roomRecord);

        return userRecordDao.save(user);
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
