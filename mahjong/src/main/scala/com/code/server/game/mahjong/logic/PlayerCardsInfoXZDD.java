package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sunxianping on 2019-04-15.
 * <p>
 * 血战到底 麻将
 */
public class PlayerCardsInfoXZDD extends PlayerCardsInfoMj {
    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_碰碰胡, 2);
        specialHuScore.put(hu_清一色碰碰胡, 4);
        specialHuScore.put(hu_清一色, 3);
        specialHuScore.put(hu_七小对, 3);
        specialHuScore.put(hu_幺九, 3);
        specialHuScore.put(hu_豪华七小对, 5);
        specialHuScore.put(hu_双豪七小对_山西, 5);
        specialHuScore.put(hu_清一色七小对, 5);
        specialHuScore.put(hu_清一色豪华七小对, 6);
        specialHuScore.put(hu_清一色双豪华七小对, 6);
        specialHuScore.put(hu_天胡, 6);
        specialHuScore.put(hu_地胡, 6);

        specialHuScore.put(hu_将对, 4);
//        specialHuScore.put(hu_门清, 1);
//        specialHuScore.put(hu_中张, 1);
//        specialHuScore.put(hu_将七对, 4);

    }


    @Override
    public boolean isHasChi(String card) {
        return false;
    }

    @Override
    public boolean isHasGang() {
        if (isAlreadyHu) return false;
        List<String> temp = new ArrayList<>();
        temp.addAll(cards);
        Set<Integer> set = getHasGangList(temp);
        for (int cardType : set) {
            int group = CardTypeUtil.getCardGroupByCardType(cardType);
            if (group != dingqueGroupType) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean isCanPengAddThisCard(String card) {
        if (isAlreadyHu) return false;
        int group = CardTypeUtil.getCardGroup(card);
        if(group == dingqueGroupType) return false;
        return super.isCanPengAddThisCard(card);
    }

    @Override
    public boolean isCanGangAddThisCard(String card) {
        if (isAlreadyHu) return false;
        int group = CardTypeUtil.getCardGroup(card);
        if(group == dingqueGroupType) return false;
        return super.isCanGangAddThisCard(card);
    }

    @Override
    public boolean isCanGangThisCard(String card) {
        if (isAlreadyHu) return false;
        int group = CardTypeUtil.getCardGroup(card);
        if(group == dingqueGroupType) return false;
        return super.isCanGangThisCard(card);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        if (isAlreadyHu) return false;
        int group = CardTypeUtil.getCardGroup(card);
        if(group == dingqueGroupType) return false;
        if(getGroupNum() == 3) return false;
        return super.isCanHu_dianpao(card);
    }

    @Override
    public boolean isCanHu_zimo(String card) {
        if (isAlreadyHu) return false;
        int group = CardTypeUtil.getCardGroup(card);
        if(group == dingqueGroupType) return false;
        if(getGroupNum() == 3) return false;
        return super.isCanHu_zimo(card);
    }

    @Override
    public boolean isCanTing(List<String> cards) {
        return false;
//        if (isAlreadyHu) return false;
//        return super.isCanTing(cards);
    }

    @Override
    public void hu_zm(RoomInfo room, GameInfo gameInfo, String card) {
        super.hu_zm(room, gameInfo, card);
        this.gameInfo.isAlreadyHu = false;
    }

    @Override
    public void hu_dianpao(RoomInfo room, GameInfo gameInfo, long dianpaoUser, String disCard) {
        super.hu_dianpao(room, gameInfo, dianpaoUser, disCard);
        this.gameInfo.isAlreadyHu = false;
    }

    private int getGroupNum(){
        Set<Integer> set = new HashSet<>();
        this.cards.forEach(card->{
            int group = CardTypeUtil.getCardGroup(card);
            set.add(group);
        });
        return set.size();
    }
    /**
     * 获得最大听牌分数
     * @return
     */
    protected int getMaxTingScore(){
        List<HuCardType> hulist = getTingHuCardType(this.cards, new HuLimit(0));
        hulist.forEach(this::resetFan);
        HuCardType huCardType = getMaxScoreHuCardType(hulist);
        return huCardType.fan;
    }


    private void resetFan(HuCardType huCardType) {
        int fan = 0;
        for (int huType : huCardType.specialHuList) {
            if (this.specialHuScore.containsKey(huType)) {
                int temp = this.specialHuScore.get(huType);
                if (temp > fan) {
                    fan = temp;
                }
            }
        }
        huCardType.fan = fan;
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
                //判断是否过了一圈才杠的
                //最后一个碰的类型
                int lastPengType = pengList.get(pengList.size() - 1);
                if (lastPengType == cardType) {

                    if (this.operateList.get(this.operateList.size() - 4) == type_peng) {
                        noScoreGang.add(cardType);
                    }

                }
                pengType.remove(cardType);//从碰中移除
                pengList.remove(Integer.valueOf(cardType));
                mingGangType.put(cardType, diangang);
                isMing = true;

            } else {
                anGangType.add(cardType);
                isMing = false;

            }
        }
        return isMing;
    }


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card) {
        super.gangCompute(room, gameInfo, isMing, diangangUser, card);
        int cardType = CardTypeUtil.getTypeByCard(card);
        //不算分的杠
        if (this.noScoreGang.contains(cardType)) {
            return;
        }
        //算分
        int score = 1;
        int allScore = 0;
        if (isMing && diangangUser != -1) {
            score = 2;
            PlayerCardsInfoMj diangang = this.gameInfo.playerCardsInfos.get(diangangUser);
            diangang.addScore(-score);
            diangang.addGangScore(-score);
            this.roomInfo.addUserSocre(diangangUser, -score);
            allScore += score;
            this.otherGangScore.put(diangangUser,this.otherGangScore.getOrDefault(diangangUser,0D) +2 );

        } else {
            if(!isMing) score = 2;
            for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.playerCardsInfos.values()) {
                //不是自己并且没胡
                if (playerCardsInfoMj.getUserId() != this.userId) {
                    allScore += score;
                    playerCardsInfoMj.addScore(-score);
                    playerCardsInfoMj.addGangScore(-score);
                    this.roomInfo.addUserSocre(diangangUser, -score);
                    this.otherGangScore.put(playerCardsInfoMj.getUserId(),this.otherGangScore.getOrDefault(playerCardsInfoMj.getUserId(),0D) + score );
                }
            }

        }

        //加杠分
        this.addScore(allScore);
        this.addGangScore(allScore);
        this.roomInfo.addUserSocre(this.userId, allScore);



    }

    /**
     * 获得根数
     * @param cards
     * @return
     */
    private int getGenNum(List<String> cards){
        Map<Integer, Integer> cardNum = new HashMap<>();
        cards.forEach(card->{
            int type = CardTypeUtil.getTypeByCard(card);
            if (!this.mingGangType.containsKey(type) && !this.anGangType.contains(type)) {
                int num = cardNum.getOrDefault(type, 0);
                cardNum.put(type, num + 1);
            }
        });
        return (int)cardNum.values().stream().filter(num->num == 4).count();
    }


    private int getLimitFan(){
        return 6;
    }

    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
