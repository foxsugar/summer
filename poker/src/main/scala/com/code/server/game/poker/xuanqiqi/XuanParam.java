package com.code.server.game.poker.xuanqiqi;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：记录宣时候的相关参数
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class XuanParam {

    public long xuan_UserId;//宣的人的Id
    public long xuaned_UserId;//被宣的人
    public Integer xuan_LuoNum;//宣的人的罗数
    public Integer xuaned_LuoNum;//被宣的人的罗数
    public boolean gotLuo;//宣的人是否达到罗数


    public long getXuan_UserId() {
        return xuan_UserId;
    }

    public void setXuan_UserId(long xuan_UserId) {
        this.xuan_UserId = xuan_UserId;
    }

    public long getXuaned_UserId() {
        return xuaned_UserId;
    }

    public void setXuaned_UserId(long xuaned_UserId) {
        this.xuaned_UserId = xuaned_UserId;
    }

    public Integer getXuan_LuoNum() {
        return xuan_LuoNum;
    }

    public void setXuan_LuoNum(Integer xuan_LuoNum) {
        this.xuan_LuoNum = xuan_LuoNum;
    }

    public Integer getXuaned_LuoNum() {
        return xuaned_LuoNum;
    }

    public void setXuaned_LuoNum(Integer xuaned_LuoNum) {
        this.xuaned_LuoNum = xuaned_LuoNum;
    }

    public boolean isGotLuo() {
        return gotLuo;
    }

    public void setGotLuo(boolean gotLuo) {
        this.gotLuo = gotLuo;
    }
}
