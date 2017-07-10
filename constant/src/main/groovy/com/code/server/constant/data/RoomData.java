package com.code.server.constant.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class RoomData {
    public int id;
    public final Map<Integer, Integer> money = new HashMap<>();
    public final Map<Integer, Integer> eachMoney = new HashMap<>();
    public boolean isAddGold;


    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("E:\\StaticDataGenerator\\out\\static_data.data");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int off = 0;
        while ((off = in.read(buffer, 0, 1024)) > 0) {
            out.write(buffer, 0, off);
        }
        String byteString = new String(out.toByteArray()).replace("\r\n", "\n");
        byte[] bytes = byteString.getBytes();

        StaticDataProto.DataManager m = StaticDataProto.DataManager.parseFrom(bytes);



    }
}
