package com.code.server.game.poker.pullmice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardUtils {

    //发牌之前先计算排序id
    public static void calListPxId(List<PlayerPullMice> list,  List<Long> users_){
        PlayerPullMice player = list.get(0);
        //第一轮发牌
        if (player.getCards().size() == 0){
            for (int i = 0; i < users_.size(); i++){
                for (PlayerPullMice playerPullMice : list){
                    if (playerPullMice.getUserId() == users_.get(i)){
                        playerPullMice.setPxId(i + 1);
                        break;
                    }
                }
            }
        }else if (player.getCards().size() == 1){
            for (int i = 0; i < users_.size(); i++){
                for (PlayerPullMice playerPullMice : list){
                    if (playerPullMice.getUserId() == users_.get(i)){
                        playerPullMice.setPxId(i + 1);
                        break;
                    }
                }
            }
        }else {
            for (int i = 0; i < list.size() - 1; i++){
                for (int j = i + 1; j < list.size(); j++){
                    PlayerPullMice pA = list.get(i);
                    PlayerPullMice pB = list.get(j);

                    if (pA.isEscape() && (!pB.isEscape())){
                        Collections.swap(list, i, j);
                        continue;
                    }

                    if ((!pA.isEscape()) && pB.isEscape()){
                        continue;
                    }

                    if (pA.isEscape() && pB.isEscape()){
                        continue;
                    }

                    int vA = (pA.getCards().get(list.size() -1) - 2) / 4;
                    int vB = (pB.getCards().get(list.size() -1) - 2) / 4;
                    if (vA > vB){
                        Collections.swap(list, i, j);
                    }else if(vA == vB){
                        //看座位号码
                        int seatIdxA = users_.indexOf(pA.getUserId());
                        int seatInxB = users_.indexOf(pB.getUserId());
                        if (seatIdxA > seatInxB){
                            Collections.swap(list, i, j);
                        }
                    }
                }
            }
            for (int i = 0; i < list.size(); i++){
                PlayerPullMice playerPullMice = list.get(i);
                playerPullMice.setPxId(i + 1);
            }
        }
    }

    /*
    * 算点
    * */
    public static int calculatePoint(Integer card){

        if (card == 0){
            return 20;
        }else if(card == 1){
            return 17;
        }else if((card - 2) / 4 == 1){
            return 15;
        }else if((card - 2) / 4 == 2){
            return 13;
        }else {
            return 15 - ((card - 2) / 4);
        }
    }

    /*
    * */
    public static boolean is3Xor4X(List<Integer> list){
        List<Integer> aList = new ArrayList<>();
        aList.addAll(list);
        Collections.sort(aList);

        Integer center = aList.get(2);
        int count = 0;
        for (int i = 0; i < aList.size(); i++){
            if ((center - 2) / 4 == (aList.get(i) - 2) / 4){
                count++;
            }
        }
        return count > 2;
    }

    //是不是顺子
    public static boolean isShunZi(List<Integer> list){

        List<Integer> aList = new ArrayList<>();
        aList.addAll(list);
        Collections.sort(aList);

        if (aList.get(0) == 0 || aList.get(0) == 1 || aList.get(1) == 0 || aList.get(1) == 1){
            return false;
        }

        Integer last = aList.get(0);
        for (int i = 1; i < aList.size(); i++){
            Integer current = aList.get(i);
            if ( ( current - 2) / 4 - (last - 2) / 4 != 0){
                return false;
            }
            last = current;
        }

        return true;
    }

    //比较两个顺子的大小
    public static int shunAisBiggerTanShunB(PlayerPullMice playerA, PlayerPullMice playerB){

        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        aList.addAll(playerA.getCards());
        bList.addAll(playerB.getCards());

        Collections.sort(aList);
        Collections.sort(bList);

        if ((aList.get(0) - 2) / 4 > (bList.get(0) - 2) / 4){
            return 0;
        }else if((aList.get(0) - 2) / 4 < (bList.get(0) - 2) / 4){
            return 2;
        }else {
            return 1;
        }
    }

    public static int calculateTotalPoint(List<Integer> list){

        int point = 0;
        for (Integer card : list){
            point += calculatePoint(card);
        }

        //第五张牌发完以后才计算
        if (list.size() == 5 && is3Xor4X(list)){
            point += 30;
        }
        return point;
    }

    public static int compare(PlayerPullMice playerPullMice1, PlayerPullMice playerPullMice2){

        if (playerPullMice1.isEscape() && (!playerPullMice2.isEscape())){
            return 2;
        }
        if ((!playerPullMice1.isEscape()) && playerPullMice2.isEscape()){
            return 0;
        }
        if (playerPullMice1.isEscape() && playerPullMice2.isEscape()){
            return 1;
        }

        playerPullMice1.setPoint(calculateTotalPoint(playerPullMice1.getCards()));
        playerPullMice2.setPoint(calculateTotalPoint(playerPullMice2.getCards()));

        if (!(isShunZi(playerPullMice1.getCards())) && isShunZi(playerPullMice2.getCards())){
            return 2;
        }else if(isShunZi(playerPullMice1.getCards()) && (!isShunZi(playerPullMice2.getCards()))){
            return 0;
        }else if ((isShunZi(playerPullMice1.getCards())) && isShunZi(playerPullMice2.getCards())){
            return shunAisBiggerTanShunB(playerPullMice1, playerPullMice2);
        }

        int ret = playerPullMice1.getPoint() > playerPullMice2.getPoint() ? 0 : (playerPullMice1.getPoint() < playerPullMice2.getPoint() ? 2 : 1);
        return ret;
    }

    public static PlayerPullMice findWinner(List<PlayerPullMice> list){

        for (int i = 0; i < list.size() - 1; i ++){
            for (int j = i + 1; j < list.size(); j++){
                int compareRet = compare(list.get(i), list.get(j));
                if (compareRet == 2){
                    Collections.swap( list, i, j);
                }else if(compareRet == 1){
                    if (list.get(i).getPxId() < list.get(j).getPxId()){
                        Collections.swap( list, i, j);
                    }
                }
            }
        }
        return list.get(0);
    }

}
