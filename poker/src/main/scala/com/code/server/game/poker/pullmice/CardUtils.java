package com.code.server.game.poker.pullmice;

import java.util.*;

public class CardUtils extends BaseCardUtils{

    private static Map<Integer, Integer> cardDict = new HashMap<>();

    public static Map<Integer, Integer> getCardDict() {
        if (cardDict.size() == 0){
            cardDict.put(0, 54);
            cardDict.put(1, 53);

            cardDict.put(2, 1);
            cardDict.put(3, 2);
            cardDict.put(4, 3);
            cardDict.put(5, 4);

            cardDict.put(6, 49);
            cardDict.put(7, 50);
            cardDict.put(8, 51);
            cardDict.put(9, 52);

            cardDict.put(10, 45);
            cardDict.put(11, 46);
            cardDict.put(12, 47);
            cardDict.put(13, 48);

            cardDict.put(14, 41);
            cardDict.put(15, 42);
            cardDict.put(16, 43);
            cardDict.put(17, 44);

            cardDict.put(18, 37);
            cardDict.put(19, 38);
            cardDict.put(20, 39);
            cardDict.put(21, 40);

            cardDict.put(22, 33);
            cardDict.put(23, 34);
            cardDict.put(24, 35);
            cardDict.put(25, 36);

            cardDict.put(26, 29);
            cardDict.put(27, 30);
            cardDict.put(28, 31);
            cardDict.put(29, 32);

            cardDict.put(30, 25);
            cardDict.put(31, 26);
            cardDict.put(32, 27);
            cardDict.put(33, 28);

            cardDict.put(34, 21);
            cardDict.put(35, 22);
            cardDict.put(36, 23);
            cardDict.put(37, 24);

            cardDict.put(38, 17);
            cardDict.put(39, 18);
            cardDict.put(40, 19);
            cardDict.put(41, 20);

            cardDict.put(42, 13);
            cardDict.put(43, 14);
            cardDict.put(44, 15);
            cardDict.put(45, 16);

            cardDict.put(46, 9);
            cardDict.put(47, 10);
            cardDict.put(48, 11);
            cardDict.put(49, 12);

            cardDict.put(50, 5);
            cardDict.put(51, 6);
            cardDict.put(52, 7);
            cardDict.put(53, 8);
        }
        return cardDict;
    }


    public static Integer transformSingleCard2ClientCard(Integer card){
        return CardUtils.getCardDict().get(card);
    }

    public static List<Integer> transformLocalCards2ClientCards(List<Integer> aList){

        List<Integer> list = new ArrayList<>();

        for (Integer card : aList){
            Integer value = CardUtils.getCardDict().get(card);
            list.add(value);
        }
        return list;
    }

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

            for (int i = 0; i < list.size() - 1; i++){
                for (int j = i + 1; j < list.size(); j++){
                    if (list.get(i).getPxId() > list.get(j).getPxId()){
                        Collections.swap(list, i, j);
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

            for (int i = 0; i < list.size() - 1; i++){
                for (int j = i + 1; j < list.size(); j++){
                    if (list.get(i).getPxId() > list.get(j).getPxId()){
                        Collections.swap(list, i, j);
                    }
                }
            }

        }else {

            //拥有最大点数的人
            int vMin = 10000;
            int index = 0;

            for (int i = 0; i < list.size(); i++){
                PlayerPullMice pA = list.get(i);
                if (pA.isEscape()){
                    continue;
                }
                int vA = 0;
                int cardValue = pA.getCards().get(pA.getCards().size() -1);
                if (pA.getCards().size() == 5){
                    cardValue = pA.getCards().get(pA.getCards().size() -2);
                }
                if (cardValue == 0 ){
                    vA = -2;
                }else if(cardValue == 1){
                    vA = -1;
                }else {
                    vA = (cardValue - 2) / 4;
                }

                if (vMin > vA){
                    vMin = vA;
                }
            }

            //如果点数相同，那么就按照上一轮的发牌顺序找到first
          List<PlayerPullMice> aList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++){
                PlayerPullMice pA = list.get(i);
                if (pA.isEscape()){
                    continue;
                }
                int vA = 0;
                int cardValue = pA.getCards().get(pA.getCards().size() -1);
                if (pA.getCards().size() == 5){
                    cardValue = pA.getCards().get(pA.getCards().size() -2);
                }
                if (cardValue == 0 ){
                    vA = -2;
                }else if(cardValue == 1){
                    vA = -1;
                }else {
                    vA = (cardValue - 2) / 4;
                }
                if (vA == vMin){
                    aList.add(pA);
                }
            }
            PlayerPullMice pIndex = aList.get(0);

            for (int i = 1; i < aList.size(); i++){
                PlayerPullMice pCurrent = aList.get(i);
                if (pIndex.getPxId() > pCurrent.getPxId()){
                    pIndex = pCurrent;
                }
            }

            pIndex.setPxId(1);

            long currentUid = pIndex.getUserId();

            while (true){

                long nextId = nextUserId(users_, currentUid);
                if (nextId == pIndex.getUserId()){
                    break;
                }

                PlayerPullMice pCurrent = null;
                PlayerPullMice pNext = null;
                for (PlayerPullMice pp : list){
                    if (pp.getUserId() == nextId){
                        pNext = pp;
                    }else if(pp.getUserId() == currentUid){
                        pCurrent = pp;
                    }
                }
                pNext.setPxId(pCurrent.getPxId() + 1);
                currentUid = nextId;

            }

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

                    if (pA.getPxId() > pB.getPxId()){
                        Collections.swap(list, i, j);
                    }
                }
            }

