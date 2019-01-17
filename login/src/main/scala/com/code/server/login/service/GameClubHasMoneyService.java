package com.code.server.login.service;

import com.code.server.constant.club.RoomModel;
import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.model.Club;
import com.code.server.login.config.ServerConfig;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sunxianping on 2018-11-28.
 */
@Service("gameClubHasMoneyService")
public class GameClubHasMoneyService extends GameClubService {


    /**
     * 初始化俱乐部
     *
     * @param club
     */
    public void initRoomInstance(Club club) {

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
            //不花钱
//            roomModel.setMoney(roomData.getMoneyMap().get(gameNumber));
            roomModel.setServiceName(serviceName);

            club.getClubInfo().getRoomModels().add(roomModel);

        }
//        Set<String> set = new HashSet();
//        club.getClubInfo().getRoomModels().forEach(roomModel -> set.add(roomModel.getId()));


        //实例化房间
//        initRoomInstance(club);
//
//
//        RoomModel roomModel = club.getClubInfo().getRoomModels().get(club.getClubInfo().getRoomModels().size() - 1);
//        sendMsg(msgKey, new ResponseVo("clubService", "createRoomModel", roomModel));
//        createRoom(club, roomModel);

        //删除没用的model
//        List<RoomModel> removeList = new ArrayList<>();
//        for (RoomModel roomModel1 : club.getClubInfo().getRoomModels()) {
//            if (!roomModel1.getId().equals(roomModel.getId())) {
//                if (!club.getClubInfo().getRoomInstance().containsKey(roomModel1.getId())) {
//                    removeList.add(roomModel1);
//                }
//            }
//        }
//
//        club.getClubInfo().getRoomModels().removeAll(removeList);
        return 0;
    }


}
