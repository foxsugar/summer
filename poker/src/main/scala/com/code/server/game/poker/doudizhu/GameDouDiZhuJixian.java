package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;

import java.util.Collections;
import java.util.List;

/**
 * Created by sunxianping on 2018/3/2.
 */
public class GameDouDiZhuJixian extends GameDouDiZhu{

    public void init(List<Long> users, long dizhuUser) {
        initCardNum = 16;
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoDouDiZhu playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);


        shuffle();
        deal();
        //第一局 第一个玩家做地主
        dizhuUser = dizhuUser != 0 ? dizhuUser : users.get(0);
        chooseDizhu(dizhuUser);
    }



    @Override
    protected void shuffle(){
        for(int i=1;i<=55;i++){//多个癞子
            cards.add(i);
        }
        //去掉两张2
        cards.remove((Integer)7);
        cards.remove((Integer)5);
        Collections.shuffle(cards);
    }

    protected void compute(boolean isDizhuWin) {

        if (isSpring) {
            this.zhaCount += 1;
        }
        double subScore = 0;
        int s = isDizhuWin ? -1 : 1;
        multiple *= tableScore;
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
        if (playerCardInfoDizhu.isQiang()) {
            multiple *= 2;
        }
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            //不是地主 扣分
            if (dizhu != playerCardInfo.getUserId()) {
//                double score = multiple * s;
//                if (playerCardInfo.isQiang()) {
//                    score *= 2;
//                }
                double score = computeScore(playerCardInfo) * s * tableScore;
                subScore += score;
                playerCardInfo.setScore(score);
                room.addUserSocre(playerCardInfo.getUserId(), score);
            }
        }

        playerCardInfoDizhu.setScore(-subScore);
        room.addUserSocre(dizhu, -subScore);

    }



    private int computeScore(PlayerCardInfoDouDiZhu playerCardInfo){
        int result = 0;
        int tempZha = zhaCount;
        if (playerCardInfo.isQiang()) {
            tempZha += 1;
        }
        result = 1 << tempZha;
        //不封顶 大于5炸
        if(room.getMultiple() == -1 ){
            if (tempZha > 5) {
                int more = tempZha - 5;
                result = 1<<5 + more * 5;
            }
        }else {
            if(tempZha > this.room.getMaxZhaCount()){
                result = 1 << this.room.getMaxZhaCount();
            }
    }

        return result;
    }

    protected void computeBomb(CardStruct cardStruct){
        if(cardStruct.getType()==CardStruct.type_炸){
            List<Integer> cards = cardStruct.getCards();
            if(cards.size()==4 && CardUtil.getTypeByCard(cards.get(0)) == 0 && CardUtil.getTypeByCard(cards.get(cards.size()-1))==0){ //3333
                zhaCount += 3;//记录炸的数量
                multiple *= 8;//记录倍数
            }else{ //除4个三的炸
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            }
        }else if(cardStruct.getType()==CardStruct.type_火箭){
            zhaCount += 1;//记录炸的数量
            multiple *= 2;//记录倍数
        }
    }
    @Override
    protected void handleBomb(CardStruct cardStruct){
        if(zhaCount < room.getMultiple() || room.getMultiple() == -1){
            computeBomb(cardStruct);
        }
    }

}
