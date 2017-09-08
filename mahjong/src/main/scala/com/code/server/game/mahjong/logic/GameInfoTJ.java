package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.HuWithHun;

import java.util.*;

/**
 * Created by sunxianping on 2017/8/11.
 */
public class GameInfoTJ extends GameInfo {

    public static final int mode_素本混龙 = 0;
    public static final int mode_拉龙五 = 1;
    public static final int mode_无杠黄庄 = 2;
    public static final int mode_金杠 = 3;
    public static final int mode_铲 = 4;
    public static final int mode_天胡 = 5;



    protected List<String> chanCards = new ArrayList<>();//铲的牌


    /**
     * 初始化方法
     *
     * @param firstTurn
     * @param users
     */
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;
        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        initHun(room);
    }

    /**
     * 初始化混
     *
     * @param room
     */
    public void initHun(RoomInfo room) {
        //随机混
        Random rand = new Random();
        int hunIndex = 2 + rand.nextInt(11);
        List<String> hunRemoveCards = new ArrayList<>();
        for (int i = 0; i < hunIndex * 2; i++) {
            hunRemoveCards.add(remainCards.get(0));
            remainCards.remove(0);
        }

        //确定混
        String card = hunRemoveCards.get(0);
        hunRemoveCards.remove(0);
        this.hunRemoveCards = hunRemoveCards;
        int cardType = CardTypeUtil.getTypeByCard(card);
        //todo 刮大风
        this.hun = HuWithHun.getHunTypeGDF(cardType);
    }


    @Override
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo){
        //是否是杠后摸牌
        int size = playerCardsInfo.operateList.size();
        if(size>0 && playerCardsInfo.operateList.get(size - 1) == PlayerCardsInfoMj.type_gang && chanCards.size()>0){
            return false;
        }else {
            return remainCards.size()==0;
        }

    }

    @Override
    protected void handleHuangzhuang(long userId) {

        //算杠分
        computeAllGang();

        //无杠荒庄
        if(this.room.isHasMode(mode_无杠黄庄)){
            boolean isHasGang = playerCardsInfos.values().stream().filter(playerInfo->playerInfo.getScore()!=0).count() != 0;
            //没有杠的话 每个人给庄家2分
            if (!isHasGang) {
                PlayerCardsInfoTJ banker = (PlayerCardsInfoTJ)playerCardsInfos.get(this.firstTurn);
                banker.computeAddScore(2,this.firstTurn, false);
            }
        }
        sendResult(false, userId);
        noticeDissolutionResult();
        //通知所有玩家结束
        room.clearReadyStatus();
        //庄家换下个人
        if(room instanceof RoomInfo){
            RoomInfo roomInfo = (RoomInfo)room;
            if(roomInfo.isChangeBankerAfterHuangZhuang()){
                room.setBankerId(nextTurnId(room.getBankerId()));
            }

        }

    }
    /**
     * 摸一张牌
     * @param playerCardsInfo
     * @return
     */
    @Override
    protected String getMoPaiCard(PlayerCardsInfoMj playerCardsInfo){
        //拿出一张
        String card = null;
        //有换牌需求
        if (isTest && playerCardsInfo.nextNeedCard != -1) {
            String needCard = getCardByTypeFromRemainCards(playerCardsInfo.nextNeedCard);
            playerCardsInfo.nextNeedCard = -1;
            if (needCard != null) {
                card = needCard;
                remainCards.remove(needCard);
            } else {
                card = remainCards.remove(0);
            }
        } else {
            //是否是杠后摸牌
            int size = playerCardsInfo.operateList.size();
            //如果是杠后摸牌 从废牌里拿出一张
            if(size>0 && playerCardsInfo.operateList.get(size - 1) == PlayerCardsInfoMj.type_gang && chanCards.size()>0){
                card = chanCards.remove(0);
            }else {
                card = remainCards.remove(0);
            }
        }
        return card;
    }



    @Override
    public int chupai(long userId, String card) {
        int rtn = super.chupai(userId, card);

        if (rtn == 0 && this.room.isHasMode(mode_铲)) {
            //铲
            if (chanCards.size() <= 7) {

                chanCards.add(card);
                int chanCardSize = chanCards.size();
                if (chanCardSize % this.users.size() == 0) {
                    if(isCardSame(chanCards.subList(chanCardSize -4, chanCardSize))){
                        //todo 通知有铲
                    }

                }
            }
        }
        return rtn;
    }


    protected void doGang_hand_after(PlayerCardsInfoMj playerCardsInfo, boolean isMing, int userId, String card) {
        boolean isJinGang = this.hun.contains(CardTypeUtil.getTypeByCard(card));
        playerCardsInfo.gangCompute(room, this, isMing, -1, card);
        //金杠直接胡
        if (isJinGang) {
            computeAllGang();
            handleHu(playerCardsInfo);
        }else{
            mopai(playerCardsInfo.getUserId(), "userId : " + playerCardsInfo.getUserId() + " 自摸杠抓牌");
        }
        turnId = playerCardsInfo.getUserId();


    }


    /**
     * 牌是否一样
     *
     * @param cards
     * @return
     */
    private boolean isCardSame(List<String> cards) {
        Set<Integer> set = new HashSet<>();
        cards.forEach(card -> set.add(CardTypeUtil.getTypeByCard(card)));
        return set.size() == 1;

    }

    /**
     * 获得铲的个数
     * @return
     */
    protected int getChanNum() {
        int result = 0;
        int chanNum = chanCards.size() / 4;

        if (chanNum > 1) {
            if(isCardSame(chanCards.subList(0,4))){
                result = 1;
            }
        }
        if (result == 1 && chanNum == 2) {
            if(isCardSame(chanCards.subList(4,8))){
                result = 2;
            }
        }
        return result;
    }
}
