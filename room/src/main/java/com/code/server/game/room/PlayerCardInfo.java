package com.code.server.game.room;

import com.code.server.constant.response.IfacePlayerInfoVo;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class PlayerCardInfo implements IfacePlayerInfo {
    public long userId;
    public double score;


    @Override
    public IfacePlayerInfoVo toVo() {
        return null;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return null;
    }

    public long getUserId() {
        return userId;
    }

    public PlayerCardInfo setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public double getScore() {
        return score;
    }

    public PlayerCardInfo setScore(double score) {
        this.score = score;
        return this;
    }
}
