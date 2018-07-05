package com.code.server.game.poker.playseven;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.*;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class GamePlaySeven extends Game{

    public boolean shouQiDouble = false;//首七

    public boolean shuangLiangDouble = false;//双亮




    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> tableCards = new ArrayList<>();//底牌

    protected long chuPaiId;

    protected RoomPlaySeven room;

    public Map<Long, PlayerCardInfoPlaySeven> playerCardInfos = new HashMap<>();

    public void startGame(List<Long> users, Room room) {
        this.room = (RoomPlaySeven) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }

    public void init(List<Long> users) {
        //初始化玩家
        for (Long uid : users) {
            PlayerCardInfoPlaySeven playerCardInfo = getGameTypePlayerCardInfo();
            playerCardInfos.put(uid, playerCardInfo);
        }
        this.users.addAll(users);
        shuffle();
        deal();

        updateLastOperateTime();
    }

    public PlayerCardInfoPlaySeven getGameTypePlayerCardInfo() {
        switch (room.getGameType()) {
            case "1":
                return new PlayerCardInfoPlaySeven();
            default:
                return new PlayerCardInfoPlaySeven();
        }
    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        cards.addAll(CardsUtil.cardsOf108.keySet());
        Collections.shuffle(cards);
    }

    protected void deal(){
        //发所有
        for (PlayerCardInfoPlaySeven playerCardInfo : playerCardInfos.values()) {
            if(4==room.getPersonNumber()){
                for (int i = 0; i < 25; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }else if(5==room.getPersonNumber()){
                for (int i = 0; i < 20; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }
        }
        this.tableCards.addAll(cards);
        //定时
        int timer = 0;
        if(4==room.getPersonNumber()){
            while(timer<25){
                for (int i = 0; i < 25; i++) {
                    sendSingleCardAndWait500s(i);
                }
                timer++;
            }
        }else if(5==room.getPersonNumber()){
            while(timer<20){
                for (int i = 0; i < 20; i++) {
                    sendSingleCardAndWait500s(i);
                }
                timer++;
            }
        }


    }

    //单张发并且等待500s
    private void sendSingleCardAndWait500s(int i) {
        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", l);
            int tempcardNum = playerCardInfos.get(l).handCards.get(i);
            if(i==0){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfos.get(l).setShouQi("1");
                    playerCardInfos.get(l).setDanLiang("1");
                }
            }else{
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfos.get(l).setDanLiang("1");
                }
            }
            if("1".equals(playerCardInfos.get(l).getDanLiang())){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfos.get(l).setShuangLiang("1");
                }
            }
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "cardAndCanOperate", msg), l);
        }
        try {
            Thread.sleep(500);//发牌停顿
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
