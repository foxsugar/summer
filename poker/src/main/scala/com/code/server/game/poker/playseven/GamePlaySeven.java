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

    protected void deal() {
        for (PlayerCardInfoPlaySeven playerCardInfo : playerCardInfos.values()) {
            if(4==room.getPersonNumber()){
                for (int i = 0; i < 25; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }else{
                for (int i = 0; i < 20; i++) {
                    playerCardInfo.handCards.add(cards.remove(0));
                }
            }
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.handCards), playerCardInfo.userId);
        }
    }
}
