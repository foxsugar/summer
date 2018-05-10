package com.code.server.game.room.kafka;

import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/9.
 */
public interface IfaceMsgSender {

    public void callback(Object object, long userId);

}
