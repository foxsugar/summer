package com.code.server.db.Service;

import com.code.server.db.dao.IClubDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/1/15.
 */
@Service("clubService")
public class ClubService {


    @Autowired
    private IClubDao clubDao;

    public IClubDao getClubDao() {
        return clubDao;
    }

    public ClubService setClubDao(IClubDao clubDao) {
        this.clubDao = clubDao;
        return this;
    }
}
