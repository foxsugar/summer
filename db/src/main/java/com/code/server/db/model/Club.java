package com.code.server.db.model;

import java.util.List;

/**
 * Created by sunxianping on 2018/1/12.
 */
public class Club {
    private long id;
    private String clubId;
    private String name;
    private long president;//会长
    private String presidentWx;//会长id
    private String area;
    private String desc;
    private String image;
    private List<Long> member;
    private double money;
}
