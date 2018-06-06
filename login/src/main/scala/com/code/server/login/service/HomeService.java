package com.code.server.login.service;

import com.code.server.login.vo.HomePageVo;

/**
 * Created by dajuejinxian on 2018/5/8.
 */

public interface HomeService {

    HomePageVo showHomePage(long agentId);
}
