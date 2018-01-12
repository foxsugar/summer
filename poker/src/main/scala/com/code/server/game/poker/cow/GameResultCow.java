package com.code.server.game.poker.cow;

import com.code.server.constant.response.IfacePlayerInfoVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class GameResultCow {

    public List<IfacePlayerInfoVo> playerCardInfos = new ArrayList<>();
    public Map<Long, Double> userScores = new HashMap<>();
    public List<Long> winnerList = new ArrayList<>();
    public Long bankerId;

    public List<IfacePlayerInfoVo> getPlayerCardInfos() {
            return playerCardInfos;
        }

    public GameResultCow setPlayerCardInfos(List<IfacePlayerInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
        return this;
    }


    public Map<Long, Double> getUserScores() {
            return userScores;
        }

    public GameResultCow setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
        return this;
    }

    public List<Long> getWinnerList() {
            return winnerList;
    }

    public void setWinnerList(List<Long> winnerList) {
        this.winnerList = winnerList;
    }

    public Long getBankerId() {
        return bankerId;
    }

    public void setBankerId(Long bankerId) {
        this.bankerId = bankerId;
    }
}

