package com.code.server.login.service;

import com.code.server.constant.club.*;
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
import com.code.server.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.code.server.constant.game.IGameConstant.CLUB_MODE_USER_PAY;

/**
 * Created by sunxianping on 2018/1/15.
 */
@Service("gameClubService")
public class GameClubService {

    @Autowired
    ClubService clubService;

    @Autowired
    MsgProducer kafkaMsgProducer;

    @Autowired
    ClubRecordService clubRecordService;

    @Autowired
    ClubChargeService clubChargeService;

    @Autowired
    UserService userService;


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
            if (club != null) {
                club.getClubInfo().getMember().get("" + userId).setLastLoginTime("" + System.currentTimeMillis());
                list.add(getClubVo_simple(club));
            }
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
        clubVo.getPartner().addAll(club.getClubInfo().getPartner());

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
        vo.setCreateCommand(roomInstance.getCreateCommand());
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

        boolean loadClubMoney = SpringUtil.getBean(ServerConfig.class).getHasClubMoney() == 1;
        if (loadClubMoney) {
            ClubManager.getInstance().clubMoneyWrite2Redis(club);
        }
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

        Map<String, Object> r = new HashMap<>();
        r.put("clubId", club.getId());

        sendMsg(new ResponseVo("clubService", "dissolvePush", r), getClubUser(club));

        return 0;
    }


    public List<Long> getClubUser(Club club) {
        List<Long> users = new ArrayList<>();
        club.getClubInfo().getMember().forEach((id,clubMember)->{
            users.add(clubMember.getUserId());
        });
        return users;
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
        } else {
            //加入申请列表
            if (isInApplyList(club, userId)) {
                return ErrorCode.CLUB_CANNOT_JOIN;
            }
            club.getClubInfo().getApplyList().add(apply);
        }

        Map<String, Object> result = new HashMap<>();
        sendMsg(msgKey, new ResponseVo("clubService", "joinClub", result));

        //给管理员推送 申请列表
        List<Long> adminList = new ArrayList<>();
        adminList.add(club.getPresident());
        adminList.addAll(club.getClubInfo().getAdmin());
        Map<String, Object> r = new HashMap<>();
        r.put("clubId", club.getId());
        adminList.forEach(uid -> sendMsg2Player(new ResponseVo("clubService", "joinClubPush2Admin", r), uid));
