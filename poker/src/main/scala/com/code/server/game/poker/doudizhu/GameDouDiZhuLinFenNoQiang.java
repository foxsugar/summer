package com.code.server.game.poker.doudizhu;

import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.kafka.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/26.
 */
public class GameDouDiZhuLinFenNoQiang extends GameDouDiZhuLinFenLongQi {
    private static final Logger logger = LoggerFactory.getLogger(GameDouDiZhuLinFen.class);
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
                handleLiuju();
            } else {
                long nextJiao = nextTurnId(userId);
                canJiaoUser = nextJiao;
                noticeCanJiao(nextJiao);
            }
        } else {//叫了 开始游戏
            jiaoUser = userId;
            dizhu = userId;
            //选定地主
            chooseDizhu();
            startPlay(jiaoUser);
        }

        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isJiao", isJiao);
        MsgSender.sendMsg2Player("gameService","jiaoResponse",rs,users);

        MsgSender.sendMsg2Player("gameService","jiaoDizhu",0,userId);

        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_JDZ(userId,score,!isJiao));
        return 0;
    }
}
