package com.code.server.login.action;
import com.code.server.constant.game.AgentBean;
import com.code.server.constant.response.ErrorCode;
import com.code.server.db.dao.IAgentUserDao;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.ILogRecordDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.AgentUser;
import com.code.server.db.model.Charge;
import com.code.server.db.model.LogRecord;
import com.code.server.db.model.User;
import com.code.server.login.service.HomeService;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.util.MD5Util;
import com.code.server.login.vo.DChargeAdminVo;
import com.code.server.login.vo.OneLevelInfoVo;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Char;
import sun.jvm.hotspot.utilities.Assert;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by dajuejinxian on 2018/6/22.
 */
@RestController
@RequestMapping("/admin")
public class DemoAction{

    @Autowired
    private IAgentUserDao agentUserDao;

    @Autowired
    private ILogRecordDao logRecordDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IChargeDao chargeDao;

    @Autowired
    private HomeService homeService;

    public static final int MONEY_TYPE = 0;

    public static final int GOLD_TYPE = 1;

    public static String getToken(long userId) {
        return MD5Util.MD5Encode("salt," + userId + System.currentTimeMillis(), "UTF-8");
    }

    public static boolean isNumber(String str) {
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        //可以判断正负、整数小数
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();

        return isInt || isDouble;
    }

    public static Map<String, Object> getUserInfo(HttpServletRequest request){
        return (Map<String, Object>)AgentUtil.caches.get("a");
    }

