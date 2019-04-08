package com.code.server.login.action;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Club;
import com.code.server.db.model.User;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.ClubManager;
import com.code.server.login.service.GameUserService;
import com.code.server.login.service.ServerManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.code.server.redis.config.IConstant.ROOM_USER;
import static com.code.server.redis.config.IConstant.USER_GATE;

/**
 * Created by sunxianping on 2017/6/27.
 */
@RestController
@EnableAutoConfiguration
public class ManagerAction extends Cors {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @RequestMapping("/getOnlineUser")
    public Map<String, Object> getOnlineUser() {
        BoundHashOperations<String, String, String> user_gate = redisTemplate.boundHashOps(USER_GATE);
        BoundHashOperations<String, String, String> room = redisTemplate.boundHashOps(ROOM_USER);
        Map<String, Object> result = new HashMap<>();
        result.put("userNum", user_gate.size());
        result.put("roomNum", room.size());
        return result;
    }


    @RequestMapping("/getRoomUser")
    public Map<String, Object> getRoomUser(String roomId) {
        Map<String, Object> result = new HashMap<>();
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if (serverId == null) {

            result.put("user", null);
        } else {
            result.put("user", RedisManager.getRoomRedisService().getUsers(roomId));
        }
        return result;

    }


    @RequestMapping("/getRoomUserInfo")
    public Map<String, Object> getRoomUserInfo(String roomId) {
        Map<String, Object> result = new HashMap<>();
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if (serverId == null) {
            result.put("user", null);
        } else {
            Set<Long> users = RedisManager.getRoomRedisService().getUsers(roomId);
            result.put("user", RedisManager.getUserRedisService().getUserBeans(users));
        }
        return result;

    }

    @RequestMapping("/")
    public Map<String, Object> test(String roomId) {
        Map<String, Object> result = new HashMap<>();
        result.put("hello", "hello");
        return result;

    }

    @RequestMapping("/user/login")
    public AgentResponse test(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.code = 20000;
        agentResponse.setData(result);

        result.put("token", "1");
        return agentResponse;

    }

    @RequestMapping("/getDownloadUrl")
    public String getDownloadUrl(String platform, String versionNow) {
        Map<String, Object> result = new HashMap<>();
        result.put("hello", "hello");
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        ServerManager.constant.getVersionOfAndroid();
        String url = "http://" + serverConfig.getDomain() + "/client/" + platform;
        return url;
    }


    @RequestMapping("/removeUserClub")
    public String removeUserClub(String userId, String clubId) {
        List<String> clubs = ClubManager.getInstance().getUserClubs(Long.valueOf(userId));
        System.out.println("clubs = " + clubs);
        clubs.remove(clubId);
        System.out.println("remove clubs = " + clubs);
        return "ok";
    }


    @RequestMapping("/openCheat")
    public String openCheat(String userId, int flag) {

        long uid = Long.valueOf(userId);
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(uid);
        if (userBean != null) {
            userBean.setVip(flag);
            RedisManager.getUserRedisService().updateUserBean(uid, userBean);
            User user = GameUserService.userBean2User(userBean);
            userService.save(user);
        }else{
            User user = userService.getUserByUserId(uid);
            if (user != null) {
                user.setVip(flag);
                userService.save(user);
            }
        }
        return "ok";
    }


    @RequestMapping("/dissolveRoom")
    public AgentResponse dissolveRoom(String roomId) {
        System.out.println("admin解散房间");
        Map<String, Object> rs = new HashMap<>();
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(rs);
        String serverId = RedisManager.getRoomRedisService().getServerId(roomId);
        if (serverId == null) {
            rs.put("result", "ok");
//            rs.put("")
//            agentResponse.setMsg("房间不存在");
//            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);

            RedisManager.removeRoomAllInfo(roomId);
            return agentResponse;
        }
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        KafkaMsgKey msgKey = new KafkaMsgKey();
        msgKey.setUserId(0);
        msgKey.setRoomId(roomId);
        msgKey.setPartition(Integer.valueOf(serverId));
        ResponseVo responseVo = new ResponseVo("roomService", "dissolutionRoom", result);
        msgProducer.send2Partition("roomService", Integer.valueOf(serverId), msgKey, responseVo);

        rs.put("result", "ok");
        return agentResponse;
    }


