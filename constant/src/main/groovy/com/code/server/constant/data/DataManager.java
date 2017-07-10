package com.code.server.constant.data;

import com.code.server.constant.game.IGameConstant;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class DataManager implements IGameConstant {

    private static DataManager ourInstance = new DataManager();

    public Map<String, RoomData> roomDatas = new HashMap<>();

    public  static StaticDataProto.DataManager data;

    static{
        init();
    }

    private static void initData(String file) throws IOException {
        InputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int off = 0;
        while ((off = in.read(buffer, 0, 1024)) > 0) {
            out.write(buffer, 0, off);
        }
        String byteString = new String(out.toByteArray()).replace("\r\n", "\n");
        byte[] bytes = byteString.getBytes();

        data = StaticDataProto.DataManager.parseFrom(bytes);


    }

    public static void main(String[] args) throws IOException {
        initData("E:\\StaticDataGenerator\\out\\static_data.data");


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
