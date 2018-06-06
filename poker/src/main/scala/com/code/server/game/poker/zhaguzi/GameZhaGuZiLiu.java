package com.code.server.game.poker.zhaguzi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/6/6.
 */
public class GameZhaGuZiLiu extends GameZhaGuZi{

    @Override
    public void initCards() {

        for (int i = 0; i < 54; i++) {
            this.cards.add(i);
        }
        //洗牌
        Collections.shuffle(this.cards);
    }

    //算分
    public void compute(boolean isOver1, boolean isOver2) {

        //这时候出牌序号不一定有
        status = ZhaGuZiConstant.COMPUTE;

        //保存头游
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {

            if (playerZhaGuZi.rank == 1) {
                this.room.lastWinnderId = playerZhaGuZi.userId;
                break;
            }
        }

        List<PlayerZhaGuZi> aList = new ArrayList<>();
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()) {
            aList.add(playerZhaGuZi);
        }

        int ret = CardUtils.findWinnerList(aList);

        if (ret == 0) {
            logger.info("三家赢");
        } else if (ret == 1) {
            logger.info("平局");
        } else {
            logger.info("股家赢");
        }

        double base = 0;

        Integer hongtaosan = CardUtils.string2Local("红桃-3", ifCard);
        Integer fangpiansan = CardUtils.string2Local("方片-3", ifCard);
        Integer heitaosan = CardUtils.string2Local("黑桃-3", ifCard);
        Integer meihuasan = CardUtils.string2Local("梅花-3", ifCard);

        if (ret != 1) {

            //先算两三数和扎股数之和

            //具体算分
            for (PlayerZhaGuZi playerZhaGuZi : aList) {

                if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {

                    if (playerZhaGuZi.getOp() == Operator.LIANG_SAN) {
                        //如果是三家
                        List<Integer> liangList = playerZhaGuZi.getLiangList();

                        if ((!liangList.contains(hongtaosan)) && (!liangList.contains(fangpiansan)) && (!liangList.contains(heitaosan))) {
                            logger.warn("亮三错误，检查talk");
                        }

                        if (liangList.contains(hongtaosan)) {
                            base += 1;
                        }

                        if (liangList.contains(fangpiansan)) {
                            base += 1;
                        }

                        if (liangList.contains(heitaosan)) {
                            base += 1;
                        }

                        if (liangList.contains(meihuasan)) {
                            base += 1;
                        }
                    }

                } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {

                    if (playerZhaGuZi.getOp() == Operator.ZHA_GU) {
                        base++;
                        //如果是股家
                        List<Integer> liangList = playerZhaGuZi.getLiangList();

                        if (liangList.contains(meihuasan)) {
                            base++;
                        }
                    }

                }
            }

            int count = 0;

            if (ret == 0) {

                for (PlayerZhaGuZi playerZhaGuZi : aList) {
                    if ((playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) && (!playerZhaGuZi.isOver())) {
                        count++;
                    }
                }
            } else {
                for (PlayerZhaGuZi playerZhaGuZi : aList) {
                    if ((playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) && (!playerZhaGuZi.isOver())) {
                        count++;
                    }
                }
            }

            logger.warn("{}家没出完, base是{}" , count, base);

            base += count;

            if (ret == 0) {
                //三家赢
                for (PlayerZhaGuZi playerZhaGuZi : aList) {

                    double score = 0;

                    if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {
//                        if (playerZhaGuZi.getRetain3List().contains(hongtaosan)) {
//                            score += base;
//                        } else if (playerZhaGuZi.getRetain3List().contains(fangpiansan)) {
//                            score += base;
//                        }else if(playerZhaGuZi.getRetain3List().contains(heitaosan)){
//                            score += base;
//                        }
                        score += base;

                    } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {
                        score -= base;
                    }

                    playerZhaGuZi.setScore(score);

                }

            } else if (ret == 2) {
                //股家赢
                for (PlayerZhaGuZi playerZhaGuZi : aList) {

                    double score = 0;

                    if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.SAN_JIA) {
//                        if (playerZhaGuZi.getRetain3List().contains(hongtaosan)) {
//                            score -= base;
//                        } else if (playerZhaGuZi.getRetain3List().contains(fangpiansan)) {
//                            score -= base;
//                        }else if (playerZhaGuZi.getRetain3List().contains(fangpiansan)){
//                            score -= base;
//                        }
                        score -= base;

                    } else if (playerZhaGuZi.getSanJia() == PlayerZhaGuZi.GU_JIA) {
                        score += base;
                    }

                    playerZhaGuZi.setScore(score);

                }
            }

        }else {
            logger.info("平局 不计算输赢");
        }

        sendGameResult(ret);
    }

}
