package com.code.server.login.action;


import com.code.server.constant.game.AgentBean;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.Service.GameRecordService;
import com.code.server.db.Service.RecommendService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Constant;
import com.code.server.db.model.Recommend;
import com.code.server.db.model.User;
import com.code.server.login.config.ServerConfig;
import com.code.server.login.service.CenterMsgService;
import com.code.server.login.service.GameUserService;
import com.code.server.login.service.LoginService;
import com.code.server.login.service.ServerManager;
import com.code.server.login.util.MD5Util;
import com.code.server.login.util.Utils;
import com.code.server.login.util.ZXingUtil;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.IdWorker;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import scala.tools.nsc.doc.html.page.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;


/**
 * Created by win7 on 2017/3/8.
 */

@RestController
@EnableAutoConfiguration
public class LoginAction {


    @Autowired
    private UserService userService;


    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private WxMpService wxMpService;

    //    @Value("serverConfig.serverId")


    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    private int login4sqlByAccount(String account, String password, Map<String, Object> params) {
        User user = userService.getUserByAccountAndPassword(account, password);
        //查询数据库，没有新建玩家
        if (user == null) {
            //新建玩家
            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            if (getConstant().getAppleCheck() != 0 && serverConfig.getLoginCreateNewUser() == 1) {
                user = createUser(account, password);
                userService.save(user);
                //reids 记录新增玩家
                RedisManager.getLogRedisService().logRegisterUser();
                if (!"".equals(serverConfig.getQrDir())) {
                    ZXingUtil.createQrCode(user.getId());
                }
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

    private int login4sqlByOpenIdAndIP(String openId, String unionId, String userName, String img, int sex, Map<String, Object> params, String ip) {
        User user = userService.getUserByOpenId(openId);
        //查询数据库，没有新建玩家
        boolean addRebate = false;
        if (user == null) {
            //新建玩家
            user = createUser(openId, unionId, userName, img, sex);

            //代理推荐情况
            Recommend recommend = recommendService.getRecommendDao().getByUnionId(ip);
            if (recommend != null) {
                //玩家设置代理
                user.setReferee((int) recommend.getAgentId());
            }

            //保存
            userService.save(user);

            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            if (!"".equals(serverConfig.getQrDir())) {
                ZXingUtil.createQrCode(user.getId());
            }

            if (recommend != null) {
                addRebate = true;

            }

            //reids 记录新增玩家
            RedisManager.getLogRedisService().logRegisterUser();
        }
        String token = getToken(user.getId());
        saveUser2Redis(user, token);

        if (addRebate) {
            CenterMsgService.addRebate(user.getId(), 0D);
        }
        params.put("token", token);
        params.put("userId", "" + user.getId());
        return 0;
    }

    private int login4sqlByOpenId(String openId, String unionId, String userName, String img, int sex, Map<String, Object> params) {
        User user = userService.getUserByOpenId(openId);
        //查询数据库，没有新建玩家
        if (user == null) {
            //新建玩家
            user = createUser(openId, unionId, userName, img, sex);

            //代理推荐情况
            Recommend recommend = recommendService.getRecommendDao().getByUnionId(openId);
            if (recommend != null) {
                //玩家设置代理
                user.setReferee((int) recommend.getAgentId());

            }

            //保存
            userService.save(user);

            ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
            if (!"".equals(serverConfig.getQrDir())) {
                ZXingUtil.createQrCode(user.getId());
            }

            //reids 记录新增玩家
            RedisManager.getLogRedisService().logRegisterUser();

            if (recommend != null) {
                //代理多了一个玩家
                AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(recommend.getAgentId());
                if (agentBean != null) {
                    if (!agentBean.getChildList().contains(user.getId())) {
                        agentBean.getChildList().add(user.getId());
                        RedisManager.getAgentRedisService().updateAgentBean(agentBean);
                    }
                    //通知
                    try {
                        wxMpService.getKefuService().sendKefuMessage(
                                WxMpKefuMessage
                                        .TEXT()
                                        .toUser(agentBean.getOpenId())
                                        .content(userName + " 下载并进入游戏,和您成功绑定")
                                        .build());
                    } catch (WxErrorException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        String token = getToken(user.getId());
        saveUser2Redis(user, token);

        params.put("token", token);
        params.put("userId", "" + user.getId());
        return 0;
    }


    public static UserBean saveUser2Redis(User user, String token) {
        UserBean userBean = GameUserService.user2userBean(user);


        RedisManager.getUserRedisService().setUserBean(userBean);//userId-userbean
        RedisManager.getUserRedisService().setUserMoney(user.getId(), user.getMoney());//userId-money
        RedisManager.getUserRedisService().setUserGold(user.getId(), user.getGold());

        RedisManager.getUserRedisService().setToken(user.getId(), token);//userId-token


        RedisManager.getUserRedisService().setOpenIdUserId(user.getOpenId(), user.getId());//openid-userId
        RedisManager.getUserRedisService().setUserIdOpenId(user.getId(), user.getOpenId());//userId-openid


        RedisManager.getUserRedisService().setAccountUserId(userBean.getAccount(), user.getId());
        RedisManager.getUserRedisService().setUserIdAccount(user.getId(), userBean.getAccount());//userId-account
        return userBean;
    }


    /**
     * 加载机器人
     */
    public static void loadRobot() {
        UserService userService = SpringUtil.getBean(UserService.class);
        List<User> list = userService.findAllRobotUser();

        for (User user : list) {
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(user.getId());
//            if (userBean == null) {
            String token = getToken(user.getId());
            saveUser2Redis(user, token);
            RedisManager.getUserRedisService().addRobotPool(user.getId());

//            RedisManager.getUserRedisService().setAccountUserId(userBean.getAccount(), userBean.getId());
//            RedisManager.getUserRedisService().setUserIdAccount(userBean.getId(), userBean.getAccount());//userId-account
//            }
        }
    }

    public static void loadAllPlayer() {
        UserService userService = SpringUtil.getBean(UserService.class);
        for (User user : userService.getUserDao().findAll()) {
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(user.getId());
            if (userBean == null) {
                String token = getToken(user.getId());
                UserBean ub = saveUser2Redis(user, token);
                RedisManager.getUserRedisService().setAccountUserId(ub.getAccount(), ub.getId());
                RedisManager.getUserRedisService().setUserIdAccount(ub.getId(), ub.getAccount());//userId-account

            }
        }
    }

    /**
     * load userBean
     *
     * @param userId
     * @return
     */
    public static UserBean loadUserBean(long userId) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            User user = userService.getUserByUserId(userId);
            if (user != null) {
                String token = getToken(user.getId());
                userBean = saveUser2Redis(user, token);
                RedisManager.getUserRedisService().setAccountUserId(userBean.getAccount(), userId);
                RedisManager.getUserRedisService().setUserIdAccount(userId, userBean.getAccount());//userId-account
            }
        }
        return userBean;
    }

    @RequestMapping("/login")
    public Map<String, Object> login(String account, String password, String token_user, HttpServletRequest request) {
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
                userBean.setLastLoginDate(new Date());
            }

            params.put("token", redisToken);
            params.put("userId", userId);
        }

        //黑名单
        boolean isInBlackList = ServerManager.constant.getBlackList() != null && ServerManager.constant.getBlackList().contains(userId);
        if (isInBlackList) {
            code = ErrorCode.BLACK_LIST;
        } else {
            setHostAndPort(userId, params, code == 0);
        }


        System.err.println(params);
        return getParams("login", params, code);
    }


    @RequestMapping("/register")
    public Map<String, Object> register(String account, String password, String token_user, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        int code = 0;
        //redis里的数据
        String userId = userRedisService.getUserIdByAccount(account);//玩家id

        if (userId == null) {

            User user = null;
            List<User> list = userService.getUserDao().getUsersByAccount(account);

            //查询数据库，没有新建玩家
            if (list.size() == 0) {
                //新建玩家

                user = createUser(account, password);
                userService.save(user);
                //reids 记录新增玩家
                RedisManager.getLogRedisService().logRegisterUser();
                ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
                if (!"".equals(serverConfig.getQrDir())) {
                    ZXingUtil.createQrCode(user.getId());

                }
                String token = getToken(user.getId());
                saveUser2Redis(user, token);

                params.put("token", token);
                params.put("userId", user.getId());


                code = login4sqlByAccount(account, password, params);
                userId = String.valueOf(params.get("userId"));
            }
        } else {
            code = ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR;


        }

        //黑名单
        boolean isInBlackList = ServerManager.constant.getBlackList() != null && ServerManager.constant.getBlackList().contains(userId);
        if (isInBlackList) {
            code = ErrorCode.BLACK_LIST;
        } else {
            setHostAndPort(userId, params, code == 0);
        }


        System.err.println(params);
        return getParams("register", params, code);
    }





    /**
     * 文件上传功能
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String,Object> uploadImg(HttpServletRequest request, HttpServletResponse response, long userId, String token, MultipartFile file, String name)
            throws ServletException, IOException {
        Map<String, Object> params = new HashMap<>();

        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            return getParams("upload", params, ErrorCode.ACCOUT_HAS_NOT_EXITS);
        }


        if (!token.equals(RedisManager.getUserRedisService().getToken(userId))) {
            return getParams("upload", params, ErrorCode.ACCOUT_HAS_NOT_EXITS);
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(factory);
        // 处理中文问题
        sfu.setHeaderEncoding("UTF-8");
        // 限制文件大小
        sfu.setSizeMax(1024 * 1024 * 5);

        InputStream files = file.getInputStream();
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        String[] ss = fileName.split("\\.");
        if (ss.length != 2 || !"png".equals(ss[1])) {
            return getParams("upload", params, ErrorCode.IMAGE_ERROR);
        }
        String dir = serverConfig.getQrDir() + "icon"+userId + ".png";
        String url = serverConfig.getDomain() + "icon"+userId + ".png";
        FileOutputStream out = new FileOutputStream(new File(dir));
        try {
            // 每次读取的字节长度
            int n = 0;
            // 存储每次读取的内容
            byte[] bb = new byte[1024];
            while ((n = files.read(bb)) != -1) {
                // 将读取的内容，写入到输出流当中
                out.write(bb, 0, n);
            }
        }finally {
            // 关闭输入输出流
            out.close();
            files.close();
        }


        userBean.setImage(url);
        userBean.setUsername(name);
        RedisManager.getUserRedisService().updateUserBean(userId, userBean);

        return getParams("upload", params, 0);
    }



    /**
     * 文件上传功能
     * @return
     */
    @RequestMapping(value = "/uploadNotice", method = RequestMethod.POST)
    public Object uploadNotice(HttpServletRequest request, HttpServletResponse response, MultipartFile file)
            throws ServletException, IOException {
        Map<String, Object> params = new HashMap<>();


        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(factory);
        // 处理中文问题
        sfu.setHeaderEncoding("UTF-8");
        // 限制文件大小
        sfu.setSizeMax(1024 * 1024 * 50);

        InputStream files = file.getInputStream();
        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        String fileName = file.getOriginalFilename();

        String fn = ""+IdWorker.getDefaultInstance().nextId();
        String dir = serverConfig.getQrDir() + fn + ".png";
        String url = serverConfig.getQrUrl() + fn + ".png";
        FileOutputStream out = new FileOutputStream(new File(dir));
        try {
            // 每次读取的字节长度
            int n = 0;
            // 存储每次读取的内容
            byte[] bb = new byte[1024];
            while ((n = files.read(bb)) != -1) {
                // 将读取的内容，写入到输出流当中
                out.write(bb, 0, n);
            }
        }finally {
            // 关闭输入输出流
            out.close();
            files.close();
        }

        Constant constant = ServerManager.constant;
        constant.getOther().getNotice().put("gonggao", url);
        constantService.constantDao.save(constant);


        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }

    private void setHostAndPort(String userId, Map<String, Object> params, boolean isSet) {
        if (isSet) {

            String gateId = RedisManager.getUserRedisService().getGateId(Long.valueOf(userId));
            com.code.server.redis.config.ServerInfo serverInfo = null;
            if (gateId != null) {
                serverInfo = RedisManager.getGateRedisService().getServerInfo(gateId);
            }
            if (serverInfo == null) {
                serverInfo = LoginService.getSortedServer("GATE");
            }
            if (serverInfo != null) {
                params.put("port", serverInfo.getPort());
                params.put("ip", serverInfo.getHost());
                params.put("domain", serverInfo.getDomain());

            }
        }
    }


    @RequestMapping("/checkOpenId")
    public Map<String, Object> checkOpenId(String openId, String username, String image, int sex, String token_user, String unionId, HttpServletRequest request) {


        System.out.println(unionId);
        int code = 0;

        Map<String, Object> params = new HashMap<>();

        String userId = userRedisService.getUserIdByOpenId(openId);


        if (userId == null) {
            String ip = "";
            try {
                ip = Utils.getIpAddr(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            code = login4sqlByOpenIdAndIP(openId, unionId, username, image, sex, params, ip);
            userId = (String) params.get("userId");
        } else {
            //刷新redis数据
            UserBean userBean = userRedisService.getUserBean(Long.valueOf(userId));
            if (userBean != null) {
                userBean.setUsername(username);
                userBean.setImage(image);
                userBean.setSex(sex);
                userBean.setLastLoginDate(new Date());
                userBean.setUnionId(unionId);
                userRedisService.updateUserBean(Long.valueOf(userId), userBean);
            }
            String redisToken = getToken(Long.valueOf(userId));
            userRedisService.setToken(Long.valueOf(userId), redisToken);
            params.put("token", redisToken);
            params.put("userId", userId);
        }

        //黑名单
        boolean isInBlackList = ServerManager.constant.getBlackList() != null && ServerManager.constant.getBlackList().contains(userId);
        if (isInBlackList) {
            code = ErrorCode.BLACK_LIST;
        } else {
            setHostAndPort(userId, params, code == 0);
        }


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
        ServerManager.init();
        params.put("constant", ServerManager.constant);
        return getParams("refreshMemory", params, 0);
    }

    private Constant getConstant() {
        if (ServerManager.constant == null) {
            ServerManager.constant = constantService.constantDao.findOne(1L);
        }
        return ServerManager.constant;
    }


//    @RequestMapping("/")
//    public Map<String, Object> test() {
////        RedisManager.getUserRedisService().addUserMoney("", 1);
//        int partition = 0;
//        KafkaMsgKey kafkaKey = new KafkaMsgKey();
//        kafkaKey.setUserId(3);
//        kafkaKey.setPartition(1);
//        String keyJson = JsonUtil.toJson(kafkaKey);
//        SpringUtil.getBean(MsgProducer.class).send("gamePaijiuService", kafkaKey, "hello");
//
//        Map<String, Object> params = new HashMap<>();
//        System.out.println("====");
//        return params;
//    }

    @RequestMapping("/addblacklist")
    public Map<String, Object> addBlackList(String userId) {


        if (ServerManager.constant.getBlackList() == null) {
            ServerManager.constant.setBlackList(new HashSet<String>());
        }
        ServerManager.constant.getBlackList().add(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", ServerManager.constant.getBlackList());

        constantService.constantDao.save(ServerManager.constant);
        return result;
    }

    @RequestMapping("/removeblacklist")
    public Map<String, Object> removeBlackList(String userId) {

        if (ServerManager.constant.getBlackList() == null) {
            ServerManager.constant.setBlackList(new HashSet<String>());
        }
        ServerManager.constant.getBlackList().remove(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", ServerManager.constant.getBlackList());

        constantService.constantDao.save(ServerManager.constant);
        return result;
    }


    @RequestMapping("/editVIP")
    public Map<String, Object> editVIP(long userId, int vip) {


        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setVip(vip);
            RedisManager.getUserRedisService().updateUserBean(userId, userBean);

            GameUserService.saveUserBean(userId);
        } else {
            User user = userService.getUserByUserId(userId);
            user.setVip(vip);
            userService.save(user);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("vip", vip);
        return result;
    }

    @RequestMapping("/removeRecord")
    public Map<String, Object> removeRecord(String roomUid) {

        Map<String, Object> result = new HashMap<>();
        SpringUtil.getBean(GameRecordService.class).decGameRecordCount(Long.valueOf(roomUid));
        return result;
    }

    @RequestMapping("/createQrCode")
    public Object createQrCode(long userId, boolean isRobot) {

        if (isRobot) {
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
            if (userBean == null) {
                loadUserBean(userId);
                RedisManager.getUserRedisService().addRobotPool(userId);
            }

        }
        ZXingUtil.createQrCode(userId);
        return 0;

    }

    @RequestMapping("/genAllQrCode")
    public Object genAllQrCode() {
        for (User user : userService.getUserDao().findAll()) {
            ZXingUtil.createQrCode(user.getId());
        }
        return 0;
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
        newUser.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg");
        int sex = new Random().nextInt(2) + 1;
        newUser.setSex(sex);
        newUser.setVip(0);
        newUser.setUuid("0");
        newUser.setMoney(getConstant().getInitMoney());
        newUser.setGold(getConstant().getInitGold());
        newUser.setRegistDate(new Date());
        newUser.setLastLoginDate(new Date());

        return newUser;
    }

    public static void main(String[] args) {
        int sex = new Random().nextInt(2) + 1;
        System.out.println(sex);
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
    private User createUser(String openId, String unionId, String username, String image, int sex) {
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
        newUser.setGold(getConstant().getInitGold());
        newUser.setRegistDate(new Date());
        newUser.setLastLoginDate(new Date());
        newUser.setUnionId(unionId);
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


    public static JSONObject verify(long userId, String url, String receipt) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {

//            JSONObject data = new JSONObject();
//            data.put("receipt-data", receipt);
            Map<String, Object> data = new HashMap<>();
            data.put("receipt-data", receipt);
            StringEntity entity = new StringEntity(JsonUtil.toJson(data));
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String resultStr = EntityUtils.toString(httpEntity);
            JsonNode result = JsonUtil.readTree(resultStr);
//            if (result == null) {
//                return
//            }
            if (result.path("status").asInt() == 21007) {
                return verify(userId, "https://sandbox.itunes.apple.com/verifyReceipt", receipt);
            }


            JsonNode receiptNode = JsonUtil.readTree(result.path("receipt").asText());
            int itemId = receiptNode.path("app_item_id").asInt();
//
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }



/*

{
  "status": 0,
  "environment": "Sandbox",
  "receipt": {
    "receipt_type": "ProductionSandbox",
    "adam_id": 0,
    "app_item_id": 0,
    "bundle_id": "com.platomix.MicroBusinessManage",
    "application_version": "2.0.0",
    "download_id": 0,
    "version_external_identifier": 0,
    "receipt_creation_date": "2017-06-06 06:35:27 Etc/GMT",
    "receipt_creation_date_ms": "1496730927000",
    "receipt_creation_date_pst": "2017-06-05 23:35:27 America/Los_Angeles",
    "request_date": "2017-06-06 07:13:26 Etc/GMT",
    "request_date_ms": "1496733206549",
    "request_date_pst": "2017-06-06 00:13:26 America/Los_Angeles",
    "original_purchase_date": "2013-08-01 07:00:00 Etc/GMT",
    "original_purchase_date_ms": "1375340400000",
    "original_purchase_date_pst": "2013-08-01 00:00:00 America/Los_Angeles",
    "original_application_version": "1.0",
    "in_app": []
  }
}
 */
}
