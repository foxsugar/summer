package com.code.server.game.poker.cow;

import com.code.server.constant.response.GameVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2018/6/21.
 */
public class GameWzqVo extends GameVo {
    List<Long> users = new ArrayList<>();

    public List<Long> getUsers() {
        return users;
    }

    public GameWzqVo setUsers(List<Long> users) {
        this.users = users;
        return this;
    }
}
