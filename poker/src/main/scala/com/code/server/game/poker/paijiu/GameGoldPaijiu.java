package com.code.server.game.poker.paijiu;

import com.code.server.constant.response.GameOfResult;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserOfResult;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.List;

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
public class GameGoldPaijiu extends GamePaijiuEndless{



    /**
     * 比较输赢并设置分数
     */
    @Override
    public int compareAndSetScore(PlayerCardInfoPaijiu banker,PlayerCardInfoPaijiu other) {
        int mix8Score = getGroupScoreByName("mixeight");
        int bankerScore1 = getGroupScore(banker.group1());
        int bankerScore2 = getGroupScore(banker.group2());
        int otherScore1 = getGroupScore(other.group1());
        int otherScore2 = getGroupScore(other.group2());
        int result = 0;

        //金币牌九新加的算法：锅两道
        if(other.bet().one()==other.bet().two() && (other.bet().one()+other.bet().two())==this.roomPaijiu().bankerInitScore()){
            if(bankerScore1 < otherScore1 && bankerScore2 < otherScore2){//全赢
                int changeScore = other.getBetScore(otherScore2 >= mix8Score);
                banker.addScore(this.roomPaijiu(), -changeScore);
                other.addScore(this.roomPaijiu(), changeScore);
                this.roomPaijiu().addUserSocre(banker.userId(), -changeScore);
                this.roomPaijiu().addUserSocre(other.userId(), changeScore);
                other.setWinState(-1);
            }else if((bankerScore1 < otherScore1 && bankerScore2 == otherScore2)
                    ||(bankerScore1 == otherScore1 && bankerScore2 < otherScore2)){//赢一半
                int changeScore = other.getBetScore(otherScore2 >= mix8Score);
                banker.addScore(this.roomPaijiu(), -changeScore/2);
                other.addScore(this.roomPaijiu(), changeScore/2);
                this.roomPaijiu().addUserSocre(banker.userId(), -changeScore/2);
                this.roomPaijiu().addUserSocre(other.userId(), changeScore/2);
                other.setWinState(-1);
            }else{//庄赢
                int changeScore = other.getBetScore(bankerScore2 >= mix8Score);
                banker.addScore(this.roomPaijiu(), changeScore);
                other.addScore(this.roomPaijiu(), -changeScore);
                this.roomPaijiu().addUserSocre(banker.userId(), changeScore);
                this.roomPaijiu().addUserSocre(other.userId(), -changeScore);
                other.setWinState(1);
            }
        }else{//原始算法
            if (bankerScore1 >= otherScore1) result += 1;
            if (bankerScore1 < otherScore1) result -= 1;
            if (bankerScore2 >= otherScore2) result += 1;
            if (bankerScore2 < otherScore2) result -= 1;
            //庄家赢
            if (result > 0) {
                int changeScore = other.getBetScore(bankerScore2 >= mix8Score);
                banker.addScore(this.roomPaijiu(), changeScore);
                other.addScore(this.roomPaijiu(), -changeScore);
                this.roomPaijiu().addUserSocre(banker.userId(), changeScore);
                this.roomPaijiu().addUserSocre(other.userId(), -changeScore);
                other.setWinState(1);
            } else if (result < 0) {
                //闲家赢
                int changeScore = other.getBetScore(otherScore2 >= mix8Score);
                banker.addScore(this.roomPaijiu(), -changeScore);
                other.addScore(this.roomPaijiu(), changeScore);
                this.roomPaijiu().addUserSocre(banker.userId(), -changeScore);
                this.roomPaijiu().addUserSocre(other.userId(), changeScore);
                other.setWinState(-1);
            }
        }
        return result;
    }

    /**
     * 牌局结束,判断条件修改
     * 当庄家锅里值为0或者大于等于锅底10倍，有任意闲家金币小于10时游戏结束
     */
    @Override
    public void gameOver(){
        compute();
        sendResult();
        genRecord();
        this.roomPaijiu().clearReadyStatus(true);

        PlayerCardInfoPaijiu playerCardInfoPaijiu = playerCardInfos().get(bankerId()).get();
        if (playerCardInfoPaijiu.getScore() <= 0 || playerCardInfoPaijiu.getScore() >= this.roomPaijiu().bankerInitScore()*10) {
            sendFinalResult();
        }

        for (Long l:this.roomPaijiu().userScores.keySet()) {
            if(this.roomPaijiu().userScores.get(l)<10){
                sendFinalResult();
            }
        }

    }

    /**
     * 最终结算版
     */
    public void sendFinalResult(){


        List<UserOfResult> userOfResultList = this.roomPaijiu().getUserOfResult();
        for (UserOfResult u:userOfResultList) {
            double d = Double.parseDouble(u.getScores());
            if(u.getUserId()== roomPaijiu().getBankerId()){
                u.setScores(d+"");
                RedisManager.getUserRedisService().addUserMoney(u.getUserId(), d - this.roomPaijiu().bankerInitScore());//userId-money
            }else{
                u.setScores(d-RedisManager.getUserRedisService().getUserMoney(u.getUserId())+"");
                RedisManager.getUserRedisService().addUserMoney(u.getUserId(), d - RedisManager.getUserRedisService().getUserMoney(u.getUserId()));//userId-money
            }
            MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", 0), u.getUserId());

        }
        // 存储返回
        GameOfResult gameOfResult = new GameOfResult();
        gameOfResult.setUserList(userOfResultList);
        MsgSender.sendMsg2Player("gameService", "gamePaijiuFinalResult", gameOfResult, users);
        RoomManager.removeRoom(this.roomPaijiu().getRoomId());

        //庄家初始分 再减掉
        this.roomPaijiu().addUserSocre(this.roomPaijiu().getBankerId(), -this.roomPaijiu().bankerInitScore());

        //战绩
        this.roomPaijiu().genRoomRecord();

        /*for (Long l:this.roomPaijiu().userScores.keySet()) {
            RedisManager.getUserRedisService().setUserMoney(l, roomPaijiu().userScores.get(l));//userId-money
        }*/
    }


    //霸王抢：点击霸王抢后，无论有几名玩家选择抢庄，庄家都会是自己
    public int catchBanker(Long userId) {
        this.roomPaijiu().setBankerId(userId);
        this.setBankerId(userId);

    //通知玩家
    MsgSender.sendMsg2Player("gamePaijiuService", "chooseBanker", userId, users);
    //庄家选分
    bankerSetScoreStart();

    return 0;
    }

}
