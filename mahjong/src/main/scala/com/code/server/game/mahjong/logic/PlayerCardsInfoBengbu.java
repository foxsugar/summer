package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;

/**
 * Created by sunxianping on 2017/12/26.
 */
public class PlayerCardsInfoBengbu extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        this.cards = cards;
    }


    public boolean isCanTing(List<String> cards) {
        return false;
    }

    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;
        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);
    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        GameInfoBengbu gameInfoBengbu = (GameInfoBengbu)gameInfo;
        boolean isQiangGanghu = gameInfoBengbu.isQiangGanghu;
        //算杠
        int gangScore = (this.mingGangType.size() + this.anGangType.size() * 2) * this.roomInfo.getMultiple();
        int subGang = 0;
        //自摸三个人赔
        if (isZimo) {
            for (PlayerCardsInfoMj playerCardInfo : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardInfo.getUserId() != this.userId) {
                    int gangScoreTemp = gangScore * this.roomInfo.getMultiple();
                    if(!isQiangGanghu){

                        playerCardInfo.addScore(-gangScoreTemp);
                        playerCardInfo.addGangScore(-gangScoreTemp);
                        this.roomInfo.addUserSocre(playerCardInfo.getUserId(), -gangScoreTemp);
                    }
                    subGang += gangScoreTemp;
                }
            }

        } else {//点炮的人赔
            PlayerCardsInfoMj dianpaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            dianpaoPlayer.addScore(-gangScore * this.roomInfo.getMultiple());
            dianpaoPlayer.addGangScore(-gangScore * this.roomInfo.getMultiple());
            this.roomInfo.addUserSocre(dianpaoPlayer.getUserId(), -gangScore * this.roomInfo.getMultiple());
            subGang += gangScore * this.roomInfo.getMultiple();
        }
        this.addGangScore(subGang);
        this.addScore(subGang);
        this.roomInfo.addUserSocre(this.userId, subGang);

        if (isQiangGanghu) {
            PlayerCardsInfoMj qiangGangUser = this.gameInfo.playerCardsInfos.get(this.gameInfo.beJieGangUser);
            qiangGangUser.addGangScore(-subGang);
            qiangGangUser.addScore(-subGang);
            qiangGangUser.roomInfo.addUserSocre(this.gameInfo.beJieGangUser, -subGang);

        }


        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);
        int score = huCardType.fan == 0 ? 1 : huCardType.fan;
        int subScore = 0;
        boolean isBanker = this.userId == gameInfo.getFirstTurn();
        if (isBanker) score += 1;
        if (isZimo) score += 1;

        //杠后胡加倍
        boolean isGangKai = isGangKai();
        //其他人扣分
        if (isZimo) {

            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.getUserId() != userId) {
                    int scoreTemp = score;
                    //如果是庄家多输一分
                    if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn()) {
                        scoreTemp += 1;
                    }
                    scoreTemp = scoreTemp * this.roomInfo.getMultiple();
                    if(isGangKai) scoreTemp *= 2;
                    if(!isQiangGanghu){

                        playerCardsInfoMj.addScore(-scoreTemp);
                        this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -scoreTemp);
                    }
                    subScore += scoreTemp;
                }
            }
        } else {
            PlayerCardsInfoMj dianpao = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            int tempScore = dianpao.getUserId() == this.gameInfo.getFirstTurn() ? score + 1 : score;
            if(isGangKai) tempScore *= 2;
            dianpao.addScore(-tempScore);
            this.roomInfo.addUserSocre(dianpao.getUserId(), -tempScore);
            subScore = tempScore;
        }

        //

        this.addScore(subScore);
        this.roomInfo.addUserSocre(this.userId, subScore);

        if (isQiangGanghu) {
            PlayerCardsInfoMj qiangGangUser = this.gameInfo.playerCardsInfos.get(this.gameInfo.beJieGangUser);
            qiangGangUser.addScore(-subScore);
            qiangGangUser.roomInfo.addUserSocre(this.gameInfo.beJieGangUser, -subScore);

        }

    }

}
