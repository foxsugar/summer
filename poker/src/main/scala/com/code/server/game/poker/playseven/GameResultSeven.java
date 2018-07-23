package com.code.server.game.poker.playseven;

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
public class GameResultSeven {

    public List<IfacePlayerInfoVo> playerCardInfos = new ArrayList<>();
    public Map<Long, Double> userScores = new HashMap<>();
    public Long zhuId;
    public long secondBanker;
    public int tableCardFen;
    public int jianFen;
    public int kouDiBeiShu;
    public int kouDiJiaJi;
    protected List<Integer> tableCards;

    public List<IfacePlayerInfoVo> getPlayerCardInfos() {
        return playerCardInfos;
    }

    public void setPlayerCardInfos(List<IfacePlayerInfoVo> playerCardInfos) {
        this.playerCardInfos = playerCardInfos;
    }

    public Map<Long, Double> getUserScores() {
        return userScores;
    }

    public void setUserScores(Map<Long, Double> userScores) {
        this.userScores = userScores;
    }

    public Long getZhuId() {
        return zhuId;
    }

    public void setZhuId(Long zhuId) {
        this.zhuId = zhuId;
    }

    public long getSecondBanker() {
        return secondBanker;
    }

    public void setSecondBanker(long secondBanker) {
        this.secondBanker = secondBanker;
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

    public List<Integer> getTableCards() {
        return tableCards;
    }

    public void setTableCards(List<Integer> tableCards) {
        this.tableCards = tableCards;
    }

    public int getKouDiJiaJi() {
        return kouDiJiaJi;
    }

    public void setKouDiJiaJi(int kouDiJiaJi) {
        this.kouDiJiaJi = kouDiJiaJi;
    }
}
