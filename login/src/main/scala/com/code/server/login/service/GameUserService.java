package com.code.server.login.service;


import com.code.server.constant.game.*;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserVo;
import com.code.server.db.Service.*;
import com.code.server.db.model.*;
import com.code.server.db.model.UserRecord;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.rpc.RpcManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.DateUtil;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by win7 on 2017/3/10.
 */

@Service
public class GameUserService {

    @Autowired
    UserRedisService userRedisService;

    @Autowired
    MsgProducer kafkaMsgProducer;

    @Autowired
    UserService userService;

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    GameRecordService gameRecordService;

    @Autowired
    ChargeService chargeService;

    /**
     * 给人充钱
     *
     * @param msgKey
     * @param rechargeUserId 充值玩家ID
     * @param money          充值数量
     * @return
     */
    public int giveOtherMoney(KafkaMsgKey msgKey, Long rechargeUserId, double money) {

        //充值玩家id
        Long userid = msgKey.getUserId();
        UserBean userBeanOwn = userRedisService.getUserBean(userid);
        if (userBeanOwn == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        //充值玩家钱数
        double userMoney = userRedisService.getUserMoney(userid);
        //被充值玩家余额
        double rechargeUserMoney = userRedisService.getUserMoney(rechargeUserId);
        //被充值玩家对象
        UserBean userBean = userRedisService.getUserBean(rechargeUserId);

        if (userMoney - money >= 0 && money > 0) {
            if (userBean != null) {
                userRedisService.addUserMoney(rechargeUserId, money);
                //减掉充值玩家相应的钱数
                userRedisService.addUserMoney(userid, -money);
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("result", "success");
                ResponseVo vo = new ResponseVo("userService", "giveOtherMoney", results);
                sendMsg(msgKey, vo);
            } else {

                User accepter = userService.getUserByUserId(rechargeUserId);
                if (accepter == null) {
                    ResponseVo vo = new ResponseVo("userService", "giveOtherMoney", ErrorCode.NOT_HAVE_THIS_ACCEPTER);
                    sendMsg(msgKey, vo);
                } else {
                    accepter.setMoney(accepter.getMoney() + money);
                    userService.save(accepter);
                    //减掉充值玩家相应的钱数
                    userRedisService.setUserMoney(userid, userMoney - money);
                    Map<String, Object> results = new HashMap<String, Object>();
                    results.put("result", "success");
                    ResponseVo vo = new ResponseVo("userService", "giveOtherMoney", results);
                    sendMsg(msgKey, vo);
                }
            }
        } else {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }
        return 0;
    }


    public static UserBean user2userBean(User user) {
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
//        userBean.setId(user.getId());
//        userBean.setUsername(user.getUsername());
//        userBean.setImage(user.getImage());
//        userBean.setAccount(user.getAccount());
//        userBean.setPassword(user.getPassword());
//        userBean.setIpConfig(user.getIpConfig());
//        userBean.setMoney(user.getMoney());
//        userBean.setVip(user.getVip());
//        userBean.setUuid(user.getUuid());
//        userBean.setOpenId(user.getOpenId());
//        userBean.setSex(user.getSex());
//        userBean.setUserInfo(user.getUserInfo());
//        userBean.setReferee(user.getReferee());

        return userBean;
    }

    public static User userBean2User(UserBean userBean) {
        User user = new User();

        BeanUtils.copyProperties(userBean, user);
//        user.setId(userBean.getId());
//        user.setUsername(userBean.getUsername());
//        user.setImage(userBean.getImage());
//        user.setAccount(userBean.getAccount());
//        user.setPassword(userBean.getPassword());
//        user.setIpConfig(userBean.getIpConfig());
//        user.setMoney(userBean.getMoney());
//        user.setVip(userBean.getVip());
//        user.setUuid(userBean.getUuid());
//        user.setOpenId(userBean.getOpenId());
//        user.setSex(userBean.getSex());
//        user.setUserInfo(userBean.getUserInfo());
//        user.setGold(userBean.getGold());
//        user.setReferee(userBean.getReferee());

        return user;
    }


    public static void saveUserBean(long userId) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        User user = GameUserService.userBean2User(userBean);
        UserService userService = SpringUtil.getBean(UserService.class);
        userService.getUserDao().save(user);
    }
    /**
     * 获取昵称
     *
     * @param msgKey
     * @return
     */
    public int getNickNamePlayer(KafkaMsgKey msgKey) {
        UserBean userBean = userRedisService.getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;

        }
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            results.put("nickname", userBean.getUsername());
        } catch (Exception e) {
            ResponseVo vo = new ResponseVo("userService", "getNickNamePlayer", ErrorCode.NOT_HAVE_THIS_ACCEPTER);
            sendMsg(msgKey, vo);
        }
        ResponseVo vo = new ResponseVo("userService", "getNickNamePlayer", results);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int getServerInfo(KafkaMsgKey msgKey) {
        sendMsg(msgKey, new ResponseVo("userService", "getServerInfo", ServerManager.constant));
        return 0;
    }

    public int reportingCoord(KafkaMsgKey msgKey, String coord) {
        long userId = msgKey.getUserId();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setCoord(coord);
            RedisManager.getUserRedisService().setUserBean(userBean);
        }
        sendMsg(msgKey, new ResponseVo("userService", "reportingCoord", 0));
        return 0;
    }

    /**
     * 获得同一房间的所有人坐标
     *
     * @param msgKey
     * @return
     */
    public int getCoords(KafkaMsgKey msgKey) {
        long userId = msgKey.getUserId();
        String roomId = RedisManager.getUserRedisService().getRoomId(userId);
        if (roomId == null) {
            return ErrorCode.CAN_NOT_NO_ROOM;
        }
        Set<Long> users = RedisManager.getRoomRedisService().getUsers(roomId);
        Map<Long, Object> result = RedisManager.getUserRedisService().getUserBeans(users).stream().collect(Collectors.toMap(UserBean::getId, UserBean::getCoord));
        sendMsg(msgKey, new ResponseVo("userService", "getCoords", result));
        return 0;
    }

    /**
     * 查询战绩
     *
     * @param msgKey
     * @return
     */
    public int getUserRecodeByUserId(KafkaMsgKey msgKey, String roomType) {
        UserRecord userRecord = userRecordService.getUserRecordByUserId(msgKey.getUserId());
        List<RoomRecord> roomRecordList = new ArrayList<>();
        if (userRecord != null && userRecord.getRecord() != null && userRecord.getRecord().getRoomRecords().containsKey(roomType)) {
            roomRecordList.addAll(userRecord.getRecord().getRoomRecords().get(roomType));
        }
        ResponseVo vo = new ResponseVo("userService", "getUserRecodeByUserId", roomRecordList);
        sendMsg(msgKey, vo);
        return 0;
    }


    private void sendMsg(KafkaMsgKey msgKey, Object msg) {
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(), "" + msgKey.getUserId(), msg);
    }


    public int getUserMessage(KafkaMsgKey msgKey) {
        UserBean userBean = userRedisService.getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        String roomId = userRedisService.getRoomId(msgKey.getUserId());
        UserVo userVo = userBean.toVo();
        userVo.setRoomId(roomId);
        ResponseVo vo = new ResponseVo("userService", "getUserMessage", userVo);
        sendMsg(msgKey, vo);

        return 0;
    }


    //
