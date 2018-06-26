package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2018/6/25.
 */
public class GameInfoHeleKD extends GameInfoXYKD {


    public static final int mode_风耗子 = 1;
    public static final int mode_随机耗子 = 2;
    public static final int mode_单耗子 = 3;
    public static final int mode_双耗子 = 4;
    public static final int mode_显庄 = 5;
    public static final int mode_扛耗子 = 6;
    public static final int mode_摸三胡六 = 7;
    public static final int mode_摸四胡五 = 8;
    public static final int mode_摸一胡五 = 9;

    public static final int mode_明听 = 10;
    public static final int mode_大包 = 11;



    @Override
    public int ting(long userId, String card) {

//        room.isHasMode(mode_明听);
//        String ifAnKou = room.getMode();
        if(room.isHasMode(mode_明听)){
            tingAT(userId,card);
        }else {
            tingMT(userId,card);
        }
        return 0;
    }
}
