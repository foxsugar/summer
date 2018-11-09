package com.code.server.game.poker.tiandakeng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018-10-30.
 */
public class KickInfo {
    int count = 0;
    long firstUser = 0;
    long curKickUser = 0;
    List<Long> needKickUser = new ArrayList<>();
    List<Long> alreadyKickUser = new ArrayList<>();
    List<Long> notKickUser = new ArrayList<>();
    BetInfo kickBetInfo;


    public KickInfo(long firstUser, List<Long> needKickUser) {
        this.firstUser = firstUser;
        this.curKickUser = firstUser;
        this.needKickUser.addAll(needKickUser);
    }


    public void addCount() {
        this.count++;
    }
    /**
     * 下注
     * @param num
     * @param firstUser
     * @param users
     */
    public void initBetInfo(int num, long firstUser, List<Long> users) {
        //初始化betInfo
        this.kickBetInfo = new BetInfo(firstUser, users);
        this.kickBetInfo.betNum = num;
        this.kickBetInfo.bet(firstUser,false);
    }

    /**
     * 踢
     * @param userId
     * @param isKick
     */
    public void kick(long userId, boolean isKick) {
        this.alreadyKickUser.add(userId);
//        this.needKickUser.remove(userId);
        if (!isKick) {
            this.notKickUser.add(userId);
        }
    }


    /**
     * 是否结束
     * @param aliveUser
     * @return
     */
    public boolean isOver(List<Long> aliveUser){
        //所有没弃牌的玩家全部已经选择过
        return aliveUser.stream().allMatch(userId -> alreadyKickUser.contains(userId));
    }
}
