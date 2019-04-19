package com.code.server.game.mahjong.response;


import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;
import com.code.server.game.mahjong.logic.PlayerCardsInfoLS;

import java.util.*;


/**
 * Created by win7 on 2016/12/4.
 */
public class PlayerCardsResp {

    private long userId;
    List<String> cards = new ArrayList<>();//手上的牌
    List<String> handCards = new ArrayList<>();
    private List<String> disCards = new ArrayList<>();//丢弃的牌
    private Set<Integer> anGangType = new HashSet<>();//暗杠
    private List<Group> pengType = new ArrayList<>();
    private List<Group> mingGangType = new ArrayList<>();
    private List<List<String>> chiCards = new ArrayList<>();
    private Map<Integer, List<String>> xuanfengdan = new HashMap<>();
    private List<String> firstfourLS = new ArrayList<>();//立四前四张
    private boolean isTing = false;
    private double score;
    private int lastOperate;
    private String catchCard;

    private boolean canBePeng;
    private boolean canBeGang;
    private boolean canBeHu;
    private boolean canBeTing;
    private boolean canBeChi;
    private boolean canBeChiTing;
    private boolean canBePengTing;
    private boolean canBeXuanfengDan;
    private boolean canBeBufeng;
    private String huCard;

    private List<Integer> winType = new ArrayList<>();
    private int fan;
    private double allScore;
    private double gangScore;
    private Set<Integer> baoMingDan = new HashSet<>();
    private Set<Integer> baoAnDan = new HashSet<>();
    private int paofen = -1;
    private String koutingCard;
    public int dingqueGroupType = 0;
    public List<String> changeCards = new ArrayList<>();

    

    public PlayerCardsResp(){}

    public PlayerCardsResp(PlayerCardsInfoMj info) {
        init(info, true);
    }


    public PlayerCardsResp(PlayerCardsInfoMj info, boolean isMine) {
        init(info, isMine);

    }

    public void init(PlayerCardsInfoMj info, boolean isMine) {
        if (isMine) {
            this.cards.addAll(info.getCards());
            this.handCards = info.getCardsNoChiPengGang(info.getCards());
            this.catchCard = info.getCatchCard();
            this.canBeGang = info.isCanBeGang();
            this.canBePeng = info.isCanBePeng();
            this.canBeHu = info.isCanBeHu();
            this.canBeTing = info.isCanBeTing();
            this.canBeChi = info.isCanBeChi();
            this.canBeChiTing = info.isCanBeChiTing();
            this.canBePengTing = info.isCanBePengTing();
            this.canBeXuanfengDan = info.isCanBeXuanfeng();
            this.canBeBufeng = info.isCanBeBufeng();


            if(info instanceof PlayerCardsInfoLS){
                PlayerCardsInfoLS infols = (PlayerCardsInfoLS)info;
                this.firstfourLS.addAll(infols.getFirst_four());
            }
        }
        this.koutingCard = info.getKoutingCard();
        this.userId = info.getUserId();
        this.disCards.addAll(info.getDisCards());
        this.pengType = getGroup(info.getPengType());
        this.anGangType.addAll(info.getAnGangType());
        this.mingGangType = getGroup(info.getMingGangType());
        this.isTing = info.isTing();
        this.lastOperate = info.getLastOperate();
        this.score = info.getScore();
        this.huCard = info.getHuCard();
        this.fan = info.getFan();
        this.winType.addAll(info.getWinType());
        this.chiCards.addAll(info.getChiCards());
        this.baoAnDan.addAll(info.getBaoAnDan());
        this.baoMingDan.addAll(info.getBaoMingDan());
        this.xuanfengdan.putAll(info.getXuanfengDan());
        this.gangScore = info.getGangScore();
        this.paofen = info.getPaofen();
        this.dingqueGroupType = info.getDingqueGroupType();
        this.changeCards = info.getChangeCards();

    }

    public static class Group{
        int type;
        long userId;

        public Group(int type, long userId) {
            this.type = type;
            this.userId = userId;
        }
    }

