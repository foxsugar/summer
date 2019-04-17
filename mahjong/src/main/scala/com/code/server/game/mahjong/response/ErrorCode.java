package com.code.server.game.mahjong.response;

/**
 * Created by win7 on 2016/12/3.
 */
public interface ErrorCode {
    public static final int CAN_NOT_PENG = 1000;
    public static final int CAN_NOT_GANG = 1001;
    public static final int CAN_NOT_HU = 1002;
    public static final int CAN_NOT_TING = 1003;
    public static final int CAN_NOT_PLAYCARD = 1004;
    public static final int CAN_NOT_GUO = 1005;
    public static final int NOT_TURN = 1006;
    public static final int CAN_NOT_HU_ALREADY = 1007;
    public static final int CAN_NOT_CHI = 1008;
    public static final int CAN_NOT_CHI_TING = 1009;
    public static final int CAN_NOT_PENG_TING = 1010;
    public static final int CAN_PLAYCARD_IS_HU = 1011;
    public static final int CAN_NOT_XUANFENG = 1012;

    public static final int CAN_NOT_LAZHUANG = 1020;
    public static final int CAN_NOT_DINGQUE = 1030;
    public static final int CAN_NOT_HUANPAI = 1040;

    public static final int CARD_ERROR = 2000;
    public static final int USER_ERROR = 2001;
}
