package com.code.server.login.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/16.
 */

public interface BecomeDelegateService {

    //因为游戏类目只有一种 所以暂时写死
    Map<String, Object> delegateList();

}
