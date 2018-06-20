package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 小翼扣点 on 2018/5/9
 */
public class PlayerCardsInfoKD_XY extends PlayerCardsInfoKD {

    private static final int ZIMO_MIN_SCORE = 4;
    protected static final int DIANPAO_MIN_SCORE = 5;

    //创建房间mode：显庄 扣听 包杠 （1是0否）



    @Override
    public void gangCompute(RoomInfo room, GameInfo gameInfo, boolean isMing, long diangangUser, String card){
        //super.gangCompute(room,gameInfo,isMing,diangangUser,card);
    }

    public void computeALLGangPT(){

        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //暗杠计算
            for (long i : scores.keySet()) {
                for (Integer integer:playerCardsInfo.getAnGangType()) {
                    scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(integer)*2);
                }
            }
            for (Integer integer:playerCardsInfo.getAnGangType()) {
                scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+CardTypeUtil.cardTingScore.get(integer)*2*4);
            }

            //明杠计算
            for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                if(playerCardsInfo.getMingGangType().get(ii)!=-1){//点杠diangangUser
                    if(gameInfo.playerCardsInfos.get(playerCardsInfo.getMingGangType().get(ii)).isTing){//听了，3家出
                        for (long i : scores.keySet()) {
                            scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(ii));
                        }
                        scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*4);
                    }else{//未听一家出
                        scores.put(playerCardsInfo.getMingGangType().get(ii), scores.get(playerCardsInfo.getUserId()) - CardTypeUtil.cardTingScore.get(ii)*3);
                        scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*3);
                    }
                }else{//自摸明杠
                    for (long i : scores.keySet()) {
                        scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(ii));
                    }
                    scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*4);
                }
            }
        }
        for (long i : scores.keySet()) {
            gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
            roomInfo.setUserSocre(i, scores.get(i));
        }
    }

    public void computeALLGangBG(long dianpaoUser){

        Map<Long,Integer> scores = new HashMap<>();//分数计算key:use,value:score
        for (long i : gameInfo.users) {
            scores.put(i, 0);
        }

        for (PlayerCardsInfoMj playerCardsInfo : gameInfo.getPlayerCardsInfos().values()) {
            //暗杠计算
            if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
                for (long i : scores.keySet()) {
                    for (Integer integer:playerCardsInfo.getAnGangType()) {
                        scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(integer)*2);
                    }
                }
                for (Integer integer:playerCardsInfo.getAnGangType()) {
                    scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+CardTypeUtil.cardTingScore.get(integer)*2*4);
                }
            }else{
                for (Integer integer:playerCardsInfo.getAnGangType()) {
                    scores.put(dianpaoUser, scores.get(playerCardsInfo.getUserId())-CardTypeUtil.cardTingScore.get(integer)*2*3);
                }
                for (Integer integer:playerCardsInfo.getAnGangType()) {
                    scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId())+CardTypeUtil.cardTingScore.get(integer)*2*3);
                }
            }


            //明杠计算
            if(gameInfo.getPlayerCardsInfos().get(dianpaoUser).isTing){
                for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                    if(playerCardsInfo.getMingGangType().get(ii)!=-1){//点杠diangangUser
                        if(gameInfo.playerCardsInfos.get(dianpaoUser).isTing){//听了，3家出
                            for (long i : scores.keySet()) {
                                scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(ii));
                            }
                            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*4);
                        }else{//未听一家出
                            scores.put(playerCardsInfo.getMingGangType().get(ii), scores.get(playerCardsInfo.getUserId()) - CardTypeUtil.cardTingScore.get(ii)*3);
                            scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*3);
                        }
                    }else{//自摸明杠
                        for (long i : scores.keySet()) {
                            scores.put(i, scores.get(i) - CardTypeUtil.cardTingScore.get(ii));
                        }
                        scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*4);
                    }
                }
            }else{
                for (Integer ii : playerCardsInfo.getMingGangType().keySet()) {
                    scores.put(dianpaoUser, scores.get(playerCardsInfo.getUserId()) - CardTypeUtil.cardTingScore.get(ii)*3);
                    scores.put(playerCardsInfo.getUserId(), scores.get(playerCardsInfo.getUserId()) + CardTypeUtil.cardTingScore.get(ii)*3);
                }
            }
        }
        for (long i : scores.keySet()) {
            gameInfo.getPlayerCardsInfos().get(i).setScore(scores.get(i));
            roomInfo.setUserSocre(i, scores.get(i));
        }
    }




    @Override
    public void huCompute(RoomInfo room, GameInfo gameInfo, boolean isZimo, long dianpaoUser, String card) {
        String ifBaoGang = room.getMode();
        if(!ifBaoGang.isEmpty() && !(ifBaoGang.endsWith("1"))){
            computeALLGangPT();
        }else {
            computeALLGangBG(dianpaoUser);
        }
        //super.huCompute(room, gameInfo, isZimo, dianpaoUser, card);
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

        //是否是杠开
        boolean isGangKai = isGangKai();
        if (isGangKai){//杠开算自摸
            this.winType.add(HuType.hu_杠开);
            for (Long i : gameInfo.getPlayerCardsInfos().keySet()){
                gameInfo.getPlayerCardsInfos().get(i).setScore(gameInfo.getPlayerCardsInfos().get(i).getScore() - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
                room.setUserSocre(i, - 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType) * room.getMultiple());
            }
            this.score = this.score +  8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple();
            room.setUserSocre(this.userId, 8 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
            this.fan = 2 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length() - 1), card,huCardType);
            System.out.println("======自摸：" + 6 * CardUtil.KDForScoresDoubleScore(sb.toString().substring(0, sb.length()-1),card,huCardType) * room.getMultiple());
            return;
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

            PlayerCardsInfoMj dianPaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            boolean isBao_ting_hou = false;
            if (dianPaoPlayer.operateList.size() > 0) {
                isBao_ting_hou = dianPaoPlayer.operateList.get(dianPaoPlayer.operateList.size() - 1) == PlayerCardsInfoMj.type_ting;
            }
            boolean isBaoAll = !dianPaoPlayer.isTing || isBao_ting_hou;

            if(!isBaoAll){
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

        //是否是杠开
        boolean isGangKai = isGangKai();
        if (isGangKai) {
            this.winType.add(HuType.hu_杠开);
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
            return;
        }

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

            PlayerCardsInfoMj dianPaoPlayer = this.gameInfo.playerCardsInfos.get(dianpaoUser);
            boolean isBao_ting_hou = false;
            if (dianPaoPlayer.operateList.size() > 0) {
                isBao_ting_hou = dianPaoPlayer.operateList.get(dianPaoPlayer.operateList.size() - 1) == PlayerCardsInfoMj.type_ting;
            }
            boolean isBaoAll = !dianPaoPlayer.isTing || isBao_ting_hou;

            PlayerCardsInfoMj dianpaoPlayer = this.getGameInfo().getPlayerCardsInfos().get(dianpaoUser);
            //庄平胡
            if (isBankerWin) {
                dianpaoPlayer.addScore(-5);
                room.addUserSocre(dianpaoUser, -5);
            } else {//闲家胡
                //boolean isBaoHu = !dianpaoPlayer.isTing;
                //包胡
                if (isBaoAll) {
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

    /**
     * 在4点及4点以上点数时能自摸，4点以下不能自摸胡
     * @param card
     * @return
     */
    @Override
    public boolean isCanHu_zimo(String card) {
        return isTing && CardTypeUtil.getCardTingScore(card)>=ZIMO_MIN_SCORE && super.isCanHu_zimo(card);
    }

    @Override
    public boolean isCanHu_dianpao(String card) {
        return isTing && CardTypeUtil.getCardTingScore(card)>=DIANPAO_MIN_SCORE && super.isCanHu_dianpao(card);
    }

    /**
     * 听
     */
    @Override
    public void ting(String card) {
        //出牌 弃牌置为空(客户端扣牌)
        this.cards.remove(card);
        String ifAnKou = this.gameInfo.room.getMode();
        if(!ifAnKou.isEmpty() && (ifAnKou.startsWith("1",1))){
            this.disCards.add(null);
        }else {
            this.disCards.add(card);
        }

        this.isTing = true;
        tingSet = getTingCardType(cards, null);
        this.lastOperate = type_ting;
        operateList.add(type_ting);
        this.gameInfo.addUserOperate(this.userId, type_ting);
    }

}
