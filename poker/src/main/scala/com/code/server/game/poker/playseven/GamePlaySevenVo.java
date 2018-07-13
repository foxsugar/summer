package com.code.server.game.poker.playseven;

import com.code.server.constant.response.GameVo;
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
public class GamePlaySevenVo extends GameVo {
    public int cardSize;
    public boolean shouQiDouble = false;//首七
    public boolean shuangLiangDouble = false;//双亮
    public boolean seeTableCard = false;
    public boolean fanzhu = false;

    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> tableCards = new ArrayList<>();//底牌

    protected long chuPaiId;
    protected long zhuId;//第一个叫主的
    public int tableCardFen = 0;
    public int jianFen = 0;
    public int kouDiBeiShu = 1;//扣底翻倍的倍数
    public Integer liangCard;//亮的牌
    public long secondBanker;//另一个队友Id
    public int huaSe;

    //出过 1，未出 0
    protected Map<Long, Integer> ifChuPai = new HashMap<>();

    //赢为1，输为0，未比过为-1
    protected Map<Long, Integer> compareCard = new HashMap<>();

    protected Map<Long, Integer> userGetFen = new HashMap<>();//玩家的分

    protected RoomPlaySeven room;

    public Map<Long, IfacePlayerInfoVo> playerCardInfos = new HashMap<>();

}
