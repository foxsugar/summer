package com.code.server.game.poker.doudizhu;



/**
 * Created by sunxianping on 2017/4/6.
 */
public class GameDouDiZhuLinFenGold extends GameDouDiZhuLinFen {



    @Override
    protected void compute(boolean isDizhuWin){

//        double subScore = 0;
//        //地主
//        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
//        if (playerCardInfoDizhu.isQiang()) {
//            multiple *= 2;
//        }
//        User user_dizhu = this.room.getUserMap().get(dizhu);
//
//        //地主赢
//        if (isDizhuWin) {
//            for(PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()){
//                //不是地主 扣分
//                if(dizhu != playerCardInfo.getUserId()){
//                    User user_nm = this.room.getUserMap().get(playerCardInfo.getUserId());
//                    double score = multiple * room.getGoldRoomType() ;
//                    if (playerCardInfo.isQiang()) {
//                        score *=2;
//                    }
//                    score = user_nm.getMoney() >= score ? score : user_nm.getMoney();
//                    subScore += score;
//                    playerCardInfo.setScore(-score);
//                    room.addUserSocre(playerCardInfo.getUserId(),-score);
//                    user_nm.setMoney(user_nm.getMoney() - score);
//                }
//            }
//            playerCardInfoDizhu.setScore(subScore);
//            room.addUserSocre(dizhu,subScore);
//            user_dizhu.setMoney(user_dizhu.getMoney() + subScore);
//            //抽成 返利
//            double rebateNum = getRebateNum(subScore);
//            user_dizhu.setMoney(user_dizhu.getMoney() - rebateNum);
//            sendRpc(rebateNum);
//
//        } else {//地主输
//            int part = 0;
//            for(PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()){
//
//                if(dizhu != playerCardInfo.getUserId()){
//                    part += 1;
//                    double score = multiple * room.getGoldRoomType() ;
//                    if (playerCardInfo.isQiang()) {
//                        score *=2;
//                        part += 1;
//                    }
//                    subScore += score;
//                }
//            }
//
//            //要扣除的分
//            subScore = user_dizhu.getMoney()>=subScore?subScore:user_dizhu.getMoney();
//
//            double rebateSum = 0;
//            for(PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()){
//
//                //不是地主 扣分
//                if(dizhu != playerCardInfo.getUserId()){
//                    User user_nm = this.room.getUserMap().get(playerCardInfo.getUserId());
//                    double score;
//                    if (playerCardInfo.isQiang()) {
//                        score = subScore * 2 /part;
//                    } else {
//                        score = subScore * 1 /part;
//                    }
//                    playerCardInfo.setScore(score);
//                    room.addUserSocre(playerCardInfo.getUserId(),score);
//                    user_nm.setMoney(user_nm.getMoney() + score);
//
//                    //抽成
//                    double rebateNum = getRebateNum(score);
//                    user_nm.setMoney(user_nm.getMoney() - rebateNum);
//                    rebateSum += rebateNum;
//
//                }
//            }
//            playerCardInfoDizhu.setScore(-subScore);
//            room.addUserSocre(dizhu,-subScore);
//            user_dizhu.setMoney(user_dizhu.getMoney() - subScore);
//
//            //send rpc
//            sendRpc(rebateSum);
//        }
    }

    private double getRebateNum(double score) {
        if (this.room.getGoldRoomType() == 0.2) {
            return 0.03;
        } else if (this.room.getGoldRoomType() == 10) {
            return score * 5 /100;
        } else {
            return score * 3 /100;
        }
    }

    private void sendRpc(double num){
//        List<Rebate> list = new ArrayList<Rebate>();
//        for (User user : room.getUserMap().values()) {
//            Rebate rebate = new Rebate();
//            rebate.setRefereeId(user.getReferee());
//            rebate.setRebateNum(num / 3);
//            rebate.setTime(System.currentTimeMillis());
//            list.add(rebate);
//        }
//        RpcManager.getInstance().sendRpcRebat(list);
    }


}
