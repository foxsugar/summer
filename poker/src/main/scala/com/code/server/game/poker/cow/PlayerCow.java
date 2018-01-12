package com.code.server.game.poker.cow;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;

import java.util.ArrayList;
import java.util.List;

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
public class PlayerCow  implements IfacePlayerInfo {

    public long userId;
    public List<Integer> handcards = new ArrayList<>();//手上的牌
    public double score;
    public double finalScore;
    public CowPlayer player;

    //1表示显示
    protected int raise;//加注
    protected int kill;//比牌

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCowVo vo = new PlayerCowVo();
        vo.userId = this.userId;
        vo.handcards = this.handcards;
        vo.score = this.score;
        vo.finalScore = this.finalScore;
        vo.raise = this.getRaise();
        vo.kill = this.getKill();
        vo.player = this.player;
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCowVo vo = new PlayerCowVo();

        return vo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getHandcards() {
        return handcards;
    }

    public void setHandcards(List<Integer> handcards) {
        this.handcards = handcards;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRaise() {
        return raise;
    }

    public void setRaise(int raise) {
        this.raise = raise;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public CowPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CowPlayer player) {
        this.player = player;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
}