package com.code.server.login.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/3/28.
 */
public class AgentResponse implements ErrorCode{
    public int code;
    public Object data = new HashMap<>();


    public AgentResponse(int code, List<?> list) {
        this.code = code;
        Map<String, Object> map = new HashMap<>();
        map.put("result", list);
        this.data = map;

    }

    public AgentResponse(int code, Map<String, Object> data) {
        this.code = code;
        this.data = data;

    }

    public AgentResponse() {
    }

    public int getCode() {
        return code;
    }

    public AgentResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public AgentResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
