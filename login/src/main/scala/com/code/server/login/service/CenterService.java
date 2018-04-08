package com.code.server.login.service;

import com.code.server.constant.db.OnlineInfo;
import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.GameAgentService;
import com.code.server.db.Service.OnlineRecordService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.OnlineRecord;
import com.code.server.db.model.User;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sunxianping on 2017/6/16.
 */
@Service
public class CenterService {




    public static void work(){


        //检测服务器状态
        CheckHeart.check();


        //保存玩家
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(),1000L*60*5,true, CenterService::saveUser));

        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(),1000L*60*5,true, CenterService::saveAgent));

        //在线记录
        GameTimer.addTimerNode(new TimerNode(System.currentTimeMillis(),1000L*60*10,true, CenterService::onlineRecord));


    }

    private static void saveUser(){
        Set<String> users = RedisManager.getUserRedisService().getSaveUsers();
        if (users != null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            Set<String> removeList = new HashSet<>();
            users.forEach(userId-> {
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
            agents.forEach(agent->{
                long agentId = Long.valueOf(agent);
                AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
                GameAgent gameAgent = AgentService.agentBean2GameAgent(agentBean);
                gameAgentService.getGameAgentDao().save(gameAgent);
                removeList.add(agent);

            });

            if (removeList.size() > 0) {
                RedisManager.getAgentRedisService().removeSaveAgent(removeList);
            }
        }
    }

    private static void onlineRecord(){
        String date = LocalDate.now().toString();
        int hour = LocalTime.now().getHour();

        OnlineRecordService onlineRecordService = SpringUtil.getBean(OnlineRecordService.class);
        OnlineRecord onlineRecord = onlineRecordService.getOnlineRecordDao().findOne(date);
        if (onlineRecord == null) {
            onlineRecord = new OnlineRecord();
            onlineRecord.setId(date);
        }

        OnlineInfo onlineInfo = onlineRecord.getOnlineData().getInfo().get(""+hour);
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
        onlineRecord.getOnlineData().getInfo().put(""+hour,onlineInfo);

        onlineRecordService.getOnlineRecordDao().save(onlineRecord);
    }

    public static void main(String[] args) {
        onlineRecord();
    }

}
