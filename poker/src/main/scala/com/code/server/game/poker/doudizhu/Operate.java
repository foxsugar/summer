package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

/**
 * Created by sunxianping on 2017/7/4.
 */
public class Operate {

    public static final int TYPE_JDZ = 0;
    public static final int TYPE_QDZ = 1;
    public static final int TYPE_PLAY = 2;


    private int type;
    private long userId;
    private CardStruct cardStruct;
    private int score;
    private boolean isPass;


    public static Operate getOperate_JDZ(long userId,int score,boolean isPass){
        Operate operate = new Operate();
        operate.setType(Operate.TYPE_JDZ);
        operate.setUserId(userId);
        operate.setPass(isPass);
        operate.setScore(score);
        return operate;
    }

    public static Operate getOperate_QDZ(long userId,boolean isPass){
        Operate operate = new Operate();
        operate.setType(Operate.TYPE_QDZ);
        operate.setUserId(userId);
        operate.setPass(isPass);
        return operate;
    }

    public static Operate getOperate_PLAY(long userId, CardStruct cardStruct, boolean isPass) {
        Operate operate = new Operate();
        operate.setType(Operate.TYPE_PLAY);
        operate.setUserId(userId);
        operate.setPass(isPass);
        operate.setCardStruct(cardStruct);
        return operate;
    }

    public int getType() {
        return type;
    }

    public Operate setType(int type) {
        this.type = type;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public Operate setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public CardStruct getCardStruct() {
        return cardStruct;
    }

    public Operate setCardStruct(CardStruct cardStruct) {
        this.cardStruct = cardStruct;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Operate setScore(int score) {
        this.score = score;
        return this;
    }

    public boolean isPass() {
        return isPass;
    }

    public Operate setPass(boolean pass) {
        isPass = pass;
        return this;
    }
}
