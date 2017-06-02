package com.code.server.db.Service;



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

    public UserRecord getUserByUserRecord(long userid) {
        UserRecord user = userRecordDao.getUserByUserRecord(userid);
        if (user == null) {
            return null;
        }
        return user;
    }

}
