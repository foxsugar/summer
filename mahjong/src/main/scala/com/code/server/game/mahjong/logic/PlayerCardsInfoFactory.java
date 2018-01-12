package com.code.server.game.mahjong.logic;

import org.apache.log4j.Logger;

/**
 * Created by win7 on 2016/12/5.
 */
public class PlayerCardsInfoFactory {

    private static Logger logger = Logger.getLogger(PlayerCardsInfoFactory.class.getName());

    public static PlayerCardsInfoMj getInstance(RoomInfo room) {
        String model = room.getModeTotal();
        boolean isHasSpecialHu = !"1".equals(room.getMode());
        PlayerCardsInfoMj playerCardsInfo;
        switch (model) {
            case "1":
                playerCardsInfo =  new PlayerCardsInfoKD();
                break;
            case "2":
                playerCardsInfo = new PlayerCardsInfoTDH().setIsHasSpecialHu(isHasSpecialHu);
                break;
            case "3":
                playerCardsInfo = new PlayerCardsInfoSZ().setIsHasFengShun(true);
                break;
            case "4":
                playerCardsInfo = new PlayerCardsInfoGSJ();
                break;
            case "5":
                playerCardsInfo = new PlayerCardsInfoLS();
                break;
            case "6":
                playerCardsInfo = new PlayerCardsInfoDPH();
                break;
            case "10":
                playerCardsInfo = new PlayerCardsInfoQAKT();
                break;
            case "11":
                playerCardsInfo = new PlayerCardsInfoQAMT();
                break;
            case "12":
                playerCardsInfo = new PlayerCardsInfoJC();
                break;
            case "13":
                playerCardsInfo = new PlayerCardsInfoSS();
                break;
            case "14":
                playerCardsInfo = new PlayerCardsInfoJZ();
                break;
            case "124":
                playerCardsInfo = new PlayerCardsInfoJC124();
                break;
            case "15":
                playerCardsInfo = new PlayerCardsInfoSZ_LQ().setIsHasFengShun(true);
                //设置荒庄轮庄
                room.setChangeBankerAfterHuangZhuang(true);
                break;
            case "20":
                playerCardsInfo = new PlayerCardsInfoTJ();
                break;
            case "30":
                playerCardsInfo = new PlayerCardsInfoDonghu();
                break;

            case "31":
                playerCardsInfo = new PlayerCardsInfoNZZ();
                break;
            case "33":
                playerCardsInfo = new PlayerCardsInfoBengbu();
                break;
            case "34":
                playerCardsInfo = new PlayerCardsInfoNiuyezi();
                break;
            case "100":
                playerCardsInfo = new PlayerCardsInfoHM();
                break;



            default:
                logger.error("初始化 playercardsInfo 错误");
                return null;
        }
        playerCardsInfo.setHasGangBlackList(room.isHasGangBlackList);
        playerCardsInfo.setRoomInfo(room);
        return playerCardsInfo;
    }

}
