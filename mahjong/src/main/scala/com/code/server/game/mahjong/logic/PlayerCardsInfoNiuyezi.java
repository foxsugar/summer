package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

/**
 * Created by sunxianping on 2018/1/10.
 */
public class PlayerCardsInfoNiuyezi extends PlayerCardsInfoHM {

    protected static final int MODE_BANKER_1 = 0;
    protected static final int MODE_BANKER_2 = 1;
    protected static final int MODE_BANKER_3 = 2;
    protected static final int MODE_BANKER_4 = 3;

    /**
     * 是否荒庄
     *
     * @param gameInfo
     * @return
     */
    public boolean isHuangzhuang(GameInfo gameInfo) {

        int remainSize = 12;
        int gangSize = this.gameInfo.getAllGangNum();
        remainSize += gangSize * 2;
        remainSize = remainSize >= 18 ? 18 : remainSize;
        return gameInfo.getRemainCards().size() <= remainSize;
    }

    @Override
    public boolean isCanPengAddThisCard(String card) {
        if (isFeng(card)) {
            return false;
        }
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        if (isFeng(card)) {
            return false;
        }
        return super.isCanGangAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if (isFeng(card)) {
            return false;
        }
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isHasGang() {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set set = getHasGangList(temp);

        if (set.size() == 0) {
            return false;
        }
        for (Object card : set) {
            int type = (Integer)card;
            if (!isFeng(type)) {
                return true;
            }
        }
        return false;
    }


    private boolean isFeng(int type){
        int group = CardTypeUtil.getCardGroupByCardType(type);
        return group == CardTypeUtil.GROUP_ZI || group == CardTypeUtil.GROUP_FENG;

    }

    private boolean isFeng(String card){
        int type = CardTypeUtil.getTypeByCard(card);
        return isFeng(type);
    }


    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        }

        List<String> temp = getCardsNoChiPengGang(cards);
        List<HuCardType> list = getTingHuCardType(temp, null);
        for (HuCardType huCardType : list) {
            //如果大于8张 就能胡
            if (isCanHu(huCardType)) {
                return true;
            }
        }

