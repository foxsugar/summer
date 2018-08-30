package com.code.server.login.service;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.ClubStatistics;
import com.code.server.constant.club.RoomInstance;
import com.code.server.constant.club.RoomModel;
import com.code.server.constant.data.DataManager;
import com.code.server.constant.data.StaticDataProto;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ClubVo;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.RoomInstanceVo;
import com.code.server.db.Service.ClubChargeService;
import com.code.server.db.Service.ClubRecordService;
import com.code.server.db.Service.ClubService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Club;
import com.code.server.db.model.ClubCharge;
import com.code.server.db.model.ClubRecord;
import com.code.server.db.model.User;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sunxianping on 2018/1/15.
 */
@Service
public class GameClubService {

    @Autowired
    private ClubService clubService;

    @Autowired
    private MsgProducer kafkaMsgProducer;

    @Autowired
    private ClubRecordService clubRecordService;

    @Autowired
    private ClubChargeService clubChargeService;

    @Autowired
    private UserService userService;

    private static final int JOIN_LIMIT = 5;

    /**
     * 查看俱乐部
     *
     * @param msgKey
     * @param userId
     * @return
     */
    public int lookClub(KafkaMsgKey msgKey, long userId) {
        List<ClubVo> list = new ArrayList<>();

        List<String> clubs = ClubManager.getInstance().getUserClubs(userId);

        for (String clubId : clubs) {
            Club club = ClubManager.getInstance().getClubById(clubId);
            club.getClubInfo().getMember().get("" + userId).setLastLoginTime("" + System.currentTimeMillis());
            list.add(getClubVo_simple(club));
        }

        sendMsg(msgKey, new ResponseVo("clubService", "lookClub", list));
        return 0;
    }

    /**
     * 获得俱乐部信息
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int getClubInfo(KafkaMsgKey msgKey, long userId, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);

        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (!club.getClubInfo().getMember().containsKey("" + userId)) {
            return ErrorCode.CLUB_NOT_MEMBER;
        }
        //刷新房间
        initRoomInstance(club);


        boolean isPresident = club.getPresident() == userId;
        ClubVo clubVo = getClubVo_simple(club);
        clubVo.getRoomModels().addAll(club.getClubInfo().getRoomModels());
        clubVo.getFloorDesc().addAll(club.getClubInfo().getFloorDesc());
        clubVo.setStatistics(club.getStatistics());
        //玩家在线情况
        clubVo.getMember().addAll(club.getClubInfo().getMember().values());
        clubVo.setAutoJoin(club.getClubInfo().isAutoJoin());
//        if (clubVo.getMember().size() > 100) {
//            clubVo.setMember(clubVo.getMember().subList(0, 100));
//        }
        clubVo.getAdmin().addAll(club.getClubInfo().getAdmin());
        clubVo.getMember().forEach(clubMember -> {
            String gateId = RedisManager.getUserRedisService().getGateId(clubMember.getUserId());
            boolean online = gateId != null;
            clubMember.setOnline(online);
        });

        club.getClubInfo().getRoomInstance().values().forEach(roomInstance -> {
            if (roomInstance.getRoomId() != null) {
                clubVo.getRoomInstance().add(getRoomInstanceVo(roomInstance));
            }
        });

        //房间情况
        if (isPresident) {
            clubVo.getApplyList().addAll(club.getClubInfo().getApplyList());
        }

        //正在玩的房间

        List<RoomInstance> removeList = new ArrayList<>();
        club.getClubInfo().getPlayingRoom().forEach(ri -> {
            String roomId = ri.getRoomId();
            if (roomId != null) {

                if (RedisManager.getRoomRedisService().getServerId(roomId) == null) {
                    removeList.add(ri);
                } else {
                    clubVo.getPlayingRoom().add(getRoomInstanceVo(ri));
                }
            }
        });
        //删除已经解散的
        club.getClubInfo().getPlayingRoom().removeAll(removeList);


        //todo 初始化数据

        //todo 更改玩家真实信息
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (isPresident) {
            club.setPresidentName(userBean.getUsername());
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
        clubMember.setName(userBean.getUsername());
        clubMember.setImage(userBean.getImage());

        //发送结果
        sendMsg(msgKey, new ResponseVo("clubService", "getClubInfo", clubVo));
        return 0;
    }

    /**
     * 获得房间实例vo
     *
     * @param roomInstance
     * @return
     */
    private RoomInstanceVo getRoomInstanceVo(RoomInstance roomInstance) {
        RoomInstanceVo vo = new RoomInstanceVo();
        String roomId = roomInstance.getRoomId();
        vo.setRoomId(roomId);
        vo.setClubRoomModel(roomInstance.getRoomModelId());
        RedisManager.getRoomRedisService().getUsers(roomId).forEach(uid -> {
            Map<String, Object> player = new HashMap<>();
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(uid);
            if (userBean != null) {

                player.put("username", userBean.getUsername());
                player.put("image", userBean.getImage());
                player.put("id", userBean.getId());
                vo.getPlayers().add(player);
            }

        });


        return vo;
    }

