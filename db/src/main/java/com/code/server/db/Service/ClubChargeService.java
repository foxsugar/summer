package com.code.server.db.Service;

import com.code.server.db.dao.IClubChargeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/2/27.
 */
@Service("clubChargeService")
public class ClubChargeService {

    @Autowired
    private IClubChargeDao clubChargeDao;

    public IClubChargeDao getClubChargeDao() {
        return clubChargeDao;
    }

    public ClubChargeService setClubChargeDao(IClubChargeDao clubChargeDao) {
        this.clubChargeDao = clubChargeDao;
        return this;
    }
}
