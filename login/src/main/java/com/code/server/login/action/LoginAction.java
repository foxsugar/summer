package com.code.server.login.action;


import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.UserVo;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.Service.ServerService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Constant;
import com.code.server.db.model.User;


import com.code.server.login.util.MD5Util;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;

import com.code.server.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.data.redis.core.HashOperations;
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

//    @Value("serverConfig.serverId")
    public int serverId = 1;

    private Constant getConstant(){
        return constantService.constantDao.findOne(1L);
    }

    @RequestMapping("/login")
    public Map<String,Object> login( String account,String password,String token_user){
             List<Object> params = new ArrayList<>();
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

                userRedisService.setUserBean(userBean);//userid-userbean
                userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userid-money


                long time = System.currentTimeMillis();

                String token = MD5Util.MD5Encode(time + account + password, "UTF-8");

                userRedisService.setToken(user.getUserId(),token);//userid-token

                userRedisService.setAccountUserId(account,user.getUserId());//account-userid
                userRedisService.setUserIdAccount(user.getUserId(),account);//userid-account

                userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userid
                userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userid-openid

                params.add(token);
                params.add(user.getUserId());
            } else {//密码错误
                if (serverService.getAllServerInfo().get(0).getAppleCheck() == 1) {
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

                    userRedisService.setUserBean(userBean);//userid-userbean
                    userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userid-money

                    long time = System.currentTimeMillis();

                    String token = MD5Util.MD5Encode(time + account + password, "UTF-8");

                    userRedisService.setToken(user.getUserId(),token);//userid-token

                    userRedisService.setAccountUserId(account,user.getUserId());//account-userid
                    userRedisService.setUserIdAccount(user.getUserId(),account);//userid-account

                    userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userid
                    userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userid-openid

                    params.add(getUserVo(user));
                    params.add(token);
                    params.add(user.getUserId());
                } else {
                    code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
                }
            }
        }else{
            String redisTokey =userRedisService.getToken(userid);
            //判断token是否存在redis
            if(token_user!=null && redisTokey!=null){
                if(token_user.equals(redisTokey)){
                    params.add(token_user);
                }else{
                    code = ErrorCode.REDIS_NO_TOKEN;
                }
            }else {
                UserBean userBeanRedis = userRedisService.getUserBean(userid);
                //判断redis是否有UserBean
                if(userBeanRedis!=null){
                    if(password.equals(userBeanRedis.getPassword())){
                        params.add(userBeanRedis);
                    }else{
                        code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
                    }
                }else{
                    code = ErrorCode.DATE_ERROR_PLEASE_REFRESH;
                }
            }
        }

        return getParams("login",params,code);
    }


    @RequestMapping("/checkOpenId")
    public Map<String,Object> checkOpenId( final String openId,String username,final String image,int sex,String token_user){
        int code = 0;
        User user = new User();
        List<Object> params = new ArrayList<>();

        Long userid = userRedisService.getUserIdByOpenId(openId);

        if(userid==null){
            user = userService.getUserByOpenId(openId);

            String img = image;
            if(img == null || img.equals("")){
                img = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96";
            }
            if(user == null) {
                user = new User();
//                user.setId(0);
//                user.setUserId(GameManager.getInstance().nextId());
                user.setOpenId(openId);
                user.setAccount(UUID.randomUUID().toString());
                user.setPassword("111111");
                try {
                    user.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                user.setImage(img);
                user.setSex(sex);
                user.setVip(0);
                user.setUuid("0");
                user.setMoney(getConstant().getInitMoney());
                userService.save(user);

                params.add(getUserVo(user));
            }else{
                try {
                    user.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                user.setImage(image);
                user.setSex(sex);
                userService.save(user);

                params.add(getUserVo(user));
            }


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

            userRedisService.setUserBean(userBean);//userid-userbean
            userRedisService.setUserMoney(user.getUserId(), user.getMoney());//userid-money


            long time = System.currentTimeMillis();

            String token = MD5Util.MD5Encode(time + user.getAccount() + user.getPassword(), "UTF-8");

            userRedisService.setToken(user.getUserId(),token);//userid-token

            userRedisService.setAccountUserId(user.getAccount(),user.getUserId());//account-userid
            userRedisService.setUserIdAccount(user.getUserId(),user.getAccount());//userid-account

            userRedisService.setOpenIdUserId(user.getOpenId(),user.getUserId());//openid-userid
            userRedisService.setUserIdOpenId(user.getUserId(),user.getOpenId());//userid-openid

            params.add(token);
        }else{
            String redisTokey =userRedisService.getToken(userid);
            //判断token是否存在redis
            if(token_user!=null && redisTokey!=null){
                if(token_user.equals(redisTokey)){
                    params.add(token_user);
                }else{
                    code = ErrorCode.REDIS_NO_TOKEN;
                }
            }
        }

        return getParams("checkOpenId",params,code);
    }

    @RequestMapping("/appleCheck")
    public Map<String,Object> appleCheck(){
        Map<String,Object> params = new HashMap<>();
        params.put("isInAppleCheck",serverService.getAllServerInfo().get(0).getAppleCheck());
        params.put("address",getConstant().getDownload());
        params.put("appleVersion",serverService.getAllServerInfo().get(0).getVersionOfIos());
        params.put("androidVersion",serverService.getAllServerInfo().get(0).getVersionOfAndroid());
        return getParams("appleCheck",params,0);
    }

    @RequestMapping("/")
    public Map<String,Object> test(){
//        RedisManager.getUserRedisService().addUserMoney("", 1);
        UserBean userBean = new UserBean();
        userBean.setGold(1);
        ValueOperations<String,UserBean> user_money = redisTemplate.opsForValue();
        user_money.set("testString",userBean);
        Map<String,Object> params = new HashMap<>();
        return params;
    }

    public Map<String,Object> getParams(String url , Object params,int code){
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

    public static void main(String[] args) {
        Map<Integer,Integer> map = new HashMap<>();
        List<Test> list = new ArrayList<>();
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());

        map.put(1, 2);
        map.put(2, 2);
        map.put(3, 2);
        map.put(4, 2);
        map.put(5, 2);


    }


    public static class Test{
        int a = 1;
        String b = "";
    }
}
