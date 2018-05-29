package com.code.server.login.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/8.
 */

public interface HomeService {
    Map<Object, Object> findChargeInfoByAgentId(long agentId);
}
