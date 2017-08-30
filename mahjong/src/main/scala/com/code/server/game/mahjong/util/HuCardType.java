package com.code.server.game.mahjong.util;



import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by win7 on 2016/12/8.
 */
public class HuCardType implements HuType {
    public Set<Integer> chi = new HashSet<>();
    public Set<Integer> peng = new HashSet<>();
    public Set<Integer> mingGang = new HashSet<>();
    public Set<Integer> anGang = new HashSet<>();
    public List<Integer> shun = new ArrayList<>();//顺中的第一个
    public List<Integer> ke = new ArrayList<>();
    public List<Integer> hun2 = new ArrayList<>();
    public List<Integer> hun3 = new ArrayList<>();
    public boolean hunJiang = false;
    public int jiang = -1;

    public List<List<Integer>> feng_shun = new ArrayList<>();
    public int zi_shun;//有几个中发白组合

    public int fan;

    public int tingCardType = -1;

    public Set<Integer> specialHuList = new HashSet<>();//特殊胡法
    public List<String> cards = new ArrayList<>();
    public String tingRemoveCard;
    public boolean isCheckYiZhangying = true;


    public HuCardType() {
    }


    public static HuCardType getSpecialHuInstance(int huType) {
        HuCardType huCardType = new HuCardType();
        huCardType.specialHuList.add(huType);
        return huCardType;
    }


    public int clearRepeat(PlayerCardsInfoMj playerCardsInfo, int fanTemp) {
        if (this.specialHuList.contains(hu_清龙)) {
            fanTemp = removeHu(playerCardsInfo, hu_一条龙, fanTemp);
            fanTemp = removeHu(playerCardsInfo, hu_清一色, fanTemp);

        }
        if (this.specialHuList.contains(hu_清七对)) {
            fanTemp = removeHu(playerCardsInfo, hu_清一色, fanTemp);
            fanTemp = removeHu(playerCardsInfo, hu_七小对, fanTemp);
            fanTemp = removeHu(playerCardsInfo, hu_豪华七小对, fanTemp);
            fanTemp = removeHu(playerCardsInfo, hu_双豪七小对_山西, fanTemp);
        }
        return fanTemp;
    }

    private int removeHu(PlayerCardsInfoMj playerCardsInfo, int huType, int fanTemp) {
        if (this.specialHuList.remove(huType)) {
            fanTemp -= playerCardsInfo.getSpecialHuScore(huType);
        }
        return fanTemp;
    }

    /**
     * 获得所有胡牌类型
     *
     * @return
     */
    public static HuCardType setHuCardType(HuCardType cardType, PlayerCardsInfoMj playerCardsInfo) {
        cardType.chi.addAll(playerCardsInfo.getChiType());
        cardType.peng.addAll(playerCardsInfo.getPengType().keySet());
        cardType.mingGang.addAll(playerCardsInfo.getMingGangType().keySet());
        cardType.anGang.addAll(playerCardsInfo.getAnGangType());
        return cardType;
    }

    public static boolean isEquil(HuCardType huCardType1, HuCardType huCardType2) {
        if (huCardType1.jiang != huCardType2.jiang) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(huCardType1.ke, huCardType2.ke)) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(huCardType1.shun, huCardType2.shun)) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(huCardType1.feng_shun, huCardType2.feng_shun)) {
            return false;
        }
        if (huCardType1.zi_shun != huCardType2.zi_shun) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HuCardType{" +
                "chi=" + chi +
                ", peng=" + peng +
                ", mingGang=" + mingGang +
                ", anGang=" + anGang +
                ", shun=" + shun +
                ", ke=" + ke +
                ", hun2=" + hun2 +
                ", hun3=" + hun3 +
                ", hunJiang=" + hunJiang +
                ", jiang=" + jiang +
                ", feng_shun=" + feng_shun +
                ", zi_shun=" + zi_shun +
                '}';
    }

    public Set<Integer> getPeng() {
        return peng;
    }

    public HuCardType setPeng(Set<Integer> peng) {
        this.peng = peng;
        return this;
    }

    public Set<Integer> getMingGang() {
        return mingGang;
    }

    public HuCardType setMingGang(Set<Integer> mingGang) {
        this.mingGang = mingGang;
        return this;
    }

    public Set<Integer> getAnGang() {
        return anGang;
    }

    public void setAnGang(Set<Integer> anGang) {
        this.anGang = anGang;
    }

    public int getJiang() {
        return jiang;
    }

    public void setJiang(int jiang) {
        this.jiang = jiang;
    }

    public List<Integer> getShun() {
        return shun;
    }

    public void setShun(List<Integer> shun) {
        this.shun = shun;
    }

    public List<Integer> getKe() {
        return ke;
    }

    public void setKe(List<Integer> ke) {
        this.ke = ke;
    }

    public List<List<Integer>> getFeng_shun() {
        return feng_shun;
    }

    public void setFeng_shun(List<List<Integer>> feng_shun) {
        this.feng_shun = feng_shun;
    }

    public int getZi_shun() {
        return zi_shun;
    }

    public void setZi_shun(int zi_shun) {
        this.zi_shun = zi_shun;
    }


    public int getFan() {
        return fan;
    }

    public HuCardType setFan(int fan) {
        this.fan = fan;
        return this;
    }

    public Set<Integer> getChi() {
        return chi;
    }

    public HuCardType setChi(Set<Integer> chi) {
        this.chi = chi;
        return this;
    }

    public String getTingRemoveCard() {
        return tingRemoveCard;
    }

    public HuCardType setTingRemoveCard(String tingRemoveCard) {
        this.tingRemoveCard = tingRemoveCard;
        return this;
    }

    public boolean isCheckYiZhangying() {
        return isCheckYiZhangying;
    }

    public HuCardType setCheckYiZhangying(boolean checkYiZhangying) {
        isCheckYiZhangying = checkYiZhangying;
        return this;
    }

    public List<Integer> getHun2() {
        return hun2;
    }

    public HuCardType setHun2(List<Integer> hun2) {
        this.hun2 = hun2;
        return this;
    }

    public List<Integer> getHun3() {
        return hun3;
    }

    public HuCardType setHun3(List<Integer> hun3) {
        this.hun3 = hun3;
        return this;
    }

    public boolean isHunJiang() {
        return hunJiang;
    }

    public HuCardType setHunJiang(boolean hunJiang) {
        this.hunJiang = hunJiang;
        return this;
    }
}
