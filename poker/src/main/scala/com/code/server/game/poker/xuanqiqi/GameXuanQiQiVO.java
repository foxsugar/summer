package com.code.server.game.poker.xuanqiqi;

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
public class GameXuanQiQiVO extends GameVo {

    protected int bankerMultiple = 1;
    protected long chuPaiId;
    protected long operatId;
    protected List<Integer> cards = new ArrayList<>();//牌
    protected List<Integer> randamCards = new ArrayList<>();//搬牌
    protected Map<Long,Integer> xuanOrGuo = new HashMap<>();
    protected Map<Long,Integer> ifChuPai = new HashMap<>();
    protected Map<Long,Integer> compareCard = new HashMap<>();
    protected List<XuanParam> xuanList = new ArrayList<>();
    protected RoomXuanQiQi room;
    public Map<Long, IfacePlayerInfoVo> playerCardInfos = new HashMap<>();
    public long bankerId = 0;
}