    @RequestMapping("/clearClub")
    public AgentResponse clearClub(String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            for (ClubMember clubMember : club.getClubInfo().getMember().values()) {
                clearClubMember(clubMember);
            }
        }
        AgentResponse agentResponse = new AgentResponse();
        return agentResponse;
    }

    private void clearClubMember(ClubMember clubMember) {
        LocalDate localDate = LocalDate.now();
        List<String> removeDate = new ArrayList<>();
        clubMember.getStatistics().clear();
//        for (Map.Entry<String, ClubStatistics> entry : clubMember.getStatistics().entrySet()) {
//            String key = entry.getKey();
//            LocalDate beginDateTime = LocalDate.parse(key, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            if (localDate.compareTo(beginDateTime) > 3) {
//                removeDate.add(key);
//            }
//
//        }
//
//        for (String key : removeDate) {
//            System.out.println("删除 " + key);
//            clubMember.getStatistics().remove(key);
//        }

    }

    @RequestMapping("/getClubCreditInfo")
    public Object getClubCreditInfo(String clubId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {

            return club.getClubInfo().getCreditInfo();
        }
        return new HashMap<>();
    }

    @RequestMapping("/editCLubUserScore")
    public Object editCLubUserScore(String clubId, String unionId, double num){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if(club == null) return "-1";
        User user = userService.getUserDao().getUserByUnionId(unionId);
        if(user == null) return "-2";
        ClubMember clubMember = club.getClubInfo().getMember().get("" + user.getId());
        if(clubMember == null) return "-2";
        clubMember.getAllStatistics().setAllScore(clubMember.getAllStatistics().getAllScore() + num);
        return "0";
    }
//    INSERT INTO `summer`.`agent_user` (`id`, `address`, `area`, `cell`, `create_time`, `email`, `gold`, `id_card`, `invite_code`, `level`, `money`, `parent_id`, `parent_pay_deduct`, `parent_share_deduct`, `password`, `pay_deduct`, `real_name`, `share_deduct`, `username`, `agent_info`, `agent_info_record`) VALUES ('1', '1', '1', '13800000000', '2018-07-10 15:05:13', '1234567890@qq.com', '944599988', '120223100000000000', '11', '1', '90180748', '111111', '0.01', '0.02', '111111', '1', '1', '1', 'admin', '{\"allRebate\": 0, \"everyDayCost\": {}, \"everyDayRebate\": {}, \"everyPartnerRebate\": {}}', '{\"clearingRecord\": []}');
//
//    INSERT INTO `summer`.`agent_user` (`id`, `address`, `area`, `cell`, `create_time`, `email`, `gold`, `id_card`, `invite_code`, `level`, `money`, `parent_id`, `parent_pay_deduct`, `parent_share_deduct`, `password`, `pay_deduct`, `real_name`, `share_deduct`, `username`, `agent_info`, `agent_info_record`) VALUES ('2', NULL, NULL, NULL, '2018-09-16 20:32:48', NULL, '0', NULL, NULL, '1', '0', '0', '0', '0', '100003', '0', NULL, '0', '100003', '{\"allRebate\": 0, \"everyDayCost\": {}, \"everyDayRebate\": {}, \"everyPartnerRebate\": {}}', '{\"clearingRecord\": []}');


    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        LocalDate beginDateTime = LocalDate.parse("2017-10-10", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate beginDateTime1 = LocalDate.parse("2018-12-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int rtn = localDate.compareTo(beginDateTime1);
        System.out.println(rtn);
    }

}
