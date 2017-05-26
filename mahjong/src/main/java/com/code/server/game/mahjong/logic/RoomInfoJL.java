package com.code.server.game.mahjong.logic;


public class RoomInfoJL extends RoomInfo {
    public void drawBack() {

    }

    public void spendMoney() {
        for (int userId : users) {
            User eachUser = userDao.getUser(userId);
            eachUser.setMoney(eachUser.getMoney() - 1);
            userDao.saveUser(eachUser);
        }
    }


    protected boolean isCanJoinCheckMoney(int userId) {

        User user = userDao.getUser(userId);
        if (user.getMoney() < 1) {
            return false;
        }
        return true;
    }
}
