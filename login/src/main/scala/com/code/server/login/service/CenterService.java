package com.code.server.login.service;

import com.code.server.constant.db.OnlineInfo;
import com.code.server.constant.db.PartnerRebate;
import com.code.server.constant.db.PlayerRank;
import com.code.server.constant.db.PlayerScore;
import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.*;
import com.code.server.db.model.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by sunxianping on 2017/6/16.
 */
@Service
public class CenterService {


    private Map<String,PlayerRank> rank = new HashMap<>();

    @Autowired
    private RankService rankService;

    public static void work() {


        //检测服务器状态
        CheckHeart.check();


        //保存玩家
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), 1000L * 60 * 5, true, CenterService::saveUser));


        //在线记录
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), 1000L * 60 * 10, true, CenterService::saveLogRecord));

//        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), 1000L * 60 * 5, true, CenterService::saveAgentRecord));
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), 1000L * 60 * 5, true, CenterService::saveAgent));
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(), 1000L * 60 * 5, true, CenterService::saveRank));


    }

    private static void saveUser() {
        Set<String> users = RedisManager.getUserRedisService().getSaveUsers();
        if (users != null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            Set<String> removeList = new HashSet<>();
            users.forEach(userId -> {
                long uid = Long.valueOf(userId);
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(uid);
                User user = GameUserService.userBean2User(userBean);
                //保存
                userService.save(user);
                removeList.add(userId);
            });
            //批量删除 需要保存的玩家
            if (removeList.size() > 0) {
                RedisManager.getUserRedisService().removeSaveUser(removeList.toArray());
            }

        }

        //俱乐部 保存
        ClubManager.getInstance().saveAll();
    }


    private static void saveAgent() {
        Set<String> agents = RedisManager.getAgentRedisService().getSaveAgents();
        if (agents != null) {
            GameAgentService gameAgentService = SpringUtil.getBean(GameAgentService.class);
            Set<String> removeList = new HashSet<>();
            agents.forEach(agent -> {
                long agentId = Long.valueOf(agent);
                AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
                if (agentBean != null) {
                    GameAgent gameAgent = AgentService.agentBean2GameAgent(agentBean);
                    gameAgentService.getGameAgentDao().save(gameAgent);
                    if (gameAgent.getIsPartner() == 1) {
                        saveAgentRecord(agentBean);
                    }
                }
                removeList.add(agent);

            });

            if (removeList.size() > 0) {
                RedisManager.getAgentRedisService().removeSaveAgent(removeList.toArray());
            }
        }
    }

    public static void saveAllAgent(){
        Set<String> agents = RedisManager.getAgentRedisService().getAllAgentBeanKey();
        if (agents != null) {
            GameAgentService gameAgentService = SpringUtil.getBean(GameAgentService.class);
            agents.forEach(agent -> {
                long agentId = Long.valueOf(agent);
                AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
                if (agentBean != null) {
                    GameAgent gameAgent = AgentService.agentBean2GameAgent(agentBean);
                    gameAgentService.getGameAgentDao().save(gameAgent);
                    if (gameAgent.getIsPartner() == 1) {
                        saveAgentRecord(agentBean);
                    }
                }

            });
        }
    }


    private static void saveAgentRecord(AgentBean agentBean) {
        LocalDate now = LocalDate.now();
        String date = now.toString();
//        for (String agentIdStr : RedisManager.getAgentRedisService().getSaveAgents()) {
//            long agentId = Long.valueOf(agentIdStr);
//            AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        AgentRecordService agentRecordService = SpringUtil.getBean(AgentRecordService.class);
        AgentRecords today = createAgentRecords(agentBean, date);
        if (today != null) {
            agentRecordService.getAgentRecordsDao().save(today);
        }


        int hour = LocalTime.now().getHour();
        int min = LocalTime.now().getMinute();
        if (hour == 0 && min < 20) {
            String yesStr = now.minusDays(1).toString();
            AgentRecords yesDay = createAgentRecords(agentBean, yesStr);
            if (yesDay != null) {
                agentRecordService.getAgentRecordsDao().save(yesDay);
            }
        }

//        }
    }

    private static AgentRecords createAgentRecords(AgentBean agentBean, String date) {

        if (agentBean.getAgentInfo().getEveryPartnerRebate() != null) {
            PartnerRebate partnerRebate = agentBean.getAgentInfo().getEveryPartnerRebate().get(date);

            if (partnerRebate != null) {
                AgentRecords agentRecord = new AgentRecords();
                agentRecord.setId(agentBean.getId() + "|" + date);
                agentRecord.setDate(date);
                agentRecord.setAgentId((int)agentBean.getId());
                agentRecord.setAllRebate(partnerRebate.getAllRebate());
                agentRecord.setChildCost(partnerRebate.getCost());
                agentRecord.setMoneyRebate(partnerRebate.getMoneyRebate());
                agentRecord.setGoldRebate(partnerRebate.getGoldRebate());
                return agentRecord;
            }
        }
        return null;
    }

    /**
     * 保存log信息
     */
    private static void saveLogRecord() {
        String date = LocalDate.now().toString();
        int hour = LocalTime.now().getHour();
        int min = LocalTime.now().getMinute();

        LogRecordService onlineRecordService = SpringUtil.getBean(LogRecordService.class);
        LogRecord record = onlineRecordService.getOnlineRecordDao().findOne(date);
        if (record == null) {
            record = new LogRecord();
            record.setId(date);
        }

        //玩家在线数据
        OnlineInfo onlineInfo = record.getOnlineData().getInfo().get("" + hour);
        if (onlineInfo == null) {
            onlineInfo = new OnlineInfo();
        }
        int userNum = RedisManager.getRoomRedisService().getRoomNum();
        int roomNum = RedisManager.getUserRedisService().getOnlineUserNum();
        if (userNum > onlineInfo.getUser()) {
            onlineInfo.setUser(userNum);
        }
        if (roomNum > onlineInfo.getRoom()) {
            onlineInfo.setRoom(roomNum);
        }

        record.getOnlineData().getInfo().put("" + hour, onlineInfo);

        setGameNumAndRebateData(record, date);

        onlineRecordService.getOnlineRecordDao().save(record);

        //定时保存数据,防止昨天数据最后数据没保存上,保存昨天数据
        String lastDate = LocalDate.now().minusDays(1).toString();
        //20分钟内
        if (hour == 0 && min < 20) {
            LogRecord lastRecord = onlineRecordService.getOnlineRecordDao().findOne(lastDate);
            if (lastRecord != null) {
                if (lastRecord.getGameNumData() != null && lastRecord.getGameNumData().getInfo() != null) {
                    setGameNumAndRebateData(lastRecord, lastDate);
                    onlineRecordService.getOnlineRecordDao().save(lastRecord);
                }
            }
        }

    }


    /**
     * 设置开局数,返利等相关信息
     *
     * @param logRecord
     * @param date
     */
    private static void setGameNumAndRebateData(LogRecord logRecord, String date) {
        //牌局数
        logRecord.getGameNumData().getInfo().putAll(RedisManager.getLogRedisService().getGameNumInfo(date));
        //gold 收入
        logRecord.getGoldRoomIncomeData().getInfo().putAll(RedisManager.getLogRedisService().getGoldIncomeInfo(date));
        //返利数据
        logRecord.setChargeRebate(RedisManager.getLogRedisService().getChargeRebate(date));

        //其他信息
        logRecord.setLogInfo(RedisManager.getLogRedisService().getLogInfo(date));
    }


    /**
     * 读取log信息
     */
    public static void loadLogInfo() {
        String date = LocalDate.now().toString();
        Map<String, String> gameNumInfo = RedisManager.getLogRedisService().getGameNumInfo(date);
        Map<String, String> goldIncomeInfo = RedisManager.getLogRedisService().getGoldIncomeInfo(date);

        //redis 有数据
        boolean redisHasData = (RedisManager.getLogRedisService().getChargeRebate(date) > 0) ||
                (gameNumInfo != null && gameNumInfo.size() > 0) ||
                (goldIncomeInfo != null && goldIncomeInfo.size() > 0);
        if (!redisHasData) {
            //数据库中是否有今日数据
            LogRecordService onlineRecordService = SpringUtil.getBean(LogRecordService.class);
            LogRecord data = onlineRecordService.getOnlineRecordDao().findOne(date);
            boolean isHasData = data != null && data.getGameNumData() != null && data.getGameNumData().getInfo() != null;
            if (isHasData) {
                RedisManager.getLogRedisService().addChargeRebate(data.getChargeRebate());
                RedisManager.getLogRedisService().putGameNum(data.getGameNumData().getInfo());
                RedisManager.getLogRedisService().putGoldIncome(data.getGoldRoomIncomeData().getInfo());
                RedisManager.getLogRedisService().setLogInfo(data.getLogInfo());
            }
        }
    }


    /**
     * 读取排行
     */
    public static void loadRank(){
        CenterService centerService = SpringUtil.getBean(CenterService.class);
        for(Rank rankTemp: centerService.rankService.getRankDao().findAll()){
            centerService.rank.put(rankTemp.getId(), rankTemp.getPlayerRank());
        }
    }

    /**
     * 增加人员胜场数
     * @param userId
     * @param userBean
     * @param num
     */
    public static void addWinNum(long userId,UserBean userBean,  int num) {
        CenterService centerService = SpringUtil.getBean(CenterService.class);
        String date = LocalDate.now().toString();
        PlayerRank playerRank = centerService.rank.getOrDefault(date, new PlayerRank());
        PlayerScore playerScore = playerRank.getPlayers().get(userId);
        if (playerScore == null) {
            playerScore = new PlayerScore(userId, userBean.getUsername(), userBean.getImage());
        }
        playerScore.setWinNum(playerScore.getWinNum() + num);
        playerRank.getPlayers().put(userId, playerScore);
        centerService.rank.put(date, playerRank);

    }

    /**
     * 保存排行
     */
    public static void saveRank(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM");
        String m = simpleDateFormat.format(date);
        CenterService centerService = SpringUtil.getBean(CenterService.class);
        PlayerRank playerRank = centerService.getRank().get(m);
        if (playerRank != null) {
            Rank r  = new Rank();
            r.setId(m);
            r.setPlayerRank(playerRank);
            centerService.rankService.getRankDao().save(r);
        }




    }

    public Map<String, PlayerRank> getRank() {
        return rank;
    }

    public CenterService setRank(Map<String, PlayerRank> rank) {
        this.rank = rank;
        return this;
    }

    public static void main(String[] args) {
        saveLogRecord();
    }

}
