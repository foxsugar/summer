package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunxianping on 2018/8/14.
 */
public class PlayerCardsInfoHongZhong extends PlayerCardsInfoZhuohaozi {
    public static final int DA_HU = 1;
    public static final int HAS_HONGZHONG = 2;
    public static final int HUN_RAND = 3;
    public static final int HUN_NO = 4;
    public static final int NO_FENG = 5;

    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.clear();
        if (isHasMode(this.roomInfo.mode, DA_HU)) {

            specialHuScore.put(hu_七小对, 9);
            specialHuScore.put(hu_豪华七小对, 18);
            specialHuScore.put(hu_双豪七小对_山西, 18);

            specialHuScore.put(hu_清一色, 9);
            specialHuScore.put(hu_一条龙, 9);
            specialHuScore.put(hu_清龙, 9);

        }


        this.TING_MIN_SCORE = 0;
        this.ZIMO_MIN_SCORE = 0;
        this.DIANPAO_MIN_SCORE = 0;
    }


    @Override
    public boolean isCanHu_dianpao(String card) {
        if( this.roomInfo.mustZimo == 1){
            return false;
        }

        if (!isTing && this.roomInfo.haveTing) return false;
        //混牌 不能点炮
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (this.gameInfo.hun.contains(cardType)) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int lastCard = CardTypeUtil.getTypeByCard(card);
        List<HuCardType> huList = HuUtil.isHu(this, noPengAndGang, getChiPengGangNum(), this.gameInfo.hun, lastCard);
        for (HuCardType huCardType : huList) {
            if (getMaxPoint(huCardType, true) >= DIANPAO_MIN_SCORE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (!isTing && this.roomInfo.haveTing) return false;
        int lastCard = CardTypeUtil.getTypeByCard(card);

        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), getChiPengGangNum(), this.gameInfo.hun, lastCard);
        for (HuCardType huCardType : huList) {
            if (getMaxPoint(huCardType, false) >= ZIMO_MIN_SCORE) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean isCanTing(List<String> cards) {
        if (!this.roomInfo.haveTing) {
            return false;
        }
        if (isTing) {
            return false;
        }

        List<HuCardType> huCardTypes = getTingHuCardTypeWithHun(getCardsNoChiPengGang(cards), this.gameInfo.hun, this.getChiPengGangNum(), integer -> {
//            if (integer > TING_MIN_SCORE || gameInfo.hun.contains(integer)) {
                return true;
//            }
//            return false;
        });
        for (HuCardType huCardType : huCardTypes) {
            int point = getMaxPoint(huCardType, false);
            if (point >= TING_MIN_SCORE) {
                return true;
            }

        }
        return false;
    }

    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        int allScore = 0;
        int score = 0;
        if (diangangUser != -1) {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(diangangUser);
            score = (this.roomInfo.getPersonNumber() - 1) * this.roomInfo.getMultiple();
            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(diangangUser, -score);
            allScore += score;
        } else {
            score = isMing ? this.roomInfo.getMultiple() : 2 * this.roomInfo.getMultiple();
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.userId != this.userId) {
                    playerCardsInfoMj.addScore(-score);
                    playerCardsInfoMj.addGangScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allScore += score;
                }
            }
        }
        this.addScore(allScore);
        this.addGangScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);
    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        int lastCard = CardTypeUtil.getTypeByCard(card);
        int chiPengGangNum = getChiPengGangNum();
        List<HuCardType> huList = HuUtil.isHu(this, getCardsNoChiPengGang(this.cards), chiPengGangNum, this.gameInfo.hun, lastCard);


        HuCardType huCardType = getMaxScoreHuCardType(huList);
        int score = huCardType.fan;

        this.winType.addAll(huCardType.specialHuList);
        if (score == 0) {
            score = isZimo?2:3;
        }

        if (isZimo && huCardType.fan <= 9 && isHasMode(this.roomInfo.mode, HAS_HONGZHONG) && isHas4Hongzhong()) {
            this.winType.add(hu_四个红中);
            score = 10;
        }

        score *= this.roomInfo.getMultiple();
        int allScore = 0;
        if (isZimo) {
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                if (playerCardsInfoMj.userId != this.userId) {
                    playerCardsInfoMj.addScore(-score);
                    this.roomInfo.addUserSocre(playerCardsInfoMj.getUserId(), -score);
                    allScore += score;
                }
            }
        } else {
            PlayerCardsInfoMj dianPao = this.gameInfo.getPlayerCardsInfos().get(dianpaoUser);

            dianPao.addScore(-score);
            this.roomInfo.addUserSocre(dianpaoUser, -score);
            allScore += score;
        }

        this.addScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);


    }

    private boolean isHas4Hongzhong() {
        return this.cards.stream().filter(card -> CardTypeUtil.getTypeByCard(card) == 31).count() == 4;
    }




    public static void main(String[] args) {

//        PlayerCardsInfoSZ_LQ playerCardsInfo = new PlayerCardsInfoSZ_LQ();
//        PlayerCardsInfoTDH playerCardsInfo = new PlayerCardsInfoTDH();
        PlayerCardsInfoHongZhong playerCardsInfo = new PlayerCardsInfoHongZhong();
//        playerCardsInfo.isHasFengShun = true;

//        playerCardsInfo.isHasYimenpai = true;
//        playerCardsInfo.isHasShuye = true;
//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","042","124","135"};
//        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","134","042","124","135"};
        String[] s = new String[]{"004","005","006","007","008","009", "044","045",  "064","065",  "068","100","101","124"};
//        String[] s = new String[]{"000","001","002",     "036",  "037","038",    "052", "053","054",    "080","081","082",    "112","113"};

//        String[] s = new String[]{"076","077","078",     "080",  "084","085",    "088", "089","090",    "092","093","096",    "100","101"};


        List<Integer> hun = new ArrayList<>();
        hun.add(31);
//        hun.add(31);
//        hun.add(1);
//        hun.add(8);


        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("1023");
        GameInfoNew gameInfoTJ = new GameInfoNew();
        gameInfoTJ.hun = hun;
        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.setGameInfo(gameInfoTJ);

        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.init(playerCardsInfo.cards);
        List<String> list = Arrays.asList(s);
        playerCardsInfo.cards.addAll(list);
//        System.out.println("!!!!!!!!!!!!"+HuUtil.isHu(list,playerCardsInfo,0,new HuLimit(0)));
//        playerCardsInfo.anGangType.add(24);
//        playerCardsInfo.mingGangType.put(0,1);
//        playerCardsInfo.pengType.put(0,1);
//        playerCardsInfo.pengType.put(13,1);
//        playerCardsInfo.isTing = true;
//        playerCardsInfo.pengType.put(0,1);
//        playerCardsInfo.pengType.put(19,1);



//        playerCardsInfo.pengType.put(25,1);

//        playerCardsInfo.isTing = true;
//        List<String> temp = new ArrayList<>();
//        temp.addAll(playerCardsInfo.cards);
////        playerCardsInfo.isTing = true;
//        playerCardsInfo.tingSet = new HashSet<>();
//        playerCardsInfo.tingSet.add(15);
        System.out.println(playerCardsInfo.isCanHu_zimo("124"));
//        System.out.println("==="+playerCardsInfo.getYiZhangYingSet(playerCardsInfo.getCardsNoChiPengGang(temp),null));
//        System.out.println(playerCardsInfo.isCanHu_zimo("113"));
//        System.out.println(playerCardsInfo.isCanTing(playerCardsInfo.cards));
//        List<String> cs = playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.getCards());
//        int cardType = CardTypeUtil.getTypeByCard("060");
//        HuUtil.isHu(cs, playerCardsInfo, CardTypeUtil.cardType.get("060"), new HuLimit(3));
//        System.out.println(HuUtil.isHu(playerCardsInfo.getCards(), playerCardsInfo, null));
//        System.out.println(HuUtil.isHu(playerCardsInfo.cards,playerCardsInfo,new HuLimit(6)));
//        playerCardsInfo.huCompute();
    }
}