    private int getCreateMoney() {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        return serverConfig.getClubCreateMoney();
    }

    /**
     * 创建俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubName
     * @param wx
     * @param area
     * @param desc
     * @return
     */
    public int createClub(KafkaMsgKey msgKey, long userId, String clubName, String wx, String area, String desc) {

        //钱是否够
        double money = RedisManager.getUserRedisService().getUserMoney(userId);
        if (money < getCreateMoney()) {
            return ErrorCode.CLUB_CANNOT_MONEY;
        }
        //多于5个俱乐部 不可以创建
        int num = ClubManager.getInstance().getUserClubNum(userId);
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (num >= serverConfig.getClubLimit()) {
            return ErrorCode.CLUB_CANNOT_NUM;
        }

        //人减钱
        RedisManager.getUserRedisService().addUserMoney(userId, -getCreateMoney());

        Club club = new Club();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        //id
        club.setId(ClubManager.getInstance().getClubId());
        club.setPresident(userId);
        club.setName(clubName);
        club.setPresidentName(userBean.getUsername());
        club.setPresidentWx(wx);
        club.setArea(area);
        club.setMoney(getCreateMoney());
        club.setClubDesc(desc);

        club.setImage(userBean.getImage());
        clubAddMember(club, userBean);

        clubService.getClubDao().save(club);

        ClubManager.getInstance().getClubMap().put(club.getId(), club);


        ResponseVo vo = new ResponseVo("clubService", "createClub", club);
        sendMsg(msgKey, vo);
        return 0;
    }


    /**
     * 设置俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param clubName
     * @param wx
     * @param area
     * @param desc
     * @return
     */
    public int setClub(KafkaMsgKey msgKey, long userId, String clubId, String clubName, String wx, String area, String desc) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (userId != club.getPresident() && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_CANNOT_NO_PRESIDENT;
        }
        club.setName(clubName).setPresidentWx(wx).setArea(area).setClubDesc(desc);

        sendMsg(msgKey, new ResponseVo("clubService", "setClub", getClubVo_simple(club)));
        return 0;
    }

    /**
     * mark 用户
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param markUser
     * @param mark
     * @return
     */
    public int markUser(KafkaMsgKey msgKey, long userId, String clubId, long markUser, String mark) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (userId != club.getPresident() && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_CANNOT_NO_PRESIDENT;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + markUser);
        clubMember.setMark(mark);

        sendMsg(msgKey, new ResponseVo("clubService", "markUser", "ok"));

        return 0;
    }

    /**
     * 解散
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int dissolve(KafkaMsgKey msgKey, long userId, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (userId != club.getPresident() && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_CANNOT_NO_PRESIDENT;
        }


        //玩家删除id
        List<String> removeList = new ArrayList<>();
        removeList.addAll(club.getClubInfo().getMember().keySet());

        for (String uid : removeList) {
            clubRemoveMember(club, Long.valueOf(uid));

        }

        //删除club
        ClubManager.getInstance().getClubMap().remove(clubId);

        //把钱加回去
        RedisManager.getUserRedisService().addUserMoney(userId, club.getMoney());

        //todo 在建的房间 是否退钱
        for (RoomInstance roomInstance : club.getClubInfo().getRoomInstance().values()) {
            String roomId = roomInstance.getRoomId();
            if (RedisManager.getRoomRedisService().getUsers(roomId).size() == 0) {
                RoomModel roomModel = GameClubService.getRoomModel(club, roomInstance.getRoomModelId());
                RedisManager.getUserRedisService().addUserMoney(userId, roomModel.getMoney());
            }
        }

        sendMsg(msgKey, new ResponseVo("clubService", "dissolve", club));
        return 0;
    }

    /**
     * 是否有此俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int isHasClub(KafkaMsgKey msgKey, long userId, String clubId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isHas", ClubManager.getInstance().getClubMap().containsKey(clubId));
        sendMsg(msgKey, new ResponseVo("clubService", "isHasClub", result));
        return 0;
    }

    /**
     * 加入俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int joinClub(KafkaMsgKey msgKey, long userId, String clubId, String mark) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        //自己加入了几个俱乐部
        List<String> joinList = ClubManager.getInstance().getUserClubs(userId);
        int limit = SpringUtil.getBean(ServerConfig.class).getClubJoinLimit();
        if (joinList.size() >= limit) {
            return ErrorCode.CLUB_CANNOT_NUM;
        }
        if (joinList.contains(clubId)) {
            return ErrorCode.CLUB_CANNOT_JOIN;
        }

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        String name = userBean.getUsername();
        String image = userBean.getImage();
        int sex = userBean.getSex();
        ClubMember apply = new ClubMember().setTime(System.currentTimeMillis()).setUserId(userId).setMark(mark).setName(name).setImage(image).setSex(sex);

        //自动加入
        if (club.getClubInfo().isAutoJoin()) {
            clubAddMember(club, apply);
        }else{
            //加入申请列表
            if (isInApplyList(club, userId)) {
                return ErrorCode.CLUB_CANNOT_JOIN;
            }
            club.getClubInfo().getApplyList().add(apply);
        }

        Map<String, Object> result = new HashMap<>();
        sendMsg(msgKey, new ResponseVo("clubService", "joinClub", result));

        return 0;
    }


    /**
     * 退出俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int quitClub(KafkaMsgKey msgKey, long userId, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        //todo

        //删除
        clubRemoveMember(club, userId);
        sendMsg(msgKey, new ResponseVo("clubService", "quitClub", "ok"));
        return 0;
    }


    /**
     * 同意加入俱乐部
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param agreeId
     * @param isAgree
     * @return
     */
    public int agree(KafkaMsgKey msgKey, long userId, String clubId, long agreeId, boolean isAgree) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        int limit = SpringUtil.getBean(ServerConfig.class).getClubJoinLimit();
        if (ClubManager.getInstance().getUserClubNum(agreeId) >= limit) {
            return ErrorCode.CLUB_CANNOT_JOIN;
        }
        //加入俱乐部
        ClubMember apply = getApply(club, agreeId);
        if (isAgree) {
            if (apply != null) {

                clubAddMember(club, apply);
            }
            String name = apply == null ? "" : apply.getName();
        }
        //删除申请列表
