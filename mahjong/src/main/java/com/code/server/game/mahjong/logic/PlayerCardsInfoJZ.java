package com.code.server.game.mahjong.logic;



import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

public class PlayerCardsInfoJZ extends PlayerCardsInfoQAMT {

    protected static final Integer[] yao = new Integer[]{0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};
    protected static List<Integer> yaoList = new ArrayList<>();

    static {
        yaoList.addAll(Arrays.asList(yao));
    }
    private static final int mode_小鸡飞蛋 = 0;
    private static final int mode_三风蛋 = 1;
    private static final int mode_未上听包三家 = 2;
    private static final int mode_下蛋算站立 = 3;
    private static final int mode_可缺门 = 4;

    private static final int[] cardType_1 = new int[]{0, 18};//去掉1条
    private static final int[] cardType_1_all = new int[]{0, 9, 18};//去掉1条
    private static final int[] cardType_9 = new int[]{8, 17, 26};
    private static final int[] cardType_feng = new int[]{27, 28, 29, 30};
    private static final int[] cardType_zi = new int[]{31, 32, 33};
    private static final int[] cardType_1tiao = new int[]{9};

    private static final int[] cardType_19 = new int[]{0, 9, 18,8, 17, 26,27, 28, 29, 30,31, 32, 33};

    private static final int[][] xuanfengdanType = new int[][]{cardType_1_all, cardType_9, cardType_feng, cardType_zi};

    private boolean isHasXiaojifeidan = false;
    private boolean isHasSanfengdan = true;
    private boolean isWeiShangTingBaoSanJia = false;
    private boolean isXiadansuanzhanli = false;
    private boolean isCanQuemen = false;

    protected List<String> canDanCards = new ArrayList<>();

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.clear();
        specialHuScore.put(hu_七小对, 4);
        specialHuScore.put(hu_豪华七小对, 8);
        specialHuScore.put(hu_双豪七小对, 16);
        specialHuScore.put(hu_三豪七小对, 32);

        specialHuScore.put(hu_夹张, 2);
        specialHuScore.put(hu_边张_乾安, 2);
        specialHuScore.put(hu_吊将, 2);
        specialHuScore.put(hu_飘胡, 4);

        specialHuScore.put(hu_门清,2);

