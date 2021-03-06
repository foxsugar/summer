package com.code.server.login.service;

import com.code.server.constant.club.*;
import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.ChildCost;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.game.Record;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.*;
import com.code.server.db.model.*;
import com.code.server.login.action.LoginAction;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.kafka.MsgSender;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.code.server.login.service.GameClubService.sendMsg2Player;

/**
 * Created by sunxianping on 2017/6/16.
 */
public class CenterMsgService implements IkafkaMsgId {


    private static UserRecordService userRecordService = SpringUtil.getBean(UserRecordService.class);
    private static GameUserService gameUserService = SpringUtil.getBean(GameUserService.class);
    private static GameRecordService gameRecordService = SpringUtil.getBean(GameRecordService.class);
    private static ClubRecordService clubRecordService = SpringUtil.getBean(ClubRecordService.class);
    private static AgentUserService agentUserService = SpringUtil.getBean(AgentUserService.class);

    private static ReplayService replayService = SpringUtil.getBean(ReplayService.class);

    private static UserService userService = SpringUtil.getBean(UserService.class);
    private static RebateDetailService rebateDetailService = SpringUtil.getBean(RebateDetailService.class);
    private static ChargeService chargeService = SpringUtil.getBean(ChargeService.class);
    private static ClubRoomRecordService clubRoomRecordService = SpringUtil.getBean(ClubRoomRecordService.class);


    public static void dispatch(KafkaMsgKey msgKey, String msg) {
        int msgId = msgKey.getMsgId();
        switch (msgId) {
            case KAFKA_MSG_ID_GEN_RECORD:
                ThreadPool.getInstance().executor.execute(() -> genRecord(msg));
                break;
            case KAFKA_MSG_ID_REPLAY:
                ThreadPool.getInstance().executor.execute(() -> replay(msg));
                break;
            case KAFKA_MSG_ID_GAME_RECORD:
                ThreadPool.getInstance().executor.execute(() -> genGameRecord(msg));
                break;
            case KAFKA_MSG_ID_ROOM_RECORD:
                ThreadPool.getInstance().executor.execute(() -> genRoomRecord(msg));
                break;
            case KAFKA_MSG_ID_GUESS_ADD_GOLD:
                guessAddGold(msg);
                break;
            case KAFKA_MSG_ID_REFRESH_ROOM_INSTANCE:
                refreshRoomInstance(msg);
                break;
            case KAFKA_MSG_ID_ROOM_CLUB_USER:
                getRoomClubByUser(msg);
                break;
            case KAFKA_MSG_ID_ADD_REBATE:
                addRebate(msg);
                break;

            case KAFKA_MSG_ID_ADD_THREE_REBATE:
                addThreeRebate(msg);
                break;
            case KAFKA_MSG_ID_CCONTRIBUTE:
                addContribute(msg);
                break;

            case KAFKA_MSG_ID_ADD_REBATE_LONGCHENG:
                addRebateLongcheng(msg);
                break;

            case KAFKA_MSG_ID_ADD_WIN_NUM:
                addWinNum(msg);
                break;

            case KAFKA_MSG_ID_ADD_COUPON:
                addCoupon(msg);
                break;

            case KAFKA_MSG_ID_BIND:
                sendBindMsg(msg);
                break;

            case KAFKA_MSG_ID_MONEY_SPEND:
                addSpendMoneyLongcheng(msg);
                break;
//
//            case KAFKA_MSG_ID_51_ADD_REBATE:
//                add51Rebate(msg);
//                break;


        }
    }

