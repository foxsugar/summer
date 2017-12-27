package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.FanUtil;
import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 粘粽子
 */
public class PlayerCardsInfoNZZ extends PlayerCardsInfoMj {

    public static final int TYPE_GUO = 10;

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_缺一门,1);
/*        specialHuScore.put(hu_边张,1);
        specialHuScore.put(hu_夹张,1);//砍张
        specialHuScore.put(hu_吊张,1);*/
        specialHuScore.put(hu_门清,1);
        specialHuScore.put(hu_断幺,1);
        specialHuScore.put(hu_一条龙,10);
        specialHuScore.put(hu_混一色,5);
        specialHuScore.put(hu_字一色,20);
        specialHuScore.put(hu_清一色,10);
        specialHuScore.put(hu_十三幺,30);
        specialHuScore.put(hu_一张赢,1);
    }


    //胡牌分数计算
    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){

        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(1));
        int maxFan = 1;//基础番
        HuCardType huCardTypeMax = getMaxScoreHuCardType(huList);

        maxFan += huCardTypeMax.fan;
        if(huCardTypeMax.specialHuList.contains(hu_缺一门)){
            if (huCardTypeMax.specialHuList.contains(hu_混一色)||huCardTypeMax.specialHuList.contains(hu_清一色)||huCardTypeMax.specialHuList.contains(hu_字一色)){//三元一副 自摸加1番
                maxFan -= 1;
            }
        }

        System.out.println("牌型的番数 : "+maxFan);

        //设置胡牌类型
        setWinTypeResult(getMaxScoreHuCardType(huList));
        this.fan = maxFan;


        if(isZimo){
            if(this.userId==gameInfo.getFirstTurn()){//庄赢
                for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                    gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple()-2);
                    room.setUserSocre(i, - 2 * maxFan * room.getMultiple()-2);
                }
                this.score = this.score +  8 * maxFan * room.getMultiple()+8;
                room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple()+8);
                this.fan = 2 * maxFan;
            }else{//庄输
                for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                    if(i==gameInfo.getFirstTurn()){
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple()-2);
                        room.setUserSocre(i, - 2 * maxFan * room.getMultiple()-2);
                    }else{
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * maxFan * room.getMultiple());
                        room.setUserSocre(i, - 2 * maxFan * room.getMultiple());
                    }
                }
                this.score = this.score +  8 * maxFan * room.getMultiple()+2;
                room.setUserSocre(this.userId, 8 * maxFan * room.getMultiple()+2);
                this.fan = 2 * maxFan;
            }

            System.out.println("======自摸：" + 6 * maxFan * room.getMultiple());
        }else {
            if (gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing) {
                if(this.userId==gameInfo.getFirstTurn()){//庄赢
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple()-1);
                        room.setUserSocre(i, - maxFan * room.getMultiple()-1);
                    }
                    this.score = this.score + 4 * maxFan * room.getMultiple() +4;
                    room.setUserSocre(this.userId, 4 * maxFan * room.getMultiple()+4);
                    this.fan = maxFan;
                }else{//庄输
                    for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                        if(i==gameInfo.getFirstTurn()){//庄-1
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple()-1);
                            room.setUserSocre(i, - maxFan * room.getMultiple()-1);
                        }else{
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple());
                            room.setUserSocre(i, - maxFan * room.getMultiple());
                        }
                    }
                    this.score = this.score + 4 * maxFan * room.getMultiple()+1;
                    room.setUserSocre(this.userId, 4 * maxFan * room.getMultiple()+1);
                    this.fan = maxFan;
                }
                System.out.println("======点炮（已听）：" + 3 * maxFan * room.getMultiple());
            } else {
                if(!this.roomInfo.isHaveTing()){
                    if(this.userId==gameInfo.getFirstTurn()){//庄赢
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple()-1);
                            room.setUserSocre(i, - maxFan * room.getMultiple()-1);
                        }
                        this.score = this.score + 4 * maxFan * room.getMultiple() +4;
                        room.setUserSocre(this.userId, 4 * maxFan * room.getMultiple()+4);
                        this.fan = maxFan;
                    }else{//庄输
                        for (Long i : gameInfo.getPlayerCardsInfos().keySet()) {
                            if(i==gameInfo.getFirstTurn()){//庄-1
                                gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple()-1);
                                room.setUserSocre(i, - maxFan * room.getMultiple()-1);
                            }else{
                                gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - maxFan * room.getMultiple());
                                room.setUserSocre(i, - maxFan * room.getMultiple());
                            }
                        }
                        this.score = this.score + 4 * maxFan * room.getMultiple()+1;
                        room.setUserSocre(this.userId, 4 * maxFan * room.getMultiple()+1);
                        this.fan = maxFan;
                    }
                    System.out.println("======点炮（已听）：" + 3 * maxFan * room.getMultiple());
                }else{
                    if(this.userId==gameInfo.getFirstTurn()){//庄赢
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * maxFan * room.getMultiple()-3);
                        this.score = this.score + 3 * maxFan * room.getMultiple()+3;
                        room.setUserSocre(dianpaoUser, -3 * maxFan * room.getMultiple()-3);
                        room.setUserSocre(this.userId, 3 * maxFan * room.getMultiple()+3);
                    }else{
                        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * maxFan * room.getMultiple()-1);
                        this.score = this.score + 3 * maxFan * room.getMultiple()+1;
                        room.setUserSocre(dianpaoUser, -3 * maxFan * room.getMultiple()-1);
                        room.setUserSocre(this.userId, 3 * maxFan * room.getMultiple()+1);
                    }
                    this.fan = maxFan;
                    System.out.println("======点炮（未听）：" + 3 * maxFan * room.getMultiple());
                }
            }
        }
    }


    @Override
    public boolean isHasChi(String card) {
        if (isTing) {
            return false;
        }
        if(!roomInfo.canChi){
            return false;
        }
        return super.isHasChi(card);
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
        yiZhangyingSet = getYiZhangYingSet(getCardsNoChiPengGang(cards), null);
        int cardType = CardTypeUtil.cardType.get(card);
        return HuUtil.isHu(noPengAndGang, this, cardType, new HuLimit(1)).size() > 0;
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if(1==roomInfo.getMustZimo() && roomInfo.isHaveTing()){
            if (!isTing){
                return false;
            }
        }
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        yiZhangyingSet = getYiZhangYingSet(getCardsNoChiPengGang(cards), null);
        int cardType = CardTypeUtil.cardType.get(card);
        return HuUtil.isHu(cs, this,cardType , new HuLimit(1)).size()>0;
    }


    @Override
    public boolean isCanTing(List<String> cards) {
        if (isTing) {
            return false;
        }
        if(!roomInfo.isHaveTing()){
            return false;
        }else{
            System.out.println("---------------NZZ是否可听--------------start userId : "+userId);
            List<String> temp = getCardsNoChiPengGang(cards);
            System.out.println("听的牌 : "+cards);
            System.out.println("听的牌 没有碰杠: "+temp);

            List<String> tempCards = getCardsNoChiPengGang(cards);

            int needFan = 1;
            List<HuCardType> list = getTingHuCardType(tempCards,null);
            Set<Integer> yzyTingSet = getYiZhangYingSet(tempCards, null);
            System.out.println("=================一张赢的所有类型 : "+yzyTingSet);
            for (HuCardType huCardType : list) {
                System.out.println("");
                System.out.println("============= 听的类型: "+huCardType.tingCardType);
                HuCardType.setHuCardType(huCardType, this);
                //听后牌有新添加的
                int fanResult = FanUtil.compute(huCardType.cards, huCardType,huCardType.tingCardType , this);
                System.out.println("算番的结果== : " + fanResult);
                System.out.println("是否可听: "+(fanResult >= needFan));
                //是一张赢
                if (yzyTingSet.contains(huCardType.tingCardType) && huCardType.isCheckYiZhangying) {
                    System.out.println("是一张赢加一番");
                    fanResult++;
                }
                if (fanResult >= needFan) {
                    return true;
                }
            }
            System.out.println("---------------ZNN是否可听--------------end userId : "+userId);
            return false;
        }

    }

    //杠牌分数计算
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {

    }

    /**
     * 听
     */
    public void ting(String card) {
        //出牌 弃牌置为空(客户端扣牌)
        this.cards.remove(card);
        this.disCards.add(card);

        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);
        yiZhangyingSet = getYiZhangYingSet(getCardsNoChiPengGang(cards), null);
    }

    public Set<Integer> getYiZhangYingSet(List<String> cards, HuLimit limit) {
        //获得没有碰和杠的牌
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        Set<Integer> yzySet = new HashSet<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
            for (String card : handCards) {
                List<String> tempCards = new ArrayList<>();
                tempCards.addAll(handCards);
                tempCards.remove(card);
                List<Integer> tingList = HuUtil.isTing(tempCards, this, limit);
                if (tingList.size() == 1) {
                    yzySet.addAll(tingList);
                }
            }
        } else {
            List<Integer> yzyList = HuUtil.isTing(handCards, this,limit);
            if (yzyList.size() == 1) {
                yzySet.addAll(yzyList);
            }
        }
        return yzySet;
    }
}