//        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);

        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this, CardTypeUtil.cardType.get(card), new HuLimit(0));
        if (isTianhu()) {
            huList.forEach(huCardType -> huCardType.specialHuList.add(hu_天胡));
        }
        if (isDihu()) {
            huList.forEach(huCardType -> huCardType.specialHuList.add(hu_地胡));
        }
        huList.forEach(this::resetFan);
        HuCardType huCardType = getMaxScoreHuCardType(huList);
        setWinTypeResult(getMaxScoreHuCardType(huList));

        int fan = huCardType.fan;




        //杠加番
        fan += getGangNum();

        //根加番
        fan += getGenNum(this.cards);

        if (isGangKai()) fan+= 1;
        this.winType.add(hu_杠上开花);

        //截杠胡
        if(isJieGangHu) fan+= 1;
        this.winType.add(hu_截杠胡);

        //杠上点炮
        PlayerCardsInfoMj dianPao = this.gameInfo.playerCardsInfos.get(dianpaoUser);
        if (dianPao != null) {
            if(dianPao.isGangPao()) {
                fan += 1;
                this.winType.add(hu_杠上点炮);
            }
        }

        //是否超过最大番
        if (fan > getLimitFan()) {
            fan = getLimitFan();
        }
        this.fan = fan;


        int score = 1<<fan;
        AtomicInteger allScore = new AtomicInteger();
        if (isZimo) {
            this.gameInfo.playerCardsInfos.forEach((id,otherInfo)->{
                if (id != this.userId) {
                    otherInfo.addScore(-score);
                    this.roomInfo.addUserSocre(id, -score);
                    allScore.addAndGet(score);
                }
            });
        }else{
            if (dianPao != null) {
                dianPao.addScore(-score);
                this.roomInfo.addUserSocre(dianpaoUser, -score);
                allScore.addAndGet(score);
            }
        }

        this.addScore(allScore.get());
        this.roomInfo.addUserSocre(this.userId, allScore.get());


    }
}
