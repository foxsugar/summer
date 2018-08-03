package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/7/25.
 */
public class PlayerCardInfoLuanGuaFeng extends  PlayerCardsInfoMj{
private static final int FANGZUOBI = 1;
    @Override
    public void init(List<String> cards) {
        super.init(cards);


        specialHuScore.put(hu_七小对, 10);
        specialHuScore.put(hu_豪华七小对, 10);
        specialHuScore.put(hu_清一色, 10);
        specialHuScore.put(hu_一条龙, 10);

        specialHuScore.put(hu_清龙, 20);
        specialHuScore.put(hu_清七对, 20);
    }

    @Override
    public boolean isHasXuanfengDan(List<String> cards,String card) {
        return isCanLiang();
    }

    @Override
    public boolean isCanBufeng(String card) {
        //必须亮过
        if (this.xuanfengDan.size() == 0) {
            return false;
        }
        if(!CardTypeUtil.isFeng(card)){
            return false;
        }


        return true;
    }

    @Override
    public boolean isHasChi(String card) {
        return false;
    }



    @Override
    public boolean isCanTing(List<String> cards) {
        return false;
    }

    private boolean isCanLiang(){
        System.out.println("card num : " + cards.size());
        int opSize = this.operateList.size();

        int size = 0;
        for (String c : this.cards) {
            if (isFeng(c) || isHun(c)) {
                size += 1;
            }
        }
        System.out.println("size : " + size);
        if(opSize == 1 && this.operateList.get(opSize - 1) == type_mopai){
            return this.cards.stream().filter(card->isFeng(card) || isHun(card)).count() >= 3;
        }
        return false;
    }


    private boolean isFeng(String card) {
        int type = CardTypeUtil.getCardGroup(card);
        return type == CardTypeUtil.GROUP_FENG || type == CardTypeUtil.GROUP_ZI;
    }

    private boolean isHun(String card) {
        int type = CardTypeUtil.getCardGroup(card);
        return this.gameInfo.hun.contains(type);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        //混牌 不能点炮
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (this.gameInfo.hun.contains(cardType)) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int lastCard = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(this, noPengAndGang, getChiPengGangNum(), this.gameInfo.hun, lastCard);
        return huList.size() > 0;
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        int lastCard = CardTypeUtil.getTypeByCard(card);

        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), getChiPengGangNum(), this.gameInfo.hun, lastCard);
        return huList.size() > 0;
    }


    @Override
    public void computeALLGang() {
        int gangFan = 0;
        gangFan += this.mingGangType.size() * 2;
        gangFan += this.anGangType.size() * 4;
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

    public boolean isHuangzhuang(GameInfo gameInfo) {
        return gameInfo.getRemainCards().size() <= 16;
    }

    public int getChiPengGangNum() {
        return this.chiCards.size() + this.pengType.size() + this.mingGangType.size() +
                this.anGangType.size() + this.xuanfengDan.getOrDefault(0,new ArrayList<>()).size()/3;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        //显庄 庄家输赢每家多10分

        this.gameInfo.computeAllGang();


        List<String> cs = getCardsNoChiPengGang(cards);

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();

        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        int maxPoint = huCardType.fan == 0? 2: huCardType.fan;

        //加上旋风蛋的分
        maxPoint += this.xuanfengDan.getOrDefault(0, new ArrayList<>()).size() + this.xuanfengDan.getOrDefault(1, new ArrayList<>()).size();

        if (isZimo) {
            maxPoint *= 2;
        }

        boolean isBao = !isZimo && isHasMode(this.roomInfo.mode, FANGZUOBI);

        double allScore = 0;

        for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
            if (playerCardsInfoMj.getUserId() != this.userId) {

                if (!isBao) {
                    playerCardsInfoMj.addScore(-maxPoint);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -maxPoint);
                }
                maxPoint += allScore;
            }
        }

        if (isBao) {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);
            dianPao.addScore(-allScore);

        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }
}
