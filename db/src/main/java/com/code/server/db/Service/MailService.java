package com.code.server.db.Service;

import com.code.server.db.dao.IMailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2019-09-06.
 */
@Service("mailService")
public class MailService {

    @Autowired
    private IMailDao mailDao;

    public IMailDao getMailDao() {
        return mailDao;
    }

    public MailService setMailDao(IMailDao mailDao) {
        this.mailDao = mailDao;
        return this;
    }
}