//        sendMsg2Player(new ResponseVo("clubService", "joinClubPush2Admin",0), );

        return 0;
    }


    public int partnerRecommend(KafkaMsgKey msgKey, long partnerId, String clubId, long recommendId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        //自己加入了几个俱乐部
        List<String> joinList = ClubManager.getInstance().getUserClubs(recommendId);
        int limit = SpringUtil.getBean(ServerConfig.class).getClubJoinLimit();
        if (joinList.size() >= limit) {
            return ErrorCode.CLUB_CANNOT_NUM;
        }
        if (joinList.contains(clubId)) {
            return ErrorCode.CLUB_CANNOT_JOIN;
        }

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(recommendId);
        String name = "";
        String image = "";
        int sex = 0;
        if (userBean != null) {

            name = userBean.getUsername();
            image = userBean.getImage();
            sex = userBean.getSex();
        } else {
            User user = userService.getUserByUserId(recommendId);
            if (user == null) {
                return ErrorCode.CLUB_CANNOT_JOIN;
            }
            name = user.getUsername();
            image = user.getImage();
            sex = user.getSex();
        }
        ClubMember apply = new ClubMember().setTime(System.currentTimeMillis()).setUserId(recommendId).setMark("" + partnerId)
                .setName(name).setImage(image).setSex(sex).setReferrer(partnerId);

        //自动加入

        //加入申请列表
        if (isInApplyList(club, recommendId)) {
            return ErrorCode.CLUB_CANNOT_JOIN;
        }
        club.getClubInfo().getApplyList().add(apply);


        Map<String, Object> result = new HashMap<>();
        sendMsg(msgKey, new ResponseVo("clubService", "partnerRecommend", result));

        //给管理员推送 申请列表
        List<Long> adminList = new ArrayList<>();
        adminList.add(club.getPresident());
        adminList.addAll(club.getClubInfo().getAdmin());
        Map<String, Object> r = new HashMap<>();
        r.put("clubId", club.getId());
        adminList.forEach(uid -> sendMsg2Player(new ResponseVo("clubService", "joinClubPush2Admin", r), uid));
//        sendMsg2Player(new ResponseVo("clubService", "joinClubPush2Admin",0), );
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
        if (isAgree && !club.getClubInfo().getMember().containsKey("" + agreeId)) {
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

    public int setFloor(KafkaMsgKey msgKey, long userId, String clubId, int floor, String desc) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        for (int i = 0; i < 5; i++) {
            if (club.getClubInfo().getFloorDesc().size() > i) {

//                if(club.getClubInfo().getFloorDesc().get(i).equals())
//                club.getClubInfo().getFloorDesc().add("");
            } else {
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
        } else {
            club.getClubInfo().getAdmin().remove(adminUser);
        }
        sendMsg(msgKey, new ResponseVo("clubService", "setAdmin", "ok"));

        return 0;
    }

    public int addUser(KafkaMsgKey msgKey, String clubId, long userId, long referee) {
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

            clubAddMember(club, userBean);
        } else {
            User user = userService.getUserByUserId(userId);

            ClubMember clubMember = new ClubMember();
            clubMember.setSex(user.getSex()).setImage(user.getImage()).setName(user.getUsername()).setUserId(user.getId()).setTime(System.currentTimeMillis());
            clubAddMember(club, clubMember);

        }
        //推荐人不为0
        if (referee != 0) {
            ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
            clubMember.setReferrer(referee);
        }
        sendMsg(msgKey, new ResponseVo("clubService", "addUser", "ok"));
        return 0;
    }


    public int removeFloor(KafkaMsgKey msgKey, String clubId, long userId, int floor) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (club.getClubInfo().getRoomModels().size() < floor * 8) {
            return ErrorCode.CLUB_PARAM_ERROR;
        }
        for (int i = 0; i < 8; i++) {
            club.getClubInfo().getRoomModels().remove(floor * 8);
        }
        if (club.getClubInfo().getFloorDesc().size() > floor) {

            club.getClubInfo().getFloorDesc().remove(floor);
        }
        sendMsg(msgKey, new ResponseVo("clubService", "removeFloor", "ok"));
        return 0;
    }


    /**
     * club join room 推送
     *
     * @param clubId
     * @param userId
     * @param roomModelId
     */
    public void clubJoinRoom(String clubId, long userId, String roomModelId, String roomId) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getClubPushUserRoomInfo() == 0) return;

        Club club = ClubManager.getInstance().getClubById(clubId);
        List<Long> users = new ArrayList<>();
        if (club != null) {
            club.getClubInfo().getMember().forEach((id, ClubMember) -> users.add(Long.valueOf(id)));
        }
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        r.put("roomModelId", roomModelId);
        r.put("clubId", clubId);
        ResponseVo responseVo = new ResponseVo("clubService", "clubJoinRoom", r);
        users.forEach(uid -> sendMsg2Player(responseVo, uid));


        if (serverConfig.getSend_lq_http() == 1) {
            List<Long> us = new ArrayList<>();
            us.addAll(RedisManager.getRoomRedisService().getUsers(roomId));
            send_Lq_start(club, roomId, roomModelId, us, 0);
        }
    }


    /**
     * 退出房间推送
     *
     * @param clubId
     * @param userId
     * @param roomModelId
     */
    public void clubQuitRoom(String clubId, long userId, String roomModelId, String roomId) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getClubPushUserRoomInfo() == 0) return;

        Club club = ClubManager.getInstance().getClubById(clubId);
        List<Long> users = new ArrayList<>();
        if (club != null) {
            club.getClubInfo().getMember().forEach((id, ClubMember) -> users.add(Long.valueOf(id)));
        }
        Map<String, Object> r = new HashMap<>();
        r.put("userId", userId);
        r.put("roomModelId", roomModelId);
        r.put("clubId", clubId);
        ResponseVo responseVo = new ResponseVo("clubService", "clubQuitRoom", r);
        users.forEach(uid -> sendMsg2Player(responseVo, uid));

        if (serverConfig.getSend_lq_http() == 1) {
            List<Long> us = new ArrayList<>();
            us.addAll(RedisManager.getRoomRedisService().getUsers(roomId));
            send_Lq_start(club, roomId, roomModelId, us, 0);
        }
        System.out.println("user: " + userId + " quitClub = " + clubId);
    }


    public void removeClubInstance(String clubId, String roomModelId, String roomId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        RoomInstance roomInstance = club.getClubInfo().getRoomInstance().get(roomModelId);
        if (roomInstance != null && roomInstance.getRoomId().equals(roomId)) {
            club.getClubInfo().getRoomInstance().remove(roomModelId);
        }
    }


    public int setAutoJoin(KafkaMsgKey msgKey, String clubId, long userId, boolean auto) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        club.getClubInfo().setAutoJoin(auto);
        sendMsg(msgKey, new ResponseVo("clubService", "setAutoJoin", "ok"));
        return 0;
    }

    /**
     * 初始化数据 懒加载
     */
    public void initRoomData() {
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
        if (set.size() != 8) {
            System.out.println("set =====================   " + set);
        }


        //实例化房间
        initRoomInstance(club);

        RoomModel roomModel = club.getClubInfo().getRoomModels().get(club.getClubInfo().getRoomModels().size() - 1);
        sendMsg(msgKey, new ResponseVo("clubService", "createRoomModel", roomModel));
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
            send_Lq_start(club, roomId, clubModelId, us, 0);
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
    public int cludGameStart(String clubId, String clubModelId, List<Long> users, String roomId, int gameNumber) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            synchronized (club.lock) {
                RoomInstance roomInstance = club.getClubInfo().getRoomInstance().get(clubModelId);
                if (roomInstance.getRoomId().equals(roomId)) {

                    //加入已开房间
                    addPlayingRoom(club, roomInstance);
                    //删除一个房间
                    club.getClubInfo().getRoomInstance().remove(clubModelId);
                    //统计
                }

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

//                        ClubStatistics clubStatistics = clubMember.getStatistics().getOrDefault(date, new ClubStatistics());
//                        club.getStatistics().getStatistics().put(date, clubStatistics);
//                        clubStatistics.setOpenNum(clubStatistics.getOpenNum() + 1);
//                        clubMember.getStatistics().remove(removeDate);
                    }
                }
            }
            initRoomInstance(club);

            //龙七 发送游戏开始
            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            if (serverConfig.getSend_lq_http() == 1 && gameNumber == 1) {
                send_Lq_start(club, roomId, clubModelId, users, 2);
            }
        }
        return 0;
    }


    private static void send_Lq_start(Club club, String roomId, String clubRoomModel, List<Long> users, int roomStatus) {
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
            for (long userId : users) {
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


            String url1 = "http://long7.l7jqr.com/RoomResult_club_info.php?strContext=" + json;

            try {
                URL url = new URL(url1);
                URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                System.out.println(url);
                System.out.println(uri);
                HttpGet request = new HttpGet(uri);
                request.setConfig(requestConfig);
                try {
                    HttpResponse httpResponse = httpClient.execute(request);
                    System.out.println(httpResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private static ClubStatistics getClubStatistics(Club club) {
        String date = LocalDate.now().toString();

        System.out.println("club = " + club);
        System.out.println("date = " + date);
        ClubStatistics clubStatistics = club.getStatistics().getStatistics().get(date);
        System.out.println("statistis = " + clubStatistics);
        if (clubStatistics == null) {
            System.out.println("clubStatistics is null =============== ");
            clubStatistics = new ClubStatistics();
            club.getStatistics().getStatistics().put(date, clubStatistics);
        }
        return clubStatistics;
//        return club.getStatistics().getStatistics().get(date);
    }

    private void addStatisticeOpenNum(Club club, int num) {
        ClubStatistics clubStatistics = getClubStatistics(club);
        System.out.println("之前的局数 " + clubStatistics.getOpenNum());
        clubStatistics.setOpenNum(clubStatistics.getOpenNum() + num);
        System.out.println("之后的局数 " + clubStatistics.getOpenNum());
    }

    private static void addStatisticeConsume(Club club, int num) {
        ClubStatistics clubStatistics = getClubStatistics(club);
        System.out.println("clubStatistics " + clubStatistics);
        System.out.println("之前的消耗 " + clubStatistics.getConsumeNum());
        clubStatistics.setConsumeNum(clubStatistics.getConsumeNum() + num);
        System.out.println("之后的消耗 " + clubStatistics.getConsumeNum());

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
//        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        Map<String, Object> result = new HashMap<>();
        result.put("clubId", clubId);
        result.put("roomId", roomId);
        result.put("inviteUser", inviteUser);
        result.put("roomModelInfo", getRoomModel(club, roomModel));
        result.put("name", name);
//        msgProducer.send();
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

//        if (club.getPresident() != userId && !club.getClubInfo().getAdmin().contains(userId)) {
//            return ErrorCode.CLUB_NOT_PRESIDENT;
//        }

        ClubRecord clubRecord = clubRecordService.getClubRecordDao().getClubRecordById(unionId);

        sendMsg(msgKey, new ResponseVo("clubService", "getClubRecord", clubRecord));
        return 0;
    }

    public int getClubRecordByDate(KafkaMsgKey msgKey, long userId, String clubId, String date) {
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
    public int clubDrawBack(String clubId, String clubModelId,String roomId,int clubMode) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            RoomModel roomModel = getRoomModel(club, clubModelId);
            if (roomModel != null) {
                int money = roomModel.getMoney();
                synchronized (club.lock) {
                    club.setMoney(club.getMoney() + money);

                    //统计减去消耗
                    String today = LocalDate.now().toString();
                    //每个玩法的统计
                    ClubStatistics roomModelClubStatistics = roomModel.getStatisticsMap().get(today);
                    if (roomModelClubStatistics != null) {
                        roomModelClubStatistics.setConsumeNum(roomModelClubStatistics.getConsumeNum() - roomModel.getMoney());
                        roomModelClubStatistics.setOpenNum(roomModelClubStatistics.getOpenNum() - 1);
                    }
                    //俱乐部的统计
                    ClubStatistics clubStatistics = getClubStatistics(club);
                    if (clubStatistics != null) {
                        //不是玩家自己付
                        if (!Utils.isHasMode(CLUB_MODE_USER_PAY, clubMode)) {
                            addStatisticeConsume(club, -roomModel.getMoney());
                            club.getStatistics().setConsume(club.getStatistics().getConsume() - roomModel.getMoney());
                        }
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
        System.out.println("kickUser : " + " src = " + userId + " des = " + kickUser);

        Map<String, Object> map = new HashMap<>();
        map.put("clubId", clubId);
        sendMsg(new ResponseVo("clubService", "pushKickUser", map), kickUser);
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


    public static void initRoomInstanceStatic(Club club) {

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

                if (roomModel == null) {
                    continue;
                }
                createRoom(club, roomModel);

                //不要统计
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
                    //减钱 玩家花钱的话 消耗为0
                    int needMoney = isUserSpendMoney(roomModel)?0:roomModel.getMoney();
                    int moneyNow = club.getMoney() - needMoney;
                    club.setMoney(moneyNow);

                    //统计
                    addStatisticeConsume(club, needMoney);

                    club.getStatistics().setConsume(club.getStatistics().getConsume() + needMoney);
                }
            }
        }
    }

    public static boolean isUserSpendMoney(RoomModel roomModel) {
        JsonNode jsonNode = JsonUtil.readTree(roomModel.getCreateCommand());
        JsonNode paramNode = jsonNode.path("params");
        ObjectNode objectNode = (ObjectNode) paramNode;
        int clubMode = objectNode.path("clubMode").asInt(0);
        return Utils.isHasMode(CLUB_MODE_USER_PAY, clubMode);
    }

    /**
     * 初始化俱乐部
     *
     * @param club
     */
    public void initRoomInstance(Club club) {
        System.out.println("init------------------");

        initRoomInstanceStatic(club);

    }

    /**
     * 创建实例  只有鱼虾蟹用到
     *
     * @param clubId
     * @param userId
     * @param clubModelId
     * @return
     */
    public int createInstance(KafkaMsgKey msgKey, String clubId, long userId, String clubModelId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        RoomModel roomModel = getRoomModel(club, clubModelId);


        if (getUserMoney(club.getPresident()) < roomModel.getMoney()) {
            return ErrorCode.CLUB_CANNOT_MONEY;
        }

        RoomInstance roomInstance = new RoomInstance();
        roomInstance.setMoney(roomModel.getMoney());
        roomInstance.setRoomModelId(roomModel.getId());

        long newId = IdWorker.getDefaultInstance().nextId();
        JsonNode jsonNode = JsonUtil.readTree(roomModel.getCreateCommand());
        JsonNode paramNode = jsonNode.path("params");
        ObjectNode objectNode = (ObjectNode) paramNode;
        objectNode.put("clubRoomModel", newId);
        //放进 房间实例 列表
        club.getClubInfo().getRoomInstance().put("" + newId, roomInstance);
        roomInstance.setCreateCommand(jsonNode.toString());
        System.out.println(jsonNode.toString());

        //发消息创建房间
        sendMsgForCreateRoom(roomModel.getServiceName(), jsonNode.toString());

        //减掉钱
//        club.setMoney(club.getMoney() - roomModel.getMoney());


        addPresidentMoney(club.getPresident(), -roomModel.getMoney());


        sendMsg(msgKey, new ResponseVo("clubService", "createInstance", "ok"));

        sendMsg( new ResponseVo("clubService", "createInstancePush", "ok"), getClubUser(club) );

        return 0;
    }


    protected void addPresidentMoney(long userId, double money) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            RedisManager.getUserRedisService().addUserMoney(userId, money);
        }else{
            User user = userService.getUserByUserId(userId);
            user.setMoney(user.getMoney() + money);
            userService.save(user);
        }
    }

    protected double getUserMoney(long userId) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            return userBean.getMoney();
        }else{
            User user = userService.getUserByUserId(userId);
            return user.getMoney();
        }
    }

    /**
     * 后的某合伙人的玩家
     *
     * @param msgKey
     * @param clubId
     * @param userId
     * @param partnerId
     * @return
     */
    public int getUserByPartner(KafkaMsgKey msgKey, String clubId, long userId, long partnerId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        List<ClubMember> result = new ArrayList<>();
        club.getClubInfo().getMember().values().forEach(clubMember -> {
            if (partnerId == 0 || clubMember.getReferrer() == partnerId) {
                clubMember.setMoney(RedisManager.getClubRedisService().getClubUserMoney(clubId, clubMember.getUserId()));
                result.add(clubMember);
            }
        });
        sendMsg(msgKey, new ResponseVo("clubService", "getUserByPartner", result));
        return 0;
    }


    public int getPartner(KafkaMsgKey msgKey, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        List<ClubMember> result = new ArrayList<>();
        club.getClubInfo().getMember().values().forEach(clubMember -> {
            if (club.getClubInfo().getPartner().contains(clubMember.getUserId())) {
                clubMember.setMoney(RedisManager.getClubRedisService().getClubUserMoney(clubId, clubMember.getUserId()));
                result.add(clubMember);
            }
        });
        sendMsg(msgKey, new ResponseVo("clubService", "getPartner", result));
        return 0;


    }


    /**
     * 获取admin
     * @param msgKey
     * @param clubId
     * @return
     */
    public int getClubAdmin(KafkaMsgKey msgKey, String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        List<Long> admin = new ArrayList<>();
        admin.add(club.getPresident());
        admin.addAll(club.getClubInfo().getAdmin());

        sendMsg(msgKey, new ResponseVo("clubService", "getClubAdmin", admin));
        return 0;
    }


    /**
     * 设置信用信息
     * @param msgKey
     * @param clubId
     * @param creditMode
     * @param creditMin
     * @param dayingjia
     * @param aa
     * @return
     */
    public int setCreditInfo(KafkaMsgKey msgKey, String clubId, int creditMode, int creditMin, boolean only, int dayingjia, int aa){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        club.getClubInfo().setCreditInfo(new HashMap<>());
        if (club.getClubInfo().getCreditInfo() == null) {
            club.getClubInfo().setCreditInfo(new HashMap<>());
        }
        club.getClubInfo().getCreditInfo().put("creditMode", creditMode);
        club.getClubInfo().getCreditInfo().put("creditMin", creditMin);
        club.getClubInfo().getCreditInfo().put("only", only);
//        club.getClubInfo().getCreditInfo().put("creditMode", creditMode);
//        club.getClubInfo().getCreditInfo().put("creditMode", creditMode);

        sendMsg(msgKey, new ResponseVo("clubService", "setCreditInfo", 0));
        return 0;
    }

    /**
     * 设置信用分
     * @param msgKey
     * @param clubId
     * @param toUser
     * @param score
     * @return
     */
    public int setCreditScore(KafkaMsgKey msgKey, String clubId, long toUser, int score){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        ClubMember clubMember = club.getClubInfo().getMember().get("" + toUser);
        if (clubMember == null) {
            return ErrorCode.CLUB_NO_USER;
        }

        clubMember.getAllStatistics().setAllScore(clubMember.getAllStatistics().getAllScore() + score);
        sendMsg(msgKey, new ResponseVo("clubService", "setCreditScore", 0));
        return 0;
    }




    /**
     * 创建房间
     *
     * @param club
     * @param roomModel
     */
    public static void createRoom(Club club, RoomModel roomModel) {
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
    public String setRoomModelCommand(String createCommand, String clubId, String modelId) {
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
        clubVo.setRoomModelNum(club.getClubInfo().getRoomModels().size());
        clubVo.getAdmin().addAll(club.getClubInfo().getAdmin());


        return clubVo;
    }


    /**
     * 转让俱乐部
     *
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
        if (clubMember == null) {
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
     *
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
        if (!club.getClubInfo().getMember().containsKey("" + partnerId)) {
            return ErrorCode.CLUB_NO_USER;
        }
        if (!club.getClubInfo().getPartner().contains(partnerId)) {
            club.getClubInfo().getPartner().add(partnerId);
        }

        sendMsg(msgKey, new ResponseVo("clubService", "setPartner", "ok"));
        return 0;
    }


    /**
     * 删除代理
     *
     * @param msgKey
     * @param clubId
     * @param userId
     * @param partnerId
     * @return
     */
    public int removePartner(KafkaMsgKey msgKey, String clubId, long userId, long partnerId) {
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

    public int changePartner(KafkaMsgKey msgKey, String clubId, long userId, long newPartner, long changeUser) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + changeUser);
        if (clubMember == null) {
            return ErrorCode.CLUB_NOT_CHANGE_USER;
        }
        clubMember.setReferrer(newPartner);

        sendMsg(msgKey, new ResponseVo("clubService", "changePartner", "ok"));
        return 0;
    }


    /**
     * 上下分
     *
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
        List<UpScoreItem> list = club.getUpScoreInfo().getInfo().getOrDefault(date.toString(), new ArrayList<>());
        int type = num >= 0 ? 1 : 0;
        UpScoreItem upScoreItem = new UpScoreItem().setSrcUserId(userId).setDesUserId(toUser).setNum(num)
                .setTime(System.currentTimeMillis()).setType(type).setName(clubMember.getName());

        //添加一条记录
        list.add(upScoreItem);
        club.getUpScoreInfo().getInfo().put(date.toString(), list);
        club.getUpScoreInfo().getInfo().remove(dateBefore.toString());

//        Map<String, String> rs = new HashMap<>();
//        MsgSender.sendMsg2Player(new ResponseVo("roomService", "pushScoreChange", rs), toUser);

        String rid = RedisManager.getUserRedisService().getRoomId(toUser);
        if (rid != null) {
            KafkaMsgKey msgKey1 = new KafkaMsgKey();

            msgKey1.setRoomId(rid);
            String sid = RedisManager.getRoomRedisService().getServerId(rid);
            int partitionId = Integer.valueOf(sid);
            msgKey1.setPartition(partitionId);
            msgKey1.setUserId(userId);
            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);

            ResponseVo responseVo = new ResponseVo();
            responseVo.setService("roomService");
            responseVo.setMethod("pushScoreChange");
            responseVo.setParams("inner");
            msgProducer.send2Partition("roomService", partitionId, msgKey1, responseVo);
        }

        sendMsg(msgKey, new ResponseVo("clubService", "upScore", "ok"));
        return 0;
    }


    /**
     * 上下分记录
     *
     * @param msgKey
     * @param clubId
     * @param userId
     * @return
     */
    public int getUpScoreLog(KafkaMsgKey msgKey, String clubId, long userId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        List<UpScoreItem> list = new ArrayList<>();
        club.getUpScoreInfo().getInfo().values().forEach(list::addAll);
        sendMsg(msgKey, new ResponseVo("clubService", "getUpScoreLog", list));
        return 0;

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
    void sendMsg(KafkaMsgKey msgKey, Object msg) {
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(), "" + msgKey.getUserId(), msg);
    }

    void sendMsg(Object msg, long userId) {
        String gate = RedisManager.getUserRedisService().getGateId(userId);
        if (gate != null) {
            kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, Integer.valueOf(gate), userId, msg);
        }
    }
    void sendMsg(Object msg, List<Long> users){
        for (long userId : users) {
            String gate = RedisManager.getUserRedisService().getGateId(userId);
            if (gate != null) {
                kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, Integer.valueOf(gate), userId, msg);
            }
        }
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
        member.setReferrer(apply.getReferrer());

        club.getClubInfo().getMember().put("" + apply.getUserId(), member);

        //加到全局列表
        ClubManager.getInstance().userAddClub(apply.getUserId(), club.getId());


        //加入俱乐部的推送
        Map<String, Object> joinResult = new HashMap<>();
        joinResult.put("clubId", club.getId());
        sendMsg2Player(new ResponseVo("clubService", "joinClubPush", joinResult), apply.getUserId());
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
    public void clubRemoveMember(Club club, long userId) {
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
