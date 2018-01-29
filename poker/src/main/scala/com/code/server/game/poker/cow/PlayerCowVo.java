package com.code.server.game.poker.cow;

import com.code.server.constant.response.IfacePlayerInfoVo;

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
public class PlayerCowVo implements IfacePlayerInfoVo {
    public long userId;
    public List<Integer> handcards = new ArrayList<>();//手上的牌
    public double score;
    public double finalScore;
    public CowPlayer player;

    public List<Integer> sanzhangshi = new ArrayList<>();//手上的牌

    //1表示显示
    protected int raise;//加注
    protected int kill;//比牌

    public PlayerCowVo(){

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

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public CowPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CowPlayer player) {
        this.player = player;
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

    public List<Integer> getSanzhangshi() {
        return sanzhangshi;
    }

    public void setSanzhangshi(List<Integer> sanzhangshi) {
        this.sanzhangshi = sanzhangshi;
    }
}
