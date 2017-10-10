package com.code.server.game.mahjong.util;

import java.util.List;

/**
 * Created by sunxianping on 2017/10/9.
 */
public class ShunHaveHun {
    private int hun;
    private List<Integer> other;
    private int shun;

    public int getHun() {
        return hun;
    }

    public ShunHaveHun setHun(int hun) {
        this.hun = hun;
        return this;
    }

    public List<Integer> getOther() {
        return other;
    }

    public ShunHaveHun setOther(List<Integer> other) {
        this.other = other;
        return this;
    }

    public int getShun() {
        return shun;
    }

    public ShunHaveHun setShun(int shun) {
        this.shun = shun;
        return this;
    }

    @Override
    public String toString() {
        return "ShunHaveHun{" +
                "hun=" + hun +
                ", other=" + other +
                ", shun=" + shun +
                '}';
    }
}
