package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/7/16.
 */
public class PlayerCardsInfoGSJ_New extends PlayerCardsInfoDonghu{

    protected boolean is_硬八张 = false;
    @Override
    public void init(List<String> cards) {

        this.cards = cards;
        isHasTing = true;

        specialHuScore.put(hu_清一色,15);
        specialHuScore.put(hu_一条龙,15);
        specialHuScore.put(hu_七小对,15);
        specialHuScore.put(hu_十三幺,30);
        specialHuScore.put(hu_清龙,30);

        //坎胡
        specialHuScore.put(hu_边张,10);
//        specialHuScore.put(hu_夹张,1);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), new HuLimit(0));

        if (is_硬八张){

            long wanCount = this.cards.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 <= 8);
                 }).count();

            long tongCount = this.cards.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 >= 18) && (Integer.valueOf(x) / 4 < 26);
            }).count();

            long tiaoCount = this.cards.stream().filter(x ->{
                return (Integer.valueOf(x) / 4 >= 9) && (Integer.valueOf(x) / 4 <= 17);
            }).count();

            if (wanCount < 7 && tongCount <= 7 && tiaoCount <= 7){
                return false;
            }
        }

        return !isTing && list.stream().filter(hct -> hct.specialHuList.contains(hu_缺一门) || hct.specialHuList.contains(hu_缺两门)).count() > 0;
    }

    /**
     * 杠之后是否能听
     *
     * @param cards
     * @param cardType
     * @return
     */
    @Override
    protected boolean isCanTingAfterGang(List<String> cards, int cardType) {
//        // 先删除这次杠的
//        removeCardByType(cards, cardType, 4);
//        for (int pt : pengType.keySet()) {// 如果杠的是之前碰过的牌
//            //去掉碰
//            if (pt != cardType) {
//                removeCardByType(cards, pt, 3);
//            }
//        }
        // 去掉杠的牌
        cards = getCardsNoGang(cards);
        List<HuCardType> list = getTingHuCardType(cards, null);
        return list.stream().filter(hct -> hct.specialHuList.contains(hu_缺一门) || hct.specialHuList.contains(hu_缺两门)).count() > 0;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

//        this.mingGangType;
//        this.mingGangType

//        自摸明杠  每人5分 ；点杠 点杠者单人扣5分
//        自摸暗杆   每人10分

//        int gangScore = 0;
//        //自摸明杠
//        for (Map.Entry<Integer,Long> entry : mingGangType.entrySet()){
//            if (entry.getValue() == -1l) continue;
//            gangScore += 5;
//        }


//        //算杠
//        int gangScore = (this.mingGangType.size() + this.anGangType.size() * 2) * this.roomInfo.getMultiple();
//        int subGang = 0;
//        //自摸三个人赔
//        if (isZimo) {
//            for (PlayerCardsInfoMj playerCardInfo : this.gameInfo.playerCardsInfos.values()) {
//                if (playerCardInfo.getUserId() != this.userId) {
//                    int gangScoreTemp = gangScore * this.roomInfo.getMultiple();
//                    playerCardInfo.addScore(-gangScoreTemp);
//                    playerCardInfo.addGangScore(-gangScoreTemp);
//                    this.roomInfo.addUserSocre(playerCardInfo.getUserId(), -gangScoreTemp);
//                    subGang += gangScoreTemp;
//                }
//            }
//
//        } else {//点炮的人赔
//            PlayerCardsInfoMj dianpaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
//            dianpaoPlayer.addScore(-gangScore * this.roomInfo.getMultiple());
//            dianpaoPlayer.addGangScore(-gangScore * this.roomInfo.getMultiple());
//            this.roomInfo.addUserSocre(dianpaoPlayer.getUserId(), -gangScore * this.roomInfo.getMultiple());
//            subGang += gangScore * this.roomInfo.getMultiple();
//        }
//        this.addGangScore(subGang);
//        this.addScore(subGang);
//        this.roomInfo.addUserSocre(this.userId, subGang);
//
//
//        List<String> cs = getCardsNoChiPengGang(cards);
//        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
//        // 设置胡牌类型
//        HuCardType huCardType = getMaxScoreHuCardType(huList);
//        this.winType.addAll(huCardType.specialHuList);
//        int score = huCardType.fan == 0 ? 1 : huCardType.fan;
//        int subScore = 0;
//        boolean isBanker = this.userId == gameInfo.getFirstTurn();
//        if (isBanker) score += 1;
//
//        //其他人扣分
//        if (isZimo) {
//
//            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
//                if (playerCardsInfoMj.getUserId() != userId) {
//                    int scoreTemp = score;
//                    //如果是庄家多输一分
//                    if (playerCardsInfoMj.getUserId() == this.gameInfo.getFirstTurn()) {
//                        scoreTemp += 1;
//                    }
//                    scoreTemp = scoreTemp * this.roomInfo.getMultiple();
//                    playerCardsInfoMj.addScore(-scoreTemp);
//                    subScore += scoreTemp;
//                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -scoreTemp);
//                }
//            }
//        } else {
//            PlayerCardsInfoMj dianpao = this.gameInfo.playerCardsInfos.get(dianpaoUser);
//            int tempScore = dianpao.getUserId() == this.gameInfo.getFirstTurn() ? score + 1 : score;
//            dianpao.addScore(-tempScore);
//            this.roomInfo.addUserSocre(dianpao.getUserId(), -tempScore);
//            subScore = tempScore;
//        }
//
//        //
//
//        this.addScore(subScore);
//        this.roomInfo.addUserSocre(this.userId, subScore);

    }


}
