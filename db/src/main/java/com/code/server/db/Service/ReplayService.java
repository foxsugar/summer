package com.code.server.db.Service;

import com.code.server.db.dao.IReplayDao;
import com.code.server.db.model.Replay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Created by Administrator on 2017/7/3.
 */
@Service("replayService")
public class ReplayService {
    @Autowired
    private IReplayDao replayDao;

    public Replay save(Replay replay) {
        Replay r = replayDao.save(replay);
        r.setId(replay.getId());
        return r;
    }

    public Replay getReplay(long id) {
        Replay r = replayDao.getReplayById(id);
        return r;
    }

    public Integer getReplayLeftCount(long id) {
        return replayDao.getReplayCountById(id);
    }
    public void decReplayCount(long id){
        Integer count = getReplayLeftCount(id);
        if (count != null) {
            if (count <= 1) {
                replayDao.delete(id);
            } else {
                replayDao.decReplayCountById(id);
            }
        }
    }
}
