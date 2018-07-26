package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/4/8.
 */
public class PlayerCardsInfoHS extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {

        this.cards = cards;
        specialHuScore.put(hu_七小对, 3);
        specialHuScore.put(hu_豪华七小对, 3);
        specialHuScore.put(hu_双豪七小对, 3);
        specialHuScore.put(hu_三豪七小对, 3);


    }

    public boolean isCanTing(List<String> cards) {

        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), null);
        if(isMoreOneCard())
        tingWhatInfo = list;
        return list.size() > 0;
    }

    public boolean isHasChi(String card) {
        return false;
    }


    public static int getYuNum(String mode){
        if (isHasMode(mode, 2)) {
            return 2;
        }
        if (isHasMode(mode, 3)) {
            return 5;
        }
        if (isHasMode(mode, 4)) {
            return 8;
        }
        if (isHasMode(mode, 5)) {
            return 12;
        }
        if (isHasMode(mode, 6)) {
            return 20;
        }
        return 0;
    }
    private int getYu() {
        return getYuNum(this.roomInfo.getMode());

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
                int temp = 3 * this.roomInfo.getMultiple();
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


        //算杠
        gameInfo.computeAllGang();

        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);
        int score = huCardType.fan == 0 ? 1 : huCardType.fan;
        int subScore = 0;

        this.fan = huCardType.fan;
        //yu
        int yu = getYu();

        //自摸
        if (isZimo) {

            if (isGangKai()) {
                score *= 6;
                this.winType.add(hu_杠上开花);
                this.fan = 3;
            } else {
                score *= 2;
            }

            score += yu;

            //三家出
            score *= this.roomInfo.getMultiple();
            for (PlayerCardsInfoMj playerCardsInfo : this.gameInfo.getPlayerCardsInfos().values()) {
                if (playerCardsInfo.getUserId() != userId) {

                    playerCardsInfo.addScore(-score);
                    roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                    subScore += score;
                }
            }


        } else {
            //点炮
            PlayerCardsInfoMj dianpaoPlayer = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);

            //截杠胡 输三倍
            if (isJieGangHu) {
                score *= 9;
                this.fan *= 3;
            } else { //普通点炮 输两倍
                score *= 3;
            }
            score += yu;
            //点炮的人出
            score *= this.roomInfo.getMultiple();
            dianpaoPlayer.addScore(-score);
            roomInfo.setUserSocre(dianpaoPlayer.getUserId(), -score);

            subScore += score;

        }


        //赢得人加分
        this.addScore(subScore);
        roomInfo.setUserSocre(this.getUserId(), subScore);


    }
}
