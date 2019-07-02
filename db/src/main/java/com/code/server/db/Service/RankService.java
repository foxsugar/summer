package com.code.server.db.Service;

import com.code.server.db.dao.IRankDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2019-07-02.
 */
@Service("randService")
public class RankService {

    @Autowired
    private IRankDao rankDao;

    public IRankDao getRankDao() {
        return rankDao;
    }

    public RankService setRankDao(IRankDao rankDao) {
        this.rankDao = rankDao;
        return this;
    }
}
