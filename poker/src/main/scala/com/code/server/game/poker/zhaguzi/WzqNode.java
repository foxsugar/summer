package com.code.server.game.poker.zhaguzi;

/**
 * Created by sunxianping on 2018/7/31.
 */
public class WzqNode {
    public int x;
    public int y;
    public long userId;


    public long getUserId() {
        return userId;
    }

    public WzqNode setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public int getX() {
        return x;
    }

    public WzqNode setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public WzqNode setY(int y) {
        this.y = y;
        return this;
    }
}
