package com.code.server.db.Service;

import com.code.server.db.dao.IUserClubDao;
import com.code.server.db.model.UserClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/1/16.
 */

@Service("userClubService")
public class UserClubService {
    @Autowired
    private IUserClubDao userClubDao;


    public void save(UserClub userClub){
        userClubDao.save(userClub);
    }

    public UserClub getUserClubById(int id){
        return userClubDao.getUserClubById(id);
    }

    public IUserClubDao getUserClubDao() {
        return userClubDao;
    }

    public UserClubService setUserClubDao(IUserClubDao userClubDao) {
        this.userClubDao = userClubDao;
        return this;
    }
}
