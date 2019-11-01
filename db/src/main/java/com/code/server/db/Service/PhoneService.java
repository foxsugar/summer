package com.code.server.db.Service;

import com.code.server.db.dao.IPhoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2019-11-01.
 */
@Service("phoneService")
public class PhoneService {
    @Autowired
    private IPhoneDao phoneDao;

    public IPhoneDao getPhoneDao() {
        return phoneDao;
    }

    public PhoneService setPhoneDao(IPhoneDao phoneDao) {
        this.phoneDao = phoneDao;
        return this;
    }
}