        if(isHasMode(this.roomInfo.getMode(),mode_小鸡飞蛋)){
            isHasXiaojifeidan = true;
        }
        if(isHasMode(this.roomInfo.getMode(),mode_三风蛋)){
            isHasSanfengdan = true;
        }
        if(isHasMode(this.roomInfo.getMode(),mode_未上听包三家)){
            isWeiShangTingBaoSanJia = true;
        }
//        if(isHasMode(this.roomInfo.getMode(),mode_三风蛋)){
//            isHasSanfengdan = true;
//        }
        if(isHasMode(this.roomInfo.getMode(),mode_下蛋算站立)){
            isXiadansuanzhanli = true;
        }
        if(isHasMode(this.roomInfo.getMode(),mode_可缺门)){
            isCanQuemen = true;
            specialHuScore.put(hu_清一色, 2);
        }
    }

    private void addCanDanCard(String card){
        if(operateList.size() <= 1 && canDanCards.size() < 14){
            canDanCards.add(card);
        }
    }
    @Override
    public void chi(String card, String one, String two) {
        super.chi(card, one, two);
        addCanDanCard(card);
    }

    @Override
    public void mopai(String card) {
        super.mopai(card);
        addCanDanCard(card);
    }

    @Override
    public void peng(String card, long playUser) {
        super.peng(card, playUser);
        addCanDanCard(card);
    }

    @Override
    public boolean gang_hand(RoomInfo room, GameInfo info, long diangangUser, String card) {
        return super.gang_hand(room, info, diangangUser, card);
    }

    @Override
    public boolean gang_discard(RoomInfo room, GameInfo gameInfo, long diangangUser, String disCard) {
        addCanDanCard(disCard);
        return super.gang_discard(room, gameInfo, diangangUser, disCard);
    }

    private List<String> canXindanCardList(List<String> cards){
        List<String> result =  new ArrayList<>();
        for(String card : cards){
            if(canDanCards.contains(card)){
                result.add(card);
            }
        }
        return result;
//        return cards.stream().filter(card-> canDanCards.contains(card)).collect(Collectors.toList());

    }

    protected boolean cardIsYao(String card){
        int cardType = CardTypeUtil.getTypeByCard(card);
        for(int yaoType : yaoList){
            if (yaoType == cardType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isHasXuanfengDan(List<String> cards,String card) {
        List<String> temp = getCardsNoChiPengGang(cards);

        //第一次操作
        if (isCanXindan()) {

            return isXuanfengdanList(canXindanCardList(temp)) || (this.xuanfengDan.size()>0 && isXuanfengdanCard(temp));
        } else {
            if(this.xuanfengDan.size()==0){
                return false;
            }
            if (isTing) {
                List<String> cs = new ArrayList<>();
                cs.add(card);
                return isXuanfengdanCard(cs);
            } else {
                return isXuanfengdanCard(temp);
            }
        }
    }

    private boolean isCanXindan(){
        int size = this.gameInfo.userOperateList.size();
        Map<Long, Integer> map = this.gameInfo.userOperateList.get(size - 1);
        if (!map.containsKey(this.getUserId())) {
            return false;
        }
        //是否有其他玩家操作
        boolean isHasOther = false;
        for(int i=size -1;i>=0;i--){
            Map<Long, Integer> oper = this.gameInfo.userOperateList.get(i);
            if(!oper.containsKey(this.getUserId())){
                isHasOther = true;
            }
            //如果操作不连续 说明不可新蛋
            if (isHasOther) {
                if(oper.containsKey(this.getUserId())){
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 是否可以旋风蛋
     * @param xuanfengCards
     * @return
     */
    public boolean isCanXuanfengdan(List<String> xuanfengCards) {
        System.out.println(xuanfengCards);
        List<String> cards = getCardsNoChiPengGang(this.cards);
        if (!cards.containsAll(xuanfengCards)) {
            return false;
        }
        if (xuanfengCards.size() > 1 && isCanXindan()) {
            return isXuanfengdanList(xuanfengCards);
        } else if (xuanfengCards.size() == 1 && this.xuanfengDan.size() > 0) {
            boolean tingCheck = !isTing || xuanfengCards.get(0).equals(catchCard);
            return isXuanfengdanCard(xuanfengCards) && tingCheck;
        } else {
            return false;
        }
    }

    private boolean isXuanfengdanList(List<String> xuanfengCards) {
        int num9 = getCardNum(xuanfengCards, cardType_9);
        int numzi = getCardNum(xuanfengCards, cardType_zi);
        int tiao1 = get1tiaoNum(xuanfengCards);
        int num1 = getCardNum(xuanfengCards, cardType_1);
        int numfeng = getCardNum(xuanfengCards, cardType_feng);

        System.out.println("num9: "+num9 );
        System.out.println("numzi: "+numzi );
        System.out.println("tiao1: "+tiao1 );
        System.out.println("num1: "+num1 );
        System.out.println("numfeng: "+numfeng );

        //有小鸡飞蛋
        if (this.isHasXiaojifeidan) {

            int remainSize = 3 - tiao1;
            if (remainSize == 0) {
                remainSize = 1;
            }
//            //四风蛋 数量减掉1
//            if (!isHasSanfengdan) {
//                numfeng -= 1;
//            }
            return num9 >= remainSize || num1 >= remainSize || numfeng >= remainSize || numzi >= remainSize;
        } else {
            //三风蛋
            if (isHasSanfengdan) {
                return num9 >= 3 || (num1 + tiao1) >= 3 || numfeng >= 3 || numzi >= 3;
            } else {
                return num9 >= 3 || (num1 + tiao1) >= 3 || numfeng >= 4 || numzi >= 3;
            }
        }

    }

    /**
     * 是否是旋风蛋的
     * @param xuanfengdan
     * @return
     */
    private boolean isXuanfengdanCard(List<String> xuanfengdan) {
        if (get1tiaoNum(xuanfengdan) > 0 && isHasXiaojifeidan) {
            return true;
        }
        //获得有的旋风蛋类型
        for (int xft : this.xuanfengDan.keySet()) {
            if (getCardNum(xuanfengdan, xuanfengdanType[xft]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 旋风蛋
     * @param xuanfeng
     * @param xuanfengType
     */
    public void xuanfengdan(List<String> xuanfeng, int xuanfengType) {
        if (xuanfeng.size() > 1) {
            this.xuanfengDan.put(xuanfengType, xuanfeng);
        } else{
            List<String> cs = getXuanfengList(xuanfengType);
            if (cs != null) {
                cs.addAll(xuanfeng);
            }
        }
        this.lastOperate = type_xuanfengdan;
        this.operateList.add(type_xuanfengdan);
        this.gameInfo.addUserOperate(this.userId,type_xuanfengdan);
    }

    private List<String> getXuanfengList(int xuanfengType) {
        for (List<String> cs : this.xuanfengDan.values()) {
            if (getCardNum(cs, xuanfengdanType[xuanfengType]) > 0) {
                return cs;
            }
        }
        return null;
    }

    /**
     * 获得牌拥有数组中牌的数量
     * @param cards
     * @param array
     * @return
     */
    private int getCardNum(List<String> cards, int[] array) {
//      return cards.stream().filter(card->isCardInArray(card,cardType_9)).collect(Collectors.groupingBy(card->CardTypeUtil.cardType.get(card))).size();
        Set<Integer> cardSet = new HashSet<>();
        for (String card : cards) {
            if (isCardInArray(card, array)) {
                cardSet.add(CardTypeUtil.cardType.get(card));
            }
        }
        return cardSet.size();
    }

    private int get1tiaoNum(List<String> cards){
        int count = 0;
        for (String card : cards) {
            if (isCardInArray(card, cardType_1tiao)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 牌是否在数组中
     * @param card
     * @param types
     * @return
     */
    private boolean isCardInArray(String card, int[] types) {
        int type = CardTypeUtil.cardType.get(card);
        for (int t : types) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有幺
     * @param cards
     * @return
     */
    protected boolean isHasYao(List<String> cards) {
        List<Integer> list = new ArrayList<>();
        for (String card : cards) {
            int type = CardTypeUtil.cardType.get(card);
            if (yaoList.contains(type)) {
                if (type != 9) {
                    return true;
                }
                list.add(type);
            }
        }
        if (list.size() == 0) {
            return false;
        }

        List<String> num_1tiao = new ArrayList<>();
        for (List<String> ls : this.xuanfengDan.values()) {
            for (String cd : ls) {
                if (CardTypeUtil.getTypeByCard(cd) == 9) {
                    num_1tiao.add(cd);
                }
            }
        }
        return num_1tiao.size() != list.size();

    }

    /**
     * 条子里只有一条并且是混
     *
     * @return
     */
    private boolean is1tiaoisHun(List<String> cards) {
        List<Integer> list = new ArrayList<>();
        for (String card : cards) {
            int type = CardTypeUtil.cardType.get(card);
            int group = CardTypeUtil.getCardGroup(card);
            if(group == CardTypeUtil.GROUP_TIAO && type != 9){
                return false;
            }
            list.add(type);

        }
        if (list.size() == 0) {
            return true;
        }

        List<String> num_1tiao = new ArrayList<>();
        for (List<String> ls : this.xuanfengDan.values()) {
            for (String cd : ls) {
                if (CardTypeUtil.getTypeByCard(cd) == 9) {
                    num_1tiao.add(cd);
                }
            }
        }
        return num_1tiao.size() == list.size();
    }

    protected Set<Integer> getTingListNoCPG(List<String> allCards, List<String> cards, boolean isCheckMenqing, boolean isChi, boolean isCanRemoveYao, boolean isGang) {
        System.out.println("是否可听: "+cards);
        Set<Integer> result = new HashSet<>();
        List<HuCardType> list = getTingHuCardType(cards, new HuLimit(0));
        for (HuCardType huCardType : list) {
            //如果听删除的牌是幺 && 手牌只有一张幺
            if (!isCanRemoveYao && huCardType.tingRemoveCard != null) {
                if (cardIsYao(huCardType.tingRemoveCard)) {
                    continue;
                }
            }

            List<String> temp = new ArrayList<>();
            temp.addAll(allCards);
            if (huCardType.tingRemoveCard != null) {
                temp.remove(huCardType.tingRemoveCard);
            }
            int groupSize = getCardGroup(temp);

            //不能缺门
            if (groupSize < 3 && !isCanQuemen) {
                continue;
            }
            if (is1tiaoisHun(temp)) {
                continue;
            }
            if (huCardType.specialHuList.contains(hu_七小对) || huCardType.specialHuList.contains(hu_豪华七小对)
                    || huCardType.specialHuList.contains(hu_双豪七小对) || huCardType.specialHuList.contains(hu_三豪七小对)) {//有七小对
                result.add(huCardType.tingCardType);
            } else if (huCardType.specialHuList.contains(hu_飘胡)) {
                if (isChi) {
                    continue;
                }
//                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
//                    continue;
//                }
                result.add(huCardType.tingCardType);
            } else {
//                if ((!huCardType.specialHuList.contains(hu_夹张) && !huCardType.specialHuList.contains(hu_边张_乾安))) {
//                    continue;
//                }
//                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
//                    continue;
//                }
                //必须有碰等 或一对中发白
                if (!isGang && this.xuanfengDan.size()==0 && (huCardType.peng.size() == 0 && huCardType.mingGang.size() == 0
                        && huCardType.anGang.size() == 0 && huCardType.ke.size() == 0 && !cardIsZFB(huCardType.jiang))) {
                    continue;
                }

                result.add(huCardType.tingCardType);
            }

        }
        return result;
    }

    private boolean cardIsZFB(int cardType) {
        return CardTypeUtil.getCardGroupByCardType(cardType) == CardTypeUtil.GROUP_ZI;
    }

    @Override
    public boolean isCanChiTing(String card) {
        return false;
    }

    @Override
    public boolean isCanPengTing(String card) {
        return false;
    }


    @Override
    public void hu_zm(RoomInfo room, GameInfo gameInfo, String card) {
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (cardType == gameInfo.baoType) {
            winType.add(hu_摸宝);
            //摸宝次数
            room.addMoBaoNum(userId);
        }
        super.hu_zm(room, gameInfo, card);
    }


    @Override
    public void computeALLGang() {
        int gangFan = 0;
        gangFan += getGangScore(mingGangType.keySet(), true, baoMingDan);
        System.out.println("userId : "+this.userId +"  明杠的分数 : "+ gangFan);
        gangFan += getGangScore(anGangType, false, baoAnDan);
        System.out.println("userId : "+this.userId +"  暗杠的分数 : "+ gangFan);
        gangFan += getXuanfengDanScore(this.xuanfengDan);
        System.out.println("userId : "+this.userId +"  旋风蛋的分数 : "+ gangFan);
        int score = gangFan * roomInfo.getMultiple();
        int sub = 0;
        for (PlayerCardsInfo playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                playerCardsInfo.addGangScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addScore(sub);
        this.addGangScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);
    }

    private boolean isZhanli() {
        int chiSize = this.chiType.size();
        int pengSize = this.pengType.size();
        int mingGangSize = this.mingGangType.size();
        if (chiSize > 0 || pengSize > 0 || mingGangSize > 0) {
            return false;
        }
        if (!isXiadansuanzhanli) {
            if (this.xuanfengDan.size() > 0) {
                return false;
            }
        }
        return true;
    }

    private int getScoreByHuCardType(HuCardType huCardType) {

        Set<Integer> hutype = huCardType.specialHuList;

        if(hutype.contains(hu_三豪七小对)){
            this.winType.add(hu_三豪七小对);
            return this.specialHuScore.get(hu_三豪七小对);
        }
        if(hutype.contains(hu_豪华七小对)){
            this.winType.add(hu_豪华七小对);
            return this.specialHuScore.get(hu_豪华七小对);
        }
        if(hutype.contains(hu_双豪七小对)){
            this.winType.add(hu_双豪七小对);
            return this.specialHuScore.get(hu_双豪七小对);
        }
        if(hutype.contains(hu_七小对)){
            this.winType.add(hu_七小对);
            return this.specialHuScore.get(hu_七小对);
        }
        if(hutype.contains(hu_飘胡)){
            this.winType.add(hu_飘胡);
            return this.specialHuScore.get(hu_飘胡);
        }
        if(hutype.contains(hu_夹张)){
            this.winType.add(hu_夹张);
            return this.specialHuScore.get(hu_夹张);
        }
        if(hutype.contains(hu_边张_乾安)){
            this.winType.add(hu_边张_乾安);
            return this.specialHuScore.get(hu_边张_乾安);
        }
        if(hutype.contains(hu_吊将)){
            this.winType.add(hu_吊将);
            return this.specialHuScore.get(hu_吊将);
        }


        return 1;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        int cardType = CardTypeUtil.getTypeByCard(card);
        int score = 1;

        HuCardType huCardType = getMaxScoreHuCardType(huList);
        if (huCardType != null) {
            score = getScoreByHuCardType(huCardType);

        }


        //算所有的杠
        gameInfo.computeAllGang();
        //摸宝并且没胡
        if (winType.contains(hu_摸宝) && huList.size() == 0) {

            List<HuCardType> hl = new ArrayList<>();
            for (Integer tingType : tingSet) {
                List<String> temp = new ArrayList<>();
                temp.addAll(cards);
                temp.remove(card);
                String aCard = CardTypeUtil.getCardStrByType(tingType);
                temp.add(aCard);

                hl.addAll(HuUtil.isHu(getCardsNoChiPengGang(temp), this, tingType, new HuLimit(0)));
            }
            huCardType = getMaxScoreHuCardType(hl);
            score = getScoreByHuCardType(huCardType);
        }
        System.out.println("分数: " + score);

        if(score == 0){
            score = 1;
        }
        int huFan = score;//番的分数不加杠的分
        int gangFan = 0;

//        gangFan += getGangScore(mingGangType.keySet(), true, baoMingDan);
//        gangFan += getGangScore(anGangType, false, baoAnDan);
//        gangFan += getXuanfengDanScore(this.xuanfengDan);

        score += gangFan;

        System.out.println("分数加杠: " + score);

        int power = 0;
        //自摸加倍
        if (isZimo) {
            power += 1;
            System.out.println("自摸 : " + power);
        }
        //庄家加倍
        if (gameInfo.getFirstTurn() == this.userId) {
            power += 1;
            System.out.println("庄家 : " + power);
        }
        //海底捞加倍
        if (winType.contains(hu_海底捞)) {
            power += 1;
            System.out.println("hu_海底捞 : " + power);
        }
        if (winType.contains(hu_摸宝)) {
            power += 1;
            System.out.println("hu_摸宝 : " + power);
        }
        if (winType.contains(hu_直对)) {
            power += 2;
            System.out.println("hu_直对 : " + power);
        }
        if (isZhanli()) {
            winType.add(hu_门清);
            power += 1;
            System.out.println("hu_站立 : " + power);
        }
        if (huCardType!= null && huCardType.specialHuList.contains(hu_清一色)) {
            winType.add(hu_清一色);
            power += 1;
            System.out.println("hu_清一色 : " + power);
        }
        boolean isDianPaoLoss = !isZimo && !gameInfo.playerCardsInfos.get(dianpaoUser).isTing && isWeiShangTingBaoSanJia;
        int sub = 0;
        int subFan = 0;
        int subGangScore = 0;
        int subHuScore = 0;
        for (PlayerCardsInfo playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //不是自己
            if (playerCardsInfo.getUserId() != userId) {
                System.out.println("==================================begin====  " + playerCardsInfo.getUserId() + "  ==============");
                int myPower = power;
                //庄家加倍
                if (gameInfo.getFirstTurn() == playerCardsInfo.getUserId()) {
                    myPower += 1;
                    System.out.println("庄家输加倍 : " + power);
                }

                //点炮
                if (dianpaoUser == playerCardsInfo.getUserId()) {
                    myPower += 1;
                    System.out.println(playerCardsInfo.getUserId() + " 点炮 " + myPower);
                }

                int scale = 1 << myPower;
                if (topScale > 0) {
                    if (scale > topScale) {
                        scale = topScale;
                    }
                }
                System.out.println(playerCardsInfo.getUserId() + " 输的番 : " + myPower);
                System.out.println(playerCardsInfo.getUserId() + " 输的倍数 : " + scale);
                int gangScore = gangFan * room.getMultiple();
                int huScore = huFan * room.getMultiple() * scale;
                int subScore = gangScore + huScore;

                System.out.println("最后分数: " + subScore);
                sub += subScore;
                subFan += huFan * scale;
                subHuScore += huScore;
                subGangScore += gangScore;

                //每个人都扣分
                if (!isDianPaoLoss) {
                    playerCardsInfo.addScore(-subScore);
                    room.setUserSocre(playerCardsInfo.getUserId(), -subScore);
                } else {//只扣杠分
                    playerCardsInfo.addScore(-gangScore);
                    room.setUserSocre(playerCardsInfo.getUserId(), -gangScore);
                }
                System.out.println("==================================end==================");

            }
        }
        //点炮包三家 包胡不包杠
        if (isDianPaoLoss) {
            System.out.println("点炮包三家 ");
            gameInfo.playerCardsInfos.get(dianpaoUser).addScore(-subHuScore);
            room.setUserSocre(dianpaoUser, -subHuScore);
        }
        //给自己加分
        this.addScore(sub);
        room.setUserSocre(userId, sub);
        this.fan = subFan;


    }


    protected int getGangScore(Set<Integer> set, boolean isMing, Set<Integer> danList) {
        int score = 0;
        for (int key : set) {
            if (key == 9 || key == 18 || key == 31 ||key == 32 ||key == 33) {
                score += 2;
            } else {
                score += 1;
            }
            //没有宝蛋
//            if (danList.contains(key)) {
//                score *= 2;
//            }
        }
        if (isMing) {
            score *= 2;
        } else {
            score *= 4;
        }
        return score;
    }

    /**
     * 旋风蛋的分数
     * @param xfdList
     * @return
     */
    protected int getXuanfengDanScore(Map<Integer, List<String>> xfdList) {
        int result = 0;
        for(Map.Entry<Integer,List<String>> entry : xfdList.entrySet()){
            int type = entry.getKey();
            List<String> list = entry.getValue();
            int cardNum = list.size();
            if (cardNum == 0) {
                continue;
            }
//            if (type == 2 && !isHasSanfengdan) {//四风蛋
//                result += cardNum -3;
//            } else {
                result += cardNum -2;
//            }
        }
        return result;
    }

    public static void main(String[] args) {

        PlayerCardsInfoJZ playerCardsInfo = new PlayerCardsInfoJZ();
        String[] s = new String[]{"052", "053", "054",    "008", "012", "016", "017", "020", "024",      "088","089", "100", "101",   "120"};
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("63");

        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.cards.addAll(Arrays.asList(s));

        playerCardsInfo.init(playerCardsInfo.cards);
        List<List<String>> chis = new ArrayList<>();
        List<String> chi = new ArrayList<>();
        chi.add("048");
        chi.add("052");
        chi.add("056");

//        List<String> chi1 = new ArrayList<>();
//        chi1.add("008");
//        chi1.add("012");
//        chi1.add("016");

//        chis.add(chi);
        playerCardsInfo.chiCards = chis;

        Map<Integer, List<String>> xfd = new HashMap<>();
        List<String> xf1 = new ArrayList<>();
        xf1.add("108");
        xf1.add("112");
        xf1.add("116");
        xf1.add("120");

        List<String> xf2 = new ArrayList<>();
        xf2.add("032");
        xf2.add("068");
        xf2.add("104");
        xf2.add("036");
        xf2.add("105");
        xf2.add("069");

//        xfd.put(2, xf1);
//        xfd.put(1,xf2);
        playerCardsInfo.xuanfengDan = xfd;

        playerCardsInfo.pengType.put(13, -1L);


        System.out.println(playerCardsInfo.isCanTing(playerCardsInfo.cards));


    }

}
