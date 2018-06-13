package com.code.server.game.poker.xuanqiqi;

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
public class PlayerCardInfoXuanQiQiVo implements IfacePlayerInfoVo {

    public long userId;
    public Integer randomCard;//手上的牌
    public List<Integer> handCards = new ArrayList<>();//手上的牌
    public List<Integer> playCards = new ArrayList<>();//当前出的牌
    public List<Integer> winCards = new ArrayList<>();//罗上的牌
    public Map<Integer,Boolean> cardsType= new HashMap<>();//罗上牌明或扣的状态, true明 ，false扣
    protected double score;
    protected double allScore;

    public Integer safeNum = 0;//有效罗数

    //状态 got推送客户端一次，catch表示已经扣过，可以得到分数
    protected boolean gotThree = false;
    protected boolean gotFive = false;
    protected boolean gotSix = false;

    protected boolean catchThree = false;
    protected boolean catchFive = false;
    protected boolean catchSix = false;

    //游戏状态
    //1表示显示
    protected String canSetMultiple;//庄可以加倍
    protected String canChoose;//可选择操作
    protected String canSendCard;//可出牌

    protected String canXuan;//可选
    protected String canKou;//可扣
    protected String canGuo;//可过
    public int curRoundNumber;//当前轮数

    protected boolean display;

    protected int cardNum;

    public PlayerCardInfoXuanQiQiVo(){

    }
}
