package com.code.server.game.mahjong.response;

/**
 * Created by win7 on 2016/12/1.
 */
public class RequestVo {
    private String service;
    private String method;
    private Object params;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}
