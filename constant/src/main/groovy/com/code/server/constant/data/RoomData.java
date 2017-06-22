package com.code.server.constant.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/20.
 */
public class RoomData {
    public int id;
    public final Map<Integer,Integer> money = new HashMap<>();
    public final Map<Integer, Integer> eachMoney = new HashMap<>();
    public boolean isAddGold;


}
