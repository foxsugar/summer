package com.code.server.login.action;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.ThreeRebate;
import com.code.server.constant.game.*;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.*;
import com.code.server.db.model.*;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
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

    @Autowired
    private RecommendService recommendService;
    @Autowired
    private ChargeService chargeService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private PhoneService phoneService;

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


    @RequestMapping("/getChargeRecord")
    public int getChargeRecord(long userId, String type) {

        List<Charge> list = chargeService.chargeDao.getChargesByUserid(userId, type);
        System.out.println(list);
        return 0;
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


//    login?tid=2709363745&openid=1003Page%20not%20found%20at%20/login HTTP/1.1" 404 7116


    @RequestMapping("/getTid")
    public Object getTid(String tid) {

        return "您当前群聊id为: " + tid;
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
    @RequestMapping("/test11111")
    public Object test11111(){
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setId(123456);
        com.code.server.constant.game.UserRecord  userRecord = new com.code.server.constant.game.UserRecord();
        userRecord.setName("a").setImage("httt").setScore(1).setUserId(1);
        roomRecord.addRecord(userRecord);

        Club club = new Club();
        club.getClubInfo().setDuoliaoTid("2709363745");

//        DuoLiaoService.sendRecord(roomRecord, club);


        DuoLiaoService.bind("111111", "2709363745");
        return "";
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
    @RequestMapping("/uploadPhone")
    public Object uploadPhone(String phone, String gameType) {
        Phone phone1 = new Phone();
        phone1.setId(phone);
        phone1.setGameType(gameType);
        phoneService.getPhoneDao().save(phone1);
        return "success";
    }

    @RequestMapping("/bindReferrer")
    public Object bindReferrer(long userId, long referrer){
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setReferee((int)referrer);
            RedisManager.getUserRedisService().updateUserBean(userId, userBean);
            userService.save(GameUserService.userBean2User(userBean));
        }else{
            User user = userService.getUserByUserId(userId);
            user.setReferee((int)referrer);
            userService.save(user);
        }
        CenterMsgService.addRebate(userId, 0);
        return 0;
    }
    @RequestMapping("/setVip")
    public Object setVip(long userId, int vip){
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setVip(vip);
            RedisManager.getUserRedisService().updateUserBean(userId, userBean);
            userService.save(GameUserService.userBean2User(userBean));
        }
        return 0;
    }

    @RequestMapping("/setUserInfo")
    public Object setUserInfo(UserBean userVo){
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userVo.getId());
        if (userBean != null) {
            userBean.setPassword(userVo.getPassword());
            userBean.setImage(userVo.getImage());
            userBean.setSex(userVo.getSex());
            
            //因为编码问题 先不更新用户名
//            userBean.setUsername(userVo.getUsername());
            RedisManager.getUserRedisService().updateUserBean(userVo.getId(),userBean);
            userService.save(GameUserService.userBean2User(userBean));
        }
        return 0;
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
        List<User> userList = userService.getUserDao().getUserByUnionId(unionId);
        if(userList == null || userList.size()==0 ) return "-2";
        User user = userList.get(0);
        ClubMember clubMember = club.getClubInfo().getMember().get("" + user.getId());
        if(clubMember == null) return "-2";
        clubMember.getAllStatistics().setAllScore(clubMember.getAllStatistics().getAllScore() + num);
        return "0";
    }
