package com.code.server.login.action;


import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Constant;
import com.code.server.db.model.User;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.GameUserService;
import com.code.server.login.service.LoginService;
import com.code.server.login.service.ServerManager;
import com.code.server.login.util.MD5Util;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    private UserRedisService userRedisService;

    @Autowired
    private ConstantService constantService;

    //    @Value("serverConfig.serverId")




    private String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    private int login4sqlByAccount(String account, String password, Map<String, Object> params) {
        User user = userService.getUserByAccountAndPassword(account, password);
        //查询数据库，没有新建玩家
        if (user == null) {
            //新建玩家
            if (getConstant().getAppleCheck() == 1) {
                user = createUser(account, password);
                userService.save(user);
            } else {
                return ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
            }
        }
        String token = getToken(user.getId());
        saveUser2Redis(user, token);

        params.put("token", token);
        params.put("userId", user.getId());
        return 0;
    }

    private int login4sqlByOpenId(String openId, String userName, String img, int sex, Map<String, Object> params) {
        User user = userService.getUserByOpenId(openId);
        //查询数据库，没有新建玩家
        if (user == null) {
            //新建玩家
            user = createUser(openId, userName, img, sex);
            userService.save(user);
        }
        String token = getToken(user.getId());
        saveUser2Redis(user, token);

        params.put("token", token);
        params.put("userId", ""+user.getId());
        return 0;
    }


    private void saveUser2Redis(User user, String token) {
        UserBean userBean = GameUserService.user2userBean(user);


        userRedisService.setUserBean(userBean);//userId-userbean
        userRedisService.setUserMoney(user.getId(), user.getMoney());//userId-money

        userRedisService.setToken(user.getId(), token);//userId-token

        userRedisService.setAccountUserId(user.getAccount(), user.getId());//account-userId
        userRedisService.setUserIdAccount(user.getId(), user.getAccount());//userId-account

        userRedisService.setOpenIdUserId(user.getOpenId(), user.getId());//openid-userId
        userRedisService.setUserIdOpenId(user.getId(), user.getOpenId());//userId-openid
    }

    @RequestMapping("/login")
    public Map<String, Object> login(String account, String password, String token_user) {
        Map<String, Object> params = new HashMap<>();
        int code = 0;
        //redis里的数据
        String userId = userRedisService.getUserIdByAccount(account);//玩家id

        if (userId == null) {
            code = login4sqlByAccount(account, password, params);
            userId = String.valueOf(params.get("userId"));
        } else {
            String redisToken = userRedisService.getToken(Long.valueOf(userId));
            UserBean userBean = userRedisService.getUserBean(Long.valueOf(userId));
            if (!password.equals(userBean.getPassword())) {
                code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;
            } else {
                redisToken = getToken(Long.valueOf(userId));
                userRedisService.setToken(Long.valueOf(userId), redisToken);
            }

            params.put("token", redisToken);
            params.put("userId", userId);
        }


        setHostAndPort(userId,params, code == 0);
        System.err.println(params);
        return getParams("login", params, code);
    }

    private void setHostAndPort(String userId, Map<String, Object> params, boolean isSet) {
        if(isSet){

            String gateId = RedisManager.getUserRedisService().getGateId(Long.valueOf(userId));
            com.code.server.redis.config.ServerInfo serverInfo = null;
            if (gateId != null) {
                serverInfo = RedisManager.getGateRedisService().getServerInfo(gateId);
            }
            if (serverInfo == null) {
                serverInfo = LoginService.getSortedServer("GATE");
            }
            if (serverInfo != null && isSet) {
                params.put("port", serverInfo.getPort());
                params.put("ip", serverInfo.getHost());
                params.put("domain", serverInfo.getDomain());

            }
        }
    }


    @RequestMapping("/checkOpenId")
    public Map<String, Object> checkOpenId(String openId, String username, String image, int sex, String token_user) {


        int code = 0;

        Map<String, Object> params = new HashMap<>();

        String userId = userRedisService.getUserIdByOpenId(openId);

        if (userId == null) {
            code = login4sqlByOpenId(openId, username, image, sex, params);
            userId = (String)params.get("userId");
        } else {
            //刷新redis数据
            UserBean userBean = userRedisService.getUserBean(Long.valueOf(userId));
            if (userBean != null) {
                userBean.setUsername(username);
                userBean.setImage(image);
                userBean.setSex(sex);
                userRedisService.updateUserBean(Long.valueOf(userId), userBean);
            }
            String redisToken =  getToken(Long.valueOf(userId));
            userRedisService.setToken(Long.valueOf(userId), redisToken);
            params.put("token", redisToken);
            params.put("userId", userId);
        }

        setHostAndPort(userId,params, code == 0);

        return getParams("checkOpenId", params, code);
    }

    @RequestMapping("/appleCheck")
    public Map<String, Object> appleCheck() {
        Map<String, Object> params = new HashMap<>();
        params.put("isInAppleCheck", getConstant().getAppleCheck());
        params.put("address", getConstant().getDownload());
        params.put("appleVersion", getConstant().getVersionOfIos());
        params.put("androidVersion", getConstant().getVersionOfAndroid());
        return getParams("appleCheck", params, 0);
    }

    @RequestMapping("/refreshMemory")
    public Map<String, Object> refreshMemory() {
        Map<String, Object> params = new HashMap<>();
        ServerManager.constant = constantService.constantDao.findOne(1L);

        params.put("constant", ServerManager.constant);

        return getParams("refreshMemory", params, 0);
    }

    private Constant getConstant() {
        if (ServerManager.constant  == null) {
            ServerManager.constant  = constantService.constantDao.findOne(1L);
        }
        return ServerManager.constant ;
    }


    @RequestMapping("/")
    public Map<String, Object> test() {
//        RedisManager.getUserRedisService().addUserMoney("", 1);
        int partition = 0;
        KafkaMsgKey kafkaKey = new KafkaMsgKey();
        kafkaKey.setUserId(3);
        kafkaKey.setPartition(1);
        String keyJson = JsonUtil.toJson(kafkaKey);
        SpringUtil.getBean(MsgProducer.class).send("gamePaijiuService", kafkaKey, "hello");

        Map<String, Object> params = new HashMap<>();
        System.out.println("====");
        return params;
    }


    public Map<String, Object> getParams(String url, Object params, int code) {
        Map<String, Object> results = new HashMap<>();
        results.put("url", url);
        results.put("params", params);
        results.put("code", code);
        return results;
    }


    /**
     * 创建玩家
     *
     * @param account
     * @param password
     * @return
     */
    private User createUser(String account, String password) {
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        User newUser = new User();
        newUser.setAccount(account);
        newUser.setPassword(password);
        newUser.setOpenId("" + new IdWorker(serverConfig.getServerId(), 1).nextId());
        newUser.setUsername(decodeStr(account));
        newUser.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96");
        newUser.setSex(1);
        newUser.setVip(0);
        newUser.setUuid("0");
        newUser.setMoney(getConstant().getInitMoney());
        newUser.setRegistDate(new Date());
        newUser.setLastLoginDate(new Date());

        return newUser;
    }

    /**
     * 创建玩家
     *
     * @param openId
     * @param username
     * @param image
     * @param sex
     * @return
     */
    private User createUser(String openId, String username, String image, int sex) {
        User newUser = new User();
        newUser.setAccount(openId);
        newUser.setPassword("111111");
        newUser.setOpenId(openId);
        newUser.setUsername(decodeStr(username));
        newUser.setImage(image);
        newUser.setSex(sex);
        newUser.setVip(0);
        newUser.setUuid("0");
        newUser.setMoney(getConstant().getInitMoney());
        newUser.setRegistDate(new Date());
        newUser.setLastLoginDate(new Date());
        return newUser;
    }

    /**
     * 对字符串进行编码
     *
     * @param str
     * @return
     */
    private String decodeStr(String str) {
        String result = "";
        try {
            result = URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
