package com.code.server.game.mahjong.logic;




import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;

/**
 * 乾安快听
 */
public class PlayerCardsInfoQAKT extends PlayerCardsInfoMj {
//    protected static final Logger logger = Logger.getLogger("game");

    protected static final Integer[] yao = new Integer[]{0, 8, 9, 17, 18, 26, 31};
    protected static List<Integer> yaoList = new ArrayList<>();

//	256封顶、128封顶、杠呲宝、未上听包三家、杠上开花、三七夹
    private static final int mode_三七夹 = 0;
    private static final int mode_杠上开花 = 1;
    private static final int mode_未上听包三家 = 2;
    private static final int mode_杠上开宝 = 3;
    private static final int mode_128封顶 = 4;
    private static final int mode_256封顶 = 5;
    protected int topScale = -1;
    protected boolean isHasGangShangKaiHua;
    protected boolean isHasGangShangKaiBao;
    protected boolean isWeiShangTingBaoSanJia;


    static{
        yaoList.addAll(Arrays.asList(yao));
    }

    @Override
    public void init(List<String> cards) {
        super.init(cards);
        specialHuScore.put(hu_普通七小对, 8);
        specialHuScore.put(hu_夹张,2);
        specialHuScore.put(hu_飘胡,8);

        specialHuScore.put(hu_缺两门,0);
        specialHuScore.put(hu_缺一门,0);
        specialHuScore.put(hu_门清,0);

        if(isHasMode(this.roomInfo.getMode(),mode_三七夹)){
        	specialHuScore.put(hu_边张_乾安,0);
        }
        if (isHasMode(roomInfo.getMode(), mode_128封顶)) {
            topScale  = 128;
        }
        if (isHasMode(roomInfo.getMode(), mode_256封顶)) {
            topScale = 256;
        }
        isHasGangShangKaiHua = isHasMode(roomInfo.getMode(),mode_杠上开花);
        isHasGangShangKaiBao = isHasMode(roomInfo.getMode(), mode_杠上开宝);
        isWeiShangTingBaoSanJia = isHasMode(roomInfo.getMode(), mode_未上听包三家);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        //听之后 杠后的牌还能听
        if (isTing && super.isCanGangAddThisCard(card)) {
            List<String> temp = getCardsAddThisCard(card);
            //去掉 这张杠牌
            int ct = CardTypeUtil.cardType.get(card);
            return isCanTingAfterGang(temp, ct);

        } else return super.isCanGangAddThisCard(card);

    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if (isTing && super.isCanGangThisCard(card)) {
            List<String> temp = new ArrayList<>();
            temp.addAll(cards);
            //去掉 这张杠牌
            int cardType = CardTypeUtil.cardType.get(card);

            return isCanTingAfterGang(temp, cardType);

        } else return super.isCanGangThisCard(card);

    }

    @Override
    public boolean isHasGang() {
        if (isTing) {
            Set<Integer> canGangType = getHasGangList(cards);
            for (int gt : canGangType) {
                List<String> temp = new ArrayList<>();
                temp.addAll(cards);
                if (isCanTingAfterGang(temp, gt)) {
                    return true;
                }
            }
            return false;

        }else return super.isHasGang();
    }

    /**
     * 杠之后是否能听
     * @param cards
     * @param cardType
     * @return
     */
    public boolean isCanTingAfterGang(List<String> cards,int cardType){
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        boolean isCanRemoveYao = getYaoNum(cards)>1;
        //先删除这次杠的
        removeCardByType(cards,cardType,4);
        //去掉碰的牌
        for(int pt : pengType.keySet()){
            if (pt != cardType) {
                removeCardByType(cards,pt,3);
            }
        }
        //去掉杠的牌
        cards = getCardsNoChiGang(cards);

        return  getTingListNoCPG(temp,cards,false,false,isCanRemoveYao,true).size()>0;
    }

    @Override
    public boolean isCanTing(List<String> cards) {

        if (isTing) {
            return false;
        }
        List<String> temp = getCardsNoChiPengGang(cards);

        if (!isHasYao(cards)) {
            return false;
        }
        return getTingListNoCPG(cards,temp,true,false,getYaoNum(cards)>1,false).size()>0;

    }

