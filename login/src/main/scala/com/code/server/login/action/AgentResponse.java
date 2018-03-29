package com.code.server.login.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/3/28.
 */
public class AgentResponse {
    public int code;
    public Map<String, Object> data = new HashMap<>();

    public AgentResponse(int code, Map<String, Object> data) {
        this.code = code;
        this.data = data;
    }

    public AgentResponse() {
    }
}
