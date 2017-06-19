package com.code.server.login.action;


import com.code.server.constant.game.*;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.UserVo;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.Service.ServerService;
import com.code.server.db.Service.UserRecordService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.*;

import com.code.server.login.config.ServerConfig;
import com.code.server.login.util.MD5Util;
import com.code.server.redis.service.UserRedisService;

import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;


/**
 * Created by win7 on 2017/3/8.
 */

@RestController
@EnableAutoConfiguration
public class LoginAction {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private ServerConfig serverConfig;

//    @Value("serverConfig.serverId")
    public int serverId = 1;

    Constant constant;
    ServerInfo serverInfo;



    private String ip ="192.168.1.132";
    private String port = "8002";



    @Autowired
    public UserRecordService userRecordService;

   @RequestMapping("/hello1")
    public Map<String,Object> hello1(){
        int code = 0;
        Map<String,Object> params = new HashMap<>();

        List<Record.RoomRecord> userRecord = userRecordService.getUserByUserIDAndType(11,3);

        params.put("UserRecord",serverConfig);


        return getParams("hello",params,code);
    }
/*
    @RequestMapping("/hello")
    public Map<String,Object> hello(){
        int code = 0;
        Map<String,Object> params = new HashMap<>();

        //List<Record.RoomRecord> userRecord = userRecordService.getUserByUserRecord(11,1);


        Record.UserRecord UserRecord = new Record.UserRecord();
        UserRecord.setUserId(11);
        UserRecord.setName("测试11");
        UserRecord.setRoomId("789456");
        UserRecord.setScore(-10);

        Record.UserRecord UserRecord1 = new Record.UserRecord();
        UserRecord1.setUserId(12);
        UserRecord1.setName("测试21");
        UserRecord1.setRoomId("789456");
        UserRecord1.setScore(0);

        Record.UserRecord UserRecord2 = new Record.UserRecord();
        UserRecord2.setUserId(12);
        UserRecord2.setName("测试13");
        UserRecord2.setRoomId("789456");
        UserRecord2.setScore(10);

        List<Record.UserRecord> list = new ArrayList<>();
        list.add(UserRecord);
        list.add(UserRecord1);
        list.add(UserRecord2);

        Record.RoomRecord roomRecord = new Record.RoomRecord();
        roomRecord.setType(3);
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setRecords(list);



        UserRecord userRecord = userRecordService.addRecord(11,roomRecord);



        params.put("UserRecord",userRecord);
        return getParams("hello",params,code);
    }*/