            //重新设置pxId
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
        }else if((card - 2) / 4 == 0){
            return 15;
        }else if((card - 2) / 4 == 1){
            return 13;
        }else {
            return 14 - ((card - 2) / 4);
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

            if (aList.get(i) - 2  < 0){
                continue;
            }

            if ((center - 2) / 4 == (aList.get(i) - 2) / 4){
                count++;
            }
        }
        return count > 2;
    }

    public static boolean is12345(List<Integer> list){
        if (list.size() != 5){
            return false;
        }
        
        List<Integer> aList = new ArrayList<>();
        aList.addAll(list);
        Collections.sort(aList);
        boolean is12345 = true;
        //判断第一张牌是不是A

        if (((aList.get(0) - 2) / 4 == 0)){
            Integer last = aList.get(1);

            if ((last - 2) / 4 != 9){
                return false;
            }

            for (int i = 2; i < aList.size(); i++){
                Integer current = aList.get(i);
                if ((current - 2) / 4 - (last - 2) / 4 != 1){
                    is12345 = false;
                    break;
                }
                last = current;
            }
        }else {
            return false;
        }

        return is12345;
    }

    //是不是顺子
    public static boolean isShunZi(List<Integer> list){

        List<Integer> aList = new ArrayList<>();
        aList.addAll(list);
        Collections.sort(aList);

        if (aList.get(0) == 0 || aList.get(0) == 1 || aList.get(1) == 0 || aList.get(1) == 1){
            return false;
        }

        if (is12345(aList)){
            return true;
        }

        Integer last = aList.get(0);
        for (int i = 1; i < aList.size(); i++){
            Integer current = aList.get(i);
            if ( ( current - 2) / 4 - (last - 2) / 4 != 1){
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

        boolean ret1 = is12345(aList);
        boolean ret2 = is12345(bList);

        if (ret1 == true && ret2 == false){
            return 2;
        }else if(ret1 == false && ret2 == true){
            return 0;
        }else if(ret1 && ret2){
            return 1;
        }

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

        boolean ret1 = isShunZi(playerPullMice1.getCards());
        boolean ret2 = isShunZi(playerPullMice2.getCards());

//        if ((ret1 == false && ret2 == true){
//            return 2;
//        }else if(isShunZi(playerPullMice1.getCards()) && (!isShunZi(playerPullMice2.getCards()))){
//            return 0;
//        }else if ((isShunZi(playerPullMice1.getCards())) && isShunZi(playerPullMice2.getCards())){
//            return shunAisBiggerTanShunB(playerPullMice1, playerPullMice2);
//        }

        if (ret1 == false && ret2 == true){
            return 2;
        }else if(ret1 == true && ret2 == false){
            return 0;
        }else if(ret1 && ret2){
            return shunAisBiggerTanShunB(playerPullMice1, playerPullMice2);
        }

        int ret = playerPullMice1.getPoint() > playerPullMice2.getPoint() ? 0 : (playerPullMice1.getPoint() < playerPullMice2.getPoint() ? 2 : 1);
        return ret;
    }

    public static List<PlayerPullMice> findWinnerList(List<PlayerPullMice> list){

        for (int i = 0; i < list.size() - 1; i ++){
            for (int j = i + 1; j < list.size(); j++){
                int compareRet = compare(list.get(i), list.get(j));
                if (compareRet == 2){
                    Collections.swap( list, i, j);
                }else if(compareRet == 1){

                    //如果点数相等，并且是无不封，再处理平手的情况
                    boolean isWuBuFeng = false;
                    for (int k = 0; k < list.size(); k++){
                        PlayerPullMice p = list.get(k);

                        if (p.getBetList().size() >= 4){
                            if (p.getBetList().get(3).getZhu() == Bet.WU_BU_FENG){
                                isWuBuFeng = true;
                                break;
                            }
                        }

                    }

                    if (isWuBuFeng){

                        PlayerPullMice pI = list.get(i);
                        PlayerPullMice pJ = list.get(j);

                        if (pI.isEscape() && pJ.isEscape()){
                            if (list.get(i).getPxId() > list.get(j).getPxId()){
                                Collections.swap( list, i, j);
                            }
                        }else{

                            if (pJ.isAlreadyFeng() == true){
                                Collections.swap( list, i, j);
                            }
                        }

                    }else if (list.get(i).getPxId() > list.get(j).getPxId()){
                        Collections.swap( list, i, j);
                    }
                }
            }
        }
        return list;
    }

    public static PlayerPullMice findWinner(List<PlayerPullMice> list){
        return findWinnerList(list).get(0);
    }

    public static long nextUserId(List<Long> users,Long currentId){
        int index = users.indexOf(currentId);
        int next = index + 1;
        if (next >= users.size()){
            next = 0;
        }
        return users.get(next);
    }

}
