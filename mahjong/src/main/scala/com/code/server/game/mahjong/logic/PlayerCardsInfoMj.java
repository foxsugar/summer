package com.code.server.game.mahjong.logic;


import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.mahjong.util.*;
import com.code.server.game.room.PlayerCardInfo;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by T420 on 2016/11/30.
 */
public class PlayerCardsInfoMj extends PlayerCardInfo implements HuType {
    public static final int type_gang = 1;
    public static final int type_peng = 2;
    public static final int type_ting = 3;
    public static final int type_hu = 4;
    public static final int type_mopai = 5;
    public static final int type_play = 6;
    public static final int type_chi = 7;
    public static final int type_xuanfengdan = 8;
    public static final int type_bufeng = 9;


    protected List<String> cards = new ArrayList<>();//手上的牌
    protected List<String> disCards = new ArrayList<>();//丢弃的牌
    protected Map<Integer, Long> pengType = new HashMap<>();//碰
    protected List<Integer> pengList = new ArrayList<>();//碰的列表
    protected Set<Integer> anGangType = new HashSet<>();//暗杠
    protected Map<Integer, Long> mingGangType = new HashMap<>();//明杠
    protected Set<Integer> chiType = new HashSet<>();
    protected List<List<String>> chiCards = new ArrayList<>();
    protected Map<Integer, List<String>> xuanfengDan = new HashMap<>();
    protected boolean isTing = false;
    //    protected double score;//分数
    protected Set<Integer> tingSet = new HashSet<>();//听得牌
    public int lastOperate;//上次的操作
    protected String catchCard;//上次摸得牌
    protected Set<Integer> winType = new HashSet<>();//胡牌类型
    protected boolean isHasFengShun;//有没有带风能当顺的玩法
    protected boolean isHasZiShun;
    protected boolean isHasYaojiuShun;
    protected boolean isHasSpecialHu = true;//带不带特殊胡法
    protected String huCard;//胡的牌
    protected int fan;//番数
    protected int gangScore;

    protected boolean canBeChi;
    protected boolean canBePeng;
    protected boolean canBeGang;
    protected boolean canBeHu;
    protected boolean canBeTing;
    protected boolean canBeChiTing;
    protected boolean canBePengTing;
    protected boolean canBeXuanfeng;
    protected boolean canBeBufeng;


    protected Map<Integer, Integer> specialHuScore = new HashMap<>();

    protected int nextNeedCard = -1;//下把需要抓到的牌 测试功能

    protected boolean isHasGangBlackList = false;//是否有杠的黑名单
    protected Set<Integer> gangBlackList = new HashSet<>();//杠的黑名单
    protected transient GameInfo gameInfo;
    protected transient RoomInfo roomInfo;
    protected List<Integer> operateList = new ArrayList<>();
    protected boolean isAlreadyHu = false;
    protected Set<Integer> baoMingDan = new HashSet<>();
    protected Set<Integer> baoAnDan = new HashSet<>();
    protected boolean isShowAnGang = true;
    protected Set<Integer> yiZhangyingSet = new HashSet<>();

    protected boolean isPlayHun = false;//是否打出混

    private boolean isGuoHu;

    protected boolean isJieGangHu = false;
    protected List<HuCardType> tingWhatInfo = new ArrayList<>();
    protected Set<Integer> guoPengSet = new HashSet<>();
    protected int paofen = -1;
    protected String koutingCard = "";
    public int dingqueGroupType = 0;
    public List<String> changeCards = new ArrayList<>();

    //其他人的杠分往来
    protected Map<Long, Double> otherGangScore = new HashMap<>();
    //上次杠牌的分数
    protected double lastGangScore = 0;

    protected List<Integer> noScoreGang = new ArrayList<>();

    /**
     * 根据发的牌初始化
     *
     * @param cards
     */
    public void init(List<String> cards) {
        this.cards = cards;
    }

    /**
     * 是否荒庄
     *
     * @param gameInfo
     * @return
     */
    public boolean isHuangzhuang(GameInfo gameInfo) {
        return gameInfo.getRemainCards().size() <= 0;
    }

    /**
     * 摸牌
     *
     * @param card
     */
    public void mopai(String card) {
        //加入手牌列表
        this.cards.add(card);
        this.lastOperate = type_mopai;
        this.catchCard = card;
        operateList.add(type_mopai);
        this.gameInfo.addUserOperate(this.userId, type_mopai);
    }

    /**
     * 出牌
     *
     * @param card
     */
    public void chupai(String card) {
        boolean isRemove = this.cards.remove(card);
        if (!isRemove) {
            gameInfo.logger.error("userId : " + userId + " 删牌 没有删掉 : " + card);
            gameInfo.logger.error("玩家的牌 : " + this.cards);
            gameInfo.logger.error("明杠 : " + this.mingGangType);
            gameInfo.logger.error("暗杠 : " + this.anGangType);

        }
        this.disCards.add(card);
        this.lastOperate = type_play;
        operateList.add(type_play);
        this.gameInfo.addUserOperate(this.userId, type_play);
    }