    @RequestMapping("/login")
    public Map<String,Object> login( String account,String password,String token_user){
             Map<String,Object> params = new HashMap<>();
             int code = 0;
           String userid = userRedisService.getUserIdByAccount(account);//玩家id

        if(userid==null){
            User user = userService.getUserByAccountAndPassword(account, password);
            //查询数据库，没有新建玩家
            if (user != null) {
                UserBean userBean = new UserBean();
                userBean.setId(user.getUserId());
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
                userBean.setMarquee(getConstant().getMarquee());
                userBean.setDownload2(getConstant().getDownload2());
                userBean.setUserInfo(user.getUserInfo());

                userRedisService.setUserBean(userBean);//userId-userbean
                userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userId-money


                long time = System.currentTimeMillis();

                String token = MD5Util.MD5Encode(time + account + password, "UTF-8");

                userRedisService.setToken(user.getUserId(),token);//userId-token

                userRedisService.setAccountUserId(account,user.getUserId());//account-userId
                userRedisService.setUserIdAccount(user.getUserId(),account);//userId-account

                userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userId
                userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userId-openid

                params.put("token",token);
                params.put("userId",user.getUserId());
            } else {//密码错误
                if (getServerInfo().getAppleCheck() == 1) {
                    user = createUser(account, password);
                    userService.save(user);

                    UserBean userBean = new UserBean();
                    userBean.setId(user.getUserId());
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
                    userBean.setMarquee(getConstant().getMarquee());
                    userBean.setDownload2(getConstant().getDownload2());

                    userRedisService.setUserBean(userBean);//userId-userbean
                    userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userId-money

                    long time = System.currentTimeMillis();

                    String token = MD5Util.MD5Encode(time + account + password, "UTF-8");

                    userRedisService.setToken(user.getUserId(),token);//userId-token

                    userRedisService.setAccountUserId(account,user.getUserId());//account-userId
                    userRedisService.setUserIdAccount(user.getUserId(),account);//userId-account

                    userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userId
                    userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userId-openid


                    params.put("user",getUserVo(user));
                    params.put("token",token);
                    params.put("userId",user.getUserId());
                } else {
                    code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
                }
            }
        }else{
            String redisTokey = userRedisService.getToken(Long.valueOf(userid));

//            String redisTokey = null;
            //判断token是否存在redis
            if(token_user!=null && redisTokey!=null){
                if(token_user.equals(redisTokey)){
                    params.put("token",token_user);
                }else{
                    code = ErrorCode.REDIS_NO_TOKEN;
                }
            }else {
                UserBean userBeanRedis = userRedisService.getUserBean(Long.valueOf(userid));
                //判断redis是否有UserBean
                if(userBeanRedis!=null){
                    if(password.equals(userBeanRedis.getPassword())){
                        params.put("user",userBeanRedis);
                    }else{
                        code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
                    }
                }else{
                    code = ErrorCode.DATE_ERROR_PLEASE_REFRESH;
                }
            }
        }

        params.put("ip",ip);
        params.put("port",port);
        return getParams("login",params,code);
    }



