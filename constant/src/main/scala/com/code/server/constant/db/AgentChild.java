package com.code.server.constant.db;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sunxianping on 2018/6/6.
 */
public class AgentChild {

    private Set<Long> children = new HashSet<>();

    public Set<Long> getChildren() {
        return children;
    }

    public AgentChild setChildren(Set<Long> children) {
        this.children = children;
        return this;
    }
}