        return false;
    }


    @Override
    protected boolean isCanTingAfterGang(List<String> cards, int cardType, boolean isDianGang) {
        //先删除这次杠的
        removeCardByType(cards, cardType, 4);
        boolean isMing = false;
        //去除碰
        for (int pt : pengType.keySet()) {//如果杠的是之前碰过的牌
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
            } else {
                isMing = true;
            }
        }
        //去掉杠的牌
        cards = getCardsNoGang(cards);

        //胡牌类型加上杠
        List<HuCardType> list = getTingHuCardType(cards, null);


        for (HuCardType huCardType : list) {
            //加上这次杠
            huCardType.anGang.add(cardType);
            //如果大于8张 就能胡
            if (isCanHu(huCardType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        if (this.isGuoHu()) {
            return false;
        }
        if (roomInfo.mustZimo == 1) {
            return false;
        }
        if (!isTing && this.roomInfo.isHaveTing()) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        System.out.println("检测是否可胡点炮= " + noPengAndGang);
        int cardType = CardTypeUtil.cardType.get(card);

        List<HuCardType> huList = HuUtil.isHu(noPengAndGang, this, cardType, null);
        return huList.stream().filter(this::isCanHu).count() > 0;

    }

    /**
     * 是否可胡 自摸
     *
     * @param card
     * @return
     */
    @Override
    public boolean isCanHu_zimo(String card) {
        if (roomInfo.isHaveTing()) {
            if (!isTing) {
                return false;
            }
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);
        List<HuCardType> huList = HuUtil.isHu(cs, this, cardType, null);
        return huList.stream().filter(this::isCanHu).count() > 0;

    }

    @Override
    public void computeALLGang() {
        int gangFan = 0;
        gangFan += this.anGangType.size() * 2;
        int score = gangFan * roomInfo.getMultiple();
        int sub = 0;
        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                playerCardsInfo.addGangScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        super.gangCompute(room, gameInfo, isMing, diangangUser, card);
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        //算杠
        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            playerCardsInfoMj.computeALLGang();
        }


        //算胡

        //数页
        int score = getShuyeFan(this.cards);

        if (this.userId == gameInfo.getFirstTurn()) {
            score += bankerAddScore();
        }


        //dianpao
        if (!isZimo) {
            PlayerCardsInfoMj dianPaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            boolean isBao_ting_hou = false;
            if (dianPaoPlayer.operateList.size() > 0) {
                isBao_ting_hou = dianPaoPlayer.operateList.get(dianPaoPlayer.operateList.size() - 1) == PlayerCardsInfoMj.type_ting;
            }
            boolean isBaoAll = !dianPaoPlayer.isTing || isBao_ting_hou;


            int subScore = 0;
            for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfo.getUserId() != this.userId) {
                    int scoreTemp = score;
                    if (playerCardsInfo.getUserId() == gameInfo.getFirstTurn()) {
                        scoreTemp += bankerAddScore();
                    }
                    scoreTemp = scoreTemp * room.getMultiple();
                    if (!isBaoAll) {
                        playerCardsInfo.addScore(-scoreTemp );
                        this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -scoreTemp );
                    }
                    subScore += scoreTemp;
                }
            }

            if (isBaoAll) {
                dianPaoPlayer.addScore(-subScore);
                this.roomInfo.addUserSocre(dianPaoPlayer.getUserId(), -subScore);

            }
            this.addScore(subScore);
            this.roomInfo.addUserSocre(this.getUserId(), subScore);

        } else {//自摸

            int subScore = 0;
            for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfo.getUserId() != this.userId) {
                    int scoreTemp = score;
                    if (playerCardsInfo.getUserId() == gameInfo.getFirstTurn()) {
                        scoreTemp += bankerAddScore();
                    }
                    scoreTemp *= 2;
                    scoreTemp *= room.getMultiple();
                    playerCardsInfo.addScore(-scoreTemp);
                    this.roomInfo.addUserSocre(playerCardsInfo.getUserId(), -scoreTemp);
                    subScore += scoreTemp;

                }
            }
            this.addScore(subScore);
            this.roomInfo.addUserSocre(this.getUserId(), subScore);

        }
    }

    /**
     * 听
     */
    @Override
    public void ting(String card) {
        //出牌 弃牌置为空(客户端扣牌)
        this.cards.remove(card);
        this.disCards.add(card);
        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);
    }

    private int bankerAddScore() {
        int one = isHasMode(this.roomInfo.mode, MODE_BANKER_1) ? 1 : 0;
        int two = isHasMode(this.roomInfo.mode, MODE_BANKER_2) ? 2 : 0;
        int three = isHasMode(this.roomInfo.mode, MODE_BANKER_3) ? 3 : 0;
        int four = isHasMode(this.roomInfo.mode, MODE_BANKER_4) ? 4 : 0;
        return one + two + three + four;
    }


    protected int getShuyeFan(List<String> cards) {
        Map<Integer, Integer> shuye = PlayerCardsInfoSZ.getNumByGroup(cards);
        int result = 0;
        for (Integer num : shuye.values()) {
            if (num > 7) {
                result += num - 7;
            }
        }
        return result;
    }

    /**
     * 是否能胡
     *
     * @param huCardType
     * @return
     */
    private boolean isCanHu(HuCardType huCardType) {
        return getHuGroupCardNum(huCardType) >= 8 && !isHasFeng(huCardType);


    }

    /**
     * 是否有风
     *
     * @param huCardType
     * @return
     */
    private boolean isHasFeng(HuCardType huCardType) {
        //碰
        for (Integer pengType : huCardType.peng) {
            int group = CardTypeUtil.getCardGroupByCardType(pengType);
            if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
                return true;
            }

        }
        //杠
        for (Integer gangType : huCardType.mingGang) {
            int group = CardTypeUtil.getCardGroupByCardType(gangType);
            if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
                return true;
            }
        }
        for (Integer gangType : huCardType.anGang) {
            int group = CardTypeUtil.getCardGroupByCardType(gangType);
            if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
                return true;
            }
        }

        for (String card : huCardType.cards) {
            int group = CardTypeUtil.getCardGroup(card);
            if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 一种类型的牌的数量
     *
     * @param huCardType
     * @return
     */
    private int getHuGroupCardNum(HuCardType huCardType) {
        Map<Integer, Integer> groupSize = new HashMap<>();
        //将
        mapAddCard(groupSize, huCardType.jiang, 2);
        //碰
        for (Integer pengType : huCardType.peng) {
            mapAddCard(groupSize, pengType, 3);
        }
        //杠
        for (Integer gangType : huCardType.mingGang) {
            mapAddCard(groupSize, gangType, 4);
        }
        for (Integer gangType : huCardType.anGang) {
            mapAddCard(groupSize, gangType, 4);
        }

        for (Integer ke : huCardType.ke) {
            mapAddCard(groupSize, ke, 3);
        }
        for (Integer shun : huCardType.shun) {
            mapAddCard(groupSize, shun, 3);
        }

        int max = 0;
        for (Integer a : groupSize.values()) {
            if (a > max) {
                max = a;
            }
        }
        return max;
    }

    private static void mapAddCard(Map<Integer, Integer> map, int cardType, int size) {
        int group = CardTypeUtil.getCardGroupByCardType(cardType);
        if (map.containsKey(group)) {
            map.put(group, map.get(group) + size);
        } else {
            map.put(group, size);
        }
    }

}
