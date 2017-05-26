package com.code.server.game.mahjong.response;

import com.byz.mj.serviceNew.PlayerCardsInfo;
import com.byz.mj.serviceNew.PlayerCardsInfoLS;

import java.util.*;


/**
 * Created by win7 on 2016/12/4.
 */
public class PlayerCardsResp {

    private int userId;
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
    private int score;
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
    private String huCard;

    private List<Integer> winType = new ArrayList<>();
    private int fan;
    private int allScore;
    private int gangScore;
    private Set<Integer> baoMingDan = new HashSet<>();
    private Set<Integer> baoAnDan = new HashSet<>();

    

    public PlayerCardsResp(){}

    public PlayerCardsResp(PlayerCardsInfo info) {
        init(info, true);
    }


    public PlayerCardsResp(PlayerCardsInfo info,boolean isMine) {
        init(info, isMine);

    }

    public void init(PlayerCardsInfo info,boolean isMine) {
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
         
            if(info instanceof PlayerCardsInfoLS){
            	PlayerCardsInfoLS infols = (PlayerCardsInfoLS)info;
            	this.firstfourLS.addAll(infols.getFirst_four());
            }
        }
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

    }

    public static class Group{
        int type;
        int userId;

        public Group(int type, int userId) {
            this.type = type;
            this.userId = userId;
        }
    }

    private List<Group> getGroup(Map<Integer,Integer> map) {
        List<Group> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Group group = new Group(entry.getKey(), entry.getValue());
            list.add(group);
        }
        return list;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public int getAllScore() {
        return allScore;
    }

    public PlayerCardsResp setAllScore(int allScore) {
        this.allScore = allScore;
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
}
