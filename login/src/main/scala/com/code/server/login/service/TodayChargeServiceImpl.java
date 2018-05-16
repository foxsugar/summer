package com.code.server.login.service;
import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IGameAgentDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.util.CookieUtil;
import com.code.server.login.vo.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Char;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/14.
 */

@Service
public class TodayChargeServiceImpl implements TodayChargeService {

    @Autowired
    private IChargeDao chargeDao;

    @Autowired
    private IUserDao userDao;

    //提现
    public static final String CHARGE_TYPE_CASH = "11";

    public TodayChargeVo showTodayCharge() {

        long agentId = CookieUtil.getAgentIdByCookie();
        List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(agentId), DateUtil.getDayBegin(), DateUtil.getDayEnd());

        //总价格
        double total = 0;
        for (Charge charge : list){
            total += charge.getMoney();
        }

        //我推荐的代理
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        List<Long> childList = agentBean.getChildList();

        return null;
    }

    @Override
    public List<WaterRecordVo> waterRecords() {

        long agentId = CookieUtil.getAgentIdByCookie();
        Date start = DateUtil.getThisYearStart();
        Date end = new Date();
        List<Charge> list = chargeDao.getChargesByUserrAndRechargeSourceAndDate(agentId, CHARGE_TYPE_CASH, start, end);

        List<WaterRecordVo> voList = new ArrayList<>();
        for (Charge charge : list){

            WaterRecordVo vo = new WaterRecordVo();
            vo.setUid(charge.getUserid());
            vo.setMoney("¥" + charge.getMoney());
            vo.setTimeStamp(DateUtil.convert2String(charge.getCreatetime()));
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public OneLevelVo oneLevelCharges() {
        long agentId = CookieUtil.getAgentIdByCookie();
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        Date start = DateUtil.getDayBegin();
        Date end = new Date();

        OneLevelVo oneLevelVo = new OneLevelVo();
        oneLevelVo.setCategoryName("game name");

        double total = 0d;
        List<OneLevelInfoVo> oneLevelInfoVoList = new ArrayList<>();
        for (Long uid : agentBean.getChildList()){
            List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(uid), start, end);

            double totalMoney = 0d;
            for (Charge charge : list){
                totalMoney += charge.getMoney();
            }
            total += totalMoney;
            User user = userDao.findOne(uid);
            OneLevelInfoVo oneLevelInfoVo = new OneLevelInfoVo();
            oneLevelInfoVo.setImage(user.getImage());
            oneLevelInfoVo.setUsername(user.getUsername());
            oneLevelInfoVo.setMoney("¥" + totalMoney);
            oneLevelInfoVoList.add(oneLevelInfoVo);
        }

        oneLevelVo.setMoney(total);
        oneLevelVo.setList(oneLevelInfoVoList);

        return oneLevelVo;
    }

    @Override
    public TwoLevelVo twoLevelCharges() {

        long agentId = CookieUtil.getAgentIdByCookie();
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);

        //一级代理表
        List<AgentBean> childs = new ArrayList<>();
        for (Long uid : agentBean.getChildList()){
            AgentBean bean = RedisManager.getAgentRedisService().getAgentBean(uid);
            if (bean == null) continue;
            childs.add(bean);
        }

        Date start = DateUtil.getDayBegin();
        Date end = new Date();

        TwoLevelVo twoLevelVo = new TwoLevelVo();
        double total = 0d;

        for (AgentBean bean : childs){
            TwoLevelInfoVo infoVo = new TwoLevelInfoVo();
            infoVo.setImage(bean.getImage());
            //查询今日充值
            List<Charge> charges = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(bean.getId()), start, end);

            double totalMoney = 0.0;
            for (Charge charge : charges){
                totalMoney += charge.getMoney();
            }
            total += totalMoney;
            infoVo.setMoney("¥" + totalMoney);
            User u = userDao.findOne(bean.getId());
            infoVo.setUsername(u.getUsername());
            twoLevelVo.getList().add(infoVo);
        }

        twoLevelVo.setMoney(total);
        twoLevelVo.setCategoryName("Game Name");
        return twoLevelVo;
    }

}
