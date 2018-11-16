package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-09.
 */
public class PlayerCardsInfoFanshi extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_缺一门, 0);
        specialHuScore.put(hu_缺两门, 0);

        specialHuScore.put(hu_夹张, 0);
//        specialHuScore.put(hu_边张, 0);
        specialHuScore.put(hu_吊张, 0);


        specialHuScore.put(hu_清一色, 9);
        specialHuScore.put(hu_一条龙, 4);
        specialHuScore.put(hu_清龙, 12);
        specialHuScore.put(hu_架龙, 9);
        specialHuScore.put(hu_清一色架龙, 18);


        specialHuScore.put(hu_七小对, 6);
        specialHuScore.put(hu_豪华七小对, 10);
        specialHuScore.put(hu_双豪七小对_山西, 20);

        specialHuScore.put(hu_清一色七小对, 15);
        specialHuScore.put(hu_清一色豪华七小对, 19);
        specialHuScore.put(hu_清一色双豪华七小对, 29);


        specialHuScore.put(hu_碰碰胡, 4);
        specialHuScore.put(hu_清一色碰碰胡, 10);

        this.setHasGangBlackList(false);

    }

    public boolean isCanTing(List<String> cards) {
        return false;
    }

    public boolean isHasChi(String card) {
        return false;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {


        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int cardType = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(noPengAndGang, this, cardType, new HuLimit(0));
        return huList.stream().anyMatch(huCardType -> huCardType.specialHuList.contains(hu_缺一门) || huCardType.specialHuList.contains(hu_缺两门));

    }

    @Override
    public boolean isCanHu_zimo(String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        int cardType = CardTypeUtil.cardType.get(card);
        List<HuCardType> huList = HuUtil.isHu(cs, this, cardType, new HuLimit(0));
        return huList.stream().anyMatch(huCardType -> huCardType.specialHuList.contains(hu_缺一门) || huCardType.specialHuList.contains(hu_缺两门));
    }


    @Override
    public void computeALLGang() {
        //明杠1分
        int sub = 0;
        int gangFan = 0;
        gangFan += this.anGangType.size() * 2;

        for (Map.Entry<Integer, Long> entry : this.mingGangType.entrySet()) {
            long dianGangUser = entry.getValue();
            //点杠
            if (dianGangUser != -1) {
                PlayerCardsInfoMj dianGangPlayer = this.gameInfo.getPlayerCardsInfos().get(dianGangUser);
                int temp = 1 * this.roomInfo.getMultiple();
                dianGangPlayer.addScore(-temp);
                dianGangPlayer.addGangScore(-temp);
                roomInfo.setUserSocre(dianGangUser, -temp);

                //自己加分
                this.addScore(temp);
                this.addGangScore(temp);
                roomInfo.setUserSocre(this.getUserId(), temp);

            } else {
                gangFan += 1;
            }
        }


        //除了点杠
        int score = gangFan * roomInfo.getMultiple();

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                playerCardsInfo.addGangScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addGangScore(sub);
        this.addScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);
    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {



        this.gameInfo.computeAllGang();



        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);
        int score = huCardType.fan == 0 ? 1 : huCardType.fan;
        if (huCardType.fan == 0) {
//            if(huCardType.specialHuList.contains(hu_夹张) || huCardType.specialHuList.contains(hu_吊张)){
//                score = 3;
//            }else{
                score = 2;
//            }
        }
        if(huCardType.specialHuList.contains(hu_夹张) || huCardType.specialHuList.contains(hu_吊张)){
            score += 1;
        }
        //杠开 +1
        boolean isGangkai = isGangKai();
        if (isGangkai) {
            score += 1;
        }

        //是否是海底捞
        boolean isHaidilao = gameInfo.remainCards.size() == 0;

        //海底捞 +1
        if (isHaidilao) {
            score += 1;
        }

        int allScore = 0;
        if (isZimo) {

            for(PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()){
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    playerCardsInfoMj.addScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allScore += score;

                }
            }
        } else {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);


            //是否是杠后炮
            boolean isGangHouPao =false;
            if (dianPao.operateList.size() >= 3) {
                isGangHouPao = dianPao.operateList.get(dianPao.operateList.size() - 3) == type_gang;
            }
            if (isGangHouPao) {
                score += 1;
            }
            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(dianPao.getUserId(), -score);
            allScore += score;

        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);

    }


}
