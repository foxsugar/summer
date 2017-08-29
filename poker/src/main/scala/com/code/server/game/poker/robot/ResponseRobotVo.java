package com.code.server.game.poker.robot;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class ResponseRobotVo {

    private String service;
    private String method;
    private Object params;
    public ResponseRobotVo(){}
    public ResponseRobotVo(String service, String method, Object params) {
        this.service = service;
        this.method = method;
        this.params = params;
    }


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
