package com.code.server.game.poker.doudizhu;

import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.kafka.MsgSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018-10-15.
 */
public class GameDouDiZhuHuaPaiNoQiang extends GameDouDiZhuLinFenLongQi{


    /**
     * 叫地主
     * @param userId
     * @param isJiao
     * @return
     */
    @Override
    public int jiaoDizhu(long userId, boolean isJiao, int score){

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
