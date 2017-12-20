package com.code.server.game.mahjong.logic;


import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int maxFan = 0;//基础番
        for (HuCardType huCardType : huList) {
            maxFan += huCardType.fan;
        }
        System.out.println("牌型的番数 : "+maxFan);

        //设置胡牌类型
        setWinTypeResult(getMaxScoreHuCardType(huList));
        this.fan = maxFan;

        if(isZimo){
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
            //TODO
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

    @Deprecated
    public void baoGang(RoomInfo room, long dianpaoUser, long winner){
        //点炮的人包杠
        int temp = 0;
        for(PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()){
            if(playerCardsInfo.getScore()<0){
                temp += playerCardsInfo.getScore();
                room.setUserSocre(playerCardsInfo.getUserId(), -playerCardsInfo.getScore());
                playerCardsInfo.setScore(0);
            }
        }
        gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() + temp);
        room.setUserSocre(dianpaoUser, temp);
    }


    public void computeALLGang(long dianpaoUser){
        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){//听牌直接计算
            for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
                //暗杠计算
                for (long i : scores.keySet()) {
                    scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
                }
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
                //明杠计算
                for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
    					/*if(playerCardsInfo.getMingGangType().get(ii)==-1){*/
                    for (long i : scores.keySet()) {
                        scores.put(i, scores.get(i) - 1);
                    }
                    scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
                }
            }
        }else{//未听牌
            for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
                //暗杠计算
                scores.put(dianpaoUser, scores.get(dianpaoUser) - playerCardsInfo.getAnGangType().size()*2*3);
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*3);
                //明杠计算
                scores.put(dianpaoUser, scores.get(dianpaoUser) - playerCardsInfo.getMingGangType().size()*3);
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getMingGangType().size()*3);
            }
        }


        for (long i : scores.keySet()) {
            gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
            roomInfo.setUserSocre(i, scores.get(i));
        }
    }

    public void computeALLGang(){

        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //暗杠计算
            for (long i : scores.keySet()) {
                scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
            }
            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
            //明杠计算
            for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                for (long i : scores.keySet()) {
                    scores.put(i, scores.get(i) - 1);
                }
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
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
}
