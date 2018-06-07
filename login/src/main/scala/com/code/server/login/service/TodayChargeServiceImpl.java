package com.code.server.login.service;
import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    public HomeChargeVo showCharge(Date start, Date end, long agentId) {

        String startStr = DateUtil.convert2DayString(start);
        String endStr = DateUtil.convert2DayString(end);
        OneLevelVo oneLevelVo = oneLevelCharges(start, end, agentId);
        TwoLevelVo twoLevelVo = twoLevelCharges(start, end, agentId);
        ThreeLevelVo threeLevelVo = threeLevelCharges(start, end, agentId);
        HomeChargeVo homeChargeVo = new HomeChargeVo();
        homeChargeVo.setOnelevel("" + oneLevelVo.getMoney());
        homeChargeVo.setTwoLevel("" + twoLevelVo.getMoney());
        homeChargeVo.setThreeLevel("" + threeLevelVo.getMoney());
        double total = oneLevelVo.getMoney() + twoLevelVo.getMoney() + threeLevelVo.getMoney();
        homeChargeVo.setTotal("" + total);
        homeChargeVo.setStart(startStr);
        homeChargeVo.setEnd(endStr);
        return homeChargeVo;
    }

    @Override
    public List<WaterRecordVo> waterRecords(long agentId) {

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

    //直接玩家充值
    @Override
    public OneLevelVo oneLevelCharges(Date start, Date end, long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        OneLevelVo oneLevelVo = new OneLevelVo();
        oneLevelVo.setCategoryName("game name");

        double total = 0d;
        List<OneLevelInfoVo> oneLevelInfoVoList = new ArrayList<>();
        //获取自己和自己直接玩家的所有list
        List<Long> aList = new ArrayList<>();
        aList.add(agentBean.getId());
        aList.addAll(agentBean.getChildList());

        //查一下手下玩家
        for (Long uid : aList){

            List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(uid), start, end);

            double totalMoney = 0d;
            for (Charge charge : list){
                totalMoney += charge.getMoney();
            }

            total += totalMoney;
            User user = userDao.getUserById(uid);
            OneLevelInfoVo oneLevelInfoVo = new OneLevelInfoVo();
            oneLevelInfoVo.setImage(user.getImage());
            oneLevelInfoVo.setUsername(user.getUsername());
            oneLevelInfoVo.setMoney("" + totalMoney);
            oneLevelInfoVoList.add(oneLevelInfoVo);
        }

        oneLevelVo.setMoney(total);
        oneLevelVo.setList(oneLevelInfoVoList);

        return oneLevelVo;
    }

    @Override
    public TwoLevelVo twoLevelCharges(Date start, Date end, long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        TwoLevelVo twoLevelVo = new TwoLevelVo();
        twoLevelVo.setCategoryName("game name");

        //获取所有二级代理的id
        List<Long> aList = new ArrayList<>();
        for (Long id : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(agentId)){
                aList.add(id);
            }
        }

        List<List<Long>> bList = new ArrayList<>();

        for (Long childId : aList){

            AgentBean child = RedisManager.getAgentRedisService().getAgentBean(childId);
            Set<Long> grandchild = child.getChildList();

            List<Long> temp = new ArrayList<>();
            //刨除三级代理
            for (Long grandChildId : grandchild){
                //三级代理
                if (RedisManager.getAgentRedisService().isExit(grandChildId)){
                    continue;
                }
                temp.add(grandChildId);
            }
            bList.add(temp);
        }

        // bList + aList
        double total = 0d;
        //手下所有二级代理
        for (Long delegateId : aList){
            List<Charge> list = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(delegateId), start, end);
            User user = userDao.getUserById(delegateId);
            TwoLevelInfoVo twoLevelInfoVo = new TwoLevelInfoVo();

            double totalMoney = 0;
            for (Charge charge : list){
                totalMoney += charge.getMoney();
            }
            twoLevelInfoVo.setImage(user.getImage());
            twoLevelInfoVo.setUsername(user.getUsername());
            twoLevelInfoVo.setMoney("" + totalMoney);
            total += totalMoney;
            twoLevelVo.getList().add(twoLevelInfoVo);

            //二级代理手下直接用户
            AgentBean twoLevelAgentBean = RedisManager.getAgentRedisService().getAgentBean(delegateId);
            for (Long uid : twoLevelAgentBean.getChildList()){

                TwoLevelInfoVo infoVo = new TwoLevelInfoVo();
                User twoLevelUser = userDao.getUserById(uid);
                infoVo.setUsername(twoLevelUser.getUsername());
                infoVo.setImage(twoLevelUser.getImage());

                List<Charge> twoLevelChargeList = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(uid), start, end);

                double twoLevelUserTotal = 0;
                for (Charge charge : twoLevelChargeList){
                    twoLevelUserTotal += charge.getMoney();
                }
                total += twoLevelUserTotal;
                infoVo.setMoney("" + twoLevelUserTotal);
                twoLevelVo.getList().add(infoVo);
            }

            twoLevelVo.setMoney(total);
        }

        return twoLevelVo;
    }

    @Override
    public ThreeLevelVo threeLevelCharges(Date start, Date end, long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        ThreeLevelVo threeLevelVo = new ThreeLevelVo();
        threeLevelVo.setCategoryName("game name");


        //所有的二级代理
        List<Long> aList = new ArrayList<>();
        for (Long uid : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(agentId)){
                aList.add(uid);
            }
        }

        //所有的三级代理和三级代理手上的玩家
        List<Long> bList = new ArrayList<>();
        for (Long uid : aList){
            if (RedisManager.getAgentRedisService().isExit(uid)){
                bList.add(uid);
            }
            AgentBean bean = RedisManager.getAgentRedisService().getAgentBean(uid);
            for (Long id : bean.getChildList()){
                if (RedisManager.getAgentRedisService().isExit(id)) continue;
                bList.add(id);
            }
        }

        double total = 0;
        for (Long uid : bList){

            User user = userDao.getUserById(uid);
            List<Charge> chargeList = chargeDao.getChargesByUseridInAndCreatetimeBetween(Arrays.asList(uid), start, end);
            ThreeLevelInfoVo threeLevelInfoVo = new ThreeLevelInfoVo();
            threeLevelInfoVo.setUsername(user.getUsername());
            threeLevelInfoVo.setImage(user.getImage());

            double totalMoney = 0;
            for (Charge charge : chargeList){
                totalMoney += charge.getMoney();
            }
            threeLevelInfoVo.setMoney("" + totalMoney);
            total += totalMoney;
            threeLevelVo.getList().add(threeLevelInfoVo);
        }

        threeLevelVo.setCategoryName("game name");
        threeLevelVo.setMoney(total);

        return threeLevelVo;
    }

    @Override
    public HomeChargeVo showCharge(long agentId) {
        //今日
        Date start = DateUtil.getDayBegin();
        Date end = new Date();
        return showCharge(start, end, agentId);
    }
    @Override
    public OneLevelVo oneLevelCharges(long agentId) {
        //今日
        Date start = DateUtil.getDayBegin();
        Date end = new Date();
        return oneLevelCharges(start, end, agentId);
    }

    @Override
    public TwoLevelVo twoLevelCharges(long agentId) {
        Date start = DateUtil.getDayBegin();
        Date end = new Date();
        return twoLevelCharges(start, end, agentId);
    }

    @Override
    public ThreeLevelVo threeLevelCharges(long agentId) {
        Date start = DateUtil.getDayBegin();
        Date end = new Date();
        return threeLevelCharges(start, end, agentId);
    }

    @Override
    public double canBlance(long agentId) {
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        return agentBean.getRebate();
    }

}
