package com.code.server.constant.club;

/**
 * Created by sunxianping on 2019-04-02.
 */
public class ScoreItem {
    private String name;
    private double score;

    public String getName() {
        return name;
    }

    public ScoreItem setName(String name) {
        this.name = name;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ScoreItem setScore(double score) {
        this.score = score;
        return this;
    }
}
