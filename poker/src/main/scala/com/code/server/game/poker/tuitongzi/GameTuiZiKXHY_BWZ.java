package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/24.
 */
/*
* 推筒筒 同点庄赢
* */
public class GameTuiZiKXHY_BWZ extends GameTuiZiKXHY{

    protected boolean isBaWangZhuang(){
        return true;
    }

    //偏移量， 上来要在锅里放 多少钱
    protected long offset(){
        return 0;
    };

    //霸王庄的情况下是否强制下装
    protected boolean isForceUpdateBanker(){
        return false;
    }

    //到了固定局数是否要提示换庄
    protected boolean isNoticeUpdateBWZhuang(){
        return false;
    }

    /*
   * 下注
   * */
    public int bet(Long userId, Integer zhu){
        PlayerTuiTongZi playerTuiTongZi1 = playerCardInfos.get(userId);
        //玩家不存在
        if (playerTuiTongZi1 == null) return ErrorCode.NO_USER;
        //已经下过注
        if (playerTuiTongZi1.getBet() != null) return ErrorCode.ALREADY_BET;

        Bet bet = new Bet();
        bet.setZhu(zhu);
        playerTuiTongZi1.setBet(bet);

        Map result = new HashMap();
        result.put("userId", userId);

        long ret = zhu - Bet.STATE_FREE_BET;

        Map<String, Long> res = new HashMap<>();
        res.put("userId", userId);
        res.put("ret", ret);

        MsgSender.sendMsg2Player(serviceName, "betResult", res, users);
        MsgSender.sendMsg2Player(serviceName, "bet", "0" , userId);

        int count = 0;
        for (Long l : users){
            if (l != this.bankerId){
                PlayerTuiTongZi p = playerCardInfos.get(l);
                if (p.getBet() != null){
                    count++;
                }
            }
        }

        if (count == (users.size() - 1)){
            crapStart();
        }
        updateLastOperateTime();
        return 0;
    }

    //推筒筒 算分
    public void compute(Long firstId) throws Exception {

        Integer i = users.indexOf(bankerId);
        PlayerTuiTongZi playerZhuang = playerCardInfos.get(bankerId);

        List<PlayerTuiTongZi> winnerList = new ArrayList<>();
        List<PlayerTuiTongZi> loserList = new ArrayList<>();
        List<PlayerTuiTongZi> players = new ArrayList<>();
        Integer k = users.indexOf(firstId);

        //所有赢的玩家按照发牌顺序排序用来计算得分
        for (int j = 0; j < users.size(); j++){
            PlayerTuiTongZi player = playerCardInfos.get(users.get(j));
            if (player.getUserId() != bankerId){
                if (!TuiTongZiCardUtils.zhuangIsBiggerThanXian(playerZhuang, player)){
                    player.setWinner(true);
                }
            }

            if (j == k){
                player.setPxId(0);
            }else if(j > k){
                player.setPxId(j - k);
            }else {
                player.setPxId(j + k);
            }

            players.add(player);
        }

        for (int j = 0; j < players.size() - 1; j++){

            for (int w = j + 1; w < players.size(); w++){

                if (TuiTongZiCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 2){

                    Collections.swap(players, j, w);
                }else if(TuiTongZiCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 1){

                    if (players.get(j).getPxId() > players.get(w).getPxId()){
                        Collections.swap(players, j, w);
                    }
                }
            }
        }

        for (int j = 0; j < players.size(); j++){
            if (players.get(j).getUserId() != bankerId){
                if (players.get(j).isWinner()){
                    winnerList.add(players.get(j));
                }else {
                    loserList.add(players.get(j));
                }
            }
        }

        long lastGuoDi = 0;
        long currentGuoDi = lastGuoDi;
        for (PlayerTuiTongZi loser : loserList){
            long delta = loser.getBet().getZhu() - Bet.STATE_FREE_BET;
            loser.setScore(loser.getScore() - delta);
            currentGuoDi += delta;
            // 把分数加到room里
            room.addUserSocre(loser.getUserId(), loser.getScore());
        }

        for (PlayerTuiTongZi winner : winnerList){

            long delta = winner.getBet().getZhu() - Bet.STATE_FREE_BET;
            boolean isQiDui = TuiTongZiCardUtils.isDuiZi(winner.getPlayerCards());
            if (isQiDui){
                delta = delta * 2;
            }
            currentGuoDi = currentGuoDi - delta;
            playerZhuang.setScore(playerZhuang.getScore() + delta);
            winner.setScore(winner.getScore() + delta);
            room.addUserSocre(winner.getUserId(), winner.getScore());
        }

        this.room.setPotBottom(currentGuoDi);
        playerZhuang.setScore(currentGuoDi);

        //添加
        room.addUserSocre(playerZhuang.getUserId(), playerZhuang.getScore());
        this.room.setPotBottom(0);

        //原来有锅的基础上
        for (PlayerTuiTongZi p : playerCardInfos.values()){
            p.setPotBottom(0);
        }

    }
}
