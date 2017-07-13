package com.code.server.game.poker.doudizhu;

import com.code.server.constant.game.CardStruct;
import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/11.
 */
public class GameDouDiZhuLinFenLongQi extends GameDouDiZhuLinFen {
    private static final Logger logger = LoggerFactory.getLogger(GameDouDiZhuLinFenLongQi.class);

    protected OperateNode operateNode;


    @Override
    public void init(List<Long> users, long dizhuUser) {
        this.initCardNum = 16;
        super.init(users, dizhuUser);

        //生成操作列表
        operateNode = OperateNode.initOperate(canJiaoUser, users);

    }

    private static OperateNode getOperateByType(OperateNode operateNode, int type) {
        if (operateNode.children != null) {
            for (OperateNode node : operateNode.children) {
                if (node.operateType == type) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    protected void handleBomb(CardStruct cardStruct) {
        computeBomb(cardStruct);
    }

    @Override
    protected void compute(boolean isDizhuWin){

        double subScore = 0;
        int s = isDizhuWin?-1:1;
        //地主
        PlayerCardInfoDouDiZhu playerCardInfoDizhu = playerCardInfos.get(dizhu);

        for(PlayerCardInfoDouDiZhu playerCardInfo : playerCardInfos.values()){
            //不是地主 扣分
            if(dizhu != playerCardInfo.getUserId()){
                double score = multiple * s;
                if (playerCardInfo.isQiang()) {
                    score *=2;
                    //地主抢了 再乘2
                    if(playerCardInfoDizhu.isQiang()){
                        score *= 2;
                    }
                }
                //最大倍数
                if(room.getMultiple()!=-1){
                    int max = 1 << zhaCount;
                    if (max >= score) {
                        score = max;
                    }
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
     * 叫地主
     *
     * @param userId
     * @param isJiao
     * @return
     */
    @Override
    public int jiaoDizhu(long userId, boolean isJiao, int score) {

        logger.info(userId + "  叫地主 " + isJiao);
        if (canJiaoUser != userId) {
            return ErrorCode.CAN_NOT_JIAO_TURN;
        }
        //叫地主列表
        chooseJiaoSet.add(userId);

        //不叫 下个人能叫
        if (!isJiao) {
            bujiaoSet.add(userId);
            if (bujiaoSet.size() >= users.size()) {//三个人都不叫
                handleLiuju();
            } else {//下个选叫
                OperateNode node = getOperateByType(operateNode, OperateNode.BU_JIAO);
                long nid = node.children.get(0).userId;
                this.operateNode = node;
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }
        } else {//叫了 开始抢
            OperateNode node = getOperateByType(operateNode, OperateNode.JIAO);
            jiaoUser = userId;
            dizhu = userId;
            //选定地主
            chooseDizhu();
            //第三个人叫的 直接开始游戏
            if (chooseJiaoSet.size() >= users.size()) {
                startPlay(jiaoUser);
            } else {
                step = STEP_QIANG_DIZHU;
                long nextId = nextTurnId(userId);
                this.canQiangUser = nextId;
                noticeCanQiang(nextId);
            }
            this.operateNode = node;

        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isJiao", isJiao);
        MsgSender.sendMsg2Player("gameService", "jiaoResponse", rs, users);

        MsgSender.sendMsg2Player("gameService", "jiaoDizhu", 0, userId);

        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_JDZ(userId, score, !isJiao));
        return 0;
    }


    /**
     * 抢地主
     *
     * @param userId
     * @param isQiang
     * @return
     */
    @Override
    public int qiangDizhu(long userId, boolean isQiang) {
        logger.info(userId + "  抢地主 " + isQiang);

        if (userId != canQiangUser) {
            return ErrorCode.CAN_NOT_QIANG_TURN;
        }
        this.chooseQiangSet.add(userId);
        int jiaoIndex = chooseJiaoSet.size();

        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setQiang(isQiang);

        OperateNode node;
        if (isQiang) {
            node = getOperateByType(operateNode, OperateNode.QIANG);
        } else {
            node = getOperateByType(operateNode, OperateNode.BU_QIANG);
        }
        this.qiangUser = userId;
        if (node.children.size() == 0) {
            startPlay(dizhu);
        } else {
            long canQiangUserId = node.children.get(0).userId;
            canQiangUser = canQiangUserId;
            noticeCanQiang(canQiangUser);
        }


        this.operateNode = node;

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isQiang", isQiang);
        MsgSender.sendMsg2Player("gameService", "qiangResponse", rs, users);

        MsgSender.sendMsg2Player("gameService", "qiangDizhu", 0, userId);

        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_QDZ(userId, !isQiang));
        return 0;
    }
}
