package com.code.server.constant.data;

import com.code.server.constant.game.IGameConstant;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class DataManager implements IGameConstant {

    private static DataManager ourInstance = new DataManager();

    public Map<String, RoomData> roomDatas = new HashMap<>();

    public static StaticDataProto.DataManager data;

    static {
        init();
    }

    public static void initData(String file) throws IOException {
      initByJson(file);
    }

    /**
     * 通过json数据初始化
     * @param file
     * @throws FileNotFoundException
     * @throws InvalidProtocolBufferException
     */
    private static void initByJson(String file) throws FileNotFoundException, InvalidProtocolBufferException {
        String json = readStr(file);
        JsonFormat.Parser p = JsonFormat.parser();
        StaticDataProto.DataManager.Builder builder = StaticDataProto.DataManager.newBuilder();
        p.merge(json, builder);
        data = builder.build();
    }

    /**
     * 通过二进制文件初始化
     * @param file
     * @throws IOException
     */
    private static void initByBin(String file) throws IOException {
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

    /**
     * 读取文件内容
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    private static String readStr(String path) throws FileNotFoundException {
        StringBuffer str = new StringBuffer("");

        File file = new File(path);
        try {
            FileReader fr = new FileReader(file);
            int ch = 0;
            while ((ch = fr.read()) != -1) {
                str.append((char) ch);
            }
            fr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("File reader出错");

        }

        return str.toString();
    }

    public static void main(String[] args) throws IOException {
        initData("E:\\datagenPython3\\out\\static_data.json");
//        initData("E:\\datagen\\out\\static_data.data");

//        System.out.println(data.getPaijiuCardGroupDataCount());
//        System.out.println(data.getPaijiuCardGroupScoreDataCount());
//        System.out.println(data.getPersonMap().size());
//        System.out.println(data.getPersonCount());
//        System.out.println(data.getPaijiuCardGroupScoreDataCount());

//        System.out.println(data.getPaijiuCardGroupDataMap());
//        System.out.println(data.getPaijiuCardGroupScoreDataMap());


    }

    private static void init() {
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