    @RequestMapping("/checkOpenId")
    public Map<String,Object> checkOpenId(String openId,String username,String image,int sex,String token_user){


        int code = 0;
        User newuser = new User();
        Map<String,Object> params = new HashMap<>();

        String userid = userRedisService.getUserIdByOpenId(openId);

        if(userid==null){
           User user = userService.getUserByOpenId(openId);

            String img = image;
            if(img == null || img.equals("")){
                img = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96";
            }
            if(user == null) {

//                user.setId(0);
                //user.setUserId(new IdWorker(serverId,1).nextId());
                newuser.setOpenId(openId);
                newuser.setAccount(UUID.randomUUID().toString());
                newuser.setPassword("111111");
                try {
                    newuser.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //newuser.setUsername("123456");
                newuser.setImage(img);
                newuser.setSex(sex);
                newuser.setVip(0);
                newuser.setUuid("0");
                newuser.setMoney(getConstant().getInitMoney());
                newuser = userService.save(newuser);

                params.put("user",getUserVo(newuser));
            }else{
                try {
                    newuser.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                newuser.setImage(image);
                newuser.setSex(sex);
                userService.save(newuser);

                params.put("user",getUserVo(newuser));
            }


            UserBean userBean = new UserBean();
            userBean.setId(newuser.getUserId());
            userBean.setUsername(newuser.getUsername());
            userBean.setImage(newuser.getImage());
            userBean.setAccount(newuser.getAccount());
            userBean.setPassword(newuser.getPassword());
            userBean.setIpConfig(newuser.getIpConfig());
            userBean.setMoney(newuser.getMoney());
            userBean.setVip(newuser.getVip());
            userBean.setUuid(newuser.getUuid());
            userBean.setOpenId(newuser.getOpenId());
            userBean.setSex(newuser.getSex());
            userBean.setMarquee(getConstant().getMarquee());
            userBean.setDownload2(getConstant().getDownload2());

            userRedisService.setUserBean(userBean);//userId-userbean
            userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userId-money


            long time = System.currentTimeMillis();

            String token = MD5Util.MD5Encode(time + newuser.getAccount() + newuser.getPassword(), "UTF-8");

            userRedisService.setToken(user.getUserId(),token);//userId-token

            userRedisService.setAccountUserId(user.getAccount(),user.getUserId());//account-userId
            userRedisService.setUserIdAccount(user.getUserId(),user.getAccount());//userId-account

            userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userId
            userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userId-openid

            params.put("userId",newuser.getUserId());
            params.put("token",token);
        }else{
            String redisTokey =userRedisService.getToken(Long.valueOf(userid));
            //判断token是否存在redis
            if(token_user!=null && redisTokey!=null){
                if(token_user.equals(redisTokey)){
                    params.put("token",token_user);
                }else{
                    code = ErrorCode.REDIS_NO_TOKEN;
                }
            }
        }


        params.put("ip",ip);
        params.put("port",port);

        return getParams("checkOpenId",params,code);
    }

    @RequestMapping("/appleCheck")
    public Map<String,Object> appleCheck(){
        Map<String,Object> params = new HashMap<>();
        params.put("isInAppleCheck",getServerInfo().getAppleCheck());
        params.put("address",getConstant().getDownload());
        params.put("appleVersion",getServerInfo().getVersionOfIos());
        params.put("androidVersion",getServerInfo().getVersionOfAndroid());
        params.put("ip",ip);
        params.put("port",port);
        return getParams("appleCheck",params,0);
    }

    @RequestMapping("/refreshMemory")
    public Map<String,Object> refreshMemory(){
        Map<String,Object> params = new HashMap<>();
        constant = constantService.constantDao.findOne(1L);
        serverInfo = serverService.getAllServerInfo().get(0);
        params.put("constant",constant);
        params.put("serverInfo",serverInfo);
        return getParams("refreshMemory",params,0);
    }

    private Constant getConstant(){
        if(constant==null){
            constant = constantService.constantDao.findOne(1L);
        }
        return constant;
    }

    private ServerInfo getServerInfo(){
        if(serverInfo==null){
            serverInfo = serverService.getAllServerInfo().get(0);
        }
        return serverInfo;
    }




    @RequestMapping("/appleCheck123")
    public Map<String,Object> appleCheck123(){
        Map<String,Object> params = new HashMap<>();
        User user = new User();

        user.setOpenId("1");
        user.setAccount(UUID.randomUUID().toString());
        user.setPassword("111111");
        try {
            user.setUsername(URLDecoder.decode("123", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        user.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96");
        user.setSex(1);
        user.setVip(0);
        user.setUuid("0");
        user.setMoney(getConstant().getInitMoney());
        user = userService.save(user);
        params.put("user",user);
        return getParams("appleCheck123",params,0);
    }


    public Map<String,Object> getParams(String url,Object params,int code){
        Map<String,Object> results = new HashMap<>();
        results.put("url",url);
        results.put("params",params);
        results.put("code",code);
        return results;
    }

    public UserVo getUserVo(User user){
        UserVo vo = new UserVo();

        vo.setId(user.getUserId());
        vo.setIpConfig(user.getIpConfig());
        vo.setAccount(user.getAccount());
        vo.setImage(user.getImage());
        vo.setMarquee(getConstant().getMarquee());
        vo.setDownload2(getConstant().getDownload2());
        vo.setSex(user.getSex());
        vo.setOpenId(user.getOpenId());
        vo.setMoney(user.getMoney());
        vo.setVip(user.getVip());
        vo.setUsername(user.getUsername());
        vo.setReferee(user.getReferee());
        vo.setUserInfo(user.getUserInfo());

        String room = userRedisService.getRoomId(user.getUserId());
        if (room!=null) {
            vo.setRoomId(room);
        } else {
            vo.setRoomId("0");
        }
        return vo;
    }

    private User createUser(String account,String password){
        User newUser = new User();

        newUser.setAccount(account);
        newUser.setPassword(password);
        newUser.setOpenId("" + new IdWorker(serverId,1).nextId());
        try {
            newUser.setUsername(URLDecoder.decode(account, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        newUser.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96");
        newUser.setSex(1);
        newUser.setVip(0);
        newUser.setUuid("0");
        newUser.setMoney(getConstant().getInitMoney());

        return newUser;
    }
    /**
     *  支付demo
     * @return
     */
    private void httpUrlConnection() {

    }



    public static class Test{
        int a = 1;
        String b = "";
    }
}
