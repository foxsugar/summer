package com.code.server.db.Service;

import com.code.server.db.dao.IGameAgentWxDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/5/10.
 */
@Service("gameAgentWxService")
public class GameAgentWxService {

    @Autowired
    private IGameAgentWxDao gameAgentWxDao;

    public IGameAgentWxDao getGameAgentWxDao() {
        return gameAgentWxDao;
    }

    public GameAgentWxService setGameAgentWxDao(IGameAgentWxDao gameAgentWxDao) {
        this.gameAgentWxDao = gameAgentWxDao;
        return this;
    }
}
