package com.code.server.game.mahjong.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018/8/3.
 */
public class RoomInfoGoldH extends RoomInfo {

    private static final Map<Integer, Integer> map = new HashMap<>();

    static {
        map.put(0, 1000);
        map.put(2, 2000);
        map.put(5, 3000);
        map.put(8, 5000);
        map.put(12, 5000);
        map.put(20, 10000);
    }

    @Override
    protected int getOutGold() {
        if (isGoldRoom() && this.goldRoomPermission != GOLD_ROOM_PERMISSION_DEFAULT) {
            return getEnterByMode();
        } else {

            return super.getOutGold();
        }
    }

    @Override
    protected int getEnterGold() {
        if (isGoldRoom() && this.goldRoomPermission != GOLD_ROOM_PERMISSION_DEFAULT) {
            return getEnterByMode();
        } else {
            return super.getEnterGold();
        }
    }


    private int getEnterByMode() {
        int yu = PlayerCardsInfoHS.getYuNum(this.mode);

        return map.get(yu);

    }
}
