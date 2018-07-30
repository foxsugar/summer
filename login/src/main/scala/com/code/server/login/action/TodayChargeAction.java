package com.code.server.login.action;
import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.ChildCost;
import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.login.anotation.AuthChecker;
import com.code.server.login.service.TodayChargeService;
import com.code.server.login.service.TodayChargeServiceImpl;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/14.
 */

@RestController
@RequestMapping("/todayCharge")
public class TodayChargeAction {

    private static final String AGENT_COOKIE_NAME = "AGENT_TOKEN";
    @Autowired
    private TodayChargeService todayChargeService;

    @AuthChecker
    //流水记录
    @RequestMapping("/waterRecord")
    public AgentResponse waterRecord(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        List<WaterRecordVo> waterRecordVoList = todayChargeService.waterRecords(agentId);
        AgentResponse agentResponse = new AgentResponse(200, waterRecordVoList);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/homeCharge")
    public AgentResponse homeCharge(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        HomeChargeVo homeChargeVo = todayChargeService.showCharge(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", homeChargeVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/dhomeCharge")
    public AgentResponse homeCharge(String start, String end){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        Date startDate = DateUtil.convertDay2Date(start);
        Date endDate = DateUtil.convertDay2Date(end);
        HomeChargeVo homeChargeVo = todayChargeService.showCharge(startDate, endDate, agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", homeChargeVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @RequestMapping("/tesst")
    public AgentResponse demohomeCharge(String start, String end){

//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        long agentId = AgentUtil.getAgentByRequest(request);
        long agentId = 100017;
        Date startDate = DateUtil.convertDay2Date(start);
        Date endDate = DateUtil.convertDay2Date(end);
        HomeChargeVo homeChargeVo = todayChargeService.showCharge(startDate, endDate, agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", homeChargeVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    //直接玩家
    @RequestMapping("/level1Charges")
    public AgentResponse showTodayOneLevelChargeList(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        OneLevelVo oneLevelVo = todayChargeService.oneLevelCharges(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", oneLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/dlevel1Charges")
    public AgentResponse showTodayOneLevelChargeList(String start, String end){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        Date startDate = DateUtil.convert2Date(start);
        Date endDate = DateUtil.convert2Date(end);
        OneLevelVo oneLevelVo = todayChargeService.oneLevelCharges(startDate, endDate, agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", oneLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    //二级代理充值记录
    @RequestMapping("/level2Charges")
    public AgentResponse showTodayTwoLevelChargeList(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        Map<String, Object> result = new HashMap<>();
        TwoLevelVo twoLevelVo = todayChargeService.twoLevelCharges(agentId);
        result.put("result", twoLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    //二级代理充值记录
    @RequestMapping("/dlevel2Charges")
    public AgentResponse showTodayTwoLevelChargeList(String start, String end){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        Date startDate = DateUtil.convert2Date(start);
        Date endDate = DateUtil.convert2Date(end);
        TwoLevelVo twoLevelVo = todayChargeService.twoLevelCharges(startDate, endDate, agentId);

        Map<String, Object> result = new HashMap<>();
        result.put("result", twoLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    //二级代理充值记录
    @RequestMapping("/level3Charges")
    public AgentResponse showTodayThreeLevelChargeList(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);
        ThreeLevelVo threeLevelVo = todayChargeService.threeLevelCharges(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", threeLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    //二级代理充值记录
    @RequestMapping("/dlevel3Charges")
    public AgentResponse showTodayThreeLevelChargeList(String start, String end){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);

        Date startDate = DateUtil.convert2Date(start);
        Date endDate = DateUtil.convert2Date(end);
        ThreeLevelVo threeLevelVo = todayChargeService.threeLevelCharges(startDate, endDate ,agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", threeLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

//    @AuthChecker
    @RequestMapping("/dCost")
    public AgentResponse showCost(String start, String end){

//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        long agentId = AgentUtil.getAgentByRequest(request);
        long agentId = 100017;
        List<String> list = DateUtil.getDateListIn(end, start);
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        AgentInfo agentInfo = agentBean.getAgentInfo();
        List<DChildCostVo> li = new ArrayList<>();

        double oneLevel = 0d;
        double twoLevel = 0d;
        double threeLevel = 0d;
        for (String dateStr : list){
            ChildCost childCost = agentInfo.getEveryDayCost().get(dateStr);
            DChildCostVo dChildCostVo = new DChildCostVo();
            if (childCost != null){
                BeanUtils.copyProperties(childCost, dChildCostVo);
            }
            dChildCostVo.setDateStr(dateStr);
            li.add(dChildCostVo);
            oneLevel += dChildCostVo.getFirstLevel();
            twoLevel += dChildCostVo.getSecondLevel();
            threeLevel += dChildCostVo.getThirdLevel();
        }
        double total = oneLevel + twoLevel + threeLevel;
        Map<String, Object> result = new HashMap<>();
        result.put("li", li);
        result.put("total", total);
        result.put("oneLevel", oneLevel);
        result.put("twoLevel", twoLevel);
        result.put("threeLevel", threeLevel);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }

    @AuthChecker
    @RequestMapping("/canBlance")
    public AgentResponse canBlance(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long agentId = AgentUtil.getAgentByRequest(request);
        AgentResponse agentResponse = new AgentResponse();
        double re = todayChargeService.canBlance(agentId);
        Map<String, Object> rs = new HashMap<>();
        rs.put("result", re);
        System.out.println("---------|||||" + re);
        agentResponse.setData(rs);
        return agentResponse;
    }

    @RequestMapping("/testb")
    public AgentResponse testBlance(long agentId){
        AgentResponse agentResponse = new AgentResponse();
        double re = todayChargeService.canBlance(agentId);
        Map<String, Object> rs = new HashMap<>();
        rs.put("result", re);
        agentResponse.setData(rs);
        return agentResponse;
    }
}
