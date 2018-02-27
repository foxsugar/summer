package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

/**
 * 粘粽子
 */
public class PlayerCardsInfoHM extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_七小对,2);
        specialHuScore.put(hu_一条龙,2);
        specialHuScore.put(hu_清一色,2);

        specialHuScore.put(hu_清龙,4);
        specialHuScore.put(hu_清七对,4);
        specialHuScore.put(hu_豪华七小对,4);

        specialHuScore.put(hu_十三幺,8);
    }


    //胡牌分数计算;
    @Override
    public void huCompute (RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        int maxFan = 1;//基础番
        for (HuCardType huCardType : huList) {
            maxFan += huCardType.fan;
        }
        System.out.println("牌型的番数 : "+maxFan);
        if(maxFan>1){
            maxFan-=1;
        }
        //设置胡牌类型
        setWinTypeResult(getMaxScoreHuCardType(huList));
        this.fan = maxFan;

        if(isZimo){
            computeALLGang(dianpaoUser);
            if(this.userId==gameInfo.getFirstTurn()){//庄赢
                for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                    gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * maxFan * room.getMultiple());
                    room.setUserSocre(i, - 4 * maxFan * room.getMultiple());
                }
                this.score = this.score +  16 * maxFan * room.getMultiple();
                room.setUserSocre(this.userId, 16 * maxFan * room.getMultiple());
                this.fan = 2 * maxFan;
            }else{//庄输
                for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                    if(i==gameInfo.getFirstTurn()){
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 4 * maxFan * room.getMultiple());
                        room.setUserSocre(i, - 4 * maxFan * room.getMultiple());
                    }else{
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple());
                        room.setUserSocre(i, - 2 * maxFan * room.getMultiple());
                    }
                }
                this.score = this.score +  10 * maxFan * room.getMultiple();
                room.setUserSocre(this.userId, 10 * maxFan * room.getMultiple());
                this.fan = 2 * maxFan;
            }
        }else {
            if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {
                computeALLGang(dianpaoUser);
                if(this.userId==gameInfo.getFirstTurn()){//庄赢
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple());
                        room.setUserSocre(i, - 2 * maxFan * room.getMultiple());
                    }
                    this.score = this.score + 8 * maxFan * room.getMultiple();
                    room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple());
                    this.fan = maxFan;
                }else{//庄输
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        if(i==gameInfo.getFirstTurn()){//庄
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple());
                            room.setUserSocre(i, - 2 * maxFan * room.getMultiple());
                        }else{
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple());
                            room.setUserSocre(i, - maxFan * room.getMultiple());
                        }
                    }
                    this.score = this.score + 5 * maxFan * room.getMultiple();
                    room.setUserSocre(this.userId, 5 * maxFan * room.getMultiple());
                    this.fan = maxFan;
                }
            } else {
                //杠算分:没听包
                gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - gameInfo.getPlayerCardsInfos().get(this.userId).anGangType.size()*6 - gameInfo.getPlayerCardsInfos().get(this.userId).mingGangType.size()*3);
                this.score = this.score + gameInfo.getPlayerCardsInfos().get(this.userId).anGangType.size()*6 + gameInfo.getPlayerCardsInfos().get(this.userId).mingGangType.size()*3;
                room.setUserSocre(dianpaoUser, - gameInfo.getPlayerCardsInfos().get(this.userId).anGangType.size()*6 - gameInfo.getPlayerCardsInfos().get(this.userId).mingGangType.size()*3);
                room.setUserSocre(this.userId, gameInfo.getPlayerCardsInfos().get(this.userId).anGangType.size()*6 + gameInfo.getPlayerCardsInfos().get(this.userId).mingGangType.size()*3);

                if(this.userId==gameInfo.getFirstTurn()){//庄赢
                    gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 8 * maxFan * room.getMultiple());
                    this.score = this.score + 8 * maxFan * room.getMultiple();
                    room.setUserSocre(dianpaoUser, -8 * maxFan * room.getMultiple());
                    room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple());
                }else{
                    if(gameInfo.getFirstTurn()!=dianpaoUser){
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 5 * maxFan * room.getMultiple());
                        this.score = this.score + 5 * maxFan * room.getMultiple();
                        room.setUserSocre(dianpaoUser, - 5 * maxFan * room.getMultiple());
                        room.setUserSocre(this.userId, 5 * maxFan * room.getMultiple());
                    }else{
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 6 * maxFan * room.getMultiple());
                        this.score = this.score + 6 * maxFan * room.getMultiple();
                        room.setUserSocre(dianpaoUser, -6 * maxFan * room.getMultiple());
                        room.setUserSocre(this.userId, 6 * maxFan * room.getMultiple());
                    }
                }
                this.fan = maxFan;
            }
        }
    }

    //不包杠算分
    public void computeALLGang(long dianpaoUser){

        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if(this.userId == playerCardsInfo.userId){
                //暗杠计算
                for (long i : scores.keySet()) {
                    scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
                }
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
                //明杠计算
                for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                    if(0==dianpaoUser){
                        for (long i : scores.keySet()) {
                            scores.put(i, scores.get(i) - 1);
                        }
                        scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
                    }else{
                        if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
                            for (long i : scores.keySet()) {
                                scores.put(i, scores.get(i) - 1);
                            }
                            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
                        }else{
                            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
                            scores.put(dianpaoUser, scores.get(dianpaoUser) - 4);
                        }
                    }
                }
            }
        }
        for (long i : scores.keySet()) {
            gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
            roomInfo.setUserSocre(i, scores.get(i));
        }
    }



    //杠牌分数计算
    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {}

    public boolean isHasChi(String card){
        return false;
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        } else{
            return getTingCardType(getCardsNoChiPengGang(cards),null).size()>0;
        }
    }

    @Override
    public boolean isHasGang() {
        System.out.println("是否能杠: "+" isTing = "+isTing +"  cards = "+cards);
        if (isTing) {
            Set<Integer> canGangType = getHasGangList(cards);
            for (int gt : canGangType) {
                List<String> temp = new ArrayList<>();
                temp.addAll(cards);
                if (isCanTingAfterGang(temp, gt,false)) {
                    return true;
                }
            }
            return false;

        }else return super.isHasGang();
    }

    @Override
    public boolean isCanPengAddThisCard(String card) {
        //听之后不能碰牌
        if (isTing) {
            return false;
        }
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        //听之后 杠后的牌还能听
        if (isTing && super.isCanGangAddThisCard(card)) {
            List<String> temp = getCardsAddThisCard(card);
            //去掉 这张杠牌
            int ct = CardTypeUtil.cardType.get(card);
            return isCanTingAfterGang(temp, ct,true);

        } else return super.isCanGangAddThisCard(card);

    }

    /**
     * 杠之后是否能听
     * @param cards
     * @param cardType
     * @return
     */
    protected boolean isCanTingAfterGang(List<String> cards,int cardType,boolean isDianGang){
        //先删除这次杠的
        removeCardByType(cards,cardType,4);
        boolean isMing = false;
        //去除碰
        for(int pt : pengType.keySet()){//如果杠的是之前碰过的牌
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
            } else {
                isMing = true;
            }
        }
        //去掉杠的牌
        cards = getCardsNoGang(cards);
        isMing = isMing||isDianGang;

        //胡牌类型加上杠
        List<HuCardType> list = getTingHuCardType(cards,null);
        return list.size()>0;
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
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
        return HuUtil.isHu(noPengAndGang, this, cardType, null).size() > 0;
    }

    /**
     * 是否可胡 自摸
     *
     * @param card
     * @return
     */
    @Override
    public boolean isCanHu_zimo(String card) {
        if(roomInfo.isHaveTing()){
            if (!isTing){
                return false;
            }
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);
        return HuUtil.isHu(cs, this, cardType, null).size() > 0;

    }
}