    @Override
    public void mopai(String card) {
        super.mopai(card);
        int cardType = CardTypeUtil.getTypeByCard(card);
        if (isTing && cardType == gameInfo.baoType) {
            //摸宝
            this.isAlreadyHu = true;
            this.huCard = card;
        }

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

    protected Set<Integer> getTingListNoCPG(List<String> allCards,List<String> cards,boolean isCheckMenqing,boolean isChi,boolean isCanRemoveYao,boolean isGang) {
        Set<Integer> result = new HashSet<>();
        List<HuCardType> list = getTingHuCardType(cards,new HuLimit(0));
        for (HuCardType huCardType : list) {
            //如果听删除的牌是幺 && 手牌只有一张幺
            if (!isCanRemoveYao && huCardType.tingRemoveCard!=null) {
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
            //缺两门
            if (groupSize <= 1) {
                continue;
            }
            if (huCardType.specialHuList.contains(hu_普通七小对)) {//有七小对
                result.add(huCardType.tingCardType);
            }else if(huCardType.specialHuList.contains(hu_飘胡)){
                if (isChi) {
                    continue;
                }
                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
                    continue;
                }
                result.add(huCardType.tingCardType);
            } else {
                if ((!huCardType.specialHuList.contains(hu_夹张)&&!huCardType.specialHuList.contains(hu_边张_乾安))) {
                    continue;
                }
                if (isCheckMenqing && huCardType.specialHuList.contains(hu_门清)) {
                    continue;
                }

                result.add(huCardType.tingCardType);
            }

        }
        return result;
    }


    @Override
    public boolean isCanChiTing(String card) {
        if (isTing) {
            return false;
        }
        if (!isHasChi(card)) {
            return false;
        }
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        temp.add(card);
        if (!isHasYao(temp)) {
            return false;
        }
        List<String> noCPG = getCardsNoChiPengGang(temp);
        //吃的情况
        List<List<String>> chiList = getChiList(noCPG,card);
        for (List<String> chi : chiList) {
            //去掉吃的牌
            List<String> cs = new ArrayList<>();
            cs.addAll(noCPG);
            removeCards(cs, chi);
            if (getTingListNoCPG(temp,cs,false,true,getYaoNum(temp)>1,false).size()>0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCanPengTing(String card) {
        if (isTing) {
            return false;
        }
        if (!isCanPengAddThisCard(card)) {
            return false;
        }
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        temp.add(card);
        if (!isHasYao(temp)) {
            return false;
        }
        List<String> noCPG = getCardsNoChiPengGang(temp);
        //去掉碰
        removeCardByType(noCPG,CardTypeUtil.getTypeByCard(card),3);
        return getTingListNoCPG(temp,noCPG,false,false,getYaoNum(temp)>1,false).size()>0;
    }



    @Override
    public void ting(String card) {
        this.cards.remove(card);
        this.disCards.add(card);

        this.isTing = true;
        this.lastOperate = type_ting;
        operateList.add(type_ting);

        List<String> noCPG = getCardsNoChiPengGang(cards);
        Set<Integer> set = getTingListNoCPG(this.cards,noCPG,true,false,true,false);
        tingSet = set;
    }



    @Override
    public boolean isCanHu_dianpao(String card) {
        if (!isTing) {
            return false;
        }
        int type = CardTypeUtil.getTypeByCard(card);
        if (!tingSet.contains(type)) {
            return false;
        }
        return super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (!isTing) {
            return false;
        }
        //摸宝
        int type = CardTypeUtil.getTypeByCard(card);
        if (type == gameInfo.baoType) {
            return true;
        }
        if (!tingSet.contains(type)) {
            return false;
        }
        return super.isCanHu_zimo(card);
    }


    @Override
    public void hu_zm(RoomInfo room, GameInfo gameInfo, String card) {
        int cardType = CardTypeUtil.getTypeByCard(card);
        int operateListSize = operateList.size();
        if (operateListSize > 1) {
            System.out.println("operatelist = "+operateList);
            //是杠上开花
            if (operateList.get(operateListSize - 2) == type_gang) {
                if (CardTypeUtil.getTypeByCard(card) == gameInfo.baoType) {
                    if (isHasGangShangKaiBao) {
                        winType.add(hu_杠上开宝);
                    }
                } else {
                    if (isHasGangShangKaiHua) {
                        winType.add(hu_杠上开花);
                    }
                }
            }

        }
        if(cardType == gameInfo.baoType){
            winType.add(hu_摸宝);
            //摸宝次数
            room.addMoBaoNum(userId);
        }
        super.hu_zm(room, gameInfo, card);
    }

    @Override
    public boolean isHasChi(String card) {
        if (isTing) {
            return false;
        }
        return super.isHasChi(card);
    }

    @Override
    public boolean isCanPengAddThisCard(String card) {
        if (isTing) {
            return false;
        }
        return super.isCanPengAddThisCard(card);
    }

    private int getScoreByHuCardType(HuCardType huCardType,int cardType) {
        int score_init = 1;
        if (huCardType.specialHuList.contains(hu_夹张)) {
            if (cardType == 4 || cardType == 13 || cardType == 22) {
                score_init = 2;
                this.winType.add(hu_夹五);
            }
        }
        if (huCardType.specialHuList.contains(hu_普通七小对)) {
            score_init = 4;
            this.winType.add(hu_普通七小对);
        }
        if (huCardType.specialHuList.contains(hu_飘胡)) {
            score_init = 4;
            this.winType.add(hu_飘胡);
        }
        return score_init;
    }


    @Override
    public void computeALLGang(){
        int gangFan = 0;
        gangFan += getGangScore(mingGangType.keySet(),true,baoMingDan);
        gangFan += getGangScore(anGangType,false,baoAnDan);
        int score = gangFan * roomInfo.getMultiple();
        int sub = 0;
        for(PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()){
            if (playerCardsInfo.getUserId() != this.userId) {
                playerCardsInfo.addScore(-score);
                roomInfo.setUserSocre(playerCardsInfo.getUserId(), -score);
                sub += score;
            }
        }
        this.addScore(sub);
        roomInfo.setUserSocre(this.getUserId(), sub);
    }


    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        int cardType = CardTypeUtil.getTypeByCard(card);
        int score = 2;
        for(HuCardType huCardType : huList){
            int score_init = getScoreByHuCardType(huCardType,cardType);
            if (score_init > score) {
                score = score_init;
            }
        }
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        if (huCardType != null) {
            score = getScoreByHuCardType(huCardType,cardType);
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

                hl.addAll(HuUtil.isHu(getCardsNoChiPengGang(temp),this,tingType,new HuLimit(0)));
            }
            huCardType = getMaxScoreHuCardType(hl);
            score = getScoreByHuCardType(huCardType, huCardType.tingCardType);
        }
        System.out.println("分数: "+score);

        int huFan = score;//番的分数不加杠的分
        int gangFan = 0;

//        gangFan += getGangScore(mingGangType.keySet(),true,baoMingDan);
//        gangFan += getGangScore(anGangType,false,baoAnDan);

        score += gangFan;

        System.out.println("分数加杠: "+score);

        int power = 0;
        //自摸加倍
        if (isZimo) {
            power += 1;
            System.out.println("自摸 : "+power);
        }
        //庄家加倍
        if (gameInfo.getFirstTurn() == this.userId) {
            power += 1;
            System.out.println("庄家 : "+power);
        }
        //海底捞加倍
        if (winType.contains(hu_海底捞)) {
            power += 1;
            System.out.println("hu_海底捞 : "+power);
        }
        if (winType.contains(hu_摸宝)) {
            power += 1;
            System.out.println("hu_摸宝 : "+power);
        }
        if (winType.contains(hu_直对)) {
            power += 1;
            System.out.println("hu_直对 : "+power);
        }
        if(winType.contains(hu_杠上开花)){
            power += 1;
            System.out.println("hu_杠上开花 : "+power);
        }
        if(winType.contains(hu_杠上开宝)){
            power += 1;
            System.out.println("hu_杠上开花 : "+power);
        }
        //三清
        if (isSanqing(gameInfo)) {
            power += 1;
            System.out.println("三清 : "+power);
        }
        boolean isDianPaoLoss = !isZimo && !gameInfo.playerCardsInfos.get(dianpaoUser).isTing && isWeiShangTingBaoSanJia;
        int sub = 0;
        int subFan = 0;
        int subGangScore = 0;
        int subHuScore = 0;
        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //不是自己
            if(playerCardsInfo.getUserId() != userId){
                System.out.println("==================================begin====  "+playerCardsInfo.getUserId()+"  ==============");
                int myPower = power;
                //庄家加倍
                if (gameInfo.getFirstTurn() == playerCardsInfo.getUserId()) {
                    myPower += 1;
                    System.out.println("庄家输加倍 : "+power);
                }

                //点炮
                if (dianpaoUser == playerCardsInfo.getUserId()) {

                    myPower +=1;
                    System.out.println(playerCardsInfo.getUserId()+" 点炮 "+myPower);
                    //杠后点炮
//                    if (playerCardsInfo.mingGangType.size() > 0 || playerCardsInfo.anGangType.size() > 0) {
//                        myPower += 1;
//                        System.out.println(playerCardsInfo.getUserId()+" 杠后点炮 "+myPower);
//                    }
                }
                //开门
                if (!((PlayerCardsInfoQAKT)playerCardsInfo).isKaimen()) {
                    myPower +=1;
                    System.out.println(playerCardsInfo.getUserId()+" 没开门 "+myPower);
                }
                int scale = 1<<myPower;
                if (topScale > 0) {
                    if (scale > topScale) {
                        scale = topScale;
                    }
                }
                System.out.println(playerCardsInfo.getUserId()+" 输的番 : "+ myPower);
                System.out.println(playerCardsInfo.getUserId()+" 输的倍数 : "+ scale);
                int gangScore = gangFan * room.getMultiple();
                int huScore = huFan * room.getMultiple() * scale;
                int subScore = gangScore + huScore;

                System.out.println("最后分数: "+ subScore);
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
            room.setUserSocre(dianpaoUser,-subHuScore);
        }
        //给自己加分
        this.addScore(sub);
        room.setUserSocre(userId,sub);
        this.fan = subFan;




    }

    private boolean isSanqing(GameInfo gameInfo){
        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            if (playerCardsInfo.getUserId() == userId) {
                continue;
            }
            if(((PlayerCardsInfoQAKT)playerCardsInfo).isKaimen()){
                return false;
            }
        }
        return true;
    }

    private boolean isKaimen(){
        int chiSize = this.chiType.size();
        int pengSize = this.pengType.size();
        int mingGangSize = this.mingGangType.size();
        int anGangSize = this.anGangType.size();
        if (chiSize > 0 || pengSize > 0 || mingGangSize > 0) {
            return true;
        }
        //五个对以上开门
        Map<Integer, Integer> map = PlayerCardsInfoMj.getCardNum(this.getCards());
        int duiSize = 0;
        for(Map.Entry<Integer,Integer> entry : map.entrySet()){
            if (entry.getValue() == 4) {
                duiSize += 2;
            }
            if (entry.getValue() == 3 || entry.getValue() == 2) {
                duiSize +=1;
            }

        }
        //暗杠加对子
        duiSize -= anGangSize*2;
        if (duiSize >= 5) {
            return true;
        }


        return false;
    }



    protected int getGangScore(Set<Integer> set,boolean isMing,Set<Integer> danList){
        int score = 0;
        for(int key : set) {
            if (key == 9 || key == 18 || key == 31) {
                score += 2;
            } else {
                score += 1;
            }
            if (danList.contains(key)) {
                score *=2;
            }
        }
        if (isMing) {
            score *= 2;
        } else {
            score *= 5;
        }
        return score;
    }

    protected int getYaoNum(List<String> cards) {
        int result = 0;
        for (String card : cards) {
            int type = CardTypeUtil.cardType.get(card);
            if (yaoList.contains(type)) {
                result++;
            }
        }
        return result;
    }
    protected boolean isHasYao(List<String> cards){
        for (String card : cards) {
            int type = CardTypeUtil.cardType.get(card);
            if (yaoList.contains(type)) {
                return true;
            }
        }
        return false;
    }









    private static void change() {
        int c = 1;
        System.out.println(c&1);
        String s = "040, 044, 048, 008, 009, 010, 016, 020, 024, 064, 068, 096, 097, 088";
        String result = "";
        for (String ss : s.split(",")) {
            result = result + "\"" + ss.trim() + "\",";
        }
        System.out.println(result);
    }


    public static void main(String[] args) {
        System.out.println("0000");
//        change();

        PlayerCardsInfoQAKT playerCardsInfo = new PlayerCardsInfoQAKT();
//        playerCardsInfo.isHasFengShun = true;
//
//
        String[] s = new String[]{"052","053","054","055",  "024","028",    "040","044","048","049","050",    "060","068",   "088"};
//
//"010","066","086","008","003","019","094","007","090","050","067","015","049","064"
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setMode("63");

        playerCardsInfo.setRoomInfo(roomInfo);
        playerCardsInfo.cards = new ArrayList<>();
        playerCardsInfo.cards.addAll(Arrays.asList(s));

        playerCardsInfo.init(playerCardsInfo.cards);
        List<List<String>> chis=new ArrayList<>();
        List<String> chi = new ArrayList<>();
        chi.add("044");
        chi.add("048");
        chi.add("052");

        List<String> chi1 = new ArrayList<>();
        chi1.add("008");
        chi1.add("012");
        chi1.add("016");

        //List<String> chi2 = new ArrayList<>();
        //chi1.add("024");
        //chi1.add("020");
        //chi1.add("028");

        //chis.add(chi);
        //chis.add(chi1);
        //chis.add(chi2);
//        chis.add(chi1);
        playerCardsInfo.mingGangType.put(13,1L);
//        playerCardsInfo.chiType.add(11);
//        playerCardsInfo.chiType.add(2);
        //playerCardsInfo.chiType.add(5);
//        playerCardsInfo.chiType.add(2);
        
//        playerCardsInfo.chiCards = chis;
//        playerCardsInfo.pengType.put(13,1);
//        playerCardsInfo.pengType.put(31,1);

//        System.out.println(playerCardsInfo.isCanTing(playerCardsInfo.cards));
        System.out.println(playerCardsInfo.isCanChiTing("021"));


    }

}
