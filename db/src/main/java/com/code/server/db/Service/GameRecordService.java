package com.code.server.db.Service;

import com.code.server.db.dao.IGameRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by sunxianping on 2017/9/6.
 */
@Service("gameRecordService")
public class GameRecordService {
    @PersistenceContext
    public EntityManager em;

    @Autowired
    public IGameRecordDao gameRecordDao;
}
