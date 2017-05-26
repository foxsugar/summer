package com.code.server.constant.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/3/30.
 */
public class GameFinalResult {
    private List<UserInfo> userInfos = new ArrayList<>();

    private String endTime;

    public static class UserInfo{
        private long userId;
        private double score;

        public UserInfo(long userId, double score) {
            this.userId = userId;
            this.score = score;
        }

        public UserInfo() {
        }
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    public GameFinalResult setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