    private List<Group> getGroup(Map<Integer,Long> map) {
        List<Group> list = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            Group group = new Group(entry.getKey(), entry.getValue());
            list.add(group);
        }
        return list;
    }


    public long getUserId() {
        return userId;
    }

    public PlayerCardsResp setUserId(long userId) {
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

    public List<Group> getPengType() {
        return pengType;
    }

    public PlayerCardsResp setPengType(List<Group> pengType) {
        this.pengType = pengType;
        return this;
    }

    public List<Group> getMingGangType() {
        return mingGangType;
    }

    public PlayerCardsResp setMingGangType(List<Group> mingGangType) {
        this.mingGangType = mingGangType;
        return this;
    }

    public boolean isTing() {
        return isTing;
    }

    public void setIsTing(boolean isTing) {
        this.isTing = isTing;
    }

    public double getScore() {
        return score;
    }

    public PlayerCardsResp setScore(double score) {
        this.score = score;
        return this;
    }

    public List<String> getHandCards() {
		return handCards;
	}

	public void setHandCards(List<String> handCards) {
		this.handCards = handCards;
	}
    
    


    public int getLastOperate() {
        return lastOperate;
    }

    public PlayerCardsResp setLastOperate(int lastOperate) {
        this.lastOperate = lastOperate;
        return this;
    }

    public boolean isCanBePeng() {
        return canBePeng;
    }

    public PlayerCardsResp setCanBePeng(boolean canBePeng) {
        this.canBePeng = canBePeng;
        return this;
    }

    public String getCatchCard() {
        return catchCard;
    }

    public PlayerCardsResp setCatchCard(String catchCard) {
        this.catchCard = catchCard;
        return this;
    }

    public boolean isCanBeGang() {
        return canBeGang;
    }

    public PlayerCardsResp setCanBeGang(boolean canBeGang) {
        this.canBeGang = canBeGang;
        return this;
    }

    public boolean isCanBeHu() {
        return canBeHu;
    }

    public PlayerCardsResp setCanBeHu(boolean canBeHu) {
        this.canBeHu = canBeHu;
        return this;
    }

    public boolean isCanBeTing() {
        return canBeTing;
    }

    public PlayerCardsResp setCanBeTing(boolean canBeTing) {
        this.canBeTing = canBeTing;
        return this;
    }

    public String getHuCard() {
        return huCard;
    }

    public PlayerCardsResp setHuCard(String huCard) {
        this.huCard = huCard;
        return this;
    }

    public int getFan() {
        return fan;
    }

    public PlayerCardsResp setFan(int fan) {
        this.fan = fan;
        return this;
    }

    public List<Integer> getWinType() {
        return winType;
    }

    public PlayerCardsResp setWinType(List<Integer> winType) {
        this.winType = winType;
        return this;
    }

    public double getAllScore() {
        return allScore;
    }

    public PlayerCardsResp setAllScore(double allScore) {
        this.allScore = allScore;
        return this;
    }

    public double getGangScore() {
        return gangScore;
    }

    public PlayerCardsResp setGangScore(double gangScore) {
        this.gangScore = gangScore;
        return this;
    }

    public List<List<String>> getChiCards() {
        return chiCards;
    }

    public PlayerCardsResp setChiCards(List<List<String>> chiCards) {
        this.chiCards = chiCards;
        return this;
    }

    public List<String> getFirstfourLS() {
        return firstfourLS;
    }

    public PlayerCardsResp setFirstfourLS(List<String> firstfourLS) {
        this.firstfourLS = firstfourLS;
        return this;
    }

    public PlayerCardsResp setTing(boolean ting) {
        isTing = ting;
        return this;
    }

    public boolean isCanBeChi() {
        return canBeChi;
    }

    public PlayerCardsResp setCanBeChi(boolean canBeChi) {
        this.canBeChi = canBeChi;
        return this;
    }

    public boolean isCanBeChiTing() {
        return canBeChiTing;
    }

    public PlayerCardsResp setCanBeChiTing(boolean canBeChiTing) {
        this.canBeChiTing = canBeChiTing;
        return this;
    }

    public boolean isCanBePengTing() {
        return canBePengTing;
    }

    public PlayerCardsResp setCanBePengTing(boolean canBePengTing) {
        this.canBePengTing = canBePengTing;
        return this;
    }

    public boolean isCanBeBufeng() {
        return canBeBufeng;
    }

    public PlayerCardsResp setCanBeBufeng(boolean canBeBufeng) {
        this.canBeBufeng = canBeBufeng;
        return this;
    }

    public int getPaofen() {
        return paofen;
    }

    public PlayerCardsResp setPaofen(int paofen) {
        this.paofen = paofen;
        return this;
    }

    public String getKoutingCard() {
        return koutingCard;
    }

    public PlayerCardsResp setKoutingCard(String koutingCard) {
        this.koutingCard = koutingCard;
        return this;
    }

    public int getDingqueGroupType() {
        return dingqueGroupType;
    }

    public PlayerCardsResp setDingqueGroupType(int dingqueGroupType) {
        this.dingqueGroupType = dingqueGroupType;
        return this;
    }

    public List<String> getChangeCards() {
        return changeCards;
    }

    public PlayerCardsResp setChangeCards(List<String> changeCards) {
        this.changeCards = changeCards;
        return this;
    }
}
