package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.response.IfacePlayerInfoVo;
import com.code.server.game.room.IfacePlayerInfo;
import java.util.ArrayList;
import java.util.List;

public class PlayerZhaGuZi implements IfacePlayerInfo {

    protected long userId;

    protected List<Integer> cards = new ArrayList<>();

    //发话时的操作
    private int op = Operator.MEI_LIANG;

    protected List<Integer> opList = new ArrayList<>();

    //第几个出完牌的
    protected int rank = 0;

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }
    //是否出完牌
    public boolean isOver(){
        return this.rank == 0 ? false : true;
    }

    @Override
    public IfacePlayerInfoVo toVo() {
        return null;
    }

    @Override
    public IfacePlayerInfoVo toVo(long watchUser) {
        return null;
    }
}
