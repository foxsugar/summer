package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-08.
 * 鼎盛棋牌
 *   点炮，A赢9分， B输9分，C、D不输不赢
     自摸，A赢24分，B输8分，C输8分，D输8分

     清一色、七小对、一条龙：
     点炮，A赢12分， B输12分，C、D不输不赢
     自摸，A赢36分，B输12分，C输12分，D输12分

     豪华七小对：
     点炮，A赢15分， B输15分，C、D不输不赢
     自摸，A赢45分，B输15分，C输15分，D输15分
 *
 */
public class PlayerCardsInfoDINGSHENG extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_清一色,12);
        specialHuScore.put(hu_一条龙,12);
        specialHuScore.put(hu_清龙,12);
        specialHuScore.put(hu_七小对,12);
//        specialHuScore.put(hu_十三幺,1);
        specialHuScore.put(hu_豪华七小对,15);
        specialHuScore.put(hu_双豪七小对_山西,15);

    }







    public boolean isCanTing(List<String> cards) {
        return false;
    }

    public boolean isHasChi(String card) {
        return false;
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
        int score = 0;
        int subScore = 0;

        this.fan = huCardType.fan;


        if (this.fan == 0) {
            if (isZimo) {
                score = 8;
            } else {
                score = 9;
            }
        }
        score *= this.roomInfo.getMultiple();
        //自摸
        if (isZimo) {

            //三家出
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
            //点炮的人出
            dianpaoPlayer.addScore(-score);
            roomInfo.setUserSocre(dianpaoPlayer.getUserId(), -score);
            subScore += score;

        }

        //赢得人加分
        this.addScore(subScore);
        roomInfo.setUserSocre(this.getUserId(), subScore);
    }


}
