package com.code.server.game.poker.cow;

import com.code.server.constant.response.GameVo;
import com.code.server.game.poker.zhaguzi.WzqNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/6/21.
 */
public class GameWzqVo extends GameVo {
    List<Long> users = new ArrayList<>();

    private Map<String, WzqNode> nodes;

    private long lastMoveUser;

    public List<Long> getUsers() {
        return users;
    }

    public GameWzqVo setUsers(List<Long> users) {
        this.users = users;
        return this;
    }

    public Map<String, WzqNode> getNodes() {
        return nodes;
    }

    public GameWzqVo setNodes(Map<String, WzqNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    public long getLastMoveUser() {
        return lastMoveUser;
    }

    public GameWzqVo setLastMoveUser(long lastMoveUser) {
        this.lastMoveUser = lastMoveUser;
        return this;
    }
}
