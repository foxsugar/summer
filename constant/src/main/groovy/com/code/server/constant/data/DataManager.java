package com.code.server.constant.data;

import com.code.server.constant.game.IGameConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class DataManager implements IGameConstant {

    private static DataManager ourInstance = new DataManager();

    public Map<String, RoomData> roomDatas = new HashMap<>();


    static{
        init();
    }

    private static void init(){
        RoomData roomData_longqi = new RoomData();
        RoomData roomData_qianan = new RoomData();
        roomData_qianan.money.put(10, 1);
        roomData_qianan.money.put(20, 2);

        roomData_longqi.money.put(2, 30);
        roomData_longqi.money.put(9, 30);
        roomData_longqi.money.put(18, 45);
        roomData_longqi.money.put(30, 60);

        roomData_longqi.eachMoney.put(2, 10);
        roomData_longqi.eachMoney.put(9, 10);
        roomData_longqi.eachMoney.put(18, 15);
        roomData_longqi.eachMoney.put(30, 20);
        roomData_longqi.isAddGold = true;

        getInstance().roomDatas.put(GAMETYPE_LINFEN, roomData_qianan);
        getInstance().roomDatas.put(GAMETYPE_QIANAN, roomData_qianan);
        getInstance().roomDatas.put(GAMETYPE_LONGQI, roomData_longqi);
        getInstance().roomDatas.put(GAMETYPE_LONGQI_LINFEN, roomData_longqi);
        getInstance().roomDatas.put(GAMETYPE_LONGQI_LINFEN_NO_QIANG, roomData_longqi);
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }
}
