package com.code.server.game.poker.pullmice;
import com.code.server.constant.response.IfaceGameVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePullMiceVo implements IfaceGameVo {

    protected Integer state = PullMiceConstant.STATE_START;

    protected Map<Long, PlayerPullMice> playerCardInfos = new HashMap<Long, PlayerPullMice>();

    protected List<PlayerPullMice> pxList;

    protected Long playerCurrentId;

    protected boolean allFeng;

    protected long diZhu;
}
