package com.code.server.game.poker.playseven;

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
public class PlayerCardInfoPlaySeven implements IfacePlayerInfo {

    public long userId;
    public List<Integer> handCards = new ArrayList<>();//手上的牌
    public List<Integer> playCards = new ArrayList<>();//当前出的牌


    public String shouQi = "0";//首七     0默认，1提示可操作，2已操作，3过期
    public String danLiang = "0";//单亮
    public String shuangLiang = "0";//双亮
    public String fanZhu = "0";//反主
    public String renShu = "0";//认输



    @Override
    public IfacePlayerInfoVo toVo() {
        PlayerCardInfoPlaySevenVo vo = new PlayerCardInfoPlaySevenVo();
        return vo;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        PlayerCardInfoPlaySevenVo vo = new PlayerCardInfoPlaySevenVo();
        return vo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
    }

    public List<Integer> getPlayCards() {
        return playCards;
    }

    public void setPlayCards(List<Integer> playCards) {
        this.playCards = playCards;
    }

    public String getShouQi() {
        return shouQi;
    }

    public void setShouQi(String shouQi) {
        this.shouQi = shouQi;
    }

    public String getDanLiang() {
        return danLiang;
    }

    public void setDanLiang(String danLiang) {
        this.danLiang = danLiang;
    }

    public String getShuangLiang() {
        return shuangLiang;
    }

    public void setShuangLiang(String shuangLiang) {
        this.shuangLiang = shuangLiang;
    }

    public String getFanZhu() {
        return fanZhu;
    }

    public void setFanZhu(String fanZhu) {
        this.fanZhu = fanZhu;
    }

    public String getRenShu() {
        return renShu;
    }

    public void setRenShu(String renShu) {
        this.renShu = renShu;
    }
}
