package com.code.server.login.service;

import com.code.server.login.vo.RecommandUserVo;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
public interface RecommendDelegateService {

    //查询玩家
    RecommandUserVo findRecommandUser(long userId, long agentId);

    boolean bindDelegate(long userId, long agentId);
}
