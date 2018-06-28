package com.code.server.game.poker.tuitongzi;
/**
 * Created by dajuejinxian on 2018/6/27.
 */
public class GameTuiTongZiZhongXinBWZ extends  GameTuiTongZiZhongXin{

    //偏移量， 上来要在锅里放 多少钱
    protected long offset(){
        return 60;
    };
    /*一下是霸王庄条件 */
    protected boolean isBaWangZhuang(){
        return true;
    }
}