//        removeUserFromApplyList(club, agreeId);
        club.getClubInfo().getApplyList().remove(apply);
        sendMsg(msgKey, new ResponseVo("clubService", "agree", "ok"));

        return 0;
    }


    /**
     * 充值
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param money
     * @return
     */
    public int charge(KafkaMsgKey msgKey, long userId, String clubId, int money) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }

        if (money <= 0) {
            return ErrorCode.REQUEST_PARAM_ERROR;
        }
        if (RedisManager.getUserRedisService().getUserMoney(userId) < money) {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }
        //加钱
        club.setMoney(club.getMoney() + money);
        RedisManager.getUserRedisService().addUserMoney(userId, -money);
        sendMsg(msgKey, new ResponseVo("clubService", "charge", "ok"));

        //记录

        ClubCharge clubCharge = new ClubCharge();
        clubCharge.setClubId(clubId);
        clubCharge.setNum(money);
        clubCharge.setNowMoney(club.getMoney());
        clubCharge.setChargeTime(LocalDateTime.now().toString());
        clubChargeService.getClubChargeDao().save(clubCharge);

        return 0;
    }


    public int getChargeRecord(KafkaMsgKey msgKey, long userId, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        List<ClubCharge> list = clubChargeService.getClubChargeDao().getClubChargesByClubId(clubId);
        sendMsg(msgKey, new ResponseVo("clubService", "getChargeRecord", list));
        return 0;
    }

    public int setFloor(KafkaMsgKey msgKey, long userId, String clubId, int floor,String desc) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        for (int i = 0; i < 5; i++) {
            if (club.getClubInfo().getFloorDesc().size() > i) {

//                if(club.getClubInfo().getFloorDesc().get(i).equals())
//                club.getClubInfo().getFloorDesc().add("");
            } else{
                club.getClubInfo().getFloorDesc().add("");
            }
        }
