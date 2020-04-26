package com.code.server.constant.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/8/23.
 */
public class PrepareRoomMj extends PrepareRoom {
    public String modeTotal;
    public String mode;
    public boolean yipaoduoxiang;
    public boolean canChi;
    public boolean haveTing;
    public Map<Integer,Long> seatMap = new HashMap<>();
}
