package com.code.server.constant.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2019-03-15.
 */
public class OtherConstant {
    private Map<String, String> notice = new HashMap<>();//公告
    private Map<String, String> explain = new HashMap<>();//说明
    private Map<String, String> promo = new HashMap<>();//推广
    private Map<String, Object> rebateData = new HashMap<>();
    private Map<String, String> kefu = new HashMap<>();//客服



    public Map<String, String> getNotice() {
        return notice;
    }

    public OtherConstant setNotice(Map<String, String> notice) {
        this.notice = notice;
        return this;
    }

    public Map<String, String> getExplain() {
        return explain;
    }

    public OtherConstant setExplain(Map<String, String> explain) {
        this.explain = explain;
        return this;
    }

    public Map<String, Object> getRebateData() {
        return rebateData;
    }

    public OtherConstant setRebateData(Map<String, Object> rebateData) {
        this.rebateData = rebateData;
        return this;
    }

    public Map<String, String> getPromo() {
        return promo;
    }

    public OtherConstant setPromo(Map<String, String> promo) {
        this.promo = promo;
        return this;
    }

    public Map<String, String> getKefu() {
        return kefu;
    }

    public OtherConstant setKefu(Map<String, String> kefu) {
        this.kefu = kefu;
        return this;
    }
}
