package com.code.server.db.Service;

import com.code.server.db.dao.IRecommendDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018/4/3.
 */
@Service("recommendService")
public class RecommendService {

    @Autowired
    private IRecommendDao recommendDao;

    public IRecommendDao getRecommendDao() {
        return recommendDao;
    }

    public RecommendService setRecommendDao(IRecommendDao recommendDao) {
        this.recommendDao = recommendDao;
        return this;
    }
}