//    INSERT INTO `summer`.`agent_user` (`id`, `address`, `area`, `cell`, `create_time`, `email`, `gold`, `id_card`, `invite_code`, `level`, `money`, `parent_id`, `parent_pay_deduct`, `parent_share_deduct`, `password`, `pay_deduct`, `real_name`, `share_deduct`, `username`, `agent_info`, `agent_info_record`) VALUES ('1', '1', '1', '13800000000', '2018-07-10 15:05:13', '1234567890@qq.com', '944599988', '120223100000000000', '11', '1', '90180748', '111111', '0.01', '0.02', '111111', '1', '1', '1', 'admin', '{\"allRebate\": 0, \"everyDayCost\": {}, \"everyDayRebate\": {}, \"everyPartnerRebate\": {}}', '{\"clearingRecord\": []}');
//
//    INSERT INTO `summer`.`agent_user` (`id`, `address`, `area`, `cell`, `create_time`, `email`, `gold`, `id_card`, `invite_code`, `level`, `money`, `parent_id`, `parent_pay_deduct`, `parent_share_deduct`, `password`, `pay_deduct`, `real_name`, `share_deduct`, `username`, `agent_info`, `agent_info_record`) VALUES ('2', NULL, NULL, NULL, '2018-09-16 20:32:48', NULL, '0', NULL, NULL, '1', '0', '0', '0', '0', '100003', '0', NULL, '0', '100003', '{\"allRebate\": 0, \"everyDayCost\": {}, \"everyDayRebate\": {}, \"everyPartnerRebate\": {}}', '{\"clearingRecord\": []}');


    @RequestMapping("/bindIp")
    public Object bingIp(long userId, String ip) {
        Recommend recommend = new Recommend();
        recommend.setUnionId(ip);
        recommend.setAgentId(userId);
        recommendService.getRecommendDao().save(recommend);
        return 0;
    }

    @RequestMapping("/repairRobotRebate")
    public Object repairRobotRebate(long userId){
        for (User user : userService.getUserDao().findAll()) {
            if (user.getReferee() == userId) {
                CenterMsgService.addRebate(user.getId(), 0D);
            }
        }
        return 0;
    }

