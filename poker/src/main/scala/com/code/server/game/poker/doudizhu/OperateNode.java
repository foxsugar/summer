package com.code.server.game.poker.doudizhu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/7/11.
 */
public class OperateNode {
    public static final int NONE = 0;
    public static final int JIAO = 1;
    public static final int BU_JIAO = 2;
    public static final int QIANG = 3;
    public static final int BU_QIANG = 4;
    public static final int PLAY = 5;
    public static final int RE_OPEN = 6;

    long operateType;
    long userId;
    int id;
    List<OperateNode> children = new ArrayList<>();
    OperateNode parentNode;


    public OperateNode() {
    }

    public OperateNode(int id, long userId,long operateType) {
        this.id = id;
        this.userId = userId;
        this.operateType = operateType;
    }

    public static void addNode2Parent(OperateNode child, OperateNode parent){
        child.parentNode = parent;
        parent.children.add(child);
    }

    public void addChild(OperateNode child) {
        this.children.add(child);
        child.parentNode = this;
    }

    private static long getUserId(int index) {
        return 0;
    }

    public static long nextTurnId(long curId,List<Long> users) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }
    static OperateNode initOperate(long jiaoUser,List<Long> users){
        Map<Integer, OperateNode> nodeMap = new HashMap<>();
        long user1 = jiaoUser;
        long user2 = nextTurnId(jiaoUser,users);
        long user3 = nextTurnId(user2, users);

        //root
        add2Map(nodeMap,0x00, 0, NONE);

        add2Map(nodeMap,0x10, user1, JIAO);
        add2Map(nodeMap,0x11, user1, BU_JIAO);


        add2Map(nodeMap,0x20, user2, QIANG);
        add2Map(nodeMap,0x21, user2, BU_QIANG);
        add2Map(nodeMap,0x22, user2, JIAO);
        add2Map(nodeMap,0x23, user2, BU_JIAO);

        add2Map(nodeMap,0x30, user3, QIANG);
        add2Map(nodeMap,0x31, user3, BU_QIANG);
        add2Map(nodeMap,0x32, user3, QIANG);
        add2Map(nodeMap,0x33, user3, BU_QIANG);
        add2Map(nodeMap,0x34, user3, QIANG);
        add2Map(nodeMap,0x35, user3, BU_QIANG);
        add2Map(nodeMap,0x36, user3, JIAO);
        add2Map(nodeMap,0x37, user3, BU_JIAO);

        add2Map(nodeMap,0x40, user1, QIANG);
        add2Map(nodeMap,0x41, user1, BU_QIANG);
        add2Map(nodeMap,0x42, user1, QIANG);
        add2Map(nodeMap,0x43, user1, BU_QIANG);
        add2Map(nodeMap,0x44, user2, QIANG);
        add2Map(nodeMap,0x45, user2, BU_QIANG);
        add2Map(nodeMap,0x46, user1, QIANG);
        add2Map(nodeMap,0x47, user1, BU_QIANG);

        add2Map(nodeMap,0x50, user1, QIANG);
        add2Map(nodeMap,0x51, user1, BU_QIANG);
        add2Map(nodeMap,0x52, user1, QIANG);
        add2Map(nodeMap,0x53, user1, BU_QIANG);
        add2Map(nodeMap,0x54, user2, QIANG);
        add2Map(nodeMap,0x55, user2, BU_QIANG);
        add2Map(nodeMap,0x56, user2, QIANG);
        add2Map(nodeMap,0x57, user2, BU_QIANG);


        //整理关系

        relation(nodeMap,0x00,0x10);
        relation(nodeMap,0x00,0x11);

        relation(nodeMap,0x10,0x20);
        relation(nodeMap,0x10,0x21);

        relation(nodeMap,0x11,0x22);
        relation(nodeMap,0x11,0x23);

        relation(nodeMap,0x20,0x30);
        relation(nodeMap,0x20,0x31);

        relation(nodeMap,0x21,0x32);
        relation(nodeMap,0x21,0x33);

        relation(nodeMap,0x22,0x34);
        relation(nodeMap,0x22,0x35);

        relation(nodeMap,0x23,0x36);
        relation(nodeMap,0x23,0x37);

        relation(nodeMap,0x30,0x40);
        relation(nodeMap,0x30,0x41);

        relation(nodeMap,0x31,0x42);
        relation(nodeMap,0x31,0x43);

        relation(nodeMap,0x32,0x44);
        relation(nodeMap,0x32,0x45);

        relation(nodeMap,0x34,0x46);
        relation(nodeMap,0x34,0x47);

        relation(nodeMap,0x44,0x50);
        relation(nodeMap,0x44,0x51);

        relation(nodeMap,0x45,0x52);
        relation(nodeMap,0x45,0x53);

        relation(nodeMap,0x46,0x54);
        relation(nodeMap,0x46,0x55);

        relation(nodeMap,0x47,0x56);
        relation(nodeMap,0x47,0x57);

        return nodeMap.get(0x00);


    }

    private static void relation(Map<Integer,OperateNode> map,int parent,int child){
        OperateNode parentNode = map.get(parent);
        OperateNode childNode = map.get(child);
        addNode2Parent(childNode,parentNode);
    }

    private static void add2Map(Map<Integer, OperateNode> map, int id, long userId, int type) {
        OperateNode operateNode = new OperateNode(id, userId, type);
        map.put(operateNode.id, operateNode);
    }
    public static void main(String[] args) {
        OperateNode root = new OperateNode();





    }
}
