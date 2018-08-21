package com.code.server.game.poker.pullmice;

import org.junit.Test;

public class PlayerPullMiceTest {

    PlayerPullMiceTest self = this;

    private String name;
    private String age;
    private long money;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    @Test
    public void toVo() {

        self.testSelf();
        self.name = "a";
        self.age = "19";
        self.money = 999999999;

        self.getAge();
        self.getMoney();
        self.getName();
    }

    public void testSelf(){
        System.out.println("我就想用self");
    }

}