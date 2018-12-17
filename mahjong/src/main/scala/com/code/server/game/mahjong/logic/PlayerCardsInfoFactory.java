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
                playerCardsInfo = new PlayerCardsInfoSZ().setIsHasFengShun(true).setHasZiShun(true);
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
                playerCardsInfo = new PlayerCardsInfoSZ_LQ().setIsHasFengShun(true).setHasZiShun(true);
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
            case "50":
            case "52":
                playerCardsInfo = new PlayerCardsInfoHS();
                break;
            case "100":
                playerCardsInfo = new PlayerCardsInfoHM();
                break;
            case "101":
                playerCardsInfo = new PlayerCardsInfoKD_XZ();
                break;
            case "102":
                playerCardsInfo = new PlayerCardsInfoXXPB();
                break;
            case "103":
                playerCardsInfo = new PlayerCardsInfoKD_XY();//小翼扣点
                break;
            case "104":
                playerCardsInfo = new PlayerCardsInfoZhuohaozi();
                break;
            case "105":
                playerCardsInfo = new PlayerCardsInfoTcGangKai();
                break;
            case "106":
                playerCardsInfo = new PlayerCardsInfoHeleKD();
                break;
            case "107":
                playerCardsInfo = new PlayerCardsInfoHeleKDGold();
                break;
            case "108":
                playerCardsInfo = new PlayerCardsInfoGSJ_New();
                break;
            case "109":
                playerCardsInfo = new PlayerCardInfoLuanGuaFeng();
                break;
            case "110":
                playerCardsInfo = new PlayerCardsInfoSSGK();
                break;
            case "111":
                playerCardsInfo = new PlayerCardsInfoHongZhong();
                break;
            case "112":
                playerCardsInfo = new PlayerCardsInfoZhuohaoziKX();
                break;
            case "113":
                playerCardsInfo = new PlayerCardsInfoDINGSHENG();
                break;
            case "114":
                playerCardsInfo = new PlayerCardsInfoFanshi();
                break;
            case "115":
                playerCardsInfo = new PlayerCardsInfoLongxiang();
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
