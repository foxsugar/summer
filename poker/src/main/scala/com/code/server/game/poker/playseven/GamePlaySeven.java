package com.code.server.game.poker.playseven;

import com.code.server.constant.response.*;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;

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

    public static final long STOP_TIME = 500;

    public boolean shouQiDouble = false;//首七
    public boolean shuangLiangDouble = false;//双亮
    public boolean seeTableCard = false;
    public boolean changTableCard = false;
    public boolean fanzhu = false;

    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> tableCards = new ArrayList<>();//底牌

    protected long chuPaiId;
    protected long zhuId;//第一个叫主的
    public int tableCardFen = 0;
    public int jianFen = 0;
    public int kouDiBeiShu = 1;//扣底翻倍的倍数
    public Integer liangCard;//亮的牌
    public long secondBanker= 0l;//另一个队友Id
    public int huaSe;//1230 黑红花片
    public int step;//步骤
    public int chuHuaSe=-2;//1230 黑红花片
    public long diYiChu=0l;

    //出过 1，未出 0
    protected Map<Long, Integer> ifChuPai = new HashMap<>();

    //赢为1，输为0，未比过为-1
    protected Map<Long, Integer> compareCard = new HashMap<>();

    protected Map<Long, Integer> userGetFen = new HashMap<>();//玩家的分

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
            playerCardInfo.userId = uid;
            playerCardInfos.put(uid, playerCardInfo);
            ifChuPai.put(uid,0);
            compareCard.put(uid,-1);
            userGetFen.put(uid,0);
        }
        this.users.addAll(users);
        shuffle();
        //dealOne();

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


    //==============操作=====================
    public int getCard(long userId,Integer number) {

        if(room.getPersonNumber()==5){
            if(playerCardInfos.get(userId).handCards.size()>=20){
                return ErrorCode.ERROR_CARD_MAX;
            }
        }else{
            if(playerCardInfos.get(userId).handCards.size()>=25){
                return ErrorCode.ERROR_CARD_MAX;
            }
        }

        playerCardInfos.get(userId).handCards.add(cards.remove(0));
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", playerCardInfos.get(userId).getUserId());
        int size = playerCardInfos.get(userId).handCards.size();
        int tempcardNum = playerCardInfos.get(userId).handCards.get(size-1);
        msg.put("card",tempcardNum);
        if(size==1){
            if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||
                    tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52){
                playerCardInfos.get(userId).setShouQi("1");
                playerCardInfos.get(userId).setDanLiang("1");
                msg.put("shouQi", "1");
                msg.put("danLiang", "1");
            }
        }else{
            if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||
                    tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52){
                playerCardInfos.get(userId).setDanLiang("1");
                msg.put("danLiang", "1");
            }
        }
        if("1".equals(playerCardInfos.get(userId).getDanLiang())){
            for (int j = 0; j < size; j++) {
                if(tempcardNum!=playerCardInfos.get(userId).handCards.get(j))
                    if (tempcardNum + playerCardInfos.get(userId).handCards.get(j) == 0 &&
                            (tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                            tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54)) {
                        playerCardInfos.get(userId).setShuangLiang("1");
                        playerCardInfos.get(userId).setFanZhu("4");
                        //playerCardInfos.get(userId).setRenShu("4");
                        msg.put("shuangLiang", "1");
                    }
            }
        }
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "cardAndCanOperate", msg), userId);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "getCard", 0), userId);

        if(this.step==CAN_CHANGE_TABLE_CARDS||this.step==STEP_CHUPAI||this.step==CHANGE_TABLE_CARDS_NOW){

        }else{
            boolean b = true;
            a:for (long l:playerCardInfos.keySet()) {
                if("1".equals(playerCardInfos.get(l).fanZhu)){
                    b = false;
                    break a;
                }
            }
            if(4==room.getPersonNumber()){
                if(b){
                    if(playerCardInfos.get(userId).handCards.size()==25){
                        this.step = STEP_GET_CARD_FINISH;
                    }else{
                        this.step = STEP_GET_CARD_UNFINISH;
                    }
                }
            }else{
                if(b){
                    if(playerCardInfos.get(userId).handCards.size()==20){
                        this.step = STEP_GET_CARD_FINISH;
                    }else{
                        this.step = STEP_GET_CARD_UNFINISH;
                    }
                }
            }
        }
        updateLastOperateTime();
        return 0;
    }

    public int getAllCard(long userId) {
        this.step = STEP_GET_CARD_FINISH;

        while(playerCardInfos.get(userId).handCards.size()<100/room.getPersonNumber()){
            playerCardInfos.get(userId).handCards.add(cards.remove(0));
            int size = playerCardInfos.get(userId).handCards.size();
            int tempcardNum = playerCardInfos.get(userId).handCards.get(size-1);
            if(size==1){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52){
                    playerCardInfos.get(userId).setShouQi("1");
                    playerCardInfos.get(userId).setDanLiang("1");
                }
            }else{
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52){
                    playerCardInfos.get(userId).setDanLiang("1");
                }
            }
            if("1".equals(playerCardInfos.get(userId).getDanLiang())){
                for (int j = 0; j < size; j++) {
                    if(tempcardNum!=playerCardInfos.get(userId).handCards.get(j))
                        if (tempcardNum + playerCardInfos.get(userId).handCards.get(j) == 0 &&
                                (tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54)) {
                            playerCardInfos.get(userId).setShuangLiang("1");
                            playerCardInfos.get(userId).setFanZhu("4");
                            //playerCardInfos.get(userId).setRenShu("4");
                        }
                }
            }
        }

        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", userId);
        msg.put("allCards", playerCardInfos.get(userId).handCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "getAllCard", msg), userId);

        updateLastOperateTime();
        return 0;
    }


    public int getTableCard(long userId) {
        Map<String, Object> tableCardMsg = new HashMap<>();
        tableCardMsg.put("userId", userId);
        tableCardMsg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "getTableCard", tableCardMsg), userId);
        this.step = CHANGE_TABLE_CARDS_NOW;
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellGetTableCard", userId), users);
        updateLastOperateTime();
        return 0;
    }

    public int shouQi(long userId,Integer card){
        /*if(Math.abs(card)!=49 || Math.abs(card)!=50 || Math.abs(card)!=51 || Math.abs(card)!=52 || Math.abs(card)!=53 || Math.abs(card)!=54){
            return ErrorCode.ERROR_CARD;
        }*/
        if(Math.abs(card)>52){
            this.huaSe = -1;
        }else{
            this.huaSe = Math.abs(card)%4;
        }
        this.chuPaiId = userId;
        this.liangCard = card;
        this.zhuId = userId;
        room.setBankerId(userId);
        this.shouQiDouble = true;
        this.seeTableCard =true;
        playerCardInfos.get(userId).setShouQi("2");

        Map<String, Object> msg = new HashMap<>();
        msg.put("shouQiUserId", userId);
        msg.put("shouQiCard", card);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellShouQiUserId", msg), users);

        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> opreaterMsg = new HashMap<>();
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                opreaterMsg.put("shouQi", "3");
                opreaterMsg.put("danLiang", "3");
                opreaterMsg.put("shuangLiang", "3");
            }
            /*if(ifTwo7OrWang(l)){*/
                playerCardInfos.get(l).setFanZhu("1");
                opreaterMsg.put("fanZhu", "1");
                this.step = STEP_FANZHU;
            /*}*/
            opreaterMsg.put("canOperateUserId", l);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
        }


        boolean b = false;
        a:for (long l:playerCardInfos.keySet()) {
            if(!"0".equals(playerCardInfos.get(l).fanZhu)){
                b=true;
                break a;
            }
        }
        if (b){
            playerCardInfos.get(userId).setSeeTableCard("1");
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
            this.step = CAN_CHANGE_TABLE_CARDS;
        }

        /*Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);
*/
        MsgSender.sendMsg2Player("gameService", "shouQi", 0, userId);
        updateLastOperateTime();
        return 0;
    }

    public int danLiang(long userId,Integer card){
        /*if(Math.abs(card)!=49 || Math.abs(card)!=50 || Math.abs(card)!=51 || Math.abs(card)!=52 || Math.abs(card)!=53 || Math.abs(card)!=54){
            return ErrorCode.ERROR_CARD;
        }*/
        if(Math.abs(card)>52){
            this.huaSe = -1;
        }else{
            this.huaSe = Math.abs(card)%4;
        }
        this.chuPaiId = userId;
        this.liangCard = card;
        this.zhuId = userId;
        room.setBankerId(userId);
        this.seeTableCard =true;
        playerCardInfos.get(userId).setDanLiang("2");

        Map<String, Object> msg = new HashMap<>();
        msg.put("danLiangUserId", userId);
        msg.put("danLiangCard", card);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellDanLiangUserId", msg), users);
        //this.step = STEP_FANZHU;
        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> opreaterMsg = new HashMap<>();
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                opreaterMsg.put("shouQi", "3");
                opreaterMsg.put("danLiang", "3");
                opreaterMsg.put("shuangLiang", "3");
            }
            if((playerCardInfos.get(l).handCards.contains(49)&&playerCardInfos.get(l).handCards.contains(-49))||
                    (playerCardInfos.get(l).handCards.contains(50)&&playerCardInfos.get(l).handCards.contains(-50))||
                    (playerCardInfos.get(l).handCards.contains(51)&&playerCardInfos.get(l).handCards.contains(-51))||
                    (playerCardInfos.get(l).handCards.contains(52)&&playerCardInfos.get(l).handCards.contains(-52))||
                    (playerCardInfos.get(l).handCards.contains(53)&&playerCardInfos.get(l).handCards.contains(-53))||
                    (playerCardInfos.get(l).handCards.contains(54)&&playerCardInfos.get(l).handCards.contains(-54))){
                /*if(l!=userId){*/
                    playerCardInfos.get(l).setFanZhu("1");
                    opreaterMsg.put("fanZhu", "1");
                    this.step = STEP_FANZHU;
                /*}*/
            }
            opreaterMsg.put("canOperateUserId", l);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
        }


        /*Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);*/
        boolean b = false;
        a:for (long l:playerCardInfos.keySet()) {
            if("1".equals(playerCardInfos.get(l).fanZhu)){
                b=true;
                break a;
            }
        }
        if (!b){
            playerCardInfos.get(userId).setSeeTableCard("1");
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
            this.step = CAN_CHANGE_TABLE_CARDS;
        }

        MsgSender.sendMsg2Player("gameService", "danLiang", 0, userId);

        updateLastOperateTime();
        return 0;
    }

    public int shuangLiang(long userId,Integer card){
        /*if(Math.abs(card)!=49 || Math.abs(card)!=50 || Math.abs(card)!=51 || Math.abs(card)!=52 || Math.abs(card)!=53 || Math.abs(card)!=54){
            return ErrorCode.ERROR_CARD;
        }*/
        if(Math.abs(card)>52){
            this.huaSe = -1;
        }else{
            this.huaSe = Math.abs(card)%4;
        }
        this.shuangLiangDouble = true;
        this.chuPaiId = userId;
        this.liangCard = card;
        this.zhuId = userId;
        this.secondBanker = userId;
        room.setBankerId(userId);
        this.shouQiDouble = true;
        this.seeTableCard =true;
        playerCardInfos.get(userId).setShuangLiang("2");
        this.step = STEP_FANZHU;

        playerCardInfos.get(userId).setRenShu("4");

        Map<String, Object> msg = new HashMap<>();
        msg.put("danLiangUserId", userId);
        msg.put("shuangLiangCard", card);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellShuangLiangUserId", msg), users);

        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> opreaterMsg = new HashMap<>();
            if(l!=userId){
                playerCardInfos.get(l).setShouQi("3");
                playerCardInfos.get(l).setDanLiang("3");
                playerCardInfos.get(l).setShuangLiang("3");
                opreaterMsg.put("shouQi", "3");
                opreaterMsg.put("danLiang", "3");
                opreaterMsg.put("shuangLiang", "3");
            }
            if(liangCard!=53&&liangCard!=54){
                if((playerCardInfos.get(l).handCards.contains(53)&&playerCardInfos.get(l).handCards.contains(-53))||
                        (playerCardInfos.get(l).handCards.contains(54)&&playerCardInfos.get(l).handCards.contains(-54))){
                /*if(l!=userId){*/
                    playerCardInfos.get(l).setFanZhu("1");
                    opreaterMsg.put("fanZhu", "1");
                /*}*/
                }
            }
            opreaterMsg.put("canOperateUserId", l);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
        }


        /*Map<String, Object> tableCardMsg = new HashMap<>();
        msg.put("tableCards", tableCards);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellTableCards", tableCardMsg), userId);
*/
        boolean b = false;
        a:for (long l:playerCardInfos.keySet()) {
            if("1".equals(playerCardInfos.get(l).fanZhu)){
                b=true;
                break a;
            }
        }
        if (!b){
            playerCardInfos.get(userId).setSeeTableCard("1");
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
            this.step = CAN_CHANGE_TABLE_CARDS;
        }

        MsgSender.sendMsg2Player("gameService", "shuangLiang", 0, userId);

        updateLastOperateTime();
        return 0;
    }

    public int fanZhu(long userId,boolean fan,Integer card){
        if(fan){//反主
            if(Math.abs(card)>52){
                this.huaSe = -1;
            }else{
                this.huaSe = Math.abs(card)%4;
            }
            this.shuangLiangDouble = true;
            this.liangCard = card;
            this.shouQiDouble = false;
            this.fanzhu = true;
            this.seeTableCard = false;
            playerCardInfos.get(userId).setFanZhu("2");
            playerCardInfos.get(userId).setRenShu("3");
            chuPaiId = userId;
            zhuId = userId;
            secondBanker = userId;
            room.setBankerId(userId);
            Map<String, Object> fzmsg = new HashMap<>();
            fzmsg.put("fanZhuId", userId);
            fzmsg.put("fanZhuCard", card);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellFanZhu", fzmsg), users);
            for (Long l:playerCardInfos.keySet()) {
                Map<String, Object> opreaterMsg = new HashMap<>();
                if(liangCard!=54&&liangCard!=53){
                    if((playerCardInfos.get(l).handCards.contains(53)&&playerCardInfos.get(l).handCards.contains(-53))||
                            (playerCardInfos.get(l).handCards.contains(54)&&playerCardInfos.get(l).handCards.contains(-54))){
                    /*if(l!=userId){*/
                        playerCardInfos.get(l).setFanZhu("1");
                        opreaterMsg.put("fanZhu", "1");
                    /*}*/
                    }else{
                        playerCardInfos.get(l).setFanZhu("3");
                    }
                }else{
                    for (long ll:playerCardInfos.keySet()) {
                        playerCardInfos.get(ll).setFanZhu("3");
                    }
                }
                if("2".equals(playerCardInfos.get(l).getDanLiang())||"2".equals(playerCardInfos.get(l).getShouQi())||"2".equals(playerCardInfos.get(l).getShuangLiang())){
                    if(l!=userId){
                        playerCardInfos.get(l).setDanLiang("3");
                        playerCardInfos.get(l).setShouQi("3");
                        playerCardInfos.get(l).setShuangLiang("3");
                    }
                }
                if("1".equals(playerCardInfos.get(l).getSeeTableCard())){
                    if(l!=userId){
                        playerCardInfos.get(l).setSeeTableCard("3");
                    }
                }
                if("1".equals(playerCardInfos.get(l).getSeeTableCard())){
                    if(l!=userId){
                        playerCardInfos.get(l).setSeeTableCard("3");
                    }
                }
                if("2".equals(playerCardInfos.get(l).getFanZhu())){
                    if(l!=userId){
                        playerCardInfos.get(l).setFanZhu("3");
                    }
                }
                opreaterMsg.put("canOperateUserId", l);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
            }

            /*boolean b = true;
            for (Long l:playerCardInfos.keySet()) {
                if("1".equals(playerCardInfos.get(l).getFanZhu())){
                    b =false;
                }
            }
            if(b){*/
                playerCardInfos.get(userId).setSeeTableCard("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
                this.step = CAN_CHANGE_TABLE_CARDS;
            /*}*/
        }else{
            if(playerCardInfos.get(userId).handCards.contains(liangCard)&&playerCardInfos.get(userId).handCards.contains(-liangCard)){
                playerCardInfos.get(zhuId).setRenShu("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canRenShu", 0), zhuId);
            }
            for (long l:playerCardInfos.keySet()) {
                if("1".equals(playerCardInfos.get(l).fanZhu)){
                    playerCardInfos.get(l).setFanZhu("4");
                }
            }
            //没有反主的，通知可以看底牌
            boolean b = true;
            a:for (long l:playerCardInfos.keySet()) {
                if("1".equals(playerCardInfos.get(l).fanZhu)){
                    b=false;
                    break a;
                }
            }
            if (b && !changTableCard){
                playerCardInfos.get(zhuId).setSeeTableCard("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
                this.step = CAN_CHANGE_TABLE_CARDS;
            }
            if(changTableCard){
                this.step = STEP_CHUPAI;

                Map<String, Object> msgs = new HashMap<>();
                msgs.put("zhuId", zhuId);
                msgs.put("liangCard", liangCard);
                msgs.put("shuangLiangDouble", shuangLiangDouble);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellGongTou", msgs), users);

                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), zhuId);
                Map<String, Object> msg = new HashMap<>();
                msg.put("nextUser", chuPaiId);
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChuPaiId", msg), users);
            }
        }
        //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), zhuId);

        MsgSender.sendMsg2Player("gameService", "fanZhu", 0, userId);
        updateLastOperateTime();
        return 0;
    }

    public int renShu(long userId,boolean renshu){
        if("1".equals(playerCardInfos.get(userId).getRenShu())){
            if(renshu){
                computeRenshu();
                sendResult(room.getBankerId());
                genRecord();
                room.clearReadyStatus(true);
                sendFinalResult();
            }/*else {
                playerCardInfos.get(userId).setSeeTableCard("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChangeTableCards", 0), zhuId);
                this.step = CAN_CHANGE_TABLE_CARDS;
            }*/
        }
        MsgSender.sendMsg2Player("gameService", "renShu", 0, userId);
        updateLastOperateTime();
        return 0;
    }

    public int changeTableCards(long userId,String tableDelete,String tableAdd){

        //delete和add均为底牌的操作
        List<Integer> delete  = CardsUtil.transfromStringToCards(tableDelete);
        //List<Integer> add  = CardsUtil.transfromStringToCards(tableAdd);

        playerCardInfos.get(userId).handCards.addAll(tableCards);
        playerCardInfos.get(userId).handCards.removeAll(delete);

        tableCards.clear();
        tableCards.addAll(delete);
        //tableCards.addAll(add);
        this.changTableCard = true;

        boolean b = false;//判断是不是有人可以反主
        for (Long l:playerCardInfos.keySet()) {
            if("1".equals(playerCardInfos.get(l).getFanZhu())||"4".equals(playerCardInfos.get(l).getFanZhu())){
                playerCardInfos.get(l).setFanZhu("1");
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", 0), l);
                b = true;
            }

            if(shuangLiangDouble){//双亮
                if(liangCard==53||liangCard==54){

                }else{
                    if((playerCardInfos.get(l).getHandCards().contains(54)&&playerCardInfos.get(l).getHandCards().contains(-54))||
                            (playerCardInfos.get(l).getHandCards().contains(53)&&playerCardInfos.get(l).getHandCards().contains(-53))){
                        playerCardInfos.get(l).setFanZhu("1");
                        //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", 0), l);
                        b = true;

                        Map<String, Object> opreaterMsg = new HashMap<>();
                        opreaterMsg.put("fanZhu", "1");
                        opreaterMsg.put("canOperateUserId", l);
                        MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
                    }
                }
            }else{
                if((playerCardInfos.get(l).getHandCards().contains(54)&&playerCardInfos.get(l).getHandCards().contains(-54))||
                   (playerCardInfos.get(l).getHandCards().contains(53)&&playerCardInfos.get(l).getHandCards().contains(-53))||
                   (playerCardInfos.get(l).getHandCards().contains(52)&&playerCardInfos.get(l).getHandCards().contains(-52))||
                   (playerCardInfos.get(l).getHandCards().contains(51)&&playerCardInfos.get(l).getHandCards().contains(-51))||
                   (playerCardInfos.get(l).getHandCards().contains(50)&&playerCardInfos.get(l).getHandCards().contains(-50))||
                   (playerCardInfos.get(l).getHandCards().contains(49)&&playerCardInfos.get(l).getHandCards().contains(-49))){
                    playerCardInfos.get(l).setFanZhu("1");
                    //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", 0), l);
                    Map<String, Object> opreaterMsg = new HashMap<>();
                    opreaterMsg.put("fanZhu", "1");
                    opreaterMsg.put("canOperateUserId", l);
                    MsgSender.sendMsg2Player(new ResponseVo("gameService", "canOperate", opreaterMsg), l);
                    b = true;
                }
            }
        }
        MsgSender.sendMsg2Player("gameService", "changeTableCards", 0, userId);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChangeTableCards", userId), users);
        if (b){
            this.step = STEP_FANZHU;
        }else{
            if(playerCardInfos.get(userId).handCards.contains(liangCard)&&playerCardInfos.get(userId).handCards.contains(-liangCard)){
                if("4".equals(playerCardInfos.get(userId).getRenShu())){
                    playerCardInfos.get(userId).setRenShu("1");
                    MsgSender.sendMsg2Player(new ResponseVo("gameService", "canRenShu", 0), userId);
                }
            }
            Map<String, Object> msgs = new HashMap<>();
            msgs.put("zhuId", zhuId);
            msgs.put("liangCard", liangCard);
            msgs.put("shuangLiangDouble", shuangLiangDouble);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellGongTou", msgs), users);

            this.step = STEP_CHUPAI;
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), zhuId);
            Map<String, Object> msg = new HashMap<>();
            msg.put("nextUser", chuPaiId);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChuPaiId", msg), users);
        }
        updateLastOperateTime();
        return 0;
    }

    public int play(long userId,String playCard){



        this.step = STEP_CHUPAI;
        List<Integer> playCardList  = CardsUtil.transfromStringToCards(playCard);

        /*if(-2!=chuHuaSe){
            if(chuHuaSe!=playCardList.get(0)%4){
                for (Integer i:playerCardInfos.get(userId).handCards) {
                    if(i<45 && chuHuaSe==i%4){
                        return ErrorCode.ERROR_CARD;
                    }
                }
            }
        }*/

        long tempUser = 0l;
        for (long l:compareCard.keySet()) {
            if(1==compareCard.get(l)){
                tempUser = l;
            }
        }
        if(0l!=tempUser){
            if(playCardList.size()!=playerCardInfos.get(tempUser).playCards.size()){
                return ErrorCode.ERROR_CARD;
            }
        }
        if(-2!=chuHuaSe||chuHuaSe!=Math.abs(playCardList.get(0))%4){//出的不一样
            if(-1==chuHuaSe||chuHuaSe==huaSe){//出的主
                if(Math.abs(playCardList.get(0))<45 && Math.abs(playCardList.get(0))%4!=huaSe){
                    for (Integer i:playerCardInfos.get(userId).handCards) {
                        if(!playCardList.contains(i) && (i>44 || huaSe==Math.abs(i)%4)){
                            return ErrorCode.ERROR_CARD;
                        }
                    }
                }
            }else{//不是主
                if(Math.abs(playCardList.get(0))>44 || Math.abs(playCardList.get(0))%4==huaSe){
                    for (Integer i:playerCardInfos.get(userId).handCards) {
                        if(!playCardList.contains(i) && Math.abs(i)<45  && chuHuaSe==Math.abs(i)%4){
                            return ErrorCode.ERROR_CARD;
                        }
                    }
                }
                if(chuHuaSe!=Math.abs(playCardList.get(0))%4){
                    for (Integer i:playerCardInfos.get(userId).handCards) {
                        if(!playCardList.contains(i) && Math.abs(i)<45 && chuHuaSe==Math.abs(i)%4){
                            return ErrorCode.ERROR_CARD;
                        }
                    }
                }
            }
        }

        if(playCardList.size()>1){
            List<Integer> tempList = new ArrayList<>();
            tempList.addAll(playerCardInfos.get(userId).handCards);
            tempList.removeAll(playCardList);
            boolean b = false;
            a:for (Integer i:playCardList){
                if(chuHuaSe!=Math.abs(i)%4){
                    b = true;
                    break a;
                }
            }
            if(b){
                for (Integer i:tempList) {
                    if(Math.abs(i)<45 && chuHuaSe==Math.abs(i)%4){
                        return ErrorCode.ERROR_CARD;
                    }
                }
            }
        }
        if(0==getChuPaiNum()){
            if(playCardList.size()>=4){
                if(!CardsUtil.isTuoLaJi(playCardList)){
                    return ErrorCode.ERROR_CARD;
                }
            }else if(playCardList.size()==2){
                if(playCardList.get(0)+playCardList.get(1)!=0){
                    return ErrorCode.ERROR_CARD;
                }
            }
        }else{
            if(playCardList.size()>=4){
                if(-1==chuHuaSe||chuHuaSe==huaSe){//出的主
                    if(2*CardsUtil.duiNumZhu(playCardList,huaSe)!=playCardList.size()){
                        if(CardsUtil.duiNumZhu(playerCardInfos.get(userId).handCards,huaSe)*2>=playCardList.size()){
                            return ErrorCode.ERROR_CARD;
                        }
                    }
                }
                else{//出的不是主
                    if(2*CardsUtil.duiNumNoZhu(playCardList,chuHuaSe)!=playCardList.size()){
                        if(CardsUtil.duiNumNoZhu(playerCardInfos.get(userId).handCards,chuHuaSe)*2>=playCardList.size()){
                            return ErrorCode.ERROR_CARD;
                        }
                    }
                }
            }
        }
        /*long temp = 0l;
        for (long l:playerCardInfos.keySet()) {
            if(playerCardInfos.get(l).handCards.contains(liangCard)||playerCardInfos.get(l).handCards.contains(-liangCard)){
                if(l!=zhuId){
                    temp=l;
                }
            }
        }
        if(temp!=0l){
            this.secondBanker=temp;
        }else{
            this.secondBanker=zhuId;
        }*/

        for (Integer i:playCardList) {
            if(0==(i+liangCard)||liangCard==i){//出特殊7的话 会有提示
                if(userId!=zhuId){
                    this.secondBanker = userId;
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("tiaoFanUserId", userId);
                    msg.put("liangCard", liangCard);
                    MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellGongYou", msg), users);
                }else{
                    long temp = 0l;
                    for (long l:playerCardInfos.keySet()) {
                        if(playerCardInfos.get(l).handCards.contains(liangCard)||playerCardInfos.get(l).handCards.contains(-liangCard)){
                            if(l!=zhuId){
                                temp=l;
                            }
                        }
                    }
                    if(temp==zhuId){
                        this.secondBanker=zhuId;
                    }
                }
            }
        }
        PlayerCardInfoPlaySeven chuPaiPlayer = playerCardInfos.get(userId);
        chuPaiPlayer.setPlayCards(playCardList);
        chuPaiPlayer.getHandCards().removeAll(playCardList);

        ifChuPai.put(userId,1);

        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", userId);
        msg.put("playCard", playCardList);


        if(1==getChuPaiNum()){//第一个人出牌

            if(Math.abs(playCardList.get(0))>44){
                this.chuHuaSe = -1;
            }else{
                if(Math.abs(playCardList.get(0))%4==huaSe){
                    this.chuHuaSe = -1;
                }else {
                    this.chuHuaSe = Math.abs(playCardList.get(0))%4;
                }
            }
            compareCard.put(userId,1);
            long nextUser = nextTurnId(userId);
            chuPaiId = nextUser;
            this.diYiChu = userId;
            //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), nextUser);
            Map<String, Object> msgs = new HashMap<>();
            msgs.put("nextUser", nextUser);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChuPaiId", msgs), users);
            msg.put("chuHuaSe",this.chuHuaSe);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "playCard", msg), users);

            MsgSender.sendMsg2Player(new ResponseVo("gameService", "play", 0), userId);

        }else if(room.getPersonNumber()==getChuPaiNum()){//最后一个人出牌
            boolean compareResult = CardsUtil.compareCards(huaSe,chuHuaSe,getWinCards(),playCardList);
            int fen = 0;
            long winnerId = getWinnerId();
            if(compareResult){//原来的人赢
                compareCard.put(userId,0);
            }else{
                compareCard.put(winnerId,0);
                compareCard.put(userId,1);
            }
            for (Long l :users) {
                for (Integer integer:playerCardInfos.get(l).getPlayCards()) {
                    fen+=CardsUtil.cardsOfScore.get(integer);
                }
            }
            winnerId = getWinnerId();
            playerCardInfos.get(winnerId).setFen(fen);
            userGetFen.put(winnerId,userGetFen.get(winnerId)+fen);

            msg.put("chuHuaSe",this.chuHuaSe);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "playCard", msg), users);

            Map<String, Object> msgss = new HashMap<>();
            msgss.put("userGetFen", userGetFen);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "allFen", msgss), users);

            MsgSender.sendMsg2Player(new ResponseVo("gameService", "play", 0), userId);

            if(playerCardInfos.get(userId).handCards.size()>0){
                //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), winnerId);
                Map<String, Object> msgs = new HashMap<>();
                msgs.put("nextUser", winnerId);
                this.chuPaiId = winnerId;
                this.chuHuaSe = -2;
                MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChuPaiId", msgs), users);
            }else{
                if(winnerId!=zhuId&&winnerId!=secondBanker&&secondBanker!=0l){
                    int size = playCardList.size();
                    if(1==size){
                        this.kouDiBeiShu = 2;
                    }else{
                        this.kouDiBeiShu = (int)Math.scalb(1,size/2+1);
                    }
                    for (Integer integer:tableCards) {//算底分
                        this.tableCardFen+=CardsUtil.cardsOfScore.get(integer);
                    }
                }
                for (long l:userGetFen.keySet()) {
                    if(l!=zhuId&&l!=secondBanker){
                        this.jianFen+=userGetFen.get(l);
                    }
                }

                compute(winnerId);
                sendResult(winnerId);
                genRecord();
                room.clearReadyStatus(true);
                sendFinalResult();
            }

            for (long l:users) {
                ifChuPai.put(l,0);
                compareCard.put(l,0);
                playerCardInfos.get(l).setPlayCards(null);
            }

        }else{//第二个人出牌
            boolean compareResult = CardsUtil.compareCards(huaSe,chuHuaSe,getWinCards(),playCardList);
            long winnerId = getWinnerId();
            if(compareResult){//原来的人赢
                compareCard.put(userId,0);
            }else{
                compareCard.put(winnerId,0);
                compareCard.put(userId,1);
            }
            long nextUser = nextTurnId(userId);
            this.chuPaiId = nextUser;
            //MsgSender.sendMsg2Player(new ResponseVo("gameService", "canChuPai", 0), nextUser);
            Map<String, Object> msgs = new HashMap<>();
            msgs.put("nextUser", nextUser);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "tellChuPaiId", msgs), users);

            msg.put("chuHuaSe",this.chuHuaSe);
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "playCard", msg), users);

            MsgSender.sendMsg2Player(new ResponseVo("gameService", "play", 0), userId);
        }


        updateLastOperateTime();
        return 0;
    }

    public int dealAgain(){
        room.clearReadyStatus(false);
        MsgSender.sendMsg2Player("gameService", "dealAgain", 0, users);
        updateLastOperateTime();
        return 0;
    }


    /**
     * 洗牌
     */
    protected void shuffle() {
        cards.addAll(CardsUtil.cardsOf108.keySet());
        Collections.shuffle(cards);
        tableCards.add(cards.get(107));tableCards.add(cards.get(106));
        tableCards.add(cards.get(105));tableCards.add(cards.get(104));
        tableCards.add(cards.get(103));tableCards.add(cards.get(102));
        tableCards.add(cards.get(101));tableCards.add(cards.get(100));
    }

    protected void shuffle2() {
        cards.addAll(CardsUtil.cardsOf108.keySet());

    }

    @Deprecated
    protected void dealOne() {
        int cardSize = 1;
        if(4==room.getPersonNumber()){
            while (cardSize<26){
                sendSingleCard();
                try {
                    Thread.sleep(STOP_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                cardSize++;
            }
        }else if(5==room.getPersonNumber()){
            while (cardSize<21){
                sendSingleCard();
                try {
                    Thread.sleep(STOP_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                cardSize++;
            }
        }
    }

    @Deprecated
    private void sendSingleCard(){
        for (PlayerCardInfoPlaySeven playerCardInfo : playerCardInfos.values()) {
            playerCardInfo.handCards.add(cards.remove(0));
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", playerCardInfo.getUserId());
            int size = playerCardInfo.handCards.size();
            int tempcardNum = playerCardInfo.handCards.get(size-1);
            msg.put("card",tempcardNum);
            if(size==1){
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfo.setShouQi("1");
                    playerCardInfo.setDanLiang("1");
                    msg.put("shouQi", "1");
                    msg.put("danLiang", "1");
                }
            }else{
                if(tempcardNum==49||tempcardNum==50||tempcardNum==51||tempcardNum==52||tempcardNum==53||tempcardNum==54||
                        tempcardNum==-49||tempcardNum==-50||tempcardNum==-51||tempcardNum==-52||tempcardNum==-53||tempcardNum==-54){
                    playerCardInfo.setDanLiang("1");
                    msg.put("danLiang", "1");
                }
            }
            if("1".equals(playerCardInfo.getDanLiang())){
                for (int j = 0; j < size; j++) {
                    if(tempcardNum!=playerCardInfo.handCards.get(j))
                        if (tempcardNum + playerCardInfo.handCards.get(j) == 0) {
                            playerCardInfo.setShuangLiang("1");
                            playerCardInfo.setFanZhu("4");
                            playerCardInfo.setRenShu("4");
                            msg.put("shuangLiang", "1");
                        }
                }
            }
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "cardAndCanOperate", msg), playerCardInfo.getUserId());
        }
    }



    /**
     * 发牌，定时发
     */
    @Deprecated
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
    @Deprecated
    private void sendSingleCardAndWait500s(int i) {
        for (Long l:playerCardInfos.keySet()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", l);
            int tempcardNum = playerCardInfos.get(l).handCards.get(i);
            msg.put("card",tempcardNum);
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
                for (int j = 0; j < i; j++) {
                    if(tempcardNum+playerCardInfos.get(l).handCards.get(j)==0){
                        playerCardInfos.get(l).setShuangLiang("1");
                        playerCardInfos.get(l).setFanZhu("4");
                        playerCardInfos.get(l).setRenShu("4");
                    }
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


    protected void compute(long winnerId) {
        int allScore = kouDiBeiShu*tableCardFen+jianFen;
        int temp = 0;//正分为庄
        if(allScore>=40&&allScore<80){
            temp=1;
        }else if(allScore>=5&&allScore<40){
            temp=2;
        }else if(allScore==0){
            if(5==room.fengDing){
                temp=5;
            }else {
                temp=3;
            }
        }else{
            temp = -(allScore-40)/40;
        }
        if(shuangLiangDouble||fanzhu){
            if(room.zhuangDanDaJiaBei && winnerId==room.getBankerId()){
                temp*=2;
            }
        }
        if(room.kouDiJiaJi){
            if(winnerId!=zhuId&&winnerId!=secondBanker){
                if(allScore>=80){
                    if(1==playerCardInfos.get(winnerId).playCards.size()){
                        temp-=1;
                    }else if(2==playerCardInfos.get(winnerId).playCards.size()){
                        temp-=2;
                    }else{
                        temp-=4;
                    }
                }
            }
        }

        RoomPlaySeven roomPlaySeven = null;
        if (room instanceof RoomPlaySeven) {
            roomPlaySeven = (RoomPlaySeven) room;
        }

        if(Math.abs(temp)>room.fengDing){
            if (temp>0){
                temp = room.fengDing;
            }else {
                temp = -room.fengDing;
            }
        }
        for (Long l:users) {
            if(secondBanker==zhuId){
                if(l!=secondBanker && l!=zhuId){//不是庄
                    playerCardInfos.get(l).addScore(-temp);
                    roomPlaySeven.addUserSocre(l, -temp);
                }else {
                    playerCardInfos.get(l).addScore(3*temp);
                    roomPlaySeven.addUserSocre(l, 3*temp);
                }
            }else {
                if(l!=secondBanker && l!=zhuId){//不是庄
                    playerCardInfos.get(l).addScore(-temp);
                    roomPlaySeven.addUserSocre(l, -temp);
                }else {
                    playerCardInfos.get(l).addScore(temp);
                    roomPlaySeven.addUserSocre(l, temp);
                }
            }
        }
    }

    //认输输两分
    protected void computeRenshu() {
        RoomPlaySeven roomPlaySeven = null;
        if (room instanceof RoomPlaySeven) {
            roomPlaySeven = (RoomPlaySeven) room;
        }
        for (Long l:users) {
            roomPlaySeven.addUserSocre(l, 1);
            playerCardInfos.get(l).addScore(1);
        }
        roomPlaySeven.addUserSocre(zhuId, -1*(roomPlaySeven.getPersonNumber()));
        playerCardInfos.get(zhuId).addScore(-1*(roomPlaySeven.getPersonNumber()));
    }

    protected void sendResult(long winnerId) {
        Map<Long,Double> score = new HashMap<>();
        GameResultSeven gameResultSeven  = new GameResultSeven();
        for (long l:users) {
            score.put(l,playerCardInfos.get(l).getScore());
        }
        for (PlayerCardInfoPlaySeven p : playerCardInfos.values()) {
            gameResultSeven.getPlayerCardInfos().add( p.toVo());
        }

        gameResultSeven.setZhuId(zhuId);
        gameResultSeven.setSecondBanker(secondBanker);
        gameResultSeven.setUserScores(score);
        gameResultSeven.setTableCardFen(tableCardFen);
        gameResultSeven.setJianFen(jianFen);
        gameResultSeven.setKouDiBeiShu(kouDiBeiShu);
        gameResultSeven.setTableCards(tableCards);
        if(winnerId!=zhuId && winnerId!=secondBanker){
            if(1==playerCardInfos.get(winnerId).playCards.size()){
                gameResultSeven.setKouDiJiaJi(1);
            }else if(2==playerCardInfos.get(winnerId).playCards.size()){
                gameResultSeven.setKouDiJiaJi(2);
            }else{
                gameResultSeven.setKouDiJiaJi(4);
            }
        }


        MsgSender.sendMsg2Player("gameService", "gameResult", gameResultSeven, users);
    }

    protected void genRecord() {
        Map<Long,Double> score = new HashMap<>();
        for (long l:users) {
            score.put(l,playerCardInfos.get(l).getScore());
        }
        long id = IdWorker.getDefaultInstance().nextId();
        genRecord(score, room, id);
    }


    protected void sendFinalResult() {
        //所有牌局都结束
        if (room.getCurGameNumber() > room.getGameNumber()) {
            List<UserOfResult> userOfResultList = this.room.getUserOfResult();
            // 存储返回
            GameOfResult gameOfResult = new GameOfResult();
            gameOfResult.setUserList(userOfResultList);
            MsgSender.sendMsg2Player("gameService", "gameFinalResult", gameOfResult, users);

            RoomManager.removeRoom(room.getRoomId());

            //战绩
            this.room.genRoomRecord();

        }
    }

    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    //获取已经出牌的人数
    public int getChuPaiNum(){
        int chuPaiNum = 0;
        for (long l:ifChuPai.keySet()) {
            if(1==ifChuPai.get(l)){
                chuPaiNum++;
            }
        }
        return chuPaiNum;
    }

    //获取赢的人的牌
    public List<Integer> getWinCards(){
        long winnerId = 0l;
        for (long l:compareCard.keySet()) {
            if(1==compareCard.get(l)){
                winnerId = l;
            }
        }
        return playerCardInfos.get(winnerId).playCards;
    }

    //获取赢的人
    public long getWinnerId(){
        long winnerId = 0l;
        for (long l:compareCard.keySet()) {
            if(1==compareCard.get(l)){
                winnerId = l;
            }
        }
        return winnerId;
    }

    //是否2张7
    public boolean ifTwo7OrWang(long userId){
        return (this.playerCardInfos.get(userId).handCards.contains(49) && this.playerCardInfos.get(userId).handCards.contains(-49))||
                (this.playerCardInfos.get(userId).handCards.contains(50) && this.playerCardInfos.get(userId).handCards.contains(-50))||
                (this.playerCardInfos.get(userId).handCards.contains(51) && this.playerCardInfos.get(userId).handCards.contains(-51))||
                (this.playerCardInfos.get(userId).handCards.contains(52) && this.playerCardInfos.get(userId).handCards.contains(-52))||
                (this.playerCardInfos.get(userId).handCards.contains(53) && this.playerCardInfos.get(userId).handCards.contains(-53))||
                (this.playerCardInfos.get(userId).handCards.contains(54) && this.playerCardInfos.get(userId).handCards.contains(-54));
    }

    //是否2张7
    public boolean ifTwoWang(long userId){
        return (this.playerCardInfos.get(userId).handCards.contains(53) && this.playerCardInfos.get(userId).handCards.contains(-53))||
                (this.playerCardInfos.get(userId).handCards.contains(54) && this.playerCardInfos.get(userId).handCards.contains(-54));
    }


    public boolean isShouQiDouble() {
        return shouQiDouble;
    }

    public void setShouQiDouble(boolean shouQiDouble) {
        this.shouQiDouble = shouQiDouble;
    }

    public boolean isShuangLiangDouble() {
        return shuangLiangDouble;
    }

    public void setShuangLiangDouble(boolean shuangLiangDouble) {
        this.shuangLiangDouble = shuangLiangDouble;
    }

    public boolean isSeeTableCard() {
        return seeTableCard;
    }

    public void setSeeTableCard(boolean seeTableCard) {
        this.seeTableCard = seeTableCard;
    }

    public boolean isFanzhu() {
        return fanzhu;
    }

    public void setFanzhu(boolean fanzhu) {
        this.fanzhu = fanzhu;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public List<Integer> getTableCards() {
        return tableCards;
    }

    public void setTableCards(List<Integer> tableCards) {
        this.tableCards = tableCards;
    }

    public long getChuPaiId() {
        return chuPaiId;
    }

    public void setChuPaiId(long chuPaiId) {
        this.chuPaiId = chuPaiId;
    }

    public long getZhuId() {
        return zhuId;
    }

    public void setZhuId(long zhuId) {
        this.zhuId = zhuId;
    }

    public int getTableCardFen() {
        return tableCardFen;
    }

    public void setTableCardFen(int tableCardFen) {
        this.tableCardFen = tableCardFen;
    }

    public int getJianFen() {
        return jianFen;
    }

    public void setJianFen(int jianFen) {
        this.jianFen = jianFen;
    }

    public int getKouDiBeiShu() {
        return kouDiBeiShu;
    }

    public void setKouDiBeiShu(int kouDiBeiShu) {
        this.kouDiBeiShu = kouDiBeiShu;
    }

    public Integer getLiangCard() {
        return liangCard;
    }

    public void setLiangCard(Integer liangCard) {
        this.liangCard = liangCard;
    }

    public long getSecondBanker() {
        return secondBanker;
    }

    public void setSecondBanker(long secondBanker) {
        this.secondBanker = secondBanker;
    }

    public Map<Long, Integer> getIfChuPai() {
        return ifChuPai;
    }

    public void setIfChuPai(Map<Long, Integer> ifChuPai) {
        this.ifChuPai = ifChuPai;
    }

    public Map<Long, Integer> getCompareCard() {
        return compareCard;
    }

    public void setCompareCard(Map<Long, Integer> compareCard) {
        this.compareCard = compareCard;
    }

    public Map<Long, Integer> getUserGetFen() {
        return userGetFen;
    }

    public void setUserGetFen(Map<Long, Integer> userGetFen) {
        this.userGetFen = userGetFen;
    }

    public RoomPlaySeven getRoom() {
        return room;
    }

    public void setRoom(RoomPlaySeven room) {
        this.room = room;
    }

    public Map<Long, PlayerCardInfoPlaySeven> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(Map<Long, PlayerCardInfoPlaySeven> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public int getHuaSe() {
        return huaSe;
    }

    public void setHuaSe(int huaSe) {
        this.huaSe = huaSe;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isChangTableCard() {
        return changTableCard;
    }

    public void setChangTableCard(boolean changTableCard) {
        this.changTableCard = changTableCard;
    }

    @Override
    public IfaceGameVo toVo(long userId) {

        GamePlaySevenVo vo = new GamePlaySevenVo();
        vo.shouQiDouble = this.shouQiDouble;
        vo.shuangLiangDouble = this.shuangLiangDouble;
        vo.seeTableCard = this.seeTableCard;
        vo.fanzhu = this.fanzhu;
        vo.cards = this.cards;
        vo.tableCards = this.tableCards;
        vo.chuPaiId = this.chuPaiId;
        vo.zhuId = this.zhuId;
        vo.tableCardFen = this.tableCardFen;
        vo.jianFen = this.jianFen;
        vo.kouDiBeiShu = this.kouDiBeiShu;
        vo.liangCard = this.liangCard;
        vo.ifChuPai = this.ifChuPai;
        vo.compareCard = this.compareCard;
        vo.userGetFen = this.userGetFen;
        //vo.room = this.room;
        vo.huaSe = this.huaSe;
        vo.step = this.step;
        vo.changTableCard = this.changTableCard;
        vo.chuHuaSe = this.chuHuaSe;
        vo.diYiChu = this.diYiChu;
        vo.secondBanker = this.secondBanker;

        for (long l : this.getPlayerCardInfos().keySet()) {
            if(userId == l){
                vo.playerCardInfos.put(l, this.getPlayerCardInfos().get(l).toVo(userId));
            }else{
                vo.playerCardInfos.put(l, this.getPlayerCardInfos().get(l).toVo());
            }
        }

        return vo;
    }
}
