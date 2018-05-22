package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.ErrorCode;

/**
 * Created by dajuejinxian on 2018/5/4.
 */
public interface CardUtilsError {

    public static final int LEFT_CARDS_ERROR = -1;
    public static final int RIGHT_CARDS_ERROR = -2;
    //必须接风
    public static final int MUST_JIE_FENG = -3;
    //操作失败
    public static final int OPERATOR_ERROR = ErrorCode.OPERATOR_ERROR;
}
