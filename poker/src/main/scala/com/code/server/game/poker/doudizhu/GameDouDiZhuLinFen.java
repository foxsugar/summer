package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameDouDiZhuLinFen extends GameDouDiZhu{
    private static final Logger logger = LoggerFactory.getLogger(GameDouDiZhuLinFen.class);





    @Override
    public void init(List<Long> users, long dizhuUser) {
        this.initCardNum = 16;
        super.init(users,dizhuUser);
    }

    /**
     * 洗牌
     */
    @Override
    protected void shuffle(){
        for(int i=1;i<=54;i++){
            cards.add(i);
        }
        //去掉两张2
        cards.remove((Integer)7);
        cards.remove((Integer)5);
        Collections.shuffle(cards);
    }


    @Override
    protected void deal() {
        for (PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()) {
            for (int i = 0; i < this.initCardNum; i++) {
                playerCardInfo.cards.add(cards.remove(0));
            }
            //通知发牌
            MsgSender.sendMsg2Player(new ResponseVo("gameService", "deal", playerCardInfo.cards), playerCardInfo.userId);
        }

        //底牌
        tableCards.addAll(cards);

    }

    /**
     * 叫地主
     * @param userId
     * @param isJiao
     * @return
     */
    @Override
    public int jiaoDizhu(long userId, boolean isJiao, int score){

        logger.info(userId +"  叫地主 "+ isJiao);
        if (canJiaoUser != userId) {
            return ErrorCode.CAN_NOT_JIAO_TURN;
        }
        //叫地主列表
        chooseJiaoSet.add(userId);

        //不叫 下个人能叫
        if (!isJiao) {
            bujiaoSet.add(userId);
            if (bujiaoSet.size() >= users.size()) {
                sendResult(true,false);
                room.clearReadyStatus(true);
                sendFinalResult();
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }
        } else {//叫了 开始抢
            jiaoUser = userId;
            dizhu = userId;
            //第三个人叫的 直接开始游戏
            if (chooseJiaoSet.size() >= users.size()) {
                startPlay(jiaoUser);
            } else {

                step = STEP_QIANG_DIZHU;
                long nextId = nextTurnId(userId);
                this.canQiangUser = nextId;
                noticeCanQiang(nextId);
            }

        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isJiao", isJiao);
        MsgSender.sendMsg2Player("gameService","jiaoResponse",rs,users);

        MsgSender.sendMsg2Player("gameService","jiaoDizhu",0,userId);

        updateLastOperateTime();
        return 0;
    }

    @Override
    protected void handleBomb(CardStruct cardStruct){
        if(zhaCount < room.getMultiple() || room.getMultiple() == -1){
            if(cardStruct.getType()==CardStruct.type_炸){
                List<Integer> cards = cardStruct.getCards();
                if(cards.size()==4 && CardUtil.getTypeByCard(cards.get(0)) == 0 && CardUtil.getTypeByCard(cards.get(cards.size()-1))==0){ //3333
                    zhaCount += 1;//记录炸的数量
                    multiple *= 8;//记录倍数
                }else{ //除4个三的炸
                    zhaCount += 1;//记录炸的数量
                    multiple *= 2;//记录倍数
                }
            }else if(cardStruct.getType()==CardStruct.type_火箭){
                zhaCount += 1;//记录炸的数量
                multiple *= 2;//记录倍数
            }
        }
    }

    @Override
    protected void compute(boolean isDizhuWin){

        double subScore = 0;
        int s = isDizhuWin?-1:1;
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);
        if (playerCardInfoDizhu.isQiang()) {
            multiple *= 2;
        }
        for(PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()){
            //不是地主 扣分
            if(dizhu != playerCardInfo.getUserId()){
                double score = multiple * s;
                if (playerCardInfo.isQiang()) {
                    score *=2;
                }
                subScore += score;
                playerCardInfo.setScore(score);
                room.addUserSocre(playerCardInfo.getUserId(),score);
            }
        }

        playerCardInfoDizhu.setScore(-subScore);
        room.addUserSocre(dizhu,-subScore);

    }



    /**
     * 抢地主
     * @param userId
     * @param isQiang
     * @return
     */
    @Override
    public int qiangDizhu(long userId, boolean isQiang) {
        logger.info(userId +"  抢地主 "+isQiang);

        if(userId != canQiangUser){
            return ErrorCode.CAN_NOT_QIANG_TURN;
        }
        this.chooseQiangSet.add(userId);
        int jiaoIndex = chooseJiaoSet.size();

        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setQiang(isQiang);
        if (jiaoIndex == 1) {
            handleQiang1(userId,isQiang);
        } else if (jiaoIndex == 2) {
            handleQiang2(userId, isQiang);
        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isQiang", isQiang);
        MsgSender.sendMsg2Player("gameService","qiangResponse",rs,users);

        MsgSender.sendMsg2Player("gameService","qiangDizhu",0,userId);

        updateLastOperateTime();
        return 0;
    }


    /**
     * 处理第一个人叫的情况
     * @param qiangUser
     * @param isQiang
     */
    private void handleQiang1(long qiangUser,boolean isQiang){
        logger.info("第一个人叫");
//        if (isQiang) {
            this.qiangUser = qiangUser;
//        }
        handleQiangNotice();
    }

    /**
     * 处理第二个人叫的情况
     * @param qiangUser
     * @param isQiang
     */
    private void handleQiang2(long qiangUser,boolean isQiang){
        logger.info("第二个人叫");
        this.qiangUser = qiangUser;
        if (isQiang) {
            handleQiangNotice();
        } else {//不抢
            startPlay(jiaoUser);
        }
    }


    private void handleQiangNotice(){
        if (chooseQiangSet.size() == 1) {
            canQiangUser = nextTurnId(qiangUser);
            noticeCanQiang(canQiangUser);
        } else if(chooseQiangSet.size() ==2) {
            startPlay(jiaoUser);
        }
    }

    @Override
    protected void startPlay(long dizhu){
        playStepStart(dizhu);
        //选定地主
        pushChooseDizhu();

        //把底牌加到地主身上
        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(dizhu);
        if (playerCardInfo != null) {
            playerCardInfo.cards.addAll(tableCards);
            //只给地主看
            MsgSender.sendMsg2Player(new ResponseVo("gameService","showTableCard",tableCards),dizhu);
        }

    }

}
