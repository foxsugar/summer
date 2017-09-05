package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuWithHun;

import java.util.*;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class GameInfoTJ extends GameInfo {

    protected List<String> chanCards = new ArrayList<>();//铲的牌


    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;
        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        initHun(room);
    }

    /**
     * 初始化混
     *
     * @param room
     */
    public void initHun(RoomInfo room) {
        //随机混
        Random rand = new Random();
        int hunIndex = 2 + rand.nextInt(11);
        List<String> hunRemoveCards = new ArrayList<>();
        for (int i = 0; i < hunIndex * 2; i++) {
            hunRemoveCards.add(remainCards.get(0));
            remainCards.remove(0);
        }

        //确定混
        String card = hunRemoveCards.get(0);
        hunRemoveCards.remove(0);
        this.hunRemoveCards = hunRemoveCards;
        int cardType = CardTypeUtil.getTypeByCard(card);
        //todo 刮大风
        this.hun = HuWithHun.getHunTypeGDF(cardType);
    }

    @Override
    protected void mopai(long userId, String... wz) {
        super.mopai(userId, wz);
    }

    @Override
    public int chupai(long userId, String card) {
        int rtn = super.chupai(userId, card);

        if (rtn == 0) {
            //铲
            if (chanCards.size() <= 7) {

                chanCards.add(card);
                int chanCardSize = chanCards.size();
                if (chanCardSize % this.users.size() == 0) {
                    if(isCardSame(chanCards.subList(chanCardSize -4, chanCardSize))){
                        //todo 通知有铲
                    }

                }
            }
        }
        return rtn;
    }

    /**
     * 牌是否一样
     *
     * @param cards
     * @return
     */
    private boolean isCardSame(List<String> cards) {
        Set<Integer> set = new HashSet<>();
        cards.forEach(card -> set.add(CardTypeUtil.getTypeByCard(card)));
        return set.size() == 1;

    }

    /**
     * 获得铲的个数
     * @return
     */
    protected int getChanNum() {
        int result = 0;
        int chanNum = chanCards.size() / 4;

        if (chanNum > 1) {
            if(isCardSame(chanCards.subList(0,4))){
                result = 1;
            }
        }
        if (result == 1 && chanNum == 2) {
            if(isCardSame(chanCards.subList(4,8))){
                result = 2;
            }
        }
        return result;
    }
}
