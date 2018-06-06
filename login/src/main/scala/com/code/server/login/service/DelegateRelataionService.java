package com.code.server.login.service;

import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.login.vo.ThreeLevelInfoVo;
import com.code.server.login.vo.TwoLevelInfoVo;

import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/30.
 */
public interface DelegateRelataionService {

    List<OneLevelInfoVo> fetchSelfPlayerList(long agentId);

    List<TwoLevelInfoVo> fetchTwoLevelDelegateList(long agentId);

    List<ThreeLevelInfoVo> fetchThreeLevelDelegateList(long agentId);
}
