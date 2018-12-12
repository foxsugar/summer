package com.code.server.game.poker.yuxiaxie;

/**
 * Created by sunxianping on 2018-12-10.
 */
public class Bet {

    static final int TYPE_DANYA = 0;
    static final int TYPE_BAOZI = 1;
    static final int TYPE_CHUANLIAN = 2;
    int type;
    int index1;
    int index2;
    int num;


    public Bet(int type, int index1, int index2, int num) {
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
        this.num = num;
    }

    public Bet() {
    }

    public int getType() {
        return type;
    }

    public Bet setType(int type) {
        this.type = type;
        return this;
    }

    public int getIndex1() {
        return index1;
    }

    public Bet setIndex1(int index1) {
        this.index1 = index1;
        return this;
    }

    public int getIndex2() {
        return index2;
    }

    public Bet setIndex2(int index2) {
        this.index2 = index2;
        return this;
    }

    public int getNum() {
        return num;
    }

    public Bet setNum(int num) {
        this.num = num;
        return this;
    }
}
