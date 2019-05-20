package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.mahjong.util.FanUtil;
import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

import static com.code.server.game.mahjong.logic.GameInfoLongxiang.*;

/**
 * Created by sunxianping on 2018-12-14.
 */
public class PlayerCardsInfoLongxiang extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);

        this.setIsHasFengShun(true);
        this.setHasZiShun(true);
        if(isHasMode(this.roomInfo.mode,mode_幺九谱)){
            this.setHasYaojiuShun(true);
            specialHuScore.put(hu_幺九谱1, 1);
            specialHuScore.put(hu_幺九谱2, 2);
            specialHuScore.put(hu_幺九谱3, 3);
            specialHuScore.put(hu_幺九谱4, 4);
        }

        specialHuScore.put(hu_十三幺, 0);

        specialHuScore.put(hu_缺一门, 1);
        specialHuScore.put(hu_缺两门严格, 2);
        specialHuScore.put(hu_缺三门, 3);



        specialHuScore.put(hu_中张, 0);
        specialHuScore.put(hu_将对, 0);
        specialHuScore.put(hu_四碰, 0);
        specialHuScore.put(hu_幺九, 0);


        specialHuScore.put(hu_七小对, 0);
        specialHuScore.put(hu_豪华七小对, 0);
        specialHuScore.put(hu_双豪七小对, 0);
        specialHuScore.put(hu_三豪七小对, 0);

        specialHuScore.put(hu_清一色, 0);

        specialHuScore.put(hu_清一色七小对, 0);
        specialHuScore.put(hu_清一色豪华七小对, 0);
        specialHuScore.put(hu_清一色双豪华七小对, 0);

        specialHuScore.put(hu_碰碰胡, 0);
        specialHuScore.put(hu_清一色碰碰胡, 0);


        specialHuScore.put(hu_门清, 0);

        specialHuScore.put(hu_风谱1, 1);
        specialHuScore.put(hu_风谱2, 2);
        specialHuScore.put(hu_风谱3, 3);
        specialHuScore.put(hu_风谱4, 4);
        specialHuScore.put(hu_将谱1, 1);
        specialHuScore.put(hu_将谱2, 2);
        specialHuScore.put(hu_将谱3, 3);
        specialHuScore.put(hu_将谱4, 4);

    }

    @Override
    public boolean isHasChi(String card) {
        return false;
    }


    public boolean isCanPengAddThisCard(String card) {
        if (isTing) {
            return false;
        }else{
            return super.isCanPengAddThisCard(card);
        }
    }

    @Override
    public boolean isHasGang() {
        System.out.println("是否能杠: "+" isTing = "+isTing +"  cards = "+cards);
        if (isTing) {
            Set<Integer> canGangType = getHasGangList(cards);
            for (int gt : canGangType) {
                List<String> temp = new ArrayList<>();
                temp.addAll(cards);
                if (isCanTingAfterGang(temp, gt,false)) {
                    return true;
                }
            }
            return false;

        }else return super.isHasGang();
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        //听之后 杠后的牌还能听
        if (isTing && super.isCanGangAddThisCard(card)) {
            List<String> temp = getCardsAddThisCard(card);
            //去掉 这张杠牌
            int ct = CardTypeUtil.cardType.get(card);
            return isCanTingAfterGang(temp, ct,true);

        } else return super.isCanGangAddThisCard(card);

    }

    protected boolean isCanTingAfterGang(List<String> cards,int cardType,boolean isDianGang){
        //先删除这次杠的
        removeCardByType(cards,cardType,4);
        boolean isMing = false;
        //去除碰
        for(int pt : pengType.keySet()){//如果杠的是之前碰过的牌
            if (pt != cardType) {
                removeCardByType(cards, pt, 3);
            } else {
                isMing = true;
            }
        }
        //去掉杠的牌
        cards = getCardsNoGang(cards);
        isMing = isMing||isDianGang;

        //胡牌类型加上杠
        List<HuCardType> list = getTingHuCardType(cards,null);
        return list.size()>0;
    }

    public boolean isCanTing(List<String> cards) {
        if (isTing || !isHasMode(this.roomInfo.mode, mode_扣听)) {
            return false;
        }
        String lastCard = this.cards.get(this.cards.size() - 1);
        int lastCardType = CardTypeUtil.getTypeByCard(lastCard);
        List<HuCardType> list = getTingHuCardType(getCardsNoChiPengGang(cards), null);
        for (HuCardType huCardType : list) {
            if (huCardType.tingRemoveCard == null) {
                return true;
            }else{
                int removeType = CardTypeUtil.getTypeByCard(huCardType.tingRemoveCard);
                if (removeType != lastCardType) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否可以胡这张牌
     *
     * @param card
     * @return
     */
    public boolean isCanHu_dianpao(String card) {
        if (roomInfo.mustZimo == 1) {
            return false;
        }
        List<String> temp = getCardsAddThisCard(card);
        List<String> noPengAndGang = getCardsNoChiPengGang(temp);
        int cardType = CardTypeUtil.cardType.get(card);
        List<HuCardType> huList =  HuUtil.isHu(noPengAndGang, this, cardType, new HuLimit(0));
        if (huList.size() == 0) {
            return false;
        }


        for(HuCardType huCardType : huList){
            if (huCardType.ke.contains(cardType)) {
                return true;
            }
            if (huCardType.jiang == cardType) {
                return true;
            }
            if (FanUtil.isYaoJiu(cardType)) {

                for (int shunBegin : huCardType.shun) {
                    if (shunBegin == cardType) {
                        return true;
                    }
                    if (shunBegin == cardType - 2) {
                        return true;
                    }

                }
                return false;
            }
            return true;
        }

        return false;


//        if (FanUtil.isYaoJiu(cardType)) {
//            for (HuCardType huCardType : huList) {
//                if (huCardType.yao_jiu_shun.size() == 0) {
//                    return true;
//                }
//                for(List<Integer> l : huCardType.yao_jiu_shun){
//                    if (l.contains(cardType)) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//
//        if (CardTypeUtil.ZI_CARD.contains(card)) {
//            for (HuCardType huCardType : huList) {
//                if (huCardType.zi_shun == 0) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        if (CardTypeUtil.FENG_CARD.contains(card)) {
//            for (HuCardType huCardType : huList) {
//                if (huCardType.feng_shun.size() == 0) {
//                    return true;
//                }
//                for (List<Integer> list : huCardType.feng_shun) {
//                    if (!list.contains(cardType)) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//
//        return true;

    }


    @Override
    public void ting(String card) {
        this.cards.remove(card);
        this.disCards.add(card);

        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);

        this.koutingCard = this.cards.get(this.cards.size() - 1);

        this.gameInfo.replay.getKoutingCard().put(this.getUserId(), this.koutingCard);

        //推送扣听
        Map<String, Object> r = new HashMap<>();
        r.put("card", koutingCard);
        r.put("userId", this.userId);
        MsgSender.sendMsg2Player(ResponseType.SERVICE_TYPE_GAMELOGIC, "koutingCard", r, this.gameInfo.users);
    }

    @Override
    public void computeALLGang() {
        //明杠1分
        int sub = 0;
        int gangFan = 0;
        gangFan += this.anGangType.size() * 1;






        for (Map.Entry<Integer, Long> entry : this.mingGangType.entrySet()) {
            long dianGangUser = entry.getValue();
            //点杠
            if (dianGangUser != -1) {
                PlayerCardsInfoMj dianGangPlayer = this.gameInfo.getPlayerCardsInfos().get(dianGangUser);
                int temp = 1 *getGangMutiple();
                dianGangPlayer.addScore(-temp);
                dianGangPlayer.addGangScore(-temp);
                roomInfo.setUserSocre(dianGangUser, -temp);

                //自己加分
                this.addScore(temp);
                this.addGangScore(temp);
                roomInfo.setUserSocre(this.getUserId(), temp);

            } else {
                gangFan += 1;
            }
        }

        //除了点杠
        int score = gangFan * getGangMutiple();

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                playerCardsInfo.addGangScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addGangScore(sub);
        this.addScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);

    }




    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        this.gameInfo.computeAllGang();

        int allScore = 0;
        //底分
        int score = getDifen();
        System.out.println("底分 : " + score);

        //跑分
        score += getPf();
        System.out.println("跑分 : " + score);
        boolean isAddPaofen = getPf() != 0;

        //扣听
        score += getKoutingScore();
        System.out.println("扣听 : " + score);

        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        // 设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        this.winType.addAll(huCardType.specialHuList);

        score += huCardType.fan;

        System.out.println("牌型分数 : " + score);

        if (isZimo) {
            score *= 1;
            System.out.println("自摸 : " + score);
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                PlayerCardsInfoLongxiang player = (PlayerCardsInfoLongxiang)playerCardsInfoMj;
                if (player.getUserId() != userId) {
                    int temp = score;
                    //跑分
                    if (isAddPaofen) {
                        temp += player.getPf();
                    }
                    //减分
                    player.addScore(-temp);
                    this.roomInfo.addUserSocre(player.getUserId(), -temp);
                    allScore += temp;
                    System.out.println("userId : " + player.getUserId() + "  输的分数: " + temp);
                }
            }
        }else{
            PlayerCardsInfoLongxiang player = (PlayerCardsInfoLongxiang) this.gameInfo.playerCardsInfos.get(dianpaoUser);
            int temp = score;
            if (isAddPaofen) {
                temp += player.getPf();
            }
            System.out.println("点炮 userId : " + player.getUserId() + "  输的分数: " + temp);
            player.addScore(-temp);
            this.roomInfo.addUserSocre(player.getUserId(), -temp);
            allScore += temp;
        }


        //减分
        this.addScore(allScore);
        this.roomInfo.addUserSocre(userId, allScore);

    }


    /**
     * 获得扣听分数
     * @return
     */
    private int getKoutingScore() {
        if (isTing) {
            if (isHasMode(this.roomInfo.mode, mode_扣听_2)) {
                return 2;
            }
            if (isHasMode(this.roomInfo.mode, mode_扣听_4)) {
                return 4;
            }
            if (isHasMode(this.roomInfo.mode, mode_扣听_6)) {
                return 6;
            }
        }
        return 0;
    }


    /**
     * 跑分
     * @return
     */
    public int getPf(){
        if (this.paofen != -1 && this.paofen != 0) {
            if (isHasMode(this.roomInfo.mode, mode_跑分_2)) {
                return 2;
            }
            if (isHasMode(this.roomInfo.mode, mode_跑分_4)) {
                return 4;
            }
            if (isHasMode(this.roomInfo.mode, mode_跑分_6)) {
                return 6;
            }
        }
        return 0;
    }

    /**
     * 获得底分
     * @return
     */
    private int getDifen() {
        if (isHasMode(this.roomInfo.mode, mode_底分_1)) {
            return 1;
        }
        if (isHasMode(this.roomInfo.mode, mode_底分_2)) {
            return 2;
        }
        if (isHasMode(this.roomInfo.mode, mode_底分_4)) {
            return 4;
        }
        return 0;

    }

    public int getGangMutiple(){
        if (isHasMode(this.roomInfo.mode, mode_杠牌_2)) {
            return 2;
        }
        if (isHasMode(this.roomInfo.mode, mode_杠牌_4)) {
            return 4;
        }
        if (isHasMode(this.roomInfo.mode, mode_杠牌_6)) {
            return 6;
        }
        return 1;
    }

    /**
     * 获得谱的倍数
     * @return
     */
    public int getPuMutiple(){
        if (isHasMode(this.roomInfo.mode, mode_幺九谱_1)) {
            return 1;
        }
        if (isHasMode(this.roomInfo.mode, mode_幺九谱_2)) {
            return 2;
        }
        if (isHasMode(this.roomInfo.mode, mode_幺九谱_4)) {
            return 4;
        }
        return 1;
    }


    public static void main(String[] args) {
        PlayerCardsInfoLongxiang playerCardsInfo = new PlayerCardsInfoLongxiang();





        playerCardsInfo.isHasFengShun = true;
        playerCardsInfo.isHasZiShun = true;
        playerCardsInfo.isHasYaojiuShun = true;


        String[] s = new String[]{  "024",
                "025",
                "026",
                "032",
                "033",
                "034",
                "104",
                "105",
                "106",
                "108",
                "109",
                "028",
                "029",
                };
//        String[] s = new String[]{"112", "113", "114",   "024",   "028", "032",  "088", "092", "096",  "097",    "132", "133", "124", "120"};

//        List<Integer> hun = new ArrayList<>();
//        hun.add(0);
//        hun.add(1);
//        hun.add(8);


        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("1023");
        GameInfoLongxiang gameInfoTJ = new GameInfoLongxiang();
//        gameInfoTJ.hun = hun;
        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.setGameInfo(gameInfoTJ);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.init(playerCardsInfo.cards);


//        playerCardsInfo.pengType.put(18,0L);
//        playerCardsInfo.pengType.put(30,0L);

        List<String> list = Arrays.asList(s);
        playerCardsInfo.cards.addAll(list);

//        List<HuCardType> huList = HuUtil.isHu(playerCardsInfo,
//                playerCardsInfo.getCardsNoChiPengGang(playerCardsInfo.cards),
//                playerCardsInfo.getChiPengGangNum(), hun, 23);
//        boolean isCanHu = playerCardsInfo.isCanHu_zimo("068");
        boolean isCanHu = playerCardsInfo.isCanHu_dianpao("111");


        System.out.println("是否可以胡: " + isCanHu);
//        huList.forEach(h -> System.out.println(h.specialHuList));
//        System.out.println(huList);


    }

}
