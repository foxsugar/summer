package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.CardUtil;
import com.code.server.game.mahjong.util.HuCardType;
import com.code.server.game.mahjong.util.HuLimit;
import com.code.server.game.mahjong.util.HuUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 小翼扣点 on 2018/5/9
 */
public class PlayerCardsInfoKD_XY extends PlayerCardsInfoKD {

    //创建房间mode：显庄 扣听 包杠 （1是0否）


    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
        super.gangCompute(room,gameInfo,isMing,diangangUser,card);
        String ifBaoGang = room.getMode();
        if(!ifBaoGang.isEmpty() && (ifBaoGang.endsWith("1"))){
            gangComputeBG( room,  gameInfo,  isMing,  diangangUser,  card);
        }else {
            gangComputePT( room,  gameInfo,  isMing,  diangangUser,  card);
        }
    }

    public void computeALLGang(){

        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //暗杠计算
            for (long i : scores.keySet()) {
                scores.put(i, scores.get(i) - playerCardsInfo.getAnGangType().size()*2);
            }
            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+playerCardsInfo.getAnGangType().size()*2*4);
            //明杠计算
            for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                for (long i : scores.keySet()) {
                    scores.put(i, scores.get(i) - 1);
                }
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + 4);
            }
        }
        for (long i : scores.keySet()) {
            gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
            roomInfo.setUserSocre(i, scores.get(i));
        }
    }


    /**
     * 点杠包杠
     * @param room
     * @param gameInfo
     * @param isMing
     * @param diangangUser
     * @param card
     */
    private void gangComputePT(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){

    }

    /**
     * 点炮包杠
     * @param room
     * @param gameInfo
     * @param isMing
     * @param diangangUser
     * @param card
     */
    private void gangComputeBG(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){

    }





    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);
        String ifXianZhuang = room.getMode();
        if(!ifXianZhuang.isEmpty() && (ifXianZhuang.startsWith("1"))){
            huComputeXZ( room,  gameInfo,  isZimo,  dianpaoUser,  card);
        }else {
            huComputePT( room,  gameInfo,  isZimo,  dianpaoUser,  card);
        }
    }

    /**
     * 普通结算
     * @param room
     * @param gameInfo
     * @param isZimo
     * @param dianpaoUser
     * @param card
     */
    private void huComputePT(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card){
        List<String> cs = getCardsNoChiPengGang(cards);
        List<HuCardType> huList = HuUtil.isHu(cs, this,CardTypeUtil.cardType.get(card) , new HuLimit(0));
        //设置胡牌类型
        HuCardType huCardType = getMaxScoreHuCardType(huList);

        setWinTypeResult(huCardType);
        if(winType.contains(hu_双豪七小对_山西)){
            winType.remove(hu_双豪七小对_山西);
            winType.add(hu_豪华七小对);
        }

        StringBuffer sb = new StringBuffer();
        for (String s : cards) {
            if(!s.equals(card)){
                sb.append(s);
                sb.append(",");
            }
        }
//		if (room.getMode().equals("6")){
        if(isZimo){
            for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                room.setUserSocre(i, - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
            }
            this.score = this.score +  8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple();
            room.setUserSocre(this.userId, 8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
            this.fan = 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
            System.out.println("======自摸：" + 6 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
        }else{
            if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
                for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                    gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                    room.setUserSocre(i, - CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                }
                this.score = this.score + 4 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple();
                room.setUserSocre(this.userId, 4 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                this.fan = CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
                System.out.println("======点炮（已听）：" + 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
            }else{
                gameInfo.getPlayerCardsInfos().get(dianpaoUser).setScore(gameInfo.getPlayerCardsInfos().get(dianpaoUser).getScore() - 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                this.score = this.score + 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple();
                room.setUserSocre(dianpaoUser,- 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                room.setUserSocre(this.userId, 3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
                this.fan = CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
                System.out.println("======点炮（未听）：" +  3 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
            }
        }
    }

    /**
     * 显庄
     * @param room
     * @param gameInfo
     * @param isZimo
     * @param dianpaoUser
     * @param card
     */
    private void huComputeXZ(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {

        long bankerUserId = this.gameInfo.getFirstTurn();
        boolean isBankerWin = this.userId == bankerUserId;
        PlayerCardsInfoMj bankerUser = this.gameInfo.getPlayerCardsInfos().get(bankerUserId);

        if (isZimo) {

            //闲家自摸
            if (!isBankerWin) {
                bankerUser.addScore(-10);
                room.addUserSocre(bankerUserId, -10);
                this.addScore(10);
                room.addUserSocre(this.userId, 10);

            } else {//庄家自摸
                for (PlayerCardsInfoMj playerCardsInfoMj : this.gameInfo.getPlayerCardsInfos().values()) {
                    if (playerCardsInfoMj.getUserId() != this.userId) {
                        playerCardsInfoMj.addScore(-10);
                        room.addUserSocre(playerCardsInfoMj.getUserId(), -10);
                    }
                }
                this.addScore(30);
                room.addUserSocre(this.userId, 30);
            }
        } else {//点炮

            PlayerCardsInfoMj dianpaoPlayer = this.getGameInfo().getPlayerCardsInfos().get(dianpaoUser);
            //庄平胡
            if (isBankerWin) {
                dianpaoPlayer.addScore(-5);
                room.addUserSocre(dianpaoUser, -5);
            } else {//闲家胡
                boolean isBaoHu = !dianpaoPlayer.isTing;
                //包胡
                if (isBaoHu) {
                    dianpaoPlayer.addScore(-5);
                    room.addUserSocre(dianpaoUser, -5);
                } else {
                    bankerUser.addScore(-5);
                    room.addUserSocre(bankerUserId, -5);
                }
            }

            this.addScore(5);
            room.addUserSocre(this.userId, 5);
        }
    }
}
