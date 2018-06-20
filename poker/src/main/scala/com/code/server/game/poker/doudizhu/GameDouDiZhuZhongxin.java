package com.code.server.game.poker.doudizhu;

import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.kafka.MsgSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/20.
 */
public class GameDouDiZhuZhongxin extends GameDouDiZhu {

    @Override
    public int qiangDizhu(long userId, boolean isQiang) {
        logger.info(userId + "  抢地主 " + isQiang);

        if (userId != canQiangUser) {
            return ErrorCode.CAN_NOT_QIANG_TURN;
        }
        this.chooseQiangSet.add(userId);
        if (!isQiang) {
            this.buqiangSet.add(userId);
        }

        PlayerCardInfoDouDiZhu playerCardInfo = playerCardInfos.get(userId);
        playerCardInfo.setQiang(isQiang);

        //回执
        Map<String, Object> rs = new HashMap<>();
        rs.put("userId", userId);
        rs.put("isQiang", isQiang);
        MsgSender.sendMsg2Player("gameService", "qiangResponse", rs, users);
        MsgSender.sendMsg2Player("gameService", "qiangDizhu", 0, userId);

        //两个农民都没抢
//        boolean allNoQiang = buqiangSet.size() == 2 && !isQiang;
        //开始游戏
        if (chooseQiangSet.size() == 2) {
            startPlay(dizhu);
        } else {
            canQiangUser = nextTurnId(userId);
            noticeCanQiang(canQiangUser);
        }


        updateLastOperateTime();
        //回放
        replay.getOperate().add(Operate.getOperate_QDZ(userId, !isQiang));
        return 0;
    }
}
