package com.code.server.game.mahjong.response;

/**
 * Created by win7 on 2016/12/2.
 */
public interface ResponseType {
    String SERVICE_TYPE_GAMELOGIC = "GameLogicService";


    String METHOD_TYPE_GET_HAND_CARDS = "getHandCards";
    String METHOD_TYPE_GET_CARD = "getCard";
    String METHOD_TYPE_PLAY_CARD = "disCard";
    String METHOD_TYPE_OPERATE = "operate";
    String METHOD_TYPE_RESULT = "result";
    String METHOD_TYPE_OTHER_OPERATE = "otherOperate";
    String METHOD_TYPE_EXCHANGE = "exchangeResp";
    String METHOD_TYPE_CHANGE_BAO = "changeBao";
    String METHOD_TYPE_ALL_PASS = "allPass";


    String METHOD_TYPE_NOTICE_HUN = "noticeHun";



}
