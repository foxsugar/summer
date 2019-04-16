package com.code.server.game.poker.playseven;

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
public class GamePlaySevenFive extends GamePlaySeven{


    @Override
    protected void compute(long winnerId) {
        {
            int allScore = kouDiBeiShu*tableCardFen+jianFen;
            int temp = 0;//正分为庄
            if(allScore>=40&&allScore<80){
                temp=1;
            }else if(allScore>=5&&allScore<40){
                temp=2;
            }else if(allScore==0){
                if(5==room.fengDing){
                    temp=5;
                }else {
                    temp=3;
                }
            }else{
                temp = -(allScore-40)/40;
            }
            if(shuangLiangDouble||fanzhu){
                if(room.zhuangDanDaJiaBei && winnerId==room.getBankerId()){
                    temp*=2;
                }
            }
            if(room.kouDiJiaJi){
                if(winnerId!=room.getBankerId()&&winnerId!=secondBanker){
                    if(allScore>=80){
                        if(1==playerCardInfos.get(winnerId).playCards.size()){
                            temp-=1;
                        }else if(2==playerCardInfos.get(winnerId).playCards.size()){
                            temp-=2;
                        }else{
                            temp-=4;
                        }
                    }else{
                        if (1 == playerCardInfos.get(winnerId).playCards.size()) {
                            temp -= 1;
                        } else if (2 == playerCardInfos.get(winnerId).playCards.size()) {
                            temp -= 2;
                        } else {
                            temp -= 4;
                        }
                    }
                }
            }
            if(5==room.fengDing){
                if(temp>5){
                    temp=5;
                }
            }else {
                if(temp>3){
                    temp=3;
                }
            }


            RoomPlaySeven roomPlaySeven = null;
            if (room instanceof RoomPlaySeven) {
                roomPlaySeven = (RoomPlaySeven) room;
            }
            for (Long l:users) {
                if(secondBanker==zhuId){
                    if(l!=secondBanker && l!=zhuId){//不是庄
                        playerCardInfos.get(l).addScore(-temp);
                        roomPlaySeven.addUserSocre(l, -temp);
                    }else {
                        playerCardInfos.get(l).addScore(4*temp);
                        roomPlaySeven.addUserSocre(l, 4*temp);
                    }
                }else {
                    if(l!=secondBanker && l!=zhuId){//不是庄
                        playerCardInfos.get(l).addScore(-temp);
                        roomPlaySeven.addUserSocre(l, -temp);
                    }else if(l==zhuId) {
                        playerCardInfos.get(l).addScore(2*temp);
                        roomPlaySeven.addUserSocre(l, 2*temp);
                    }else {
                        playerCardInfos.get(l).addScore(temp);
                        roomPlaySeven.addUserSocre(l, temp);
                    }
                }
            }
        }
    }

}