    @RequestMapping("/timeSearch")
    public AgentResponse doSearch(String t1, String t2, int curPage, int pageSize){

        String[] sA = null;
        if (t1.contains(",")){
            sA = t1.split(",", 1000);
        }

        String[] sB = null;
        if (t2.contains(",")){
            sB = t2.split(",", 1000);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Date> listA = new ArrayList<>();
        if (sA != null){
            for (String str : sA){
                try {
                    Date date = simpleDateFormat.parse(str);
                    listA.add(date);
                } catch (ParseException e) {
//                e.printStackTrace();
                }
            }
        }

        List<Date> listB = new ArrayList<>();
        if (sB != null){
            for (String str : sB){
                try {
                    Date date = simpleDateFormat.parse(str);
                    listB.add(date);
                } catch (ParseException e) {
                e.printStackTrace();
                }
            }
        }

        List<User> list = null;
        long count = 0;
        AgentResponse agentResponse = new AgentResponse();
        if (sA != null && sB == null){
            list = homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else if (sB != null && sB == null){
//            list = userDao.findUsersByLastLoginDateBetween(listB.get(0), listB.get(1));
            list = homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else if (sA != null && sB != null){
//            list = userDao.findUsersByRegistDateBetweenAndLastLoginDateBetween(listA.get(0), listA.get(1), listB.get(0), listB.get(1));
            homeService.timeQuery(listA, listB, new PageRequest(curPage, pageSize)).getContent();
            count = homeService.timeQueryCount(listA, listB);
        }else {
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            agentResponse.setMsg("请选择日期");
            return agentResponse;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("list", list);

        agentResponse.setData(result);
        return agentResponse;
    }

    @RequestMapping("/fetchAllPlayers")
    public AgentResponse fetchAllPlayers(int pageSize, int curPage){
        Page page =  userDao.findAll(new PageRequest(curPage, pageSize));
        List<User> list = page.getContent();
        Map<String, Object> result = new HashMap<>();
        long count = userDao.count();
        result.put("total", count);
        result.put("list", list);
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(result);
        return agentResponse;
    }

    @RequestMapping("/fetchPlayer")
    public AgentResponse fetchPlayer(long userId){

        if (userId == 0){
            return fetchAllPlayers(20, 0);
        }
        User user =  userDao.findOne(userId);
        List<User> list = new ArrayList<>();
        AgentResponse agentResponse = new AgentResponse();
        if (user == null){
            agentResponse.setData(list);
            agentResponse.setCode(com.code.server.login.action.ErrorCode.ERROR);
            Map<String, Object> result = new HashMap<>();
            agentResponse.setMsg("没有记录");
            result.put("total", 0);
            result.put("list", list);
            agentResponse.setData(result);
        }else {
            list.add(user);
            Map<String, Object> result = new HashMap<>();
            result.put("total", 1);
            result.put("list", list);
            agentResponse.setData(result);
        }
        return agentResponse;
    };

    @RequestMapping("/logout")
    public AgentResponse logout(){
        AgentUtil.caches.clear();
        AgentResponse agentResponse = new AgentResponse();
        return agentResponse;
    }

    @RequestMapping("/upward")
    public AgentResponse upwardDelegates(HttpServletRequest request, long userId){

        return null;
    }

    @RequestMapping("/doCharge")
    public AgentResponse doCharge(HttpServletRequest request, long userId, @RequestParam(value = "money", required = true) long money){

        org.springframework.util.Assert.isTrue(isNumber(money +""), "是数字");
         double curMoney = RedisManager.getUserRedisService().addUserMoney(userId, money);

         AgentResponse agentResponse = new AgentResponse();
         agentResponse.setData(curMoney);
        return agentResponse;
    }

    @RequestMapping("/charges")
    public AgentResponse chargeRecord(HttpServletRequest request, long userId){

        List<Integer> list = Arrays.asList(MONEY_TYPE, GOLD_TYPE);
        List<Charge> chargeList = null;

        if (userId == 0){
            chargeList = chargeDao.getChargesByChargeTypeIn(list);
        }else {
            chargeList = chargeDao.getChargesByChargeTypeInAndUseridIs(list, userId);
        }

        List<DChargeAdminVo> result = new ArrayList<>();
        for (Charge charge : chargeList){
            DChargeAdminVo chargeAdminVo = new DChargeAdminVo();
            BeanUtils.copyProperties(charge, chargeAdminVo);
            result.add(chargeAdminVo);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("result", result);
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setData(res);
        return agentResponse;
    }

    @RequestMapping("/downward")
    public AgentResponse downwardDelegate(HttpServletRequest request, long userId){

        Map<String, Object> userInfo =  getUserInfo(request);
        int agentId = (int) userInfo.get("id");
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        //直接玩家
        List<Long> aList = new ArrayList<>();
        List<Long> bList = new ArrayList<>();
        List<Long> cList = new ArrayList<>();

        agentBean.getChildList().stream()
                .forEach(x -> {

                    if (RedisManager.getAgentRedisService().isExit(x)){
                        bList.add(x);
                    }else {
                        aList.add(x);
                    }
                });

        bList.stream()
                .forEach(x -> {
                    if (RedisManager.getAgentRedisService().isExit(x)){
                        cList.add(x);
                    }
                });

        List<User> aUsers = userDao.findUsersByIdIn(aList);
        List<User> bUsers = userDao.findUsersByIdIn(bList);
        List<User> cUsers = userDao.findUsersByIdIn(cList);

        return null;
    }

    @RequestMapping("/login")
    public AgentResponse agentLogin(HttpServletRequest request, String username, String password){

        AgentUser agentUser = agentUserDao.findAgentUserByUsernameAndPassword(username, password);
        AgentResponse agentResponse = null;
        Map<String, Object> result = new HashMap<>();
        if (agentUser != null){
            //todo token 和 玩家的关联
            Map<String, Object> rs = new HashMap<>();
            rs.put("id", agentUser.getId());
            rs.put("username", agentUser.getUsername());
            String token = getToken(agentUser.getId());
            AgentUtil.caches.put(token, rs);
            agentResponse = new AgentResponse(0, result);
        }else {
            agentResponse = new AgentResponse(ErrorCode.ROLE_ACCOUNT_OR_PASSWORD_ERROR,result);
            agentResponse.msg = "用户不存在";
        }
        return agentResponse;
    }

    @RequestMapping("/info")
    public AgentResponse userInfo(String token){
        //todo token 验证
        AgentUtil.caches.get(token);
        Map<String, Object> r = new HashMap<>();
        int[] roles = new int[]{1};
        r.put("userId", 1);
        r.put("roles", roles);
        return new AgentResponse(0, r);
    }

    @RequestMapping("/onlineInfo")
    public AgentResponse onlineInfo(String date){
        //todo token 验证
        LogRecord logRecord = logRecordDao.findOne(date);
        return new AgentResponse(0, JsonUtil.toJson(logRecord));
    }

}
