package com.code.server.login.service;


import com.code.server.constant.game.Record;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.UserVo;
import com.code.server.db.Service.UserRecordService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.User;
import com.code.server.db.model.UserRecord;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.rpc.RpcManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
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
                }

            }
        } else {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }
        return 0;
    }


    public static UserBean user2userBean(User user) {
        UserBean userBean = new UserBean();

        userBean.setId(user.getId());
        userBean.setUsername(user.getUsername());
        userBean.setImage(user.getImage());
        userBean.setAccount(user.getAccount());
        userBean.setPassword(user.getPassword());
        userBean.setIpConfig(user.getIpConfig());
        userBean.setMoney(user.getMoney());
        userBean.setVip(user.getVip());
        userBean.setUuid(user.getUuid());
        userBean.setOpenId(user.getOpenId());
        userBean.setSex(user.getSex());
        userBean.setUserInfo(user.getUserInfo());

        return userBean;
    }

    public static User userBean2User(UserBean userBean) {
        User user = new User();

        user.setId(userBean.getId());
        user.setUsername(userBean.getUsername());
        user.setImage(userBean.getImage());
        user.setAccount(userBean.getAccount());
        user.setPassword(userBean.getPassword());
        user.setIpConfig(userBean.getIpConfig());
        user.setMoney(userBean.getMoney());
        user.setVip(userBean.getVip());
        user.setUuid(userBean.getUuid());
        user.setOpenId(userBean.getOpenId());
        user.setSex(userBean.getSex());
        user.setUserInfo(userBean.getUserInfo());
        user.setGold(userBean.getGold());

        return user;
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
        sendMsg(msgKey, new ResponseVo("userService","getServerInfo",ServerManager.constant));
        return 0;
    }

    public int reportingCoord(KafkaMsgKey msgKey,String coord) {
        long userId = msgKey.getUserId();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setCoord(coord);
            RedisManager.getUserRedisService().setUserBean(userBean);
        }
        sendMsg(msgKey, new ResponseVo("userService","reportingCoord",0));
        return 0;
    }

    /**
     * 获得同一房间的所有人坐标
     * @param msgKey
     * @return
     */
    public int getCoords(KafkaMsgKey msgKey){
        long userId = msgKey.getUserId();
        String roomId = RedisManager.getUserRedisService().getRoomId(userId);
        if (roomId == null) {
            return ErrorCode.CAN_NOT_NO_ROOM;
        }
        Set<Long> users = RedisManager.getRoomRedisService().getUsers(roomId);
        Map<Long,Object> result = RedisManager.getUserRedisService().getUserBeans(users).stream().collect(Collectors.toMap(UserBean::getId,UserBean::getCoord));
        sendMsg(msgKey, new ResponseVo("userService","getCoords",result));
        return 0;
    }

    /**
     * 查询战绩
     *
     * @param msgKey
     * @return
     */
    public int getUserRecodeByUserId(KafkaMsgKey msgKey,String roomType) {
        UserRecord userRecord = userRecordService.getUserRecordByUserId(msgKey.getUserId());
        List<Record.RoomRecord> roomRecordList = new ArrayList<>();
        if (userRecord != null) {
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
        userBean.setReferee(referrerId);
        RedisManager.getUserRedisService().addUserMoney(msgKey.getUserId(), 10);

        ResponseVo vo = new ResponseVo("userService", "bindReferrer", 0);
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

//    private User createUser(String account, String password){
//        User newUser = new User();
////        newUser.setId(-100);
////        newUser.setId(GameManager.getInstance().nextId());
//        newUser.setAccount(account);
//        newUser.setPassword(password);
//        newUser.setOpenId(""+GameManager.getInstance().nextId());
//        try {
//            newUser.setUsername(URLDecoder.decode(account, "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        newUser.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96");
//        newUser.setSex(1);
//        newUser.setVip(0);
//        newUser.setUuid("0");
//        newUser.setMoney(GameManager.getInstance().constant.getInitMoney());
//
//        return newUser;
//    }
//
//    public int getUserMessage(long userId,ChannelHandlerContext ctx) {
//
//        ThreadPool.getInstance().executor.execute(()->{
//            UserService userService = SpringUtil.getBean(UserService.class);
//            Player player  = GameManager.getInstance().players.get(userId);
//            User user = null;
//            if (player != null) {
//                user = player.getUser();
//            } else {
//                user = userService.getUserByUserId(userId);
//            }
//            ResponseVo vo = new ResponseVo("userService","getUserMessage",getUserVo(user));
//            MsgDispatch.sendMsg(ctx,vo);
//        });
//        return 0;
//    }
//
//    public int getUserImage(long userId,ChannelHandlerContext ctx) {
//        ThreadPool.getInstance().executor.execute(()->{
//            UserService userService = SpringUtil.getBean(UserService.class);
//            Player player  = GameManager.getInstance().players.get(userId);
//            User user = null;
//            if (player != null) {
//                user = player.getUser();
//            } else {
//                user = userService.getUserByUserId(userId);
//            }
//
//            JSONObject jSONObject = new JSONObject();
//            jSONObject.put("Image", user.getImage());
//
//            ResponseVo vo = new ResponseVo("userService","getUserImage",jSONObject);
//            MsgDispatch.sendMsg(ctx,vo);
//        });
//        return 0;
//    }
//
//
//    public int register(long userId,ChannelHandlerContext ctx) {
//
//        ThreadPool.getInstance().executor.execute(()->{
//            UserService userService = SpringUtil.getBean(UserService.class);
//            Player player  = GameManager.getInstance().players.get(userId);
//            User user = null;
//            if (player != null) {
//                user = player.getUser();
//            } else {
//                user = userService.getUserByUserId(userId);
//            }
//            if(user==null){
//                ResponseVo vo = new ResponseVo("userService","register",0);
//                MsgDispatch.sendMsg(ctx,vo);
//            }else{
//                ResponseVo vo = new ResponseVo("userService","register",getUserVo(user));
//                MsgDispatch.sendMsg(ctx,vo);
//            }
//        });
//        return 0;


//    }
}