//    public int getRecord(Player player,int type) {
//        User user = player.getUser();
//        user.getRecord().getRoomRecords().get(type);
//
//
//        return 0;
//    }
//
//
    public int bindReferrer(KafkaMsgKey msgKey, int referrerId) {

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        if (referrerId <= 0 || userBean.getReferee() != 0) {
            return ErrorCode.CAN_NOT_BING_REFERRER;
        }
        boolean isExist = RpcManager.getInstance().referrerIsExist(referrerId);
        if (!isExist) {
            return ErrorCode.REFERRER_NOT_EXIST;
        }
        double money = 100;
        userBean.setReferee(referrerId);
        RedisManager.getUserRedisService().updateUserBean(userBean.getId(), userBean);
        RedisManager.getUserRedisService().addUserMoney(msgKey.getUserId(), money);


        ResponseVo vo = new ResponseVo("userService", "bindReferrer", 0);
        sendMsg(msgKey, vo);

        //充值记录
        Charge charge = new Charge();
        charge.setRecharge_source("" + IChargeType.BIND_REFERRER);
        charge.setUserid(userBean.getId());
        charge.setUsername(userBean.getUsername());
        charge.setStatus(1);
        charge.setCreatetime(new Date());
        charge.setMoney_point(money);
        charge.setMoney(money);
        charge.setOrderId("" + IdWorker.getDefaultInstance().nextId());
        SpringUtil.getBean(ChargeService.class).save(charge);


        return 0;
    }

    public int getReplay(KafkaMsgKey msgKey, long id) {
        Replay r = SpringUtil.getBean(ReplayService.class).getReplay(id);
        if (r == null) {
            return ErrorCode.REPLAY_NOT_EXIST;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", r.getData());
        ResponseVo vo = new ResponseVo("userService", "getReplay", r.getData());
        sendMsg(msgKey, vo);
        return 0;
    }

    public int setReplay(KafkaMsgKey msgKey, long id) {
        ReplayService rs = SpringUtil.getBean(ReplayService.class);
//        boolean isSuccess = rs.decReplayCount(id);
//        if (!isSuccess) {
//            return ErrorCode.REPLAY_NOT_EXIST;
//        }
        rs.decReplayCount(id);
        ResponseVo vo = new ResponseVo("userService", "setReplay", 0);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int shareWX(KafkaMsgKey msgKey, String game) {

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        long lastShareTime = userBean.getUserInfo().getLastShareTime();
        long now = System.currentTimeMillis();

        if (DateUtil.isSameDate(lastShareTime, now)) {
            return ErrorCode.CANNOT_SHARE;
        }

        double money = getShareMoney(game);
        int shareCount = userBean.getUserInfo().getShareWXCount();

        //加钱
        double nowMoney = RedisManager.getUserRedisService().addUserMoney(msgKey.getUserId(), money);
        userBean.setMoney(nowMoney);
        //分享时间
        userBean.getUserInfo().setLastShareTime(now);
        //分享次数
        userBean.getUserInfo().setShareWXCount(shareCount + 1);
        //保存
        RedisManager.getUserRedisService().updateUserBean(userBean.getId(), userBean);

        //分享记录
        Charge charge = new Charge();
        charge.setOrderId("" + IdWorker.getDefaultInstance().nextId());
        charge.setUsername(userBean.getUsername());
        charge.setMoney(money);
        charge.setMoney_point(money);
        charge.setCreatetime(new Date());
        charge.setCallbacktime(new Date());
        charge.setRecharge_source("" + IChargeType.SHARE);
        charge.setStatus(1);
        chargeService.save(charge);

        ResponseVo vo = new ResponseVo("userService", "shareWX", 0);
        sendMsg(msgKey, vo);
        return 0;
    }

    /**
     * 分享获得钱数
     *
     * @param projectName
     * @return
     */
    private static double getShareMoney(String projectName) {
        //todo 从constant里读取
//        ServerManager.constant.getShareMoney();
        switch (projectName) {
            case IProjectName.JINGNAN:
                return 2;
            case IProjectName.LONGQI:
                return 10;
            case IProjectName.LAOTIE:
                return 2;
            case IProjectName.TONGCHENG:
                return 1;
            case IProjectName.BAIXING:
                return 1;
            case IProjectName.CHUANQI:
                return 1;
            case IProjectName.HUANLE:
                return 1;
            case IProjectName.DINGSHENG:
                return 0;
            case IProjectName.ACE:
                return 0;
            case IProjectName.FANSHI:
                return 3;
            default:
                return 1;
        }

    }

    public int getPrepareRoom(KafkaMsgKey msgKey) {
        long userId = msgKey.getUserId();

        Map<String, PrepareRoom> rooms = RedisManager.getUserRedisService().getPerpareRoom(userId);
        Map<Object, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        if (rooms != null) {
            for (Map.Entry<String, PrepareRoom> entry : rooms.entrySet()) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("room", entry.getValue());
                temp.put("user", RedisManager.getUserRedisService().getUserBeans(RedisManager.getRoomRedisService().getUsers(entry.getKey())));

                list.add(temp);
            }
        }
        result.put("result", list);
        ResponseVo vo = new ResponseVo("userService", "getPrepareRoom", result);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int kickUser(KafkaMsgKey msgKey, JsonNode params, JsonNode allParams) {
        //获得该room所在的逻辑服务器id
        String roomId = params.path("roomId").asText();
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if (serverId == null) {
            return ErrorCode.NO_THIS_ROOM;
        }

        kafkaMsgProducer.send2Partition(IKafaTopic.SERVER_SERVER_TOPIC, Integer.valueOf(serverId), msgKey, allParams.asText());
        return 0;
    }

    public int getRoomInfo(KafkaMsgKey msgKey, JsonNode params, JsonNode allParams) {
        //获得该room所在的逻辑服务器id
        String roomId = params.path("roomId").asText();
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if (serverId == null) {
            return ErrorCode.NO_THIS_ROOM;
        }
        kafkaMsgProducer.send2Partition(IKafaTopic.SERVER_SERVER_TOPIC, Integer.valueOf(serverId), msgKey, allParams);
        return 0;

    }

    /**
     * 猜汽车
     *
     * @param msgKey
     * @return
     */
    public int guessCarUp2Agent(KafkaMsgKey msgKey) {

        final int needMoney = 1000;
        long userId = msgKey.getUserId();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);

        if (userBean.getVip() != 0) {
            return ErrorCode.CAN_NOT_BING_REFERRER;
        }
        //减钻石
        if (userBean.getMoney() < needMoney) {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }
        userBean.setVip(Integer.valueOf("" + userId));
        RedisManager.getUserRedisService().updateUserBean(userId, userBean);
        RedisManager.getUserRedisService().addUserMoney(userId, -needMoney);

        ResponseVo vo = new ResponseVo("userService", "guessCarUp2Agent", userId);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int guessCarBindReferrer(KafkaMsgKey msgKey, int referrerId) {

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        if (referrerId <= 0 || userBean.getReferee() != 0) {
            return ErrorCode.CAN_NOT_BING_REFERRER;
        }
        if (referrerId == msgKey.getUserId()) {
            return ErrorCode.CAN_NOT_BING_REFERRER;
        }

        AgentUserService agentUserService = SpringUtil.getBean(AgentUserService.class);

        AgentUser agentUser = agentUserService.getAgentUserDao().findAgentUserByInvite_code("" + referrerId);

        if (agentUser == null) {
            return ErrorCode.REFERRER_NOT_EXIST;
        }
        if (agentUser.getInvite_code() == null || "".equals(agentUser.getInvite_code())) {
            return ErrorCode.REFERRER_NOT_EXIST;
        }

//        boolean isExist = RpcManager.getInstance().referrerIsExist(referrerId);
//        if (!isExist) {
//            return ErrorCode.REFERRER_NOT_EXIST;
//        }
//        UserBean referrUser = RedisManager.getUserRedisService().getUserBean(referrerId);
//        if (referrUser != null) {
//            if(referrUser.getVip() == 0){
//                return ErrorCode.REFERRER_NOT_EXIST;
//            }
//        }else{
//            User user = userService.getUserByUserId(referrerId);
//            if(user == null || user.getVip()== 0) return ErrorCode.REFERRER_NOT_EXIST;
//        }



        userBean.setReferee(referrerId);
        RedisManager.getUserRedisService().updateUserBean(userBean.getId(), userBean);

        RedisManager.getUserRedisService().addUserMoney(msgKey.getUserId(), SpringUtil.getBean(ServerConfig.class).getBindRefereeReward());


        ResponseVo vo = new ResponseVo("userService", "guessCarBind", 0);
        sendMsg(msgKey, vo);
        return 0;
    }


    public int accessCode(KafkaMsgKey msgKey, String accessCode) {
        if (accessCode == null || "".equals(accessCode)) {
            return ErrorCode.NO_ACCESSCODE;
        }
        String ac = ServerManager.constant.getAccessCode();
        if (ac == null || !accessCode.equals(ac)) {
            return ErrorCode.NO_ACCESSCODE;
        }
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(msgKey.getUserId());
        userBean.getUserInfo().setInputAccessCode(true);
//        RedisManager.getUserRedisService().setUserBean(userBean);
        RedisManager.getUserRedisService().updateUserBean(userBean.getId(), userBean);
        ResponseVo vo = new ResponseVo("userService", "accessCode", 0);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int getUserSimpleInfo(KafkaMsgKey msgKey, long userId) {

        Map<String, Object> result = new HashMap<>();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            User user = userService.getUserByUserId(userId);
            if (user == null) {
                result.put("isHas", false);
            }else{
                result.put("name", user.getUsername());
                result.put("image", user.getImage());
            }
        }else{
            result.put("name", userBean.getUsername());
            result.put("image", userBean.getImage());
        }
        ResponseVo vo = new ResponseVo("userService", "getUserSimpleInfo", result);
        sendMsg(msgKey, vo);
        return 0;
    }

    public int getRecordsByRoom(KafkaMsgKey msgKey, long roomUid) {
        List<com.code.server.db.model.GameRecord> list = gameRecordService.gameRecordDao.getGameRecordByUuid(roomUid);

        Map<Object, Object> result = new HashMap<>();

        result.put("result", list);
        ResponseVo vo = new ResponseVo("userService", "getRecordsByRoom", result);
        sendMsg(msgKey, vo);
        return 0;
    }

    public static void main(String[] args) {
        String s = "家用饮水机";
        String s2 = "";
        try {
            String ss = URLDecoder.decode(s, "utf-8");
            System.out.println(ss);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
