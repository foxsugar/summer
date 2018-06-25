package com.code.server.game.poker.cow;

import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class GameCowOfSetMutiple extends GameCow {

    @Override
    public int setMultipleForGetBanker(long userId,double multiple){
        playerCardInfos.get(userId).setSetMultipleForGetBanker(0);
        if(-1.0==setMultipleForGetBankers.get(userId)){
            setMultipleForGetBankers.put(userId,multiple);
        }
        MsgSender.sendMsg2Player("gameService", "setMultipleForGetBankerToAll", setMultipleForGetBankers, users);
        if(canSetRoomMultiple()){//设置roomMultiple
            room.setBankerId(maxMultiple());
            MsgSender.sendMsg2Player("gameService", "tellBankerId", room.getBankerId(), users);
            noticePlayerBet();//继续原来的步骤
            updateLastOperateTime();
        }
        MsgSender.sendMsg2Player("gameService", "setMultipleForGetBanker", 0, userId);

        return 0;
    }

    /**
     * 发送战绩
     */
    protected void sendResult() {
        GameResultCow gameResultCow  = new GameResultCow();
        List<Long> winnerList = new ArrayList<>();
        Map<Long, Double> userScores = new HashMap<>();
        for (PlayerCow p : playerCardInfos.values()) {
            if(p.getFinalScore()>0){
                winnerList.add(p.getUserId());
            }
            gameResultCow.getPlayerCardInfos().add( p.toVo());
            userScores.put(p.getUserId(),p.getFinalScore());
        }
        gameResultCow.setWinnerList(winnerList);

        //庄家无牛下庄
        if(18==playerCardInfos.get(room.getBankerId()).getPlayer().getGrade()){
            room.setBankerId(nextTurnId(room.getBankerId()));
        }
        gameResultCow.setBankerId(room.getBankerId());
        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultCow, users);
    }

    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    //是否全设置过倍数
    private boolean canSetRoomMultiple(){
        boolean b = true;
        a:for (Long l:setMultipleForGetBankers.keySet()) {
            if(-1==setMultipleForGetBankers.get(l)){
                b = false;
                break a;
            }
        }
        return b;
    }

    //选择庄
    private Long chooseBanker(){
        long l = 0l;

        return l;
    }

    //几个人叫最大分
    private long maxMultiple(){
        int one = 0;
        int two = 0;
        int three = 0;
        long banker = 0l;
        int random = Math.abs((int)System.currentTimeMillis());
        List<Long> tmpList1 = new ArrayList<>();
        List<Long> tmpList2 = new ArrayList<>();
        List<Long> tmpList3 = new ArrayList<>();
        for (Long l:setMultipleForGetBankers.keySet()) {
            if(setMultipleForGetBankers.get(l)==1.0){
                tmpList1.add(l);
                one++;
            }else if(setMultipleForGetBankers.get(l)==2.0){
                tmpList2.add(l);
                two++;
            }else if(setMultipleForGetBankers.get(l)==3.0){
                tmpList3.add(l);
                three++;
            }
        }
        if(three>0){
            banker = tmpList3.get(random%three);
            room.setMultiple(3);
        }else if(two>0){
            banker = tmpList2.get(random%two);
            room.setMultiple(2);
        }else if (one>0){
            banker = tmpList1.get(random%one);
            room.setMultiple(1);
        }
        if(banker==0l){
            banker=users.get(0);
        }
        return banker;
    }

    @Override
    protected void compute() {
        RoomCow roomCom = null;
        if(room instanceof RoomCow){
            roomCom = (RoomCow)room;
        }

        //算分
        List<PlayerCow> tempList = new ArrayList<>();
        tempList.addAll(playerCardInfos.values());
        tempList.remove(playerCardInfos.get(room.getBankerId()));
        for (PlayerCow p :tempList){
            CowPlayer c = CardUtils.findWinner(playerCardInfos.get(room.getBankerId()).getPlayer(), p.getPlayer());
            if(room.getBankerId()!=c.getId()){//庄输
                int tempGrade = playerCardInfos.get(p.getUserId()).getPlayer().getGrade();
                double tempScore =  playerCardInfos.get(p.getUserId()).getScore() * CardUtils.multipleMap.get(tempGrade);
                tempScore *= room.getMultiple();
                playerCardInfos.get(p.getUserId()).setFinalScore(tempScore);
                playerCardInfos.get(room.getBankerId()).setFinalScore(playerCardInfos.get(room.getBankerId()).getFinalScore()-tempScore);
            }else{//庄赢
                int tempGrade = playerCardInfos.get(room.getBankerId()).getPlayer().getGrade();
                double tempScore =  playerCardInfos.get(p.getUserId()).getScore() * CardUtils.multipleMap.get(tempGrade);
                tempScore *= room.getMultiple();
                playerCardInfos.get(p.getUserId()).setFinalScore(-tempScore);
                playerCardInfos.get(room.getBankerId()).setFinalScore(playerCardInfos.get(room.getBankerId()).getFinalScore()+tempScore);
            }
        }

        //设置每个人的统计
        boolean tempWin = true;
        boolean tempLost = true;
        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            if(room.getBankerId()!=playerCardInfo.userId){
                if(playerCardInfo.getFinalScore()>0){
                    tempWin = false;
                    this.room.addWinNum(playerCardInfo.getUserId());
                }
                if(playerCardInfo.getFinalScore()<0){
                    tempLost = false;
                }
            }
            if(8==playerCardInfo.getPlayer().getGrade()){//牛牛
                this.room.addCowCowNum(playerCardInfo.getUserId());
            }else if(18==playerCardInfo.getPlayer().getGrade()){//无牛
                this.room.addNullCowNum(playerCardInfo.getUserId());
            }
        }
        if(tempWin){
            this.room.addAllWinNum(room.getBankerId());
        }
        if(tempLost){
            this.room.addAllLoseNum(room.getBankerId());
        }
        if(playerCardInfos.get(room.getBankerId()).getFinalScore()>0){
            this.room.addWinNum(room.getBankerId());
        }

        for (PlayerCow playerCardInfo : playerCardInfos.values()) {
            room.addUserSocre(playerCardInfo.getUserId(),playerCardInfo.getFinalScore());
        }
    }
}
