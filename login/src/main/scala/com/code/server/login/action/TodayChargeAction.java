package com.code.server.login.action;
import com.code.server.db.dao.IChargeDao;
import com.code.server.login.service.TodayChargeService;
import com.code.server.login.service.TodayChargeServiceImpl;
import com.code.server.login.vo.TwoLevelVo;
import com.code.server.login.vo.WaterRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/5/14.
 */

@RestController
@RequestMapping("todayCharge")
public class TodayChargeAction {

    @Autowired
    private TodayChargeService todayChargeService;

    @RequestMapping("waterRecord")
    public AgentResponse waterRecord(){

        List<WaterRecordVo> waterRecordVoList = todayChargeService.waterRecords();
        AgentResponse agentResponse = new AgentResponse(200, waterRecordVoList);
        return agentResponse;
    }

    @RequestMapping("level2Charges")
    public AgentResponse showTodayTwoLevelChargeList(){

        TwoLevelVo twoLevelVo = todayChargeService.twoLevelCharges();

        Map<String, Object> result = new HashMap<>();
        result.put("result", twoLevelVo);
        AgentResponse agentResponse = new AgentResponse(200, result);
        return agentResponse;
    }
}