    private static void refreshRoomInstance(String msg) {

        JsonNode jsonNode = JsonUtil.readTree(msg);
        String clubId = jsonNode.path("clubId").asText();
        String roomModelId = jsonNode.path("roomModelId").asText();
        String roomId = jsonNode.path("roomId").asText();
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            GameClubService.initRoomInstanceStatic(club);
        }
    }

    private static void getRoomClubByUser(String msg) {
        System.out.println("center : getRoomClubByUser");
        JsonNode jsonNode = JsonUtil.readTree(msg);

        Map<String, Object> map = JsonUtil.readValue(msg, new TypeReference<HashMap<String, Object>>() {
        });

        Map<String, Object> result = JsonUtil.readValue(msg, Map.class);
        long userId = jsonNode.path("userId").asLong();
        String clubId = jsonNode.path("clubId").asText();
        Club club = ClubManager.getInstance().getClubById(clubId);

        List<String> clubs = ClubManager.getInstance().getUserClubs(userId);
        map.put("clubs", clubs);
        if (club != null) {

            map.put("credit", club.getClubInfo().getCreditInfo());
            map.put("isOpen", club.getClubInfo().isOpen());

            ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
            if (clubMember != null) {
                map.put("score", clubMember.getAllStatistics().getAllScore());
                map.put("playMinScore", clubMember.getAllStatistics().getPlayMinScore());
                map.put("isCanJoin", clubMember.isCanJoinGame());
            } else {
                map.put("score", 0);
                map.put("playMinScore", 0);
                map.put("isCanJoin", false);
            }
        }


        sendMsg2Player(new ResponseVo("roomService", "getRoomClubByUser", map), userId);

    }

    /**
     * 增加rebate
     *
     * @param msg
     */
    private static void addRebate(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        double money = JsonUtil.readTree(msg).path("money").asDouble();
        addRebate(userId, money);
    }

    /**
     * 增加三级返利
     *
     * @param msg
     */
    private static void addThreeRebate(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        double money = JsonUtil.readTree(msg).path("money").asDouble();
        int is100 = JsonUtil.readTree(msg).path("is100").asInt();
        Map m = RedisManager.getConstantRedisService().getConstant();
        double firstMoney = 0;
        double secondMoney = 0;
        double thirdMoney = 0;
        if (is100 == 1) {

            double firstlevel = Double.valueOf((String) m.get(IGameConstant.FIRST_LEVEL_100));
            double secondlevel = Double.valueOf((String) m.get(IGameConstant.SECOND_LEVEL_100));
            double thirdlevel = Double.valueOf((String) m.get(IGameConstant.THIRD_LEVEL_100));
            firstMoney = money * firstlevel / 100;
            secondMoney = money * secondlevel / 100;
            thirdMoney = money * thirdlevel / 100;
        } else {
            firstMoney = Double.valueOf((String) m.get(IGameConstant.FIRST_LEVEL));
            secondMoney = Double.valueOf((String) m.get(IGameConstant.SECOND_LEVEL));
            thirdMoney = Double.valueOf((String) m.get(IGameConstant.THIRD_LEVEL));

        }


        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        String date = LocalDate.now().toString();
        //获得今日返利
        ThreeRebate threeRebate = userBean.getUserInfo().getThreeRebate().getOrDefault(date, new ThreeRebate());
        ThreeRebate allRebate = userBean.getUserInfo().getThreeRebate().getOrDefault("all", new ThreeRebate());
        userBean.getUserInfo().getThreeRebate().put(date, threeRebate);
        userBean.getUserInfo().getThreeRebate().put("all", allRebate);

        UserBean firstUserBean = RedisManager.getUserRedisService().getUserBean(userBean.getReferee());
        if (firstUserBean != null) {
            threeRebate.setFirst(threeRebate.getFirst() + firstMoney);
            allRebate.setFirst(allRebate.getFirst() + firstMoney);
            //返利
            RedisManager.getUserRedisService().addUserGold(firstUserBean.getId(), firstMoney);

            //二级代理
            UserBean secondUserBean = RedisManager.getUserRedisService().getUserBean(firstUserBean.getReferee());
            if (secondUserBean != null) {
                threeRebate.setSecond(threeRebate.getSecond() + secondMoney);
                allRebate.setSecond(allRebate.getSecond() + secondMoney);
                RedisManager.getUserRedisService().addUserGold(secondUserBean.getId(), secondMoney);

                //三级代理
                UserBean thirdUserBean = RedisManager.getUserRedisService().getUserBean(secondUserBean.getReferee());
                if (thirdUserBean != null) {
                    threeRebate.setThird(threeRebate.getThird() + thirdMoney);
                    allRebate.setThird(allRebate.getThird() + thirdMoney);
                    RedisManager.getUserRedisService().addUserGold(thirdUserBean.getId(), thirdMoney);
                }
            }
        }

        //删除7天以前的数据
        Set<String> needDay = getNeedDay();
        List<String> deleteList = new ArrayList<>();
        for (String key : userBean.getUserInfo().getThreeRebate().keySet()) {
            if (!needDay.contains(key)) {
                deleteList.add(key);
            }
        }

        //删除
        for (String delete : deleteList) {
            userBean.getUserInfo().getThreeRebate().remove(delete);
        }

        //更新userBean
        RedisManager.getUserRedisService().updateUserBean(userId, userBean);

    }


    /**
     * 增加贡献
     *
     * @param msg
     */
    public static void addContribute(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        double money = JsonUtil.readTree(msg).path("money").asDouble();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        String date = LocalDate.now().toString();
        ThreeRebate threeRebate = userBean.getUserInfo().getThreeRebate().getOrDefault(date, new ThreeRebate());
        ThreeRebate allRebate = userBean.getUserInfo().getThreeRebate().getOrDefault("all", new ThreeRebate());
        userBean.getUserInfo().getThreeRebate().put(date, threeRebate);
        userBean.getUserInfo().getThreeRebate().put("all", allRebate);
        threeRebate.setContribute(threeRebate.getContribute() + money);
        allRebate.setContribute(allRebate.getContribute() + money);

        RedisManager.getUserRedisService().updateUserBean(userId, userBean);
    }

    /**
     * 获得需要留下的日期
     *
     * @return
     */
    private static Set<String> getNeedDay() {
        Set<String> result = new HashSet<>();
        result.add("all");
        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < 9; i++) {
            LocalDate l = localDate.minusDays(i);
            result.add(l.toString());
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getNeedDay());
    }

    private static void addRebateLongcheng(String msg) {
        try {
            long userId = JsonUtil.readTree(msg).path("userId").asLong();
            double money = JsonUtil.readTree(msg).path("money").asDouble();

            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
            long parentId = userBean.getReferee();
            if (parentId != 0) {
                UserBean parentUser = LoginAction.loadUserBean(parentId);
                if (parentUser != null) {
                    //返利记录
                    RebateDetail rebateDetail = new RebateDetail();
                    rebateDetail.setUserId(userId);
                    rebateDetail.setAgentId(parentId);
                    rebateDetail.setNum(money);
                    rebateDetail.setDate(new Date());
                    rebateDetail.setBeforeNum(parentUser.getUserInfo().getAllRebate());
                    parentUser.getUserInfo().setAllRebate(parentUser.getUserInfo().getAllRebate() + money);
                    rebateDetail.setAfterNum(parentUser.getUserInfo().getAllRebate());
                    RedisManager.getUserRedisService().addSaveUser(parentId);
                    RedisManager.getUserRedisService().updateUserBean(parentId, parentUser);
                    rebateDetailService.rebateDetailDao.save(rebateDetail);


                    Map<String, String> rs = new HashMap<>();
                    MsgSender.sendMsg2Player(new ResponseVo("userService", "refresh", rs), parentId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addWinNum(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        int num = JsonUtil.readTree(msg).path("num").asInt();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        CenterService.addWinNum(userId, userBean, num);
    }

    public static void addCoupon(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        int num = JsonUtil.readTree(msg).path("num").asInt();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        userBean.getUserInfo().setCoupon(userBean.getUserInfo().getCoupon() + num);
        RedisManager.getUserRedisService().updateUserBean(userId, userBean);
    }

    public static void addSpendMoneyLongcheng(String msg) {
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        int num = JsonUtil.readTree(msg).path("money").asInt();
        String roomType = JsonUtil.readTree(msg).path("type").asText();
        Charge charge = new Charge();
        charge.setUserid(userId);
        charge.setOrderId(IdWorker.getDefaultInstance().nextId() + "");
        charge.setCreatetime(new Date());
        charge.setMoney(num);
        charge.setRecharge_source("16");
        charge.setStatus(1);
        charge.setSign(roomType);
        chargeService.save(charge);

    }

    private static void rebateRecord(UserBean userBean, AgentUser agentUser, int level, double chargeNum, double rebateNum, boolean isAA) {
        RebateDetail rebateDetail = new RebateDetail();
        rebateDetail.setUserId(userBean.getId());
        rebateDetail.setName(userBean.getUsername());
        rebateDetail.setAgentId(agentUser.getId());
        rebateDetail.setAgentName(agentUser.getUsername());
        rebateDetail.setNum(rebateNum);
        rebateDetail.setChargeNum(chargeNum);
        rebateDetail.setAgentLevel(agentUser.getLevel());
        rebateDetail.setDate(new Date());
        rebateDetail.setUserLevel(userBean.getVip());
        rebateDetail.setType(isAA ? 1 : 2);
        rebateDetail.setLevle(level);

        if (agentUser.getLevel() < 2) {
            rebateDetail.setAgentLevel(agentUser.getAgentType());
            rebateDetail.setType(0);
        }
        rebateDetailService.rebateDetailDao.save(rebateDetail);
    }

    private static double getRebateNum(ServerConfig serverConfig, boolean isAA, int level, int agentLevel, double num, int agentType) {
        if(agentLevel<2 && level == 1) return num * serverConfig.getAgentFirstRebate().get(agentType) * 0.01;
        if (isAA && level == 1) return num * serverConfig.getZlbAAOne().get(agentLevel) * 0.01;
        if (isAA && level == 2) return num * serverConfig.getZlbAATwo().get(agentLevel) * 0.01;
        if (isAA && level == 3) return num * serverConfig.getZlbAAThree().get(agentLevel) * 0.01;
        if (!isAA && level == 1) return num * serverConfig.getZlbRebate().get(agentLevel) * 0.01;
        if (!isAA && level == 2) return num * serverConfig.getZlbRebate2().get(agentLevel) * 0.01;
        if (!isAA && level == 3) return num * serverConfig.getZlbRebate3().get(agentLevel) * 0.01;
        return 0;
    }

    /**
     * 51号返利
     *
     *
     */
    public static void add51Rebate(long userId, double num, boolean isAA) {


        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        int parent = userBean.getReferee();
        AgentUser agentUser1 = agentUserService.getAgentUserDao().findOne(parent);
        if (agentUser1 == null) return;
        String dayStr = DateUtil.convert2DayString(new Date());
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);


        AgentInfo agentInfo1 = agentUser1.getAgentInfo();
        Map<String, ChildCost> rs1 = agentInfo1.getEveryDayCost();
        ChildCost childCost1 = rs1.getOrDefault(dayStr, new ChildCost());
        double rebateNum = getRebateNum(serverConfig, isAA, 1, agentUser1.getLevel(), num, agentUser1.getAgentType());

        childCost1.firstLevel += rebateNum;
        rs1.put(dayStr, childCost1);
        agentUserService.getAgentUserDao().save(agentUser1);

        rebateRecord(userBean, agentUser1, 1, num, rebateNum, isAA);

        AgentUser agentUser2 = agentUserService.getAgentUserDao().findOne(agentUser1.getParentId());
        if (agentUser2 != null) {
            AgentInfo agentInfo2 = agentUser2.getAgentInfo();
            Map<String, ChildCost> rs2 = agentInfo2.getEveryDayCost();
            ChildCost childCost2 = rs2.getOrDefault(dayStr, new ChildCost());
            double rebateNum2 = getRebateNum(serverConfig, isAA, 2, agentUser2.getLevel(), num, agentUser2.getAgentType());
            childCost2.secondLevel += rebateNum2;
            rs2.put(dayStr, childCost2);
            agentUserService.getAgentUserDao().save(agentUser2);
            rebateRecord(userBean, agentUser2, 2, num, rebateNum2, isAA);

            AgentUser agentUser3 = agentUserService.getAgentUserDao().findOne(agentUser2.getParentId());
            if (agentUser3 != null) {
                AgentInfo agentInfo3 = agentUser3.getAgentInfo();
                Map<String, ChildCost> rs3 = agentInfo3.getEveryDayCost();
                ChildCost childCost3 = rs3.getOrDefault(dayStr, new ChildCost());
                double rebate3 = getRebateNum(serverConfig, isAA, 3, agentUser3.getLevel(), num, agentUser3.getAgentType());
                childCost3.thirdLevel += rebate3;
                rs3.put(dayStr, childCost3);
                agentUserService.getAgentUserDao().save(agentUser3);
                rebateRecord(userBean, agentUser3, 3, num, rebate3, isAA);
            }


        }

    }


    public static void sendBindMsg(String msg) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if ("".equals(serverConfig.getQrDir())) return;
        long userId = JsonUtil.readTree(msg).path("userId").asLong();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        UserBean firstBean = RedisManager.getUserRedisService().getUserBean(userBean.getReferee());
        if (firstBean != null) {
            gameUserService.sendMailToUser(getMailStr("一级", "" + userBean.getId(), userBean.getUsername()), firstBean);

            UserBean secondBean = RedisManager.getUserRedisService().getUserBean(firstBean.getReferee());
            if (secondBean != null) {
                gameUserService.sendMailToUser(getMailStr("二级", "" + userBean.getId(), userBean.getUsername()), secondBean);
                UserBean thirdBean = RedisManager.getUserRedisService().getUserBean(secondBean.getReferee());
                if (thirdBean != null) {
                    gameUserService.sendMailToUser(getMailStr("三级", "" + userBean.getId(), userBean.getUsername()), thirdBean);
                }
            }
        }
    }

    private static String getMailStr(String str1, String str2, String str3) {
        return String.format("您的%s下线成员id:%s 名字:%s已成功绑定", str1, str2, str3);
    }


    public static void addRebate(long userId, double money) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        long parentId = userBean.getReferee();
        if (parentId != 0) {
            UserBean parentUser = LoginAction.loadUserBean(parentId);
            if (parentUser != null) {
                //返利记录
                Map<Long, ScoreItem> rebateMap = parentUser.getUserInfo().getRebate();
                if (rebateMap == null) {
                    rebateMap = new HashMap<>();
                    parentUser.getUserInfo().setRebate(rebateMap);
                }
                ScoreItem scoreItem = rebateMap.getOrDefault(userId, new ScoreItem());
                scoreItem.setName(userBean.getUsername()).setScore(scoreItem.getScore() + money);
                rebateMap.put(userId, scoreItem);
                parentUser.getUserInfo().setRebate(rebateMap);

                RedisManager.getUserRedisService().updateUserBean(parentId, parentUser);
                //返利加到gold上
                RedisManager.getUserRedisService().addUserGold(parentId, money);

            }
        }
    }

    private static void genRecord(String msg) {
        RoomRecord roomRecord = JsonUtil.readValue(msg, RoomRecord.class);

        List<com.code.server.constant.game.UserRecord> lists = roomRecord.getRecords();
        for (com.code.server.constant.game.UserRecord userRecord : lists) {
            UserRecord addRecord = userRecordService.getUserRecordByUserId(userRecord.getUserId());
            if (addRecord != null) {
                userRecordService.addRecord(userRecord.getUserId(), roomRecord);
            } else {
                Record record = new Record();
                record.addRoomRecord(roomRecord);

                UserRecord newRecord = new UserRecord();
                newRecord.setId(userRecord.getUserId());
                newRecord.setRecord(record);
                userRecordService.save(newRecord);
            }
        }
    }

    private static void replay(String msg) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getSaveReplay() == 0) {
            return;
        }
        if (msg != null) {
            long id = JsonUtil.readTree(msg).path("id").asLong();
            int count = JsonUtil.readTree(msg).path("count").asInt();
            long room_uuid = JsonUtil.readTree(msg).path("room_uuid").asLong();
            Replay replay = new Replay();
            replay.setId(id);
            replay.setLeftCount(count);
            replay.setData(msg);
            replay.setRoomUuid(room_uuid);
            replay.setDate(new Date());
            replayService.save(replay);
        }

    }

    private static void genGameRecord(String msg) {
        if (msg != null) {
            JsonNode jsonNode = JsonUtil.readTree(msg);
            Map<String, Object> map = JsonUtil.readValue(msg, new TypeReference<HashMap<String, Object>>() {
            });

            long room_uuid = (Long) map.get("room_uuid");
            long replay_id = (Long) map.get("replay_id");
            int count = (int) map.get("count");
            String recordStr = jsonNode.path("record").asText();
            System.out.println(recordStr);
            Gson gson = new Gson();
            com.code.server.constant.game.GameRecord data = gson.fromJson(recordStr, com.code.server.constant.game.GameRecord.class);
//            com.code.server.constant.game.GameRecord data = (com.code.server.constant.game.GameRecord)map.get("record");
            GameRecord gameRecord = new GameRecord();
            gameRecord.setDate(new Date());
            gameRecord.setUuid(room_uuid);
            gameRecord.setLeftCount(count);
            gameRecord.setGameRecord(data);
            gameRecord.setReplayId(replay_id);
            gameRecordService.gameRecordDao.save(gameRecord);
        }
    }


    /**
     * 增加游戏数记录
     *
     * @param date
     * @param userId
     */
    private static void addPlayNum(String date, long userId) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            int before = userBean.getUserInfo().getPlayGameNum().getOrDefault(date, 0);
            int num = before + 1;
            if (userBean.getUserInfo().getPlayGameNum().size() > 1) {
                userBean.getUserInfo().getPlayGameNum().clear();
            }
            userBean.getUserInfo().getPlayGameNum().put(date, num);

            RedisManager.getUserRedisService().updateUserBean(userId, userBean);
        }
    }

    private static void genRoomRecord(String msg) {
        RoomRecord roomRecord = JsonUtil.readValue(msg, RoomRecord.class);

        boolean isAddGameNum = roomRecord.getCurGameNum() > 1;
        try {

            if (roomRecord.isOpen()) {

                List<com.code.server.constant.game.UserRecord> lists = roomRecord.getRecords();
                for (com.code.server.constant.game.UserRecord userRecord : lists) {
//                    UserRecord addRecord = userRecordService.getUserRecordByUserId(userRecord.getUserId());
//                    if (addRecord != null) {
//                        userRecordService.addRecord(userRecord.getUserId(), roomRecord);
//                    } else {
//                        Record record = new Record();
//                        record.addRoomRecord(roomRecord);
//
//                        UserRecord newRecord = new UserRecord();
//                        newRecord.setId(userRecord.getUserId());
//                        newRecord.setRecord(record);
//                        userRecordService.save(newRecord);
//                    }
                    if (isAddGameNum) {
                        String date = LocalDate.now().toString();
                        addPlayNum(date, userRecord.getUserId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //俱乐部战绩
        String clubId = roomRecord.getClubId();
        if (clubId != null && !"".equals(clubId)) {
            Club club = ClubManager.getInstance().getClubById(clubId);
            RoomModel rm = null;
            if (club != null) {
                String roomModel = roomRecord.getClubRoomModel();
                rm = GameClubService.getRoomModel(club, roomModel);
                if (rm != null) {
                    roomRecord.setName(rm.getDesc());
                }

                //数据统计


                String date = LocalDate.now().toString();
                //记录50天的数据
                String dateBefore50 = LocalDate.now().minusDays(50).toString();
                for (com.code.server.constant.game.UserRecord userRecord : roomRecord.getRecords()) {
                    ClubMember clubMember = club.getClubInfo().getMember().get("" + userRecord.getUserId());
                    if (clubMember != null) {
                        ClubStatistics clubStatistics = clubMember.getStatistics().getOrDefault(date, new ClubStatistics());
                        clubMember.getStatistics().put(date, clubStatistics);
                        clubStatistics.setAllScore(clubStatistics.getAllScore() + userRecord.getScore());
                        clubStatistics.setOpenNum(clubStatistics.getOpenNum() + 1);

                        //删除50天前数据
                        clubMember.getStatistics().remove(dateBefore50);

                        //设置总统计
                        clubMember.getAllStatistics().setOpenNum(clubMember.getAllStatistics().getOpenNum() + 1);
                        clubMember.getAllStatistics().setAllScore(clubMember.getAllStatistics().getAllScore() + userRecord.getScore());
                        if (userRecord.getScore() > 0) {
                            clubMember.getAllStatistics().setWinnerNum(clubMember.getAllStatistics().getWinnerNum() + 1);
                            clubMember.getAllStatistics().setWinScore(clubMember.getAllStatistics().getWinScore() + userRecord.getScore());
                        }
                        if (userRecord.getScore() < 0) {
                            clubMember.getAllStatistics().setLoseNum(clubMember.getAllStatistics().getLoseNum() + 1);
                            clubMember.getAllStatistics().setLoseScore(clubMember.getAllStatistics().getLoseScore() + userRecord.getScore());
                        }
                    }

                }


                //推送房间解散
                ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
                if (serverConfig.getClubPushUserRoomInfo() == 1) {
                    List<Long> users = new ArrayList<>();
                    club.getClubInfo().getMember().forEach((id, ClubMember) -> users.add(Long.valueOf(id)));

                    Map<String, Object> r = new HashMap<>();
                    r.put("userId", roomRecord.getRoomId());
                    r.put("roomModelId", roomModel);
                    r.put("clubId", clubId);
                    ResponseVo responseVo = new ResponseVo("clubService", "clubDissolutionRoom", r);
                    users.forEach(uid -> sendMsg2Player(responseVo, uid));
                }


                //龙七 发送http
                sendLq_http(roomRecord, club);

//                if (serverConfig.getSendDuoliao() == 1) {
//                    try {
//                        DuoLiaoService.sendRecord(roomRecord, club);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }

            }

            clubRecordService.addRecord(clubId, roomRecord);

            //记录俱乐部战绩

            if (rm != null) {

                ClubRoomRecord clubRoomRecord = new ClubRoomRecord();
                clubRoomRecord.setClubId(clubId);
                clubRoomRecord.setRoomModelId(roomRecord.getClubRoomModel());
                clubRoomRecord.setRecordDate(new Date());
                clubRoomRecord.setFloor(rm.getFloor());
                clubRoomRecord.setRoomRecord(roomRecord);
                clubRoomRecord.setRoomId(roomRecord.getRoomId());
                clubRoomRecordService.getClubRoomRecordDao().save(clubRoomRecord);
            }

        }
    }


    private static void sendLq_http(RoomRecord roomRecord, Club club) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        if (serverConfig.getSend_lq_http() == 1) {
            Map<String, Object> result = new HashMap<>();
            result.put("ClubNo", club.getId());
            result.put("RoomId", roomRecord.getRoomId());
            int index = getClubModelIndex(club, roomRecord.getClubRoomModel());
            result.put("OnlyNo", club.getId() + roomRecord.getRoomId() + index);
            result.put("wanfa", index);
            List<Map<String, Object>> list = new ArrayList<>();
            result.put("PlayerList", list);
            for (com.code.server.constant.game.UserRecord userRecord : roomRecord.getRecords()) {
                Map<String, Object> u = new HashMap<>();
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userRecord.getUserId());
                u.put("Unionid", userBean.getUnionId());
                u.put("WeixinName", userBean.getUsername());
                u.put("HeadImgUrl", userBean.getImage() + "/132");
                u.put("NTotalPoint", userRecord.getScore());
                list.add(u);

            }
            String json = JsonUtil.toJson(result);
            HttpClient httpClient = HttpClientBuilder.create().build();
            //设置连接超时5s
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000).build();

            String url1 = serverConfig.getLq_http_url() + "?strContext=" + json;

            try {
                URL url = new URL(url1);
                URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
                HttpGet request = new HttpGet(uri);
                request.setConfig(requestConfig);
                try {
                    httpClient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                lq_upScoreRecord(roomRecord.getRoomId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 龙七记录上分
     *
     * @param roomId
     */
    private static void lq_upScoreRecord(String roomId) {
        UpScoreRecordService upScoreRecordService = SpringUtil.getBean(UpScoreRecordService.class);
        UpScoreRecord upScoreRecord = new UpScoreRecord();
        upScoreRecord.setDate(new Date());
        upScoreRecord.setRoomId(roomId);
        upScoreRecordService.getUpScoreRecordDao().save(upScoreRecord);
    }

    public static int getClubModelIndex(Club club, String roomModel) {
        int index = 0;
        for (RoomModel rm : club.getClubInfo().getRoomModels()) {
            index += 1;
            if (roomModel.equals(rm.getId())) {
                return index;
            }
        }
        return index;
    }

    private static void guessAddGold(String msg) {
        if (msg != null) {
            JsonNode jsonNode = JsonUtil.readTree(msg);
            int userId = jsonNode.path("userId").asInt();
            double gold = jsonNode.path("gold").asDouble();
            UserBean own = RedisManager.getUserRedisService().getUserBean(userId);
            int bindUser1 = own.getReferee();


            double addGold = gold;

            AgentUser agentUser = agentUserService.getAgentUserDao().findAgentUserByInvite_code("" + bindUser1);
            if (agentUser != null) {
                agentUser.setGold(agentUser.getGold() + addGold);
                agentUserService.getAgentUserDao().save(agentUser);
            }
        }
    }


}
