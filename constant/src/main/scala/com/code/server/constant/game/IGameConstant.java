package com.code.server.constant.game;

import com.code.server.constant.kafka.IkafkaMsgId;

/**
 * Created by sunxianping on 2017/5/11.
 */
public interface IGameConstant extends IkafkaMsgId{
    int ROOM_CREATE_TYPE_CONMMON = 0;
    int ROOM_CREATE_TYPE_GOLD = 1;

    //房间加入状态
    int STATUS_JOIN = 0;//加入
    int STATUS_READY = 1;//准备
    int STATUS_IN_GAME = 2;//在游戏中
    int STATUS_DISSOLUTION = 3;//解散
    int STATUS_AGREE_DISSOLUTION = 4;//同意解散

    long FIVE_MIN = 1000L * 60 * 5;//五分钟
    long ONE_HOUR = 1000L * 60 * 60;



    String GAMETYPE_LINFEN = "0";//临汾斗地主
    String GAMETYPE_QIANAN = "1";//乾安斗地主
    String GAMETYPE_LONGQI = "2";//龙七斗地主
    String GAMETYPE_LONGQI_LINFEN = "3";//龙七 临汾斗地主
    String GAMETYPE_LONGQI_LINFEN_NO_QIANG = "4";//龙七 临汾斗地主 没有踢
    String GAMETYPE_LONGQI_NO_QIANG = "5";//龙七 斗地主 没有踢
    String GAMETYPE_LONGQI_JIXIAN = "210";//吉县 斗地主 没有踢
    String GAMETYPE_LONGQI_JIXIAN_NO_QIANG = "211";//吉县 斗地主 没有踢
    String GAMETYPE_MAOSAN = "250";//毛三有踢
    String GAMETYPE_MAOSAN_NO_QIANG = "251";//毛三 没有踢

    String GAMETYPE_HUAPAI = "350";//毛三 没有踢
    String GAMETYPE_HUAPAI_NO_QIANG = "351";//毛三 没有踢

    //炸金花
    String GAMETYPE_HITGOLDFLOWER = "11";//普通扎金花

    int STEP_JIAO_DIZHU = 1;//步骤 叫地主
    int STEP_QIANG_DIZHU = 2;//步骤 抢地主
    int STEP_PLAY = 3;//步骤 打牌

    //牛牛
    int STEP_RAISE = 4;//步骤 抢地主
    int STEP_COMPARE = 5;//步骤 打牌
    int STEP_MULTIPLE = 6;//步骤 设置倍数

    long SECOND = 1000L;//秒

    //打七
    int STEP_RENSHU = 7;
    int STEP_FANZHU = 8;
    int STEP_GET_CARD_FINISH = 9;//发完牌
    int CAN_CHANGE_TABLE_CARDS = 10;//换底牌
    int STEP_CHUPAI = 11;//出牌
    int STEP_GET_CARD_UNFINISH = 12;//未发完牌
    int CHANGE_TABLE_CARDS_NOW = 13;//正在换底牌
}
