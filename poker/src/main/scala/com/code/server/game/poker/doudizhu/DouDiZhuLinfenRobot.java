package com.code.server.game.poker.doudizhu;



import com.code.server.constant.game.CardStruct;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Room;
import scala.tools.nsc.doc.html.page.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/5/16.
 */
public class DouDiZhuLinfenRobot implements IDouDiZhuRobot,IGameConstant {
    @Override
    public void execute() {

    }

    @Override
    public void jiaoDizhu(GameDouDiZhu game) {

    }

    @Override
    public void qiangDizhu(GameDouDiZhu game) {

    }

    @Override
    public void play(GameDouDiZhu game) {

    }

    @Override
    public void pass(GameDouDiZhu game) {

    }


//    @Override
//    public void execute() {
//        GoldRoomPool.getInstance().getFullRoom().values().forEach(list->list.forEach(this::doExecute));
//    }
//
//    private void doExecute(Room room){
//        if (room == null || room.getGame() == null) {
//            return;
//        }
//        if (room.getGame() instanceof GameDouDiZhu) {
//            GameDouDiZhu game = (GameDouDiZhu) room.getGame();
//            long now = System.currentTimeMillis();
//            //执行
//            if(now > game.lastOperateTime + SECOND * 35){
//                switch (game.step) {
//                    case STEP_JIAO_DIZHU:
//                        jiaoDizhu(game);
//                        break;
//                    case STEP_QIANG_DIZHU:
//                        qiangDizhu(game);
//                        break;
//                    case STEP_PLAY:
//                        play(game);
//                        break;
//                }
//            }
//        }
//
//    }
//    //{"service":"gameService","method":"jiaoDizhu","params":{"isJiao":true}}
//    @Override
//    public void jiaoDizhu(GameDouDiZhu game) {
//        Map<String, Boolean> jiao = new HashMap<>();
//        jiao.put("isJiao", false);
//        ResponseVo vo = new ResponseVo("gameService","jiaoDizhu",jiao);
//        MessageHolder messageHolder = new MessageHolder();
//        messageHolder.msgType = MessageHolder.MSG_TYPE_INNER;
//        messageHolder.userId = game.canJiaoUser;
//        JSONObject jsonObject = JSONObject.fromObject(vo);
//        messageHolder.message = jsonObject;
//        GameProcessor.getInstance().messageQueue.add(messageHolder);
//    }
//
//    //{"service":"gameService","method":"qiangDizhu","params":{"isQiang":false}}
//    @Override
//    public void qiangDizhu(GameDouDiZhu game) {
//        Map<String, Boolean> qiang = new HashMap<>();
//        qiang.put("isQiang", false);
//        ResponseVo vo = new ResponseVo("gameService","qiangDizhu",qiang);
//        MessageHolder messageHolder = new MessageHolder();
//        messageHolder.msgType = MessageHolder.MSG_TYPE_INNER;
//        messageHolder.userId = game.canQiangUser;
//        JSONObject jsonObject = JSONObject.fromObject(vo);
//        messageHolder.message = jsonObject;
//        GameProcessor.getInstance().messageQueue.add(messageHolder);
//    }
//
//    //{"service":"gameService","method":"play","params":{"cards":{"userId":"25","cards":[16],"type":1,"dan":[16]}}}
//    @Override
//    public void play(GameDouDiZhu game) {
//        PlayerCardInfoDouDiZhu playerInfo = game.getPlayerCardInfos().get(game.playTurn);
//        if(playerInfo.cards.size() ==0){
//            return;
//        }
//        if (game.lastCardStruct == null || game.playTurn == game.lastCardStruct.getUserId()) {
//
//            CardStruct cardStruct = new CardStruct();
//            cardStruct.type = 1;
//            cardStruct.dan = game.getPlayerCardInfos().get(game.getPlayTurn()).MinimumCards();
//            cardStruct.cards = game.getPlayerCardInfos().get(game.getPlayTurn()).MinimumCards();
//            cardStruct.setUserId(game.getPlayTurn());
//
//            Map<String, Object> cs = new HashMap<>();
//            cs.put("cards", cardStruct);
//
//            System.out.println("发送 = " + cardStruct);
//
//            ResponseVo vo = new ResponseVo("gameService", "play", cs);
//            MessageHolder messageHolder = new MessageHolder();
//            messageHolder.msgType = MessageHolder.MSG_TYPE_INNER;
//            messageHolder.userId = game.getPlayTurn();
//            JSONObject jsonObject = JSONObject.fromObject(vo);
//            messageHolder.message = jsonObject;
//            GameProcessor.getInstance().messageQueue.add(messageHolder);
//        } else {
//            pass(game);
//        }
//    }
//
//    // {"service":"gameService","method":"pass","params":{"userId":"23"}}
//    @Override
//    public void pass(GameDouDiZhu game) {
//        Map<String, Long> pass = new HashMap<>();
//        pass.put("userId", game.getPlayTurn());
//        ResponseVo vo = new ResponseVo("gameService","pass",pass);
//        MessageHolder messageHolder = new MessageHolder();
//        messageHolder.msgType = MessageHolder.MSG_TYPE_INNER;
//        messageHolder.userId = game.getPlayTurn();
//        JSONObject jsonObject = JSONObject.fromObject(vo);
//        messageHolder.message = jsonObject;
//        GameProcessor.getInstance().messageQueue.add(messageHolder);
//    }

}
