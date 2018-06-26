package com.code.server.game.mahjong.logic;

import static com.code.server.game.mahjong.logic.GameInfoZhuohaozi.mode_明听;

/**
 * Created by sunxianping on 2018/6/25.
 */
public class GameInfoHeleKD extends GameInfoXYKD {





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
