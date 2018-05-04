package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.doudizhu.CardUtil;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;
import java.util.*;

public class GameZhaGuZi extends Game {

    protected static final Logger logger = LoggerFactory.getLogger(GameZhaGuZi.class);

    protected static final String serviceName = "gameZhaGuZiService";

    protected Map<Long, PlayerZhaGuZi> playerCardInfos = new HashMap<>();

    protected RoomZhaGuZi room;

    protected List<Integer> cards = new ArrayList<Integer>();

    protected List<List<Integer>> leaveCards = new ArrayList<>();

    public void startGame(List<Long> users, Room room){

        this.room = (RoomZhaGuZi) room;
        this.users = users;
        initPlayer();
        initCards();
        deal();

        //第一句 红桃5 先说话
        if (this.room.curGameNumber == 1){

            PlayerZhaGuZi player = null;

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃五
                    if (card == 50){
                        player = playerZhaGuZi;
                        break;
                    }
                }
            }
            this.room.lastWinnderId = player.userId;
            talkStart(this.room.lastWinnderId);

        }else {
            talkStart(this.room.lastWinnderId);
        }
    }

    //发牌
    public void deal(){

        int count = cards.size() / users.size();
        long lastId = this.room.firstId;
        long currentId = lastId;

        Map<Object, Object> result = new HashMap<>();
        List<PlayerZhaGuZiVo> list = new ArrayList<>();

        while (true){
            PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(currentId);
            while (true){
                playerZhaGuZi.cards.add(this.cards.get(0));
                this.cards.remove(0);
                if (playerZhaGuZi.cards.size() == count){
                    currentId = nextTurnId(currentId);
                    break;
                }
            }
            list.add((PlayerZhaGuZiVo) playerZhaGuZi.toVo());
            //轮圈完毕
            if (currentId == lastId){
                break;
            }
        }
        result.put("players", list);
        MsgSender.sendMsg2Player(serviceName, "deal", result, users);
    }

    //通知发话
    public void talkStart(long userId){
        MsgSender.sendMsg2Player(serviceName, "talkStart","pleaseTalk",userId);
    }

    public int talk(long userId, int op){
        PlayerZhaGuZi playerZhaGuZi = this.playerCardInfos.get(userId);
        playerZhaGuZi.setOp(op);

        long nextId = nextTurnId(userId);
        //全部说话完毕
        if (nextId == this.room.lastWinnderId){
            playStart();
        }else {
            talkStart(nextId);
        }
        return 0;
    }

    //开始打牌
    public void playStart(){

        int count = 0;
        for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){
            if (playerZhaGuZi.getOp() == Operator.LIANG_SAN || playerZhaGuZi.getOp() == Operator.ZHA_GU){
                count++;
            }
        }

        //没人亮牌
        if (count == 0){

        }else {

            PlayerZhaGuZi hongTao = null;
            PlayerZhaGuZi fangPian = null;
            PlayerZhaGuZi heiTao = null;

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃三
                    if (card == 7){
                        hongTao = playerZhaGuZi;
                    }else if(card == 9){
                        fangPian = playerZhaGuZi;
                    }else if(card == 6){
                        heiTao = playerZhaGuZi;
                    }
                }
            }

             if (this.room.getPersonNumber() == 5){
                //红桃三方片三是一个人
                if (hongTao == fangPian){
                    MsgSender.sendMsg2Player(serviceName, "isGiveUpStart", "0", hongTao.userId);
                }else {
                    continuePlay();
                }

             }else {
                 //红桃三方片三黑桃三是一个人
                 if (hongTao == fangPian && hongTao == heiTao){
                     MsgSender.sendMsg2Player(serviceName, "isGiveUpStart", "0", hongTao.userId);
                 }else {
                     continuePlay();
                 }
             }
        }
    }

    //出牌
    public void continuePlay(){

        PlayerZhaGuZi player = null;
        //有红桃5的先出
        if (this.room.curGameNumber == 1){

            for (PlayerZhaGuZi playerZhaGuZi : playerCardInfos.values()){

                for (Integer card : playerZhaGuZi.cards){
                    //红桃五
                    if (card == 50){
                        player = playerZhaGuZi;
                        break;
                    }
                }
            }

        }else {

            player = playerCardInfos.get(this.room.lastWinnderId);
        }

        //提示出牌
        MsgSender.sendMsg2Player(serviceName, "discardStart", "0", player.userId);

    }

    //算分
    public void compute(){

    }

    //算分
    public void computeGiveUp(){

    }

    public void noticeDiscardStart(long uid){

        int count = 0;
        for (PlayerZhaGuZi p : playerCardInfos.values()){
            if (p.isOver()){
                count++;
            }
        }

        if (count == this.users.size() - 1){
            compute();
            return;
        }

        long nextID = uid;

        while (true){
            nextID = nextTurnId(nextID);
            if (playerCardInfos.get(nextID).isOver()){
                break;
            }
        }

        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(nextID);

        MsgSender.sendMsg2Player(serviceName, "discardStart", "0", nextID);
    }

    //出牌协议
    public int beingDiscard(long uid, int op,List<Integer> list){

        PlayerZhaGuZi playerZhaGuZi = playerCardInfos.get(uid);
        //如果过的话 提示下一个人出牌
        if (op == Operator.PASS){

            playerZhaGuZi.opList.add(op);
            MsgSender.sendMsg2Player(serviceName, "discardResult", "0", uid);
            noticeDiscardStart(uid);
        }
        //管上
        else if(op == Operator.GUAN_SHANG){

            boolean ret = false;

            List<Integer> last = null;

            if (leaveCards.size() != 0){
                last = leaveCards.get(leaveCards.size() - 1);
            }
            //第一次出牌
            if (last == null){
                playerZhaGuZi.opList.add(op);
                MsgSender.sendMsg2Player(serviceName, "discardResult", "0", uid);

                for (Integer a : list){
                    playerZhaGuZi.cards.remove(a);
                }

                leaveCards.add(list);
                noticeDiscardStart(uid);
            }else {

                int  res = CardUtils.cardsCompare(last, list);
                if (res == -1 || res == -2){
                    return ErrorCode.CARDS_ERROR;
                }
                playerZhaGuZi.opList.add(op);
                //是不是接风
                PlayerZhaGuZi lastPlayer = playerCardInfos.get(lastTurn(uid));

                boolean isFeng = false;
                //假如上一个人出牌完毕
                if (lastPlayer.isOver()){
                    boolean ret1 = playerZhaGuZi.opList.size() - lastPlayer.opList.size() == 1 ? true : false;
                    boolean ret2 = true;

                    for (PlayerZhaGuZi p : playerCardInfos.values()){

                        if (p.isOver() == true){
                            continue;
                        }
                        if ((p.opList.get(p.opList.size() - 1) != Operator.PASS)){
                            ret2 = false;
                            break;
                        }
                    }

                    //轮到自己接风
                    if (ret1 && ret2){
                        isFeng = true;
                    }
                }

                if (isFeng == false){
                    if (res == 0 || res == 1){
                        return ErrorCode.CAN_NOT_DISCARD;
                    }
                }

                MsgSender.sendMsg2Player(serviceName, "discardResult", "0", uid);

                for (Integer a : list){
                    playerZhaGuZi.cards.remove(a);
                }

                leaveCards.add(list);
                noticeDiscardStart(uid);
            }
        }
        return 0;
    }

    //上一个人
    public long lastTurn(long uid){
        int index = this.users.indexOf(uid);
        int last = index - 1;
        if (last < 0){
            last = this.users.size() - 1;
        }
        return this.users.get(last);
    }

    public void isGiveUp(long uid, boolean isGiveUp){

        MsgSender.sendMsg2Player(serviceName, "isGiveUpResult", "0", uid);

        if (isGiveUp){
            System.out.println("认输了， 那么走算分");
            computeGiveUp();
        }else {
            continuePlay();
        }
    }

    public void initPlayer(){

        playerCardInfos.clear();
        for (Long uid : users){
            PlayerZhaGuZi playerZhaGuZiz = new PlayerZhaGuZi();
            playerZhaGuZiz.userId = uid;
            playerCardInfos.put(uid, playerZhaGuZiz);
        }
    }

    public void initCards(){

        for (int i = 0; i < 54; i++){
            if (i > 45 && i < 50){
                continue;
            }
            this.cards.add(i);
        }
        //洗牌
        Collections.shuffle(this.cards);
    }

}
