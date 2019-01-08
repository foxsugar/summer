package com.code.server.login.service;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.RoomModel;
import com.code.server.constant.club.UpScoreItem;
import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.model.Club;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunxianping on 2018-11-28.
 */
@Service
public class GameClubHasMoneyService extends GameClubService{



    /**
     * 初始化俱乐部
     *
     * @param club
     */
    public  void initRoomInstance(Club club) {

        //do nothing
//        initRoomInstanceStatic(club);

    }



    /**
     * 创建房间model
     *
     * @param createCommand
     * @param userId
     * @param clubId
     * @param gameType
     * @param gameNumber
     * @param desc
     * @return
     */
    public int createRoomModel(KafkaMsgKey msgKey, long userId, String clubId, String createCommand, String gameType, int gameNumber, String desc, List<Integer> indexs) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        int limit = serverConfig.getClubRoomModelLimit();
        if (club.getClubInfo().getRoomModels().size() >= limit) {
            return ErrorCode.CLUB_NOT_MODEL_LIMIT;
        }


        //初始化 房间数据
        initRoomData();

        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData == null || !roomData.getMoneyMap().containsKey(gameNumber)) {
            return ErrorCode.REQUEST_PARAM_ERROR;
        }


        int length = indexs == null ? 1 : indexs.size();

        for (int i = 0; i < length; i++) {

            RoomModel roomModel = new RoomModel();
            String id = "" + IdWorker.getDefaultInstance().nextId() + i;
            roomModel.setId(id);
            JsonNode jsonNode = JsonUtil.readTree(createCommand);
            String serviceName = jsonNode.path("service").asText();
            //设置创建命令
            createCommand = setRoomModelCommand(createCommand, clubId, id);
            roomModel.setCreateCommand(createCommand);
            roomModel.setDesc(desc);
            roomModel.setTime(System.currentTimeMillis());
            roomModel.setMoney(roomData.getMoneyMap().get(gameNumber));
            roomModel.setServiceName(serviceName);

            club.getClubInfo().getRoomModels().add(roomModel);

        }
        Set<String> set = new HashSet();
        club.getClubInfo().getRoomModels().forEach(roomModel -> set.add(roomModel.getId()));



        //实例化房间
        initRoomInstance(club);




        RoomModel roomModel = club.getClubInfo().getRoomModels().get(club.getClubInfo().getRoomModels().size() - 1);
        sendMsg(msgKey, new ResponseVo("clubService", "createRoomModel", roomModel));
        createRoom(club, roomModel);
        return 0;
    }

    /**
     * 转让俱乐部
     * @param clubId
     * @param userId
     * @param toUser
     * @return
     */
    public int transfer(KafkaMsgKey msgKey, String clubId, long userId, long toUser) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + toUser);
        if(clubMember == null){
            return ErrorCode.CLUB_NOT_TRANSFER;
        }
        club.setImage(clubMember.getImage());
        club.setPresidentName(clubMember.getName());
        club.setPresident(clubMember.getUserId());
        club.setPresidentWx("");

        sendMsg(msgKey, new ResponseVo("clubService", "transfer", "ok"));
        return 0;
    }


    /**
     * 设置合伙人
     * @param msgKey
     * @param clubId
     * @param userId
     * @param partnerId
     * @return
     */
    public int setPartner(KafkaMsgKey msgKey, String clubId, long userId, long partnerId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (!club.getClubInfo().getPartner().contains(partnerId)) {
            club.getClubInfo().getPartner().add(partnerId);
        }

        sendMsg(msgKey, new ResponseVo("clubService", "setPartner", "ok"));
        return 0;
    }


    /**
     * 删除代理
     * @param msgKey
     * @param clubId
     * @param userId
     * @param partnerId
     * @return
     */
    public int removePartner(KafkaMsgKey msgKey, String clubId, long userId, long partnerId){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        club.getClubInfo().getPartner().remove(partnerId);

        //删掉玩家身上的referee
        for (ClubMember clubMember : club.getClubInfo().getMember().values()) {
            if (clubMember.getReferrer() == partnerId) {
                clubMember.setReferrer(0);
            }
        }

        sendMsg(msgKey, new ResponseVo("clubService", "removePartner", "ok"));
        return 0;
    }

    public int changePartner(KafkaMsgKey msgKey, String clubId, long userId, long newPartner, long changeUser){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + changeUser);
        clubMember.setReferrer(newPartner);

        sendMsg(msgKey, new ResponseVo("clubService", "changePartner", "ok"));
        return 0;
    }


    /**
     * 上下分
     * @param msgKey
     * @param clubId
     * @param userId
     * @param toUser
     * @param num
     * @return
     */
    public int upScore(KafkaMsgKey msgKey, String clubId, long userId, long toUser, int num) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
        RedisManager.getClubRedisService().addClubUserMoney(clubId, toUser, num);
        //上下分记录
        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(4);
        List<UpScoreItem> list = club.getUpScoreInfo().getInfo().getOrDefault(date.toString(),new ArrayList<>());
        int type = num>=0?1:0;
        UpScoreItem upScoreItem = new UpScoreItem().setSrcUserId(userId).setDesUserId(toUser).setNum(num)
                .setTime(System.currentTimeMillis()).setType(type).setName(clubMember.getName());

        //添加一条记录
        list.add(upScoreItem);
        club.getUpScoreInfo().getInfo().put(date.toString(), list);
        club.getUpScoreInfo().getInfo().remove(dateBefore.toString());

        sendMsg(msgKey, new ResponseVo("clubService", "upScore", "ok"));
        return 0;
    }



}
