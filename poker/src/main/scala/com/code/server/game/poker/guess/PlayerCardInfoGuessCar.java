package com.code.server.game.poker.guess;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

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
public class PlayerCardInfoGuessCar implements IfacePlayerInfo {

    public long userId;
    protected double redScore;
    protected double greenScore;
    protected double finalScore;//玩家总分数

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoGuessCarVo vo = new PlayerCardInfoGuessCarVo();
        vo.userId = this.userId;
        vo.redScore = this.redScore;
        vo.greenScore = this.greenScore;
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long userId) {
        PlayerCardInfoGuessCarVo vo = new PlayerCardInfoGuessCarVo();
        vo.userId = this.userId;
        vo.redScore = this.redScore;
        vo.greenScore = this.greenScore;
        return vo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public double getRedScore() {
        return redScore;
    }

    public void setRedScore(double redScore) {
        this.redScore = redScore;
    }

    public double getGreenScore() {
        return greenScore;
    }

    public void setGreenScore(double greenScore) {
        this.greenScore = greenScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
}
