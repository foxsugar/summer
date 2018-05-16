package com.code.server.game.poker.tuitongzi;

import com.code.server.game.room.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
public class GameTuiTongTong extends GameTuiTongZi {

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
                if (!TuiTongTongCardUtils.zhuangIsBiggerThanXian(playerZhuang, player)){
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

                if (TuiTongTongCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 2){

                    Collections.swap(players, j, w);
                }else if(TuiTongTongCardUtils.mAIsBiggerThanB(players.get(j), players.get(w)) == 1){

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

        long lastGuoDi = this.room.getPotBottom();
        long currentGuoDi = lastGuoDi;
        for (PlayerTuiTongZi loser : loserList){
            long delta = 0;
            if (loser.getBet().getZhu() == Bet.Wu){
                delta = 5;
            }else if(loser.getBet().getZhu() == Bet.SHI){
                delta = 10;
            }else if(loser.getBet().getZhu() == Bet.SHI_WU){
                delta = 15;
            }else if(loser.getBet().getZhu() == Bet.ER_SHI){
                delta = 20;
            }else if(loser.getBet().getZhu() == Bet.GUO_BAN){
                delta = lastGuoDi / 2;
            }else if(loser.getBet().getZhu() == Bet.MAN_ZHU){
                delta = lastGuoDi;
            }
            loser.setScore(loser.getScore() - delta);
            currentGuoDi += delta;
            // 把分数加到room里
            room.addUserSocre(loser.getUserId(), loser.getScore());
        }


        for (PlayerTuiTongZi winner : winnerList){

            if (currentGuoDi == 0){
                winner.setScore(0);
                room.addUserSocre(winner.getUserId(), winner.getScore());
                continue;
            }

            long delta = 0;
            if (winner.getBet().getZhu() == Bet.Wu){
                delta = 5;
            }else if(winner.getBet().getZhu() == Bet.SHI){
                delta = 10;
            }else if(winner.getBet().getZhu() == Bet.SHI_WU){
                delta = 15;
            }else if(winner.getBet().getZhu() == Bet.ER_SHI){
                delta = 20;
            }else if(winner.getBet().getZhu() == Bet.GUO_BAN){
                delta = lastGuoDi / 2;
            }else if(winner.getBet().getZhu() == Bet.MAN_ZHU){
                delta = lastGuoDi;
            }


            // 是不是起对周锅
            boolean isQiDui = TuiTongTongCardUtils.isDuiZi(winner.getPlayerCards());
            if (isQiDui){
                delta = delta * 1;
            }
            if (delta > currentGuoDi){
                delta = currentGuoDi;
            }
            currentGuoDi = currentGuoDi - delta;
//            playerZhuang.setScore(playerZhuang.getScore() + delta);
            winner.setScore(winner.getScore() + delta);
            room.addUserSocre(winner.getUserId(), winner.getScore());
            //假如锅里没钱就跳出别的玩家喝水
        }
        this.room.setPotBottom(currentGuoDi);
        playerZhuang.setScore(currentGuoDi - lastGuoDi);

        for (PlayerTuiTongZi p : playerCardInfos.values()){
            p.setPotBottom(currentGuoDi);
        }

    }
}