    public void chi(String card, String one, String two) {
        this.cards.add(card);
        List<String> list = new ArrayList<>();
        list.add(one);
        list.add(card);
        list.add(two);
        chiCards.add(list);
        List<String> temp = new ArrayList<>();
        temp.addAll(list);
        Collections.sort(temp);
        chiType.add(CardTypeUtil.cardType.get(temp.get(0)));
        this.lastOperate = type_chi;
        operateList.add(type_chi);
        this.gameInfo.addUserOperate(this.userId, type_chi);
    }

    /**
     * 碰
     *
     * @param card
     * @param playUser 碰的谁的牌
     */
    public void peng(String card, long playUser) {
        this.cards.add(card);
        int cardType = CardTypeUtil.cardType.get(card);
        pengType.put(cardType, playUser);
        pengList.add(cardType);
        this.lastOperate = type_peng;
        operateList.add(type_peng);
        Map<Integer, Integer> map = getCardNum(cards);
        if (isHasGangBlackList) {
            if (map.get(cardType) != null && map.get(cardType) == 4) {
                gangBlackList.add(cardType);
            }
        }
        this.gameInfo.addUserOperate(this.userId, type_peng);

    }

    /**
     * 听
     */
    public void ting(String card) {
        //出牌 弃牌置为空(客户端扣牌)
        this.cards.remove(card);
        this.disCards.add(null);

        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);
    }

    /**
     * 亮
     *
     * @param cardType
     * @param cards
     */
    public void liang(int cardType, List<String> cards) {
        this.xuanfengDan.put(cardType, cards);
        this.lastOperate = type_xuanfengdan;
        operateList.add(type_xuanfengdan);
        this.gameInfo.addUserOperate(this.userId, type_xuanfengdan);

    }

    /**
     * 获得临时牌集合
     *
     * @param disCard
     * @return
     */
    protected List<String> getCardsAddThisCard(String disCard) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        temp.add(disCard);
        return temp;
    }

    /**
     * 每种牌的数量 34种
     *
     * @param cards
     * @return
     */
    public static Map<Integer, Integer> getCardNum(List<String> cards) {
        Map<Integer, Integer> result = new HashMap<>();
        for (String card : cards) {
            int type = CardTypeUtil.cardType.get(card);
            if (result.containsKey(type)) {
                result.put(type, result.get(type) + 1);
            } else {
                result.put(type, 1);
            }
        }
        return result;
    }

    /**
     * 检测出牌是否合法
     *
     * @param card
     * @return
     */
    public boolean checkPlayCard(String card) {
        if (!this.cards.contains(card)) {
            return false;
        }
        List<String> temp = getCardsNoChiPengGang(cards);
        boolean isMore = (temp.size() - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        int ct = CardTypeUtil.cardType.get(card);
        boolean isHas = temp.contains(card);
        for (String c : temp) {
            int type = CardTypeUtil.cardType.get(c);
            if (type == ct) {
                isHas = true;
                break;
            }
        }
        boolean tingCheck = !isTing || card.equals(catchCard);
        return isHas && isMore && tingCheck;
    }

    /**
     * 是否可吃这张牌
     *
     * @param card
     * @param one
     * @param two
     * @return
     */
    public boolean isCanChiThisCard(String card, String one, String two) {
        List<String> temp = getCardsNoChiPengGang(cards);
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        int cardType = CardTypeUtil.cardType.get(card);
        int oneType = CardTypeUtil.cardType.get(one);
        int twoType = CardTypeUtil.cardType.get(two);
        List<Integer> list = new ArrayList<>();
        list.add(cardType);
        list.add(oneType);
        list.add(twoType);
        Collections.sort(list);
        if (list.get(0) + 1 != list.get(1) || list.get(0) + 2 != list.get(2)) {
            return false;
        }
        int group = CardTypeUtil.getCardGroup(card);
        if (CardTypeUtil.GROUP_FENG == group || CardTypeUtil.GROUP_ZI == group) {
            return false;
        }
        boolean isSameGroup = CardTypeUtil.getCardGroup(card) == CardTypeUtil.getCardGroup(one)
                && CardTypeUtil.getCardGroup(card) == CardTypeUtil.getCardGroup(two);
        return isSameGroup && cardsNum.containsKey(oneType) && cardsNum.get(oneType) > 0
                && cardsNum.containsKey(twoType) && cardsNum.get(twoType) > 0;
    }

    protected void addGuoPeng(String card) {
        int type = CardTypeUtil.getTypeByCard(card);
        this.guoPengSet.add(type);
    }

    protected boolean isGuoPeng(String card) {
        int type = CardTypeUtil.getTypeByCard(card);
        return this.guoPengSet.contains(type);
    }

    public boolean isHasChi(String card) {
        return getChiList(getCardsNoChiPengGang(cards), card).size() > 0;
    }

    public boolean isCanChiTing(String card) {
        return false;
    }

    public boolean isCanPengTing(String card) {
        return false;
    }

    private boolean isHasThisCard(Map<Integer, Integer> map, int type) {
        return map.containsKey(type) && map.get(type) > 0;
    }

    private String getACardByType(List<String> list, int type) {
        for (String l : list) {
            int t = CardTypeUtil.cardType.get(l);
            if (t == type) {
                return l;
            }
        }
        return null;
    }

    /**
     * 获得可以构成吃的列表
     *
     * @param card
     * @return
     */
    public List<List<String>> getChiList(List<String> cards, String card) {
        int cardType = CardTypeUtil.cardType.get(card);
        List<List<String>> result = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        int group = CardTypeUtil.getCardGroup(card);
        if (group == CardTypeUtil.GROUP_FENG || group == CardTypeUtil.GROUP_ZI) {
            return result;
        }
        //吃的这张牌是第一张
        if (isHasThisCard(cardsNum, cardType + 1) && CardTypeUtil.getCardGroupByCardType(cardType + 1) == group
                && isHasThisCard(cardsNum, cardType + 2) && CardTypeUtil.getCardGroupByCardType(cardType + 2) == group) {

            List<String> chi = new ArrayList<>();
            chi.add(getACardByType(temp, cardType + 1));
            chi.add(getACardByType(temp, cardType + 2));
            chi.add(card);
            result.add(chi);
        }
        //吃的这张牌是第二张
        if (isHasThisCard(cardsNum, cardType - 1) && CardTypeUtil.getCardGroupByCardType(cardType - 1) == group
                && isHasThisCard(cardsNum, cardType + 1) && CardTypeUtil.getCardGroupByCardType(cardType + 1) == group) {
            List<String> chi = new ArrayList<>();
            chi.add(getACardByType(temp, cardType - 1));
            chi.add(getACardByType(temp, cardType + 1));
            chi.add(card);
            result.add(chi);
        }
        //吃的这张牌是第三张
        if (isHasThisCard(cardsNum, cardType - 1) && CardTypeUtil.getCardGroupByCardType(cardType - 1) == group
                && isHasThisCard(cardsNum, cardType - 2) && CardTypeUtil.getCardGroupByCardType(cardType - 2) == group) {
            List<String> chi = new ArrayList<>();
            chi.add(getACardByType(temp, cardType - 1));
            chi.add(getACardByType(temp, cardType - 2));
            chi.add(card);
            result.add(chi);
        }
        return result;
    }


    protected void removeGang2Peng(String card) {
        int type = CardTypeUtil.getTypeByCard(card);
        mingGangType.remove(type);
        pengType.put(type, -1L);

    }

    /**
     * 能否碰这张牌
     *
     * @param card
     * @return
     */
    public boolean isCanPengAddThisCard(String card) {
        List<String> temp = getCardsNoChiPengGang(cards);
        int cardType = CardTypeUtil.cardType.get(card);
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        return cardsNum.containsKey(cardType) && cardsNum.get(cardType) >= 2;


    }

    /**
     * 加上这张牌能否杠
     *
     * @param card
     * @return
     */
    public boolean isCanGangAddThisCard(String card) {
        if (cards.contains(card)) {
            return false;
        }

        List<String> temp = getCardsNoChiPengGang(cards);
        int cardType = CardTypeUtil.cardType.get(card);
        Map<Integer, Integer> cardsNum = getCardNum(temp);

        boolean isPeng = pengType.containsKey(cardType);
        //碰过之后 不能吃杠
        return !isPeng && cardsNum.containsKey(cardType) && cardsNum.get(cardType) == 3;
    }

    /**
     * 能否杠这张牌
     *
     * @param card
     * @return
     */
    public boolean isCanGangThisCard(String card) {
        List<String> temp = getCardsNoGang(cards);
        //去掉吃
        for (List<String> chi : chiCards) {
            temp.removeAll(chi);
        }
        int cardType = CardTypeUtil.cardType.get(card);
        if (gangBlackList.contains(cardType)) {
            return false;
        }
        Map<Integer, Integer> cardsNum = getCardNum(temp);
        return cardsNum.containsKey(cardType) && cardsNum.get(cardType) == 4;
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
        System.out.println("检测是否可胡点炮= " + noPengAndGang);
        int cardType = CardTypeUtil.cardType.get(card);
        return HuUtil.isHu(noPengAndGang, this, cardType, null).size() > 0;
    }

    /**
     * 是否可胡 自摸
     *
     * @param card
     * @return
     */
    public boolean isCanHu_zimo(String card) {
        List<String> cs = getCardsNoChiPengGang(cards);
        System.out.println("检测是否可胡自摸= " + cs);
        int cardType = CardTypeUtil.cardType.get(card);
        return HuUtil.isHu(cs, this, cardType, null).size() > 0;

    }

    /**
     * 是否有杠
     *
     * @return
     */
    public boolean isHasGang() {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set set = getHasGangList(temp);
        return set.size() > 0;
    }

    /**
     * 得到能杠的列表(去掉已经杠过的)
     *
     * @param cards
     * @return
     */
    public Set<Integer> getHasGangList(List<String> cards) {
        Set<Integer> set = new HashSet<>();
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);

        //去掉旋风蛋
        for (List<String> xuanfengdanCard : xuanfengDan.values()) {
            temp.removeAll(xuanfengdanCard);
        }

        //去掉吃
        for (List<String> chi : chiCards) {
            temp.removeAll(chi);
        }
        //去掉杠
        for (int type : mingGangType.keySet()) {
            removeCardByType(temp, type, 4);
        }
        for (int type : anGangType) {
            removeCardByType(temp, type, 4);
        }


        for (Map.Entry<Integer, Integer> entry : getCardNum(temp).entrySet()) {
            if (gangBlackList.contains(entry.getKey())) {
                continue;
            }
            int size = entry.getValue();
            if (size == 4) {
                set.add(entry.getKey());
            }
        }
        return set;
    }

    public int getHunNum() {
        int result = 0;
        for (String s : getCardsNoChiPengGang(cards)) {
            int type = CardTypeUtil.getTypeByCard(s);
            if (this.gameInfo.hun.contains(type)) {
                result++;
            }

        }
        return result;
    }

    public int getGangNum() {
        return this.mingGangType.size() + this.anGangType.size();
    }

    /**
     * 是否有旋风蛋
     *
     * @param cards
     * @return
     */
    public boolean isHasXuanfengDan(List<String> cards, String card) {
        return false;
    }

    public boolean isCanBufeng(String card) {
        return false;
    }

    protected void setCanBeOperate(boolean chi, boolean peng, boolean gang, boolean ting, boolean hu, boolean chiTing, boolean pengTing) {
        this.canBeChi = chi;
        this.canBePeng = peng;
        this.canBeGang = gang;
        this.canBeTing = ting;
        this.canBeHu = hu;
        this.canBeChiTing = chiTing;
        this.canBePengTing = pengTing;
    }


    public boolean isHasSpecialHu(int huType) {
        return specialHuScore.containsKey(huType);
    }

    public int getSpecialHuScore(int huType) {
        return specialHuScore.get(huType);
    }


    public boolean isMoreOneCard() {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        List<String> cs = getCardsNoChiPengGang(temp);
        return (cs.size() - 2) % 3 == 0;
    }

    public boolean isCanTing(List<String> cards) {
        return getTingCardType(getCardsNoChiPengGang(cards), null).size() > 0;
    }

    public Set<Integer> getTingCardType(List<String> cards, HuLimit limit) {
        //获得没有碰和杠的牌
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        Set<Integer> tingList = new HashSet<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
            for (String card : handCards) {
                List<String> tempCards = new ArrayList<>();
                tempCards.addAll(handCards);
                tempCards.remove(card);
                tingList.addAll(HuUtil.isTing(tempCards, this, limit));
            }
        } else {
            tingList.addAll(HuUtil.isTing(handCards, this, limit));
        }
        return tingList;
    }

    public void tingWhat() {

    }

    public List<HuCardType> getTingHuCardType(List<String> cards, HuLimit limit) {
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        List<HuCardType> tingList = new ArrayList<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
            for (String card : handCards) {
                List<String> tempCards = new ArrayList<>();
                tempCards.addAll(handCards);
                tempCards.remove(card);
                tingList.addAll(HuUtil.getTingHuList(tempCards, this, limit, card));
            }
        } else {
            tingList.addAll(HuUtil.getTingHuList(handCards, this, limit, null));
        }
        return tingList;
    }


    public List<HuCardType> getTingHuCardTypeWithHun(List<String> cards, List<Integer> hun, int chiPengGangNum, Predicate<Integer> predicate) {
        List<String> handCards = new ArrayList<>();
        handCards.addAll(cards);

        //是否多一张牌
        int size = handCards.size();
        boolean isMore = (size - 2) % 3 == 0;//去掉将如果能整除说明手牌多一张
        List<HuCardType> tingList = new ArrayList<>();
        if (isMore) {//多一张
            //循环去掉一张看能否听
            for (String card : handCards) {
//                int cardType = CardTypeUtil.getTypeByCard(card);
//                if(hun.contains(cardType)) continue;
                List<String> tempCards = new ArrayList<>();
                tempCards.addAll(handCards);
                tempCards.remove(card);
                tingList.addAll(HuUtil.getTingHuListWithHun(tempCards, this, hun, card, chiPengGangNum, predicate));
            }
        } else {
            tingList.addAll(HuUtil.getTingHuListWithHun(handCards, this, hun, null, chiPengGangNum, predicate));
        }
        return tingList;
    }

    public boolean isYizhangying(List<String> cards, String huCard) {
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        temp.remove(huCard);
        Set<Integer> set = getTingCardType(temp, null);
        int ct = CardTypeUtil.cardType.get(huCard);
        return set.size() == 1 && set.contains(ct);
    }


    /**
     * 杠手里的牌
     *
     * @param diangangUser
     * @param card
     * @return
     */

    public boolean gang_hand(RoomInfo room, GameInfo info, long diangangUser, String card) {
        boolean isMing = false;
        int cardType = CardTypeUtil.cardType.get(card);
        Map<Integer, Integer> cardNum = getCardNum(cards);
        long diangang = -1;
        if (cardNum.containsKey(cardType) && cardNum.get(cardType) == 4) {
            if (pengType.containsKey(cardType)) {//碰的类型包含这个 是明杠
                //diangang = pengType.get(huType);
                pengType.remove(cardType);//从碰中移除
                mingGangType.put(cardType, diangang);
                pengList.remove(Integer.valueOf(cardType));
                isMing = true;

            } else {
                anGangType.add(cardType);
                isMing = false;

            }
        }
//        gangCompute(room, info, isMing, diangang,card);
        return isMing;
    }

    public void bu_feng(RoomInfo roomInfo, GameInfo gameInfo, String card) {
        List<String> cards = this.xuanfengDan.getOrDefault(1, new ArrayList<>());
        cards.add(card);
        this.xuanfengDan.put(1, cards);
        operateList.add(type_bufeng);
        this.gameInfo.addUserOperate(this.userId, type_bufeng);
    }

    /**
     * 杠弃牌
     *
     * @param diangangUser
     * @param disCard
     * @return
     */
    public boolean gang_discard(RoomInfo room, GameInfo gameInfo, long diangangUser, String disCard) {
        this.cards.add(disCard);
        int cardType = CardTypeUtil.cardType.get(disCard);
        mingGangType.put(cardType, diangangUser);
        gangCompute(room, gameInfo, true, diangangUser, disCard);

        return false;
    }

    //杠牌分数计算
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        this.lastOperate = type_gang;

        operateList.add(type_gang);
        this.gameInfo.addUserOperate(this.userId, type_gang);


        if (isMing) {
            this.roomInfo.addMingGangNum(this.getUserId());
        } else {
            this.roomInfo.addAnGangNum(this.getUserId());
        }

    }

    //胡牌分数计算
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

    }

    public void computeALLGang() {


    }

    /**
     * 从牌中删除
     *
     * @param list
     * @param cards
     */
    protected void removeCards(List<String> list, List<String> cards) {
        for (String card : cards) {
            int type = CardTypeUtil.getTypeByCard(card);
            removeCardByType(list, type, 1);
        }
    }

    /**
     * 获得除掉碰和杠的牌
     *
     * @param list
     * @return
     */
    public List<String> getCardsNoChiPengGang(List<String> list) {
        List<String> temp = new ArrayList<>();
        temp.addAll(list);
        for (List<String> xfd : xuanfengDan.values()) {
            temp.removeAll(xfd);
//            removeCards(temp, xfd);
        }
//        System.out.println("去掉吃前 size : "+temp.size());
        for (List<String> cc : chiCards) {
//            removeCards(temp, cc);
            temp.removeAll(cc);
        }
//        System.out.println("去掉吃后 size : "+temp.size());
        for (int type : mingGangType.keySet()) {
            removeCardByType(temp, type, 4);
        }
        for (int type : anGangType) {
            removeCardByType(temp, type, 4);
        }
        for (int type : pengType.keySet()) {
            removeCardByType(temp, type, 3);
        }
        return temp;

    }

    public List<String> getCardsNoGang(List<String> list) {
        List<String> temp = new ArrayList<>();
        temp.addAll(list);
        for (int type : mingGangType.keySet()) {
            removeCardByType(temp, type, 4);
        }
        for (int type : anGangType) {
            removeCardByType(temp, type, 4);
        }
        return temp;
    }

    public List<String> getCardsNoChiGang(List<String> list) {
        List<String> temp = new ArrayList<>();
        temp.addAll(list);
        temp = getCardsNoGang(temp);
        for (List<String> chi : chiCards) {
            temp.removeAll(chi);
        }
        for (List<String> xuanfeng : xuanfengDan.values()) {
            temp.removeAll(xuanfeng);
        }
        return temp;
    }

    public int getChiPengGangNum() {
        return this.chiCards.size() + this.pengType.size() + this.mingGangType.size() + this.anGangType.size();
    }


    /**
     * 类描述：   自摸分数计算
     * 创建人：Clark
     * 创建时间：2016年12月4日 下午4:51:18
     * 修改人：Clark
     * 修改时间：2016年12月4日 下午4:51:18
     * 修改备注：
     *
     * @version 1.0
     */

    public void hu_zm(RoomInfo room, GameInfo gameInfo, String card) {
        this.huCard = card;
        huCompute(room, gameInfo, true, 0, card);
        this.lastOperate = type_hu;
        gameInfo.isAlreadyHu = true;
        this.isAlreadyHu = true;
        //胡牌次数
        room.addHuNum(this.userId);
        room.addZimoNum(this.userId);
        //连庄次数
        if (gameInfo.getFirstTurn() == userId) {
            room.addLianZhuangNum(this.userId);
        }
    }

    /**
     * 类描述：   胡点炮分数计算
     * 创建人：Clark
     * 创建时间：2016年12月4日 下午4:51:48
     * 修改人：Clark
     * 修改时间：2016年12月4日 下午4:51:48
     * 修改备注：
     *
     * @version 1.0
     */

    public void hu_dianpao(RoomInfo room, GameInfo gameInfo, long dianpaoUser, String disCard) {
        //胡牌次数
        room.addHuNum(this.userId);
        room.addJiePaoNum(this.userId);
        //连庄次数
        if (gameInfo.getFirstTurn() == userId) {
            room.addLianZhuangNum(this.userId);
        }
        //点炮次数
        room.addDianPaoNum(dianpaoUser);

        this.cards.add(disCard);
        this.huCard = disCard;
        huCompute(room, gameInfo, false, dianpaoUser, disCard);
        this.lastOperate = type_hu;

    }


    /**
     * 是否是杠开
     *
     * @return
     */
    protected boolean isGangKai() {
        int size = this.operateList.size();
        return size >= 2 && this.operateList.get(size - 2) == type_gang;
    }

    /**
     * 是否是天胡
     * @return
     */
    protected boolean isTianhu(){
        return this.userId == this.gameInfo.getFirstTurn() && this.operateList.size() == 1 && this.operateList.get(0) == type_mopai;
    }

    /**
     * 是否是地胡
     * @return
     */
    protected boolean isDihu(){
        return this.userId != this.gameInfo.getFirstTurn() && this.operateList.size() == 1 && this.operateList.get(0) == type_mopai;
    }

    protected boolean isGangPao(){
        int operateSize = this.operateList.size();
        return (operateSize>=3 && this.operateList.get(operateSize - 3) == type_gang);
    }

    /**
     * 查大叫
     * @return
     */
    protected boolean chaDajiao() {
        return isCanTing(cards);
    }

    /**
     * 查花猪
     * @return
     */
    protected boolean chaHuazhu(){
        Set<Integer> group = new HashSet<>();
       this.cards.forEach(card->group.add(CardTypeUtil.getCardGroup(card)));
       return group.size() == 3;
    }

    protected int getMaxTingScore(){
        return 0;
    }
    /**
     * 是否是sanpeng
     *
     * @return
     */
    protected boolean isSanPeng() {
        return true;
    }

    /**
     * 按类型删除card
     *
     * @param list
     * @param type
     * @param num
     */
    public static void removeCardByType(List<String> list, int type, int num) {
        List<String> removeList = new ArrayList<>();
        for (String card : list) {
            if (type == CardTypeUtil.cardType.get(card)) {
                removeList.add(card);
            }
        }
        for (int i = 0; i < num; i++) {
            list.remove(removeList.get(i));
        }
    }

    public double addScore(double s) {
        this.score = this.score + s;
        return this.score;
    }

    public int addGangScore(int score) {
        this.gangScore = this.gangScore + score;
        if (score > 0) {
            this.lastGangScore = score;
        }
        return this.gangScore;
    }

    protected void setWinTypeResult(HuCardType huCardType) {
        this.winType.addAll(huCardType.specialHuList);
    }

    protected HuCardType getMaxScoreHuCardType(List<HuCardType> list) {
        if (list.size() <= 0) {
            return null;
        }
        Collections.sort(list, new Comparator<HuCardType>() {
            @Override
            public int compare(HuCardType o1, HuCardType o2) {
                if (o1.fan > o2.fan) {
                    return -1;
                } else if (o1.fan < o2.fan) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return list.get(0);
    }

    public void baoDan(String card) {
        //加入手牌列表
        this.cards.add(card);
        this.lastOperate = type_mopai;
        this.catchCard = card;
        operateList.add(type_mopai);
    }

    protected int getCardGroup(List<String> cs) {
        Set<Integer> set = new HashSet<>();
        for (String s : cs) {
            int group = CardTypeUtil.getCardGroup(s);
            if (group == CardTypeUtil.GROUP_TONG || group == CardTypeUtil.GROUP_TIAO || group == CardTypeUtil.GROUP_WAN) {
                set.add(group);
            }
        }
        return set.size();
    }

    protected static boolean isHasMode(String mode, int type) {
        int c = Integer.parseInt(mode);
        return (c & (1 << type)) >> type == 1;
    }

    public int getPuMutiple(){
        return 1;
    }

    public static void main(String[] args) {

//        PlayerCardsInfoMj playerCardsInfo = new PlayerCardsInfoMj();
//
//        playerCardsInfo.isHasFengShun = true;
//
////        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","042","124","135"};
////        String[] s = new String[]{"064","051","097","132","045","067","101","133","092","065","134","042","124","135"};
////        String[] s = new String[]{"124","128","132",     "016",  "017","018",    "020",     "024","025","026",   "028","029",    "032","033"};
//        String[] s = new String[]{"012", "013", "014", "016", "017", "018", "020", "024", "025", "026", "028", "029", "032", "033"};
//
//        List<String> cs = Arrays.asList(s);
//        playerCardsInfo.cards = cs;
//        playerCardsInfo.init(playerCardsInfo.cards);
////        System.out.println(playerCardsInfo.getTingCardType(playerCardsInfo.cards,null));
//        System.out.println(playerCardsInfo.isCanHu_zimo("033"));
        System.out.println(isHasMode("32", 5));

        System.out.println(getNum(5));

    }

    private static int getNum(int weishu) {
        return 1 << weishu;
    }


    public long getUserId() {
        return userId;
    }

    public PlayerCardsInfoMj setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public List<String> getCards() {
        return cards;
    }


    public void setCards(List<String> cards) {
        this.cards = cards;
    }


    public List<String> getDisCards() {
        return disCards;
    }


    public void setDisCards(List<String> disCards) {
        this.disCards = disCards;
    }


    public Set<Integer> getAnGangType() {
        return anGangType;
    }


    public void setAnGangType(Set<Integer> anGangType) {
        this.anGangType = anGangType;
    }


    public Map<Integer, Long> getPengType() {
        return pengType;
    }

    public PlayerCardsInfoMj setPengType(Map<Integer, Long> pengType) {
        this.pengType = pengType;
        return this;
    }

    public Map<Integer, Long> getMingGangType() {
        return mingGangType;
    }

    public PlayerCardsInfoMj setMingGangType(Map<Integer, Long> mingGangType) {
        this.mingGangType = mingGangType;
        return this;
    }

    public boolean isTing() {
        return isTing;
    }


    public void setTing(boolean isTing) {
        this.isTing = isTing;
    }

    public void setIsTing(boolean isTing) {
        this.isTing = isTing;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public PlayerCardsInfoMj setScore(double score) {
        this.score = score;
        return this;
    }

    public int getLastOperate() {
        return lastOperate;
    }

    public PlayerCardsInfoMj setLastOperate(int lastOperate) {
        this.lastOperate = lastOperate;
        return this;
    }

    public Set<Integer> getWinType() {
        return winType;
    }

    public PlayerCardsInfoMj setWinType(Set<Integer> winType) {
        this.winType = winType;
        return this;
    }

    public String getCatchCard() {
        return catchCard;
    }

    public PlayerCardsInfoMj setCatchCard(String catchCard) {
        this.catchCard = catchCard;
        return this;
    }

    public boolean isCanBePeng() {
        return canBePeng;
    }

    public PlayerCardsInfoMj setCanBePeng(boolean canBePeng) {
        this.canBePeng = canBePeng;
        return this;
    }

    public boolean isCanBeGang() {
        return canBeGang;
    }

    public PlayerCardsInfoMj setCanBeGang(boolean canBeGang) {
        this.canBeGang = canBeGang;
        return this;
    }

    public boolean isCanBeHu() {
        return canBeHu;
    }

    public PlayerCardsInfoMj setCanBeHu(boolean canBeHu) {
        this.canBeHu = canBeHu;
        return this;
    }

    public boolean isCanBeTing() {
        return canBeTing;
    }

    public PlayerCardsInfoMj setCanBeTing(boolean canBeTing) {
        this.canBeTing = canBeTing;
        return this;
    }

    public Set<Integer> getTingSet() {
        return tingSet;
    }

    public PlayerCardsInfoMj setTingSet(Set<Integer> tingSet) {
        this.tingSet = tingSet;
        return this;
    }

    public String getHuCard() {
        return huCard;
    }

    public PlayerCardsInfoMj setHuCard(String huCard) {
        this.huCard = huCard;
        return this;
    }

    public boolean isHasFengShun() {
        return isHasFengShun;
    }

    public PlayerCardsInfoMj setIsHasFengShun(boolean isHasFengShun) {
        this.isHasFengShun = isHasFengShun;
        return this;
    }

    public boolean isHasSpecialHu() {
        return isHasSpecialHu;
    }

    public PlayerCardsInfoMj setIsHasSpecialHu(boolean isHasSpecialHu) {
        this.isHasSpecialHu = isHasSpecialHu;
        return this;
    }

    public int getFan() {
        return fan;
    }

    public PlayerCardsInfoMj setFan(int fan) {
        this.fan = fan;
        return this;
    }

    public Map<Integer, Integer> getSpecialHuScore() {
        return specialHuScore;
    }

    public PlayerCardsInfoMj setSpecialHuScore(Map<Integer, Integer> specialHuScore) {
        this.specialHuScore = specialHuScore;
        return this;
    }

    public boolean isHasGangBlackList() {
        return isHasGangBlackList;
    }

    public PlayerCardsInfoMj setHasGangBlackList(boolean hasGangBlackList) {
        isHasGangBlackList = hasGangBlackList;
        return this;
    }

    public Set<Integer> getChiType() {
        return chiType;
    }

    public PlayerCardsInfoMj setChiType(Set<Integer> chiType) {
        this.chiType = chiType;
        return this;
    }

    public List<List<String>> getChiCards() {
        return chiCards;
    }

    public PlayerCardsInfoMj setChiCards(List<List<String>> chiCards) {
        this.chiCards = chiCards;
        return this;
    }

    public boolean isCanBeChi() {
        return canBeChi;
    }

    public PlayerCardsInfoMj setCanBeChi(boolean canBeChi) {
        this.canBeChi = canBeChi;
        return this;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public PlayerCardsInfoMj setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        return this;
    }

    public boolean isCanBeChiTing() {
        return canBeChiTing;
    }

    public PlayerCardsInfoMj setCanBeChiTing(boolean canBeChiTing) {
        this.canBeChiTing = canBeChiTing;
        return this;
    }

    public boolean isCanBePengTing() {
        return canBePengTing;
    }

    public PlayerCardsInfoMj setCanBePengTing(boolean canBePengTing) {
        this.canBePengTing = canBePengTing;
        return this;
    }

    public boolean isAlreadyHu() {
        return isAlreadyHu;
    }

    public PlayerCardsInfoMj setAlreadyHu(boolean alreadyHu) {
        isAlreadyHu = alreadyHu;
        return this;
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public PlayerCardsInfoMj setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        return this;
    }

    public Set<Integer> getYiZhangyingSet() {
        return yiZhangyingSet;
    }

    public PlayerCardsInfoMj setYiZhangyingSet(Set<Integer> yiZhangyingSet) {
        this.yiZhangyingSet = yiZhangyingSet;
        return this;
    }

    public Set<Integer> getBaoMingDan() {
        return baoMingDan;
    }

    public PlayerCardsInfoMj setBaoMingDan(Set<Integer> baoMingDan) {
        this.baoMingDan = baoMingDan;
        return this;
    }

    public Set<Integer> getBaoAnDan() {
        return baoAnDan;
    }

    public PlayerCardsInfoMj setBaoAnDan(Set<Integer> baoAnDan) {
        this.baoAnDan = baoAnDan;
        return this;
    }

    public Map<Integer, List<String>> getXuanfengDan() {
        return xuanfengDan;
    }

    public PlayerCardsInfoMj setXuanfengDan(Map<Integer, List<String>> xuanfengDan) {
        this.xuanfengDan = xuanfengDan;
        return this;
    }

    public boolean isCanBeXuanfeng() {
        return canBeXuanfeng;
    }

    public PlayerCardsInfoMj setCanBeXuanfeng(boolean canBeXuanfeng) {
        this.canBeXuanfeng = canBeXuanfeng;
        return this;
    }

    public int getGangScore() {
        return gangScore;
    }

    public PlayerCardsInfoMj setGangScore(int gangScore) {
        this.gangScore = gangScore;
        return this;
    }


    @Override
    public IfacePlayerInfoVo toVo() {
        return null;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return null;
    }

    public boolean isGuoHu() {
        return isGuoHu;
    }

    public void setGuoHu(boolean guoHu) {
        isGuoHu = guoHu;
    }

    public boolean isJieGangHu() {
        return isJieGangHu;
    }

    public PlayerCardsInfoMj setJieGangHu(boolean jieGangHu) {
        isJieGangHu = jieGangHu;
        return this;
    }

    public List<HuCardType> getTingWhatInfo() {
        return tingWhatInfo;
    }

    public PlayerCardsInfoMj setTingWhatInfo(List<HuCardType> tingWhatInfo) {
        this.tingWhatInfo = tingWhatInfo;
        return this;
    }

    public boolean isCanBeBufeng() {
        return canBeBufeng;
    }

    public PlayerCardsInfoMj setCanBeBufeng(boolean canBeBufeng) {
        this.canBeBufeng = canBeBufeng;
        return this;
    }

    public boolean isHasZiShun() {
        return isHasZiShun;
    }

    public PlayerCardsInfoMj setHasZiShun(boolean hasZiShun) {
        isHasZiShun = hasZiShun;
        return this;
    }

    public boolean isHasYaojiuShun() {
        return isHasYaojiuShun;
    }

    public PlayerCardsInfoMj setHasYaojiuShun(boolean hasYaojiuShun) {
        isHasYaojiuShun = hasYaojiuShun;
        return this;
    }

    public int getPaofen() {
        return paofen;
    }

    public PlayerCardsInfoMj setPaofen(int paofen) {
        this.paofen = paofen;
        return this;
    }

    public String getKoutingCard() {
        return koutingCard;
    }

    public PlayerCardsInfoMj setKoutingCard(String koutingCard) {
        this.koutingCard = koutingCard;
        return this;
    }

    public int getDingqueGroupType() {
        return dingqueGroupType;
    }

    public PlayerCardsInfoMj setDingqueGroupType(int dingqueGroupType) {
        this.dingqueGroupType = dingqueGroupType;
        return this;
    }

    public List<Integer> getPengList() {
        return pengList;
    }

    public PlayerCardsInfoMj setPengList(List<Integer> pengList) {
        this.pengList = pengList;
        return this;
    }

    public Map<Long, Double> getOtherGangScore() {
        return otherGangScore;
    }

    public PlayerCardsInfoMj setOtherGangScore(Map<Long, Double> otherGangScore) {
        this.otherGangScore = otherGangScore;
        return this;
    }

    public List<String> getChangeCards() {
        return changeCards;
    }

    public PlayerCardsInfoMj setChangeCards(List<String> changeCards) {
        this.changeCards = changeCards;
        return this;
    }
}

