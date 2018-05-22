package com.code.server.login.vo;

/**
 * Created by dajuejinxian on 2018/5/14.
 */
public class HomeVo<T>  {
    private int code;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
