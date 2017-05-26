package com.code.server.constant.response;

import com.code.server.cardgame.core.GameManager;
import com.code.server.cardgame.core.Player;
import com.code.server.cardgame.core.Room;
import com.code.server.cardgame.core.doudizhu.RoomDouDiZhu;
import com.code.server.cardgame.core.tiandakeng.RoomTanDaKeng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/15.
 */
public class RoomVo {


    protected String roomId;
    protected int multiple;//倍数
    protected int gameNumber;
    protected long createUser;
    private GameVo game;
    private int curGameNumber;
    protected int createType;
    protected double goldRoomType;

    //填大坑专用
    protected boolean isLastDraw;//是否平局
    protected int drawForLeaveChip;//平局留下筹码
    protected int hasNine;

    protected Map<Long, Integer> userStatus = new HashMap<>();//用户状态
    protected List<UserVo> userList = new ArrayList<>();//用户列表
    protected Map<Long, Double> userScores = new HashMap<>();
    protected int personNumber;

    public RoomVo(){}

    public RoomVo(Room room, Player player){
        this.createType = room.getCreateType();
        this.roomId = room.getRoomId();
        this.multiple = room.getMultiple();
        this.gameNumber = room.getGameNumber();
        this.createUser = room.getCreateUser();
        this.userStatus.putAll(room.getUserStatus());
        this.userScores.putAll(room.getUserScores());
        this.curGameNumber = room.getCurGameNumber();
        this.goldRoomType = room.getGoldRoomType();
        this.isLastDraw = room.isLastDraw();
        this.drawForLeaveChip = room.getDrawForLeaveChip();
        this.personNumber = room.getPersonNumber();
        this.hasNine = room.getHasNine();

        for(long uid : room.getUsers()){
            userList.add(GameManager.getUserVo(room.getUserMap().get(uid)));
        }

        if(room instanceof RoomDouDiZhu){
            this.game= GameDoudizhuVo.getGameVo(room.getGame(),player.getUserId());
        }else if(room instanceof RoomTanDaKeng) {
           if(room.getGame()!=null){
               this.game = GameTianDaKengVo.getGameTianDaKengVo(room.getGame(), player.getUserId());
           }
        }

    }






}
