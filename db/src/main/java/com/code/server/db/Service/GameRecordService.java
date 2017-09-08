package com.code.server.db.Service;

import com.code.server.db.dao.IGameRecordDao;
import com.code.server.db.dao.IReplayDao;
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

    @Autowired
    public IReplayDao replayDao;



    public Integer getGameRecordLeftCount(long uuid) {
        return gameRecordDao.getGameRecordCountByUuid(uuid);
    }

    public void decGameRecordCount(long id){
        Integer count = getGameRecordLeftCount(id);
        if (count != null) {
            if (count <= 1) {
                gameRecordDao.deleteAllByUuid(id);

                //并删除回放
                replayDao.deleteAllByRoomUuid(id);

            } else {
                gameRecordDao.decGameRecordCountByUuid(id);
            }
        }
    }
}
