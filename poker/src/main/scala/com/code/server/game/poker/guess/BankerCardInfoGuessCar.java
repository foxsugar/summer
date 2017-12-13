package com.code.server.game.poker.guess;

import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.constant.response.PlayerCardInfoHitGoldFlowerVo;
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
public class BankerCardInfoGuessCar implements IfacePlayerInfo {

    public long userId;
    protected int choose;
    protected double score;

    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.score = this.score;
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        return vo;
    }

    public IfacePlayerInfoVo toVoHaveHandcards() {
        PlayerCardInfoHitGoldFlowerVo vo = new PlayerCardInfoHitGoldFlowerVo();
        vo.userId = this.userId;
        vo.score = this.score;
        return vo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getChoose() {
        return choose;
    }

    public void setChoose(int choose) {
        this.choose = choose;
    }
}
