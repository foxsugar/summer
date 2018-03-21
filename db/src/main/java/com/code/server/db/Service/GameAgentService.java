package com.code.server.db.Service;

import com.code.server.db.dao.IGameAgentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/3/13.
 */
@Service("gameAgentService")
public class GameAgentService {

    @Autowired
    private IGameAgentDao gameAgentDao;

    public IGameAgentDao getGameAgentDao() {
        return gameAgentDao;
    }

    public GameAgentService setGameAgentDao(IGameAgentDao gameAgentDao) {
        this.gameAgentDao = gameAgentDao;
        return this;
    }
}
