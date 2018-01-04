package com.code.server.game.cow;

import java.util.ArrayList;
import java.util.List;

public class CowPlayer {

    public final static int TONG_HUA_SHUN = 1;
    public final static int ZHA_DAN_NIU = 2;
    public final static int WU_HUA_NIU = 3;
    public final static int WU_XIAO_NIU = 4;
    public final static int HU_LU = 5;
    public final static int TONG_HUA = 6;
    public final static int SHUN_ZI = 7;
    public final static int NIU_NIUI = 8;
    public final static int NIU_JIU  = 9;
    public final static int NIU_BA = 10;
    public final static int NIU_QI = 11;
    public final static int NIU_Liu = 12;
    public final static int NIIU_WU = 13;
    public final static int NIU_SI = 14;
    public final static int NIU_SAN = 15;
    public final static int NIU_ER = 16;
    public final static int NIU_YI = 17;
    public final static int WU_NIU = 18;

    //用户ID
    private Long id;
    //级别
    private Integer grade;
    //倍数
    private Integer times;

    private List<Integer> pokers = new ArrayList<Integer>();

    public CowPlayer(Long id, List<Integer> pokers) {
        this.id = id;
        this.pokers = pokers;
    }

    public CowPlayer() {
    }

    public List<Integer> getPokers() {
        return pokers;
    }

    public void setPokers(List<Integer> pokers) {
        this.pokers = pokers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer compareWithOtherPlayer(CowPlayer player){
             CowPlayer p =  compare(this, player);
             if (p == this){
                 return 0;
             }
             return 1;
    }

    public CowPlayer(Long id) {
        this.id = id;
    }

    public CowPlayer(Long id, Integer card1, Integer card2, Integer card3, Integer card4, Integer card5) throws Exception {

        this.pokers.add(CardUtils.transformCardValue(card1));
        this.pokers.add(CardUtils.transformCardValue(card2));
        this.pokers.add(CardUtils.transformCardValue(card3));
        this.pokers.add(CardUtils.transformCardValue(card4));
        this.pokers.add(CardUtils.transformCardValue(card5));

        Integer grade = CardUtils.getPaiXing(this.pokers);
        this.grade = grade;
        this.id = id;
    }

    public CowPlayer(Long id, Integer card1, Integer card2, Integer card3, Integer card4, Integer card5, boolean localRule) throws Exception {

        if (localRule == false){

            this.pokers.add(CardUtils.transformCardValue(card1));
            this.pokers.add(CardUtils.transformCardValue(card2));
            this.pokers.add(CardUtils.transformCardValue(card3));
            this.pokers.add(CardUtils.transformCardValue(card4));
            this.pokers.add(CardUtils.transformCardValue(card5));

        }else {

            this.pokers.add(card1);
            this.pokers.add(card2);
            this.pokers.add(card3);
            this.pokers.add(card4);
            this.pokers.add(card5);
        }

        Integer grade = CardUtils.getPaiXing(this.pokers);
        this.grade = grade;
        this.id = id;
    }

    public CowPlayer compare(CowPlayer player1, CowPlayer player2){

        if (player1.grade < player2.grade) {
            return player1;
        }else if (player1.grade > player2.grade){
            return player2;
        }else {

            //同花顺
            if (player1.grade == TONG_HUA_SHUN){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);

                boolean ret1 = CardUtils.isA2345(player1.getPokers());
                boolean ret2 = CardUtils.isA2345(player2.getPokers());

                if (ret1 == true && ret2 == false){
                    return player2;
                }else if(ret1 == false  && ret2 == true){
                    return player1;
                }else if(ret1 == true && ret2 == true){
                    //  如果都是12345
                    if (a < b){
                        return player1;
                    }
                    return player2;

                }

                if (a < b){
                    return player1;
                }
                return player2;
            }

            if (player1.grade == ZHA_DAN_NIU){

                Integer a = player1.getPokers().get(1);
                Integer b = player2.getPokers().get(1);

                //直接比较炸弹的大小
                if (a < b){
                    return player1;
                }
                return player2;
            }

            if (player1.grade == WU_HUA_NIU){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);

                if (a < b){
                    return player1;
                }
                return player2;

            }

            if (player1.grade == WU_XIAO_NIU){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);

                if (a < b){
                    return player1;
                }
                return player2;

            }

            if (player1.grade == HU_LU){

                Integer a = player1.getPokers().get(2);
                Integer b = player2.getPokers().get(2);
                //取出对子

                if (a < b){
                    return player1;
                }

                return player2;
            }

            if (player1.grade == TONG_HUA){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);
                if (a < b){
                    return player1;
                }

                return player2;

            }

            if (player1.grade == SHUN_ZI){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);

                boolean ret1 = CardUtils.isA2345(player1.getPokers());
                boolean ret2 = CardUtils.isA2345(player2.getPokers());

                if (ret1 == true && ret2 == false){
                    return player2;
                }else if(ret1 == false  && ret2 == true){
                    return player1;
                }else if(ret1 == true && ret2 == true){
                    //  如果都是12345
                    if (a < b){
                        return player1;
                    }
                    return player2;

                }

                if (a < b){
                    return player1;
                }
                return player2;
            }

            if (player1.grade == NIU_NIUI){

                Integer a = player1.getPokers().get(0);
                Integer b = player2.getPokers().get(0);
                //取出对子
                if (a < b){
                    return player1;
                }

                return player2;
            }

            //剩下的都是 牛 - X
            Integer a = player1.getPokers().get(0);
            Integer b = player2.getPokers().get(0);

            if (a < b){
                return player1;
            }

            return player2;
        }
    }

    @Override
    public String toString() {

        String str = "";
        for (Integer i = 0; i < pokers.size(); i++){
            Integer yushu = pokers.get(i) % 4;
            String huaSe = "";
            if (yushu == 0){
                huaSe = "{"  + "♠";
            }else if(yushu == 1){
                huaSe = "{"  + "♥";
            }else if (yushu == 2){
                huaSe = "{"  + "♣";
            }else {
                huaSe = "{"  + "♦";
            }

            String paihao = "";
            Integer deshu = pokers.get(i) / 4;
            if (deshu == 0){
                paihao = "A";
            }else if(deshu == 1){
                paihao = "K";
            }else if (deshu == 2){
                paihao = "Q";
            }else if (deshu == 3){
                paihao = "J";
            }else if(deshu == 4){
                paihao = "10";
            }else {
                paihao = " " + (14 - deshu) ;
            }
            paihao += "}";
            str  +=  huaSe + paihao;
        }

        str += " " + CardUtils.getNameWithGrade(this.grade);
        return str;

    }


//    @Override
//    public String toString() {
//        return "CowPlayer{" +
//                "id=" + id +
//                ", grade=" + grade +
//                ", times=" + times +
//                ", pokers=" + pokers +
//                '}';
//    }
}
