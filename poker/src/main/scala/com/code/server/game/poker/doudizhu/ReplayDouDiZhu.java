package com.code.server.game.poker.doudizhu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/4.
 */
public class ReplayDouDiZhu {
    private long id;
    private int count;

    private Map<Long, List<Integer>> cards = new HashMap<>();
    private List<Operate> operate = new ArrayList<>();

    private Map<String, Object> roomInfo = new HashMap<>();
}
