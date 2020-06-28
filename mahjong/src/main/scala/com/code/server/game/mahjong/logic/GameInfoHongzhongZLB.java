package com.code.server.game.mahjong.logic;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.response.HandCardsResp;
import com.code.server.game.mahjong.response.ResponseType;
import com.code.server.game.mahjong.util.HuWithHun;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sunxianping on 2019-05-05.
 */
public class GameInfoHongzhongZLB extends GameInfo {


    @Override
    public void init(int gameId, long firstTurn, List<Long> users, RoomInfo room) {
        this.gameId = gameId;

        this.firstTurn = firstTurn;
        this.turnId = firstTurn;
        remainCards.addAll(CardTypeUtil.ALL_CARD);
        this.users.addAll(users);
        this.room = room;
        this.cardSize = 13;
        this.playerSize = room.getPersonNumber();

        if (this.room.isHasMode(PlayerCardsInfoHongZhong.NO_FENG)) {
            remainCards.removeAll(CardTypeUtil.FENG_CARD);
            remainCards.removeAll(CardTypeUtil.ZI_CARD);
            if (this.room.isHasMode(PlayerCardsInfoHongZhong.HAS_HONGZHONG)) {
                remainCards.add("124");
                remainCards.add("125");
                remainCards.add("126");
                remainCards.add("127");
            }
        }

        initHun();
        //不带风
        fapai();
    }

    public static void main(String[] args) {
        System.out.println(Room.isHasMode(PlayerCardsInfoHongZhong.HUN_RAND, 174));
        System.out.println(Room.isHasMode(PlayerCardsInfoHongZhong.HUN_NO, 174));
        System.out.println(Room.isHasMode(PlayerCardsInfoHongZhong.TWO_HUN, 174));
        System.out.println(Room.isHasMode(PlayerCardsInfoHongZhong.HAS_HONGZHONG, 174));
    }
    @Override
    public void initHun() {

    }

    /**
     * 发牌
     */
    public void fapai() {
        //打乱顺序
        Collections.shuffle(remainCards);
        for (int i = 0; i < this.users.size(); i++) {
            PlayerCardsInfoMj playerCardsInfo = PlayerCardsInfoFactory.getInstance(room);
            playerCardsInfo.setGameInfo(this);
            long userId = users.get(i);
            //设置id
            playerCardsInfo.setUserId(userId);
            List<String> playerCards = new ArrayList<>();
            //发牌
            for (int j = 0; j < cardSize; j++) {
                playerCards.add(remainCards.remove(0));
            }
            //初始化
            playerCardsInfo.init(playerCards);
            //放进map
            playerCardsInfos.put(userId, playerCardsInfo);

            //发牌状态通知
            HandCardsResp resp = new HandCardsResp();
            resp.setCards(playerCards);
            ResponseVo vo = new ResponseVo(ResponseType.SERVICE_TYPE_GAMELOGIC, ResponseType.METHOD_TYPE_GET_HAND_CARDS, resp);

            MsgSender.sendMsg2Player(vo, userId);


        }
        doAfterFapai();
        //回放的牌信息
        for (PlayerCardsInfoMj playerCardsInfoMj : playerCardsInfos.values()) {
            List<String> cs = new ArrayList<>();
            cs.addAll(playerCardsInfoMj.getCards());
            replay.getCards().put(playerCardsInfoMj.getUserId(), cs);
        }





        //确定耗子

        if (!room.isHasMode(PlayerCardsInfoHongZhong.HUN_NO)) {

            //随机混
            Random rand = new Random();
            int hunIndex = 0;
//            if (PlayerCardsInfoMj.isHasMode(this.room.mode, mode_风耗子)) {
//                hunIndex = 27 + rand.nextInt(7);
//            }else{
            //如果是红中玩法的话
            if(PlayerCardsInfoMj.isHasMode(this.room.mode, PlayerCardsInfoHongZhong.HAS_HONGZHONG)){
                hunIndex = 31;
            }else{
                String card = this.remainCards.remove(1);
                hunIndex = CardTypeUtil.getTypeByCard(card);
            }

//            }

            if (PlayerCardsInfoMj.isHasMode(this.room.mode, PlayerCardsInfoHongZhong.TWO_HUN)) {
                this.hun = HuWithHun.getHunType(hunIndex);
            } else {
//                String card = this.remainCards.remove(0);
//                hunIndex = CardTypeUtil.getTypeByCard(card);
                this.hun.add(hunIndex);
            }

            //通知混
            MsgSender.sendMsg2Player("gameService", "noticeHun", this.hun, users);

        }
        //第一个人抓牌
        mopai(firstTurn, "发牌");
        replay.getHun().addAll(this.hun);
    }

    public int getNeedRemainCardNum(){
        if (!this.room.isHasMode(PlayerCardsInfoHongZhong.LIUPAI)) {
            return 0;
        }
        int gangCount = 0;
        for (PlayerCardsInfoMj playerCardsInfoMj : this.playerCardsInfos.values()) {
            gangCount += playerCardsInfoMj.getGangNum();
        }
        int add =  (gangCount % 2 == 0) ? 0:1;

        return 16 + add;
    }

    /**
     * 是否荒庄
     *
     * @param playerCardsInfo
     * @return
     */
    protected boolean isHuangzhuang(PlayerCardsInfoMj playerCardsInfo) {
        return this.remainCards.size() <= getNeedRemainCardNum();
    }


}
