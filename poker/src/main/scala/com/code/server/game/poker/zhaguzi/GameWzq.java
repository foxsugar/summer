package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.cow.GameWzqVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class GameWzq extends Game {


    private Room roomWzq;

    private Map<String, WzqNode> nodes = new HashMap<>();



    private long lastMoveUser;


    public int admitDefeat(long userId) {

        double gold = RedisManager.getUserRedisService().getUserGold(userId);
        if (gold < this.roomWzq.getMultiple()) {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }


        List<Long> tempUser = new ArrayList<>();
        tempUser.addAll(this.users);
        tempUser.remove((Long) userId);
        long other = tempUser.get(0);
        double gold1 = RedisManager.getUserRedisService().addUserGold(userId, -this.roomWzq.getMultiple());

        double gold2 = RedisManager.getUserRedisService().addUserGold(other, this.roomWzq.getMultiple());

        Map<Long, Object> golds = new HashMap<>();
        golds.put(userId, gold1);
        golds.put(other, gold2);

//        golds = computeScore(userId, -this.roomWzq.getMultiple());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "admitDefeatResp", golds), users);

        MsgSender.sendMsg2Player(new ResponseVo("gameService", "admitDefeat", 0), userId);


        sendResult(other,golds);

        this.roomWzq.clearReadyStatus(true);

        sendFinalResult();

        return 0;
    }

    private Map<Long,Object> computeScore(long userId, int score){
        List<Long> tempUser = new ArrayList<>();
        tempUser.addAll(this.users);
        tempUser.remove(userId);
        long other = tempUser.get(0);
        double gold1 = RedisManager.getUserRedisService().addUserGold(userId, score);

        double gold2 = RedisManager.getUserRedisService().addUserGold(other, -score);

        Map<Long, Object> golds = new HashMap<>();
        golds.put(userId, gold1);
        golds.put(other, gold2);
        return golds;
    }


    public int setScore(long userId, int score) {
        if (roomWzq.getMultiple() != 0 || score < 0) {
            return ErrorCode.SET_SCORE_ERROR;
        }
        //去掉设置分的限制
//        for(long user : users){
//            double g = RedisManager.getUserRedisService().getUserGold(user);
//            if (score > g) {
//                return ErrorCode.SET_SCORE_ERROR;
//            }
//        }
        this.roomWzq.setMultiple(score);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("score", score);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "setScoreResp", result), users);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "setScore", 0), userId);
        return 0;
    }


    protected void sendFinalResult() {
        //所有牌局都结束
        if (this.roomWzq.isRoomOver()) {
            MsgSender.sendMsg2Player("gameService", "gameWzqFinalResult", "finalResult", users);
            RoomManager.removeRoom(this.roomWzq.getRoomId());
        }
    }

    protected void sendResult(long winnerId, Map<Long,Object> golds) {
        Map<String, Object> result = new HashMap<>();
        result.put("winner", winnerId);
        int score = this.roomWzq.getMultiple();
        if (winnerId == -1) {
            score = 0;
        }
        result.put("score", score);
        result.put("golds", golds);
        MsgSender.sendMsg2Player("gameService", "gameWzqResult", result, users);
    }

    public int move(long userId, int x, int y) {

        if (userId == lastMoveUser) {
            return ErrorCode.CAN_NOT_MOVE;
        }
        if (!isCanMove(x, y)) {
            return ErrorCode.CAN_NOT_MOVE;
        }
        WzqNode wzqNode = new WzqNode();
        wzqNode.x = x;
        wzqNode.y = y;
        wzqNode.userId = userId;
        nodes.put(getNodeKey(x, y), wzqNode);

        lastMoveUser = userId;

        Map<String, Object> result = new HashMap<>();
        result.put("x", x);
        result.put("y", y);
        result.put("userId", userId);
        MsgSender.sendMsg2Player("gameService", "moveResp", result, users);
        MsgSender.sendMsg2Player("gameService", "move", 0, userId);


        //是否有人赢
// todo 下面是输赢得判断
//        long winnerId = 0;
//        if (isWin(wzqNode, userId)) {
//            winnerId = userId;
//        }else{
//            //如果棋牌全部占满 就是平局
//            if (nodes.size() >=289) {
//                winnerId = -1;
//            }
//        }
//        if (winnerId != 0) {
//            Map<Long,Object> golds;
//            if (winnerId == -1) {
//                golds = computeScore(userId, 0);
//                sendResult(-1,golds);
//            }else{
//                golds = computeScore(userId, this.roomWzq.getMultiple());
//                sendResult(winnerId,golds);
//            }
//            sendFinalResult();
//        }

        return 0;
    }



    private boolean isWin(WzqNode wzqNode, long userId) {

        List<WzqNode> list1 = getFiveNodeByDirection(wzqNode, 1, 0);
        if(isOk(list1,userId)) return true;

        List<WzqNode> list2 = getFiveNodeByDirection(wzqNode, 0, 1);
        if(isOk(list2,userId)) return true;

        List<WzqNode> list3 = getFiveNodeByDirection(wzqNode, 1, 1);
        if(isOk(list3,userId)) return true;

        List<WzqNode> list4 = getFiveNodeByDirection(wzqNode, -1, -1);
        if(isOk(list4,userId)) return true;

        List<WzqNode> list5 = getFiveNodeByDirection(wzqNode, -1, 0);
        if(isOk(list5,userId)) return true;

        List<WzqNode> list6 = getFiveNodeByDirection(wzqNode, 0, -1);
        if(isOk(list6,userId)) return true;

        List<WzqNode> list7 = getFiveNodeByDirection(wzqNode, 1, -1);
        if(isOk(list7,userId)) return true;

        List<WzqNode> list8 = getFiveNodeByDirection(wzqNode, -1, 1);
        if(isOk(list8,userId)) return true;


        return false;
    }


    /**
     * 获得方向上的五个子
     * @param wzqNode
     * @param xAdd
     * @param yAdd
     * @return
     */
    private List<WzqNode> getFiveNodeByDirection(WzqNode wzqNode, int xAdd, int yAdd) {
        List<WzqNode> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int newX = wzqNode.x + i * xAdd;
            int newY = wzqNode.y + i * yAdd;

            String nodeStr = newX + "_" + newY;

            WzqNode node = nodes.get(nodeStr);
            list.add(node);
        }

        return list;
    }


    private boolean isOk(List<WzqNode> list,long userId) {
        for (WzqNode wzqNode : list) {
            if (wzqNode == null) {
                return false;
            }
            if (wzqNode.userId != userId) {
                return false;
            }
        }
        return true;
    }




    private String getNodeKey(int x, int y) {
        return x + "_" + y;
    }

    private boolean isCanMove(int x, int y) {
        if (x < 0 || x > 16) {
            return false;
        }
        if (y < 0 || y > 16) {
            return false;
        }
        for (WzqNode wzqNode : nodes.values()) {
            if (wzqNode.x == x && wzqNode.y == y) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void startGame(List<Long> users, Room room) {
        this.roomWzq = room;
        this.users.addAll(room.getUsers());
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameWzqBegin", "ok"), this.getUsers());
    }

    @Override
    public IfaceGameVo toVo() {
        GameWzqVo gameWzqVo = new GameWzqVo();
        gameWzqVo.getUsers().addAll(this.users);
        gameWzqVo.setLastMoveUser(this.lastMoveUser);
        gameWzqVo.setNodes(this.nodes);
        return gameWzqVo;
    }

    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameWzqVo gameWzqVo = new GameWzqVo();
        gameWzqVo.getUsers().addAll(this.users);
        gameWzqVo.setLastMoveUser(this.lastMoveUser);
        gameWzqVo.setNodes(this.nodes);
        return gameWzqVo;
    }
}