//    @RequestMapping("/clearRebate")
//    public Object clearRebate() {
//        for(String userId : RedisManager.getUserRedisService().getAllUserId()){
//            long uid = Long.valueOf(userId);
//            RedisManager.getUserRedisService().setUserGold(uid,0);
//            UserBean userBean = RedisManager.getUserRedisService().getUserBean(uid);
//            userBean.setGold(0);
//            userBean.getUserInfo().setAllRebate(0);
//            RedisManager.getUserRedisService().updateUserBean(uid, userBean);
//
//        }
//        return 0;
//    }

    @RequestMapping("/setFixNum")
    public Object setFixNum(long userId, int childNum, double weekRebate, double allRebate){
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.getUserInfo().setPlayGameTime(childNum);
            userBean.getUserInfo().setChargeMoneyNum(weekRebate);
            userBean.getUserInfo().setChargeGoldNum(allRebate);
            RedisManager.getUserRedisService().updateUserBean(userId, userBean);
        }
        return 0;
    }

    @RequestMapping("/setKefu")
    public Object setKefu(String key, String value){
        Constant constant = ServerManager.constant;
        constant.getOther().getKefu().put(key, value);
        constantService.constantDao.save(constant);
        return 0;
    }

    @RequestMapping("/getRebateInfo")
    public Object getRebateInfo(long userId) {

        List<UserBean> allUserBean = RedisManager.getUserRedisService().getAllUserBean();
        Map<Integer,UserBean> firstLevel = new HashMap<>();
        Map<Integer,UserBean> secondLevel = new HashMap<>();
        Map<Integer,UserBean> thirdLevel = new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        final double[] firstContribute = {0};
        final double[] secondContribute = {0};
        final double[] thirdContribute = {0};

        final double[] firstRebate = {0};
        final double[] secondRebate = {0};
        final double[] thirdRebate = {0};


        final double[] allFirstContribute = {0};
        final double[] allSecondContribute = {0};
        final double[] allThirdContribute = {0};

        final double[] allFirstRebate = {0};
        final double[] allSecondRebate = {0};
        final double[] allThirdRebate = {0};

        List<String> thisWeekDays = DateUtil.getThisWeekDay();

        allUserBean.forEach(userBean -> {
            if(userBean.getReferee() == userId){
                firstLevel.put((int)userBean.getId(), userBean);
                ThreeRebate allThreeRebate = userBean.getUserInfo().getThreeRebate().get("all");
                if (allThreeRebate != null) {

                    allFirstContribute[0] +=  allThreeRebate.getContribute();
                    allFirstRebate[0] +=  allThreeRebate.getFirst();
                }
                for (String date : thisWeekDays) {
                    ThreeRebate threeRebate = userBean.getUserInfo().getThreeRebate().get(date);

                    if (threeRebate != null) {
                        firstContribute[0] +=  threeRebate.getContribute();
                        firstRebate[0] +=  threeRebate.getFirst();
                    }
                }
            }
        });

        allUserBean.forEach(userBean -> {
            if(firstLevel.containsKey(userBean.getReferee())){
                secondLevel.put((int)userBean.getId(), userBean);
                ThreeRebate allThreeRebate = userBean.getUserInfo().getThreeRebate().get("all");
                if (allThreeRebate != null) {

                    allSecondContribute[0] +=  allThreeRebate.getContribute();
                    allSecondRebate[0] +=  allThreeRebate.getSecond();
                }

                for (String date : thisWeekDays) {
                    ThreeRebate threeRebate = userBean.getUserInfo().getThreeRebate().get(date);
                    if (threeRebate != null) {
                        secondContribute[0] += threeRebate.getContribute();
                        secondRebate[0] += threeRebate.getSecond();
                    }
                }
            }
        });

        allUserBean.forEach(userBean -> {
            if(secondLevel.containsKey(userBean.getReferee())){
                thirdLevel.put((int)userBean.getId(), userBean);
                ThreeRebate allThreeRebate = userBean.getUserInfo().getThreeRebate().get("all");
                if (allThreeRebate != null) {

                    allThirdContribute[0] +=  allThreeRebate.getContribute();
                    allThirdRebate[0] +=  allThreeRebate.getThird();
                }

                for (String date : thisWeekDays) {
                    ThreeRebate threeRebate = userBean.getUserInfo().getThreeRebate().get(date);
                    if (threeRebate != null) {
                        thirdContribute[0] += threeRebate.getContribute();
                        thirdRebate[0] += threeRebate.getThird();
                    }
                }
            }
        });

        List<Object> firstUser = new ArrayList<>();
        List<Object> secondUser = new ArrayList<>();
        List<Object> thirdUser = new ArrayList<>();
        firstLevel.values().forEach(userBean -> {
            firstUser.add(userBean.toVo(false));
        });
        secondLevel.values().forEach(userBean -> {
            secondUser.add(userBean.toVo(false));
        });
        thirdLevel.values().forEach(userBean -> {
            thirdUser.add(userBean.toVo(false));
        });
        result.put("firstNum", firstLevel.size());//一级人数
        result.put("secondNum", secondLevel.size());//二级人数
        result.put("thirdNum", thirdLevel.size());//三级人数

        result.put("firstContribute", firstContribute[0]);//本周一级贡献
        result.put("secondContribute", secondContribute[0]);//本周二级贡献
        result.put("thirdContribute", thirdContribute[0]);//本周三级贡献

        result.put("firstRebate", firstRebate[0]);//本周一级返利
        result.put("secondRebate", secondRebate[0]);//本周二级返利
        result.put("thirdRebate", thirdRebate[0]);//本周三级返利


        result.put("allFirstContribute", allFirstContribute[0]);//全部一级贡献
        result.put("allSecondContribute", allSecondContribute[0]);//全部二级贡献
        result.put("allThirdContribute", allThirdContribute[0]);//全部三级贡献

        result.put("allFirstRebate", allFirstRebate[0]);//全部一级返利
        result.put("allSecondRebate", allSecondRebate[0]);//全部二级返利
        result.put("allThirdRebate", allThirdRebate[0]);//全部三级返利

        result.put("firstLevelUser", firstUser);//一级所有玩家
        result.put("secondLevelUser", secondUser);//二级所有玩家
        result.put("thirdLevelUser", thirdUser);//三级所有玩家

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);

        result.put("fixNum", userBean.getUserInfo().getPlayGameTime());//修正玩家人数
        result.put("fixRebate", userBean.getUserInfo().getChargeMoneyNum());//修正本周数据
        result.put("fixAllRebate", userBean.getUserInfo().getChargeGoldNum());//修正历史数据


        return result;
    }
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        LocalDate beginDateTime = LocalDate.parse("2017-10-10", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate beginDateTime1 = LocalDate.parse("2018-12-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int rtn = localDate.compareTo(beginDateTime1);
        System.out.println(rtn);
    }

}
