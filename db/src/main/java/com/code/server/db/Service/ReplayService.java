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


    public boolean decReplayCount(long id){
        Replay replay = replayDao.getReplayCountById(id);
        if (replay == null) {
            return false;
        }
        replay.setLeftCount(replay.getLeftCount() - 1);
        if (replay.getLeftCount() <= 0){
            replayDao.delete(replay);
        }else {
            replayDao.decReplayCountById(replay.getId());
        }
        return true;
    }
}
