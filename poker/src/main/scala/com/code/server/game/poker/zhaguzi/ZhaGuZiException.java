package com.code.server.game.poker.zhaguzi;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
public class ZhaGuZiException extends RuntimeException{

    private Integer code;

    public ZhaGuZiException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
