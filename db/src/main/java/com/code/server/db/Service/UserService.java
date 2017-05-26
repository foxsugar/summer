package com.code.server.db.Service;


import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Record;
import com.code.server.db.model.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by win7 on 2017/3/8.
 */

@Service("userService")
public class UserService {

    @PersistenceContext
    public EntityManager em;

    @Autowired
    private IUserDao userDao;


    public User getUserByOpenId(String openId) {
        User user = userDao.getUserByOpenId(openId);
        if (user == null) {
            return null;
        }
        return user;
    }

    public User getUserByUserId(long userId) {
        User user = userDao.getUserByUserId(userId);
        if (user == null) {
            return null;
        }
        return user;
    }

    public User getUserByAccountAndPassword(String account, String password) {
        User user = userDao.getUserByAccountAndPassword(account, password);
        if (user == null) {
            return null;
        }
        return user;
    }

    public User save(User user) {
        User newUser = userDao.save(user);
        user.setUserId(newUser.getUserId());
        return user;
    }


    public void flush(){
        em.flush();
    }

    @Transactional
    public void batchUpdate(List<User> list) {
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);

            em.merge(user);
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}
