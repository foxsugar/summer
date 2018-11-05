package com.code.server.game.poker.tiandakeng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunxianping on 2018-10-30.
 */
public class BetInfo {
    long firstBetUser = 0;
    long curBetUser = 0;
    int betNum = 0;
    Set<Long> giveUpUser = new HashSet<>();
    Set<Long> needBetUser = new HashSet<>();
    Set<Long> alreadyBetUser = new HashSet<>();


    boolean isBetOver(){
        return alreadyBetUser.size() >= needBetUser.size();
    }

    void bet(long userId,boolean isGiveUp){
//        this.giveUpUser.add(userId);
        this.alreadyBetUser.add(userId);
        if (isGiveUp) {
            this.giveUpUser.add(userId);
        }
    }
    public BetInfo(long firstBetUser, List<Long> needBetUser) {
        this.firstBetUser = firstBetUser;
        this.curBetUser = firstBetUser;
        this.needBetUser.addAll(needBetUser);
    }


}