//        for (int i = 0; i < floor; i++) {
//          club.getClubInfo().getFloorDesc().add(floor, desc);
//        }

        club.getClubInfo().getFloorDesc().set(floor, desc);
        sendMsg(msgKey, new ResponseVo("clubService", "setFloorDesc", "ok"));

        return 0;
    }


    public int setAdmin(KafkaMsgKey msgKey, long userId, String clubId, long adminUser, boolean isAdd) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (club.getPresident() != userId) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        if (isAdd) {
            club.getClubInfo().getAdmin().add(adminUser);
        }else{
            club.getClubInfo().getAdmin().remove(adminUser);
        }
        sendMsg(msgKey, new ResponseVo("clubService", "setAdmin", "ok"));

        return 0;
    }

    public int addUser(KafkaMsgKey msgKey, String clubId, long userId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        boolean isHas = userService.getUserDao().exists(userId);
        if (!isHas) {
            return ErrorCode.USERID_ERROR;
        }
        List<String> clubs = ClubManager.getInstance().getUserClubs(userId);
        int limit = SpringUtil.getBean(ServerConfig.class).getClubJoinLimit();
        if (clubs.size() >= limit) {
            return ErrorCode.CLUB_CANNOT_NUM;
        }
        if (clubs.contains(clubId)) {
            return ErrorCode.CLUB_CANNOT_JOIN;
        }
//        ClubManager.getInstance().userAddClub(userId, clubId);

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {

            clubAddMember(club,userBean);
        }else{
            User user = userService.getUserByUserId(userId);

            ClubMember clubMember = new ClubMember();
            clubMember.setSex(user.getSex()).setImage(user.getImage()).setName(user.getUsername()).setUserId(user.getId()).setTime(System.currentTimeMillis());
            clubAddMember(club, clubMember);

        }
        //
        sendMsg(msgKey, new ResponseVo("clubService", "addUser","ok"));
        return 0;
    }


    public int removeFloor(KafkaMsgKey msgKey, String clubId, long userId,int floor){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (club.getClubInfo().getRoomModels().size() < floor * 8) {
            return ErrorCode.CLUB_PARAM_ERROR;
        }
        for(int i=0;i<8;i++) {
            club.getClubInfo().getRoomModels().remove(floor * 8);
        }
        if (club.getClubInfo().getFloorDesc().size() > floor) {

            club.getClubInfo().getFloorDesc().remove(floor);
        }
        sendMsg(msgKey, new ResponseVo("clubService", "removeFloor","ok"));
        return 0;
    }


    /**
     * club join room 推送
     * @param clubId
     * @param userId
     * @param roomModelId
     */
    public void clubJoinRoom(String clubId, long userId, String roomModelId,String roomId){
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if(serverConfig.getClubPushUserRoomInfo() == 0) return;

        Club club = ClubManager.getInstance().getClubById(clubId);
        List<Long> users = new ArrayList<>();
        if (club != null) {
            club.getClubInfo().getMember().forEach((id, ClubMember) -> users.add(Long.valueOf(id)));
        }
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        r.put("roomModelId", roomModelId);
        r.put("clubId", clubId);
        ResponseVo responseVo = new ResponseVo("clubService","clubJoinRoom",r);
        users.forEach(uid -> sendMsg2Player(responseVo, uid));


        if (serverConfig.getSend_lq_http() == 1 ) {
            List<Long> us = new ArrayList<>();
            us.addAll(RedisManager.getRoomRedisService().getUsers(roomId));
            send_Lq_start(club, roomId, roomModelId, us,0);
        }
    }


    /**
     * 退出房间推送
     * @param clubId
     * @param userId
     * @param roomModelId
     */
    public void clubQuitRoom(String clubId, long userId, String roomModelId,String roomId){
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if(serverConfig.getClubPushUserRoomInfo() == 0) return;

        Club club = ClubManager.getInstance().getClubById(clubId);
        List<Long> users = new ArrayList<>();
        if (club != null) {
            club.getClubInfo().getMember().forEach((id, ClubMember) -> users.add(Long.valueOf(id)));
        }
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        r.put("roomModelId", roomModelId);
        r.put("clubId", clubId);
        ResponseVo responseVo = new ResponseVo("clubService","clubQuitRoom",r);
        users.forEach(uid -> sendMsg2Player(responseVo, uid));

        if (serverConfig.getSend_lq_http() == 1 ) {
            List<Long> us = new ArrayList<>();
            us.addAll(RedisManager.getRoomRedisService().getUsers(roomId));
            send_Lq_start(club, roomId, roomModelId, us,0);
        }
    }


    public int setAutoJoin(KafkaMsgKey msgKey, String clubId, long userId,boolean auto) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        club.getClubInfo().setAutoJoin(auto);
        sendMsg(msgKey, new ResponseVo("clubService", "setAutoJoin","ok"));
        return 0;
    }
    /**
     * 初始化数据 懒加载
     */
    private void initRoomData() {
        //加载数据
        if (DataManager.data == null) {

            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            try {
                DataManager.initData(serverConfig.getDataFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            String id = "" + IdWorker.getDefaultInstance().nextId();
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
        RoomModel roomModel = club.getClubInfo().getRoomModels().get(club.getClubInfo().getRoomModels().size() - 1);
        sendMsg(msgKey, new ResponseVo("clubService", "createRoomModel",roomModel));

        //实例化房间
        initRoomInstance(club);
        return 0;
    }

    /**
     * 删除房间模式
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param roomModelId
     * @return
     */
    public int removeRoomModel(KafkaMsgKey msgKey, long userId, String clubId, String roomModelId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        RoomModel roomModel = getRoomModel(club, roomModelId);
        if (roomModel != null) {
            club.getClubInfo().getRoomModels().remove(roomModel);
        }
        //todo 退钱

        sendMsg(msgKey, new ResponseVo("clubService", "removeRoomModel", "ok"));
        return 0;
    }


    /**
     * 修改房间模式
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param roomModelId
     * @param createCommand
     * @param gameType
     * @param gameNumber
     * @param desc
     * @return
     */
    public int setRoomModel(KafkaMsgKey msgKey, long userId, String clubId, String roomModelId, String createCommand, String gameType, int gameNumber, String desc) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }


        //初始化 房间数据
        initRoomData();

        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData == null || !roomData.getMoneyMap().containsKey(gameNumber)) {
            return ErrorCode.REQUEST_PARAM_ERROR;
        }


        JsonNode jsonNode = JsonUtil.readTree(createCommand);
        String serviceName = jsonNode.path("service").asText();
        //设置创建命令

        RoomModel roomModel = getRoomModel(club, roomModelId);
        if (roomModel == null) {
            return ErrorCode.REQUEST_PARAM_ERROR;
        }
        createCommand = setRoomModelCommand(createCommand, clubId, roomModel.getId());
        roomModel.setCreateCommand(createCommand);
        roomModel.setDesc(desc);
        roomModel.setTime(System.currentTimeMillis());
        roomModel.setMoney(roomData.getMoneyMap().get(gameNumber));
        roomModel.setServiceName(serviceName);


        sendMsg(msgKey, new ResponseVo("clubService", "setRoomModel", roomModel));
        //实例化房间
        initRoomInstance(club);
        return 0;
    }


    /**
     * 修改房间模式
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @param createCommand
     * @param gameType
     * @param gameNumber
     * @param desc
     * @return
     */
    public int setRoomModelBatch(KafkaMsgKey msgKey, long userId, String clubId, String createCommand, String gameType, int gameNumber, String desc, List<Integer> indexs) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }


        //初始化 房间数据
        initRoomData();

        StaticDataProto.RoomData roomData = DataManager.data.getRoomDataMap().get(gameType);
        if (roomData == null || !roomData.getMoneyMap().containsKey(gameNumber)) {
            return ErrorCode.REQUEST_PARAM_ERROR;
        }


        JsonNode jsonNode = JsonUtil.readTree(createCommand);
        String serviceName = jsonNode.path("service").asText();
        //设置创建命令

        for (int index : indexs) {
            if (club.getClubInfo().getRoomModels().size() > index) {

                RoomModel roomModel = club.getClubInfo().getRoomModels().get(index);
                if (roomModel == null) {
                    return ErrorCode.REQUEST_PARAM_ERROR;
                }
                createCommand = setRoomModelCommand(createCommand, clubId, roomModel.getId());
                roomModel.setCreateCommand(createCommand);
                roomModel.setDesc(desc);
                roomModel.setTime(System.currentTimeMillis());
                roomModel.setMoney(roomData.getMoneyMap().get(gameNumber));
                roomModel.setServiceName(serviceName);
            }

        }


        sendMsg(msgKey, new ResponseVo("clubService", "setRoomModelBatch", "ok"));
        //实例化房间
        initRoomInstance(club);
        return 0;
    }

    /**
     * 俱乐部设置id
     *
     * @param clubId
     * @param clubModelId
     * @param roomId
     * @return
     */
    public int clubRoomSetId(String clubId, String clubModelId, String roomId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            synchronized (club.lock) {
                RoomInstance roomInstance = club.getClubInfo().getRoomInstance().get(clubModelId);
                if (roomInstance != null) {
                    roomInstance.setRoomId(roomId);
                }
            }

            List<Long> us = new ArrayList<>();
            send_Lq_start(club, roomId, clubModelId, us,0);
        }
        return 0;
    }

    /**
     * 俱乐部游戏开始
     *
     * @param clubId
     * @param clubModelId
     * @return
     */
    public int cludGameStart(String clubId, String clubModelId,List<Long> users,String roomId, int gameNumber) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            synchronized (club.lock) {
                //加入已开房间
                addPlayingRoom(club, club.getClubInfo().getRoomInstance().get(clubModelId));
                //删除一个房间
                club.getClubInfo().getRoomInstance().remove(clubModelId);
                //统计

                RoomModel roomModel = getRoomModel(club, clubModelId);
                if (roomModel != null) {

                    String today = LocalDate.now().toString();
                    ClubStatistics clubStatistics = roomModel.getStatisticsMap().get(today);
                    if (clubStatistics == null) {
                        clubStatistics = new ClubStatistics();
                    }
                    clubStatistics.setOpenNum(clubStatistics.getOpenNum() + 1);
                    clubStatistics.setConsumeNum(clubStatistics.getConsumeNum() + roomModel.getMoney());
                    roomModel.getStatisticsMap().put(today, clubStatistics);
                    //删除七天前的
                    roomModel.getStatisticsMap().remove(LocalDate.now().minusDays(7).toString());

                }

                //俱乐部的统计
                String date = LocalDate.now().toString();
                String removeDate = LocalDate.now().minusDays(3).toString();
                addStatisticeOpenNum(club, 1);
                addStatisticePlayer(club, users);
                club.getStatistics().getStatistics().remove(removeDate);

                //玩家身上的统计
                for (Long userId : users) {

                    ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
                    if (clubMember != null) {

                        ClubStatistics clubStatistics = clubMember.getStatistics().getOrDefault(date, new ClubStatistics());
                        club.getStatistics().getStatistics().put(date, clubStatistics);
                        clubStatistics.setOpenNum(clubStatistics.getOpenNum() + 1);
                        clubMember.getStatistics().remove(removeDate);
                    }
                }
            }
            initRoomInstance(club);

            //龙七 发送游戏开始
            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            if (serverConfig.getSend_lq_http() == 1 && gameNumber == 1) {
                send_Lq_start(club, roomId, clubModelId, users,2);
            }
        }
        return 0;
    }


    private static void send_Lq_start(Club club, String roomId, String clubRoomModel, List<Long> users ,int roomStatus){
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getSend_lq_http() == 1) {
            Map<String, Object> result = new HashMap<>();
            result.put("ClubNo", club.getId());
            result.put("RoomId", roomId);
            int index = CenterMsgService.getClubModelIndex(club, clubRoomModel);
            result.put("wanfa", index);
            result.put("OnlyNo", club.getId() + roomId + index);
            result.put("NStatus", roomStatus);
            List<Map<String, Object>> list = new ArrayList<>();
            result.put("PlayerList", list);
            for (long userId: users) {
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
                Map<String, Object> u = new HashMap<>();
                u.put("Unionid", userBean.getUnionId());
                u.put("WeixinName", userBean.getUsername());
                u.put("HeadImgUrl", userBean.getImage() + "/132");
                list.add(u);
            }
            String json = JsonUtil.toJson(result);
            System.out.println(json);
            HttpClient httpClient = HttpClientBuilder.create().build();
            //设置连接超时5s
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000).build();

            String url1 = "http://long7.l7jqr.com/RoomResult_club_info.php?strContext=" +json;

            try {
                URL url= new URL(url1);
                URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                System.out.println(url);
                System.out.println(uri);
                HttpGet request = new HttpGet(uri);
                request.setConfig(requestConfig);
                try {
                   HttpResponse httpResponse =  httpClient.execute(request);
                    System.out.println(httpResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    private static ClubStatistics getClubStatistics(Club club){
        String date = LocalDate.now().toString();
        club.getStatistics().getStatistics().putIfAbsent(date, new ClubStatistics());
        return club.getStatistics().getStatistics().get(date);
    }

    private void addStatisticeOpenNum(Club club, int num) {
        ClubStatistics clubStatistics = getClubStatistics(club);
        clubStatistics.setOpenNum(clubStatistics.getOpenNum() + num);
    }

    private static void addStatisticeConsume(Club club, int num) {
        ClubStatistics clubStatistics = getClubStatistics(club);
        clubStatistics.setConsumeNum(clubStatistics.getConsumeNum() + num);
    }

    private void addStatisticePlayer(Club club, List<Long> users) {
        ClubStatistics clubStatistics = getClubStatistics(club);
//        clubStatistics.setPlayerNum(clubStatistics.getPlayerNum() + num);
        clubStatistics.getPlayerUser().addAll(users);
    }


    public void addPlayingRoom(Club club, RoomInstance roomInstance) {
        if (roomInstance == null) return;
        if (club.getClubInfo().getPlayingRoom() == null) {
            club.getClubInfo().setPlayingRoom(new CopyOnWriteArrayList<>());
        }
        club.getClubInfo().getPlayingRoom().add(roomInstance);
    }

    private String getDateStr(long time) {
        long day = 1000L * 60 * 60 * 24;
        return "";
    }


    /**
     * 获得空闲的玩家
     *
     * @param msgKey
     * @param clubId
     * @return
     */
    public int getFreeUser(KafkaMsgKey msgKey, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        List<ClubMember> list = new ArrayList<>();
        club.getClubInfo().getMember().values().forEach(clubMember -> {
            String gateId = RedisManager.getUserRedisService().getGateId(clubMember.getUserId());
            boolean online = gateId != null;
            if (online) {
                boolean isInRoom = RedisManager.getUserRedisService().getRoomId(clubMember.getUserId()) != null;
                if (!isInRoom) {
                    list.add(clubMember);
                }
            }
        });
        sendMsg(msgKey, new ResponseVo("clubService", "getFreeUser", list));

        return 0;
    }

    /**
     * 邀请其他玩家
     *
     * @param msgKey
     * @param clubId
     * @param roomId
     * @param inviteUser
     * @return
     */
    public int invite(KafkaMsgKey msgKey, String clubId, String roomId, String inviteUser, String roomModel, String name) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        Map<String, Object> result = new HashMap<>();
        result.put("clubId", clubId);
        result.put("roomId", roomId);
        result.put("inviteUser", inviteUser);
        result.put("roomModelInfo", getRoomModel(club, roomModel));
        result.put("name", name);
        msgProducer.send();
        sendMsg2Player(new ResponseVo("clubService", "inviteUser", result), Long.valueOf(inviteUser));

        sendMsg(msgKey, new ResponseVo("clubService", "invite", "ok"));
        return 0;
    }

    /**
     * 获得俱乐部战绩
     *
     * @param msgKey
     * @param userId
     * @param clubId
     * @return
     */
    public int getClubRecord(KafkaMsgKey msgKey, long userId, String clubId) {
        String unionId = clubId + "|" + LocalDate.now().toString();
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }

        ClubRecord clubRecord = clubRecordService.getClubRecordDao().getClubRecordById(unionId);

        sendMsg(msgKey, new ResponseVo("clubService", "getClubRecord", clubRecord));
        return 0;
    }

    public int getClubRecordByDate(KafkaMsgKey msgKey, long userId, String clubId, String date){
        String unionId = clubId + "|" + date;
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        ClubRecord clubRecord = clubRecordService.getClubRecordDao().getClubRecordById(unionId);
        sendMsg(msgKey, new ResponseVo("clubService", "getClubRecordByDate", clubRecord));
        return 0;
    }


    /**
     * 俱乐部房间退钱
     *
     * @param clubId
     * @param clubModelId
     */
    public int clubDrawBack(String clubId, String clubModelId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            RoomModel roomModel = getRoomModel(club, clubModelId);
            if (roomModel != null) {
                int money = roomModel.getMoney();
                synchronized (club.lock) {
                    club.setMoney(club.getMoney() + money);

                    //统计减去消耗
                    String today = LocalDate.now().toString();
                    ClubStatistics clubStatistics = roomModel.getStatisticsMap().get(today);
                    if (clubStatistics != null) {
                        clubStatistics.setConsumeNum(clubStatistics.getConsumeNum() - roomModel.getMoney());
                    }
                }
            }
        }
        return 0;
    }

    public int kickUser(KafkaMsgKey msgKey, long userId, String clubId, long kickUser) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
            return ErrorCode.CLUB_NOT_PRESIDENT;
        }
        clubRemoveMember(club, kickUser);
        sendMsg(msgKey, new ResponseVo("clubService", "kickUser", "ok"));
        return 0;
    }


    public static void sendMsg2Player(Object msg, long userId) {
        String gateId = RedisManager.getUserRedisService().getGateId(userId);
        if (gateId != null) {
            SpringUtil.getBean(MsgProducer.class).send2Partition(IKafaTopic.GATE_TOPIC, Integer.valueOf(gateId), "" + userId, msg);
        }
    }

    /**
     * 拿到roomModel
     *
     * @param club
     * @param roomModelId
     * @return
     */
    public static RoomModel getRoomModel(Club club, String roomModelId) {
        RoomModel roomModel = null;
        for (RoomModel rm : club.getClubInfo().getRoomModels()) {
            if (roomModelId.equals(rm.getId())) {
                roomModel = rm;
            }
        }
        return roomModel;
    }

    /**
     * 初始化俱乐部
     *
     * @param club
     */
    public static void initRoomInstance(Club club) {
        System.out.println("init------------------");

        synchronized (club.lock) {

            //清理房间状态 如果房间已不存在 则去掉roomId (比如逻辑服务器重启)
            List<String> removeList = new ArrayList<>();
            for (Map.Entry<String, RoomInstance> entry : club.getClubInfo().getRoomInstance().entrySet()) {
                if (entry.getValue().getRoomId() != null && RedisManager.getRoomRedisService().getServerId(entry.getValue().getRoomId()) == null) {
                    removeList.add(entry.getKey());
                }
            }
            removeList.forEach(modelKey -> club.getClubInfo().getRoomInstance().remove(modelKey));

            //创建这几个消失的房间
            for (String s : removeList) {
                RoomModel roomModel = getRoomModel(club, s);

                createRoom(club, roomModel);
                //减钱
                int moneyNow = club.getMoney() - roomModel.getMoney();
                club.setMoney(moneyNow);

                //统计
                addStatisticeConsume(club, roomModel.getMoney());

                club.getStatistics().setConsume(club.getStatistics().getConsume() + roomModel.getMoney());

            }


            //创建
            for (RoomModel roomModel : club.getClubInfo().getRoomModels()) {
                //没有这个类型的房间 && 钱够
                RoomInstance roomInstance = club.getClubInfo().getRoomInstance().get(roomModel.getId());
                boolean flag1 = roomInstance == null || roomInstance.getRoomId() == null;
                boolean flag2 = club.getMoney() >= roomModel.getMoney();
                boolean flag3 = !removeList.contains(roomModel.getId());
                if (flag1 && flag2 && flag3) {
                    createRoom(club, roomModel);
                    //减钱
                    int moneyNow = club.getMoney() - roomModel.getMoney();
                    club.setMoney(moneyNow);

                    //统计
                    addStatisticeConsume(club, roomModel.getMoney());

                    club.getStatistics().setConsume(club.getStatistics().getConsume() + roomModel.getMoney());


                }
            }


        }
    }


    /**
     * 创建房间
     *
     * @param club
     * @param roomModel
     */
    private static void createRoom(Club club, RoomModel roomModel) {
        RoomInstance roomInstance = new RoomInstance();
        roomInstance.setRoomModelId(roomModel.getId());
        //放进 房间实例 列表
        club.getClubInfo().getRoomInstance().put(roomInstance.getRoomModelId(), roomInstance);

        //发消息创建房间
        sendMsgForCreateRoom(roomModel.getServiceName(), roomModel.getCreateCommand());
    }


    private static int getServerIdByServiceName(String serviceName) {
        if ("mahjongRoomService".equals(serviceName)) {
            return 0;
        } else if ("pokerRoomService".equals(serviceName)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 向逻辑服务器 发送创建的内部消息
     *
     * @param serviceName
     * @param createCommand
     */
    private static void sendMsgForCreateRoom(String serviceName, String createCommand) {
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        int serverId = getServerIdByServiceName(serviceName);
        KafkaMsgKey msgKey = new KafkaMsgKey();
        msgKey.setUserId(0);

        msgProducer.send2Partition(serviceName, serverId, JsonUtil.toJson(msgKey), createCommand);

    }

    /**
     * 修改房间创建命令
     *
     * @param createCommand
     * @param clubId
     * @param modelId
     * @return
     */
    private String setRoomModelCommand(String createCommand, String clubId, String modelId) {
        Map<String, Object> map = JsonUtil.readValue(createCommand, Map.class);
        Object pa = map.get("params");
        Map<String, Object> room = (Map<String, Object>) pa;
        room.put("clubId", clubId);
        room.put("clubRoomModel", modelId);
        map.put("params", room);
        return JsonUtil.toJson(map);
    }

    public static void main(String[] args) {
//        String s = "{\"service\":\"pokerRoomService\",\"method\":\"createRoom\",\"params\":{\"gameType\":\"2\",\"gameNumber\":\"9\",\"maxMultiple\":\"-1\",\"roomType\":\"2\",\"isAA\":false,\"isJoin\":false}}";
        // setRoomModelCommand(s, "1","2");
        System.out.println(LocalTime.now());
        System.out.println(LocalDate.now().toString());
        List<Integer> list = new ArrayList<>();
        list.add(2, 1);
        System.out.println(list);

    }


    /**
     * 获得俱乐部简要信息
     *
     * @param club
     * @return
     */
    private ClubVo getClubVo_simple(Club club) {
        if (club == null) {
            return new ClubVo();
        }
        ClubVo clubVo = new ClubVo();
        clubVo.setId(club.getId());
        clubVo.setPresident(club.getPresident());
        clubVo.setName(club.getName());
        clubVo.setPresidentName(club.getPresidentName());
        clubVo.setNum(club.getClubInfo().getMember().size());
        clubVo.setMoney(club.getMoney());
        clubVo.setArea(club.getArea());
        clubVo.setPresidentWx(club.getPresidentWx());
        clubVo.setApplyNum(club.getClubInfo().getApplyList().size());
        clubVo.setImage(club.getImage());


        return clubVo;
    }


    /**
     * 获得申请列表
     *
     * @param club
     * @param userId
     * @return
     */
    private ClubMember getApply(Club club, long userId) {
        for (ClubMember apply : club.getClubInfo().getApplyList()) {
            if (apply.getUserId() == userId) {
                return apply;
            }
        }
        return null;
    }

    /**
     * 玩家是否在申请列表里
     *
     * @param club
     * @param userId
     * @return
     */
    private boolean isInApplyList(Club club, long userId) {
        for (ClubMember apply : club.getClubInfo().getApplyList()) {
            if (apply.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送消息
     *
     * @param msgKey
     * @param msg
     */
    private void sendMsg(KafkaMsgKey msgKey, Object msg) {
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(), "" + msgKey.getUserId(), msg);
    }

    /**
     * 俱乐部加成员
     *
     * @param club
     * @param apply
     */
    private void clubAddMember(Club club, ClubMember apply) {
        ClubMember member = new ClubMember();
        member.setUserId(apply.getUserId());
        member.setTime(System.currentTimeMillis());
        member.setImage(apply.getImage());
        member.setName(apply.getName());
        member.setSex(apply.getSex());

        club.getClubInfo().getMember().put("" + apply.getUserId(), member);

        //加到全局列表
        ClubManager.getInstance().userAddClub(apply.getUserId(), club.getId());

    }

    /**
     * 俱乐部加入成员
     *
     * @param club
     * @param userBean
     */
    private void clubAddMember(Club club, UserBean userBean) {
        ClubMember member = new ClubMember();
        member.setUserId(userBean.getId());
        member.setTime(System.currentTimeMillis());
        member.setImage(userBean.getImage());
        member.setName(userBean.getUsername());
        member.setSex(userBean.getSex());

        club.getClubInfo().getMember().put("" + userBean.getId(), member);

        //加到全局列表
        ClubManager.getInstance().userAddClub(userBean.getId(), club.getId());
    }

    /**
     * 俱乐部删除成员
     *
     * @param club
     * @param userId
     */
    private void clubRemoveMember(Club club, long userId) {
        club.getClubInfo().getMember().remove("" + userId);

        //全局列表
        ClubManager.getInstance().userRemoveClub(userId, club.getId());
    }

    public ClubService getClubService() {
        return clubService;
    }

    public GameClubService setClubService(ClubService clubService) {
        this.clubService = clubService;
        return this;
    }
}
