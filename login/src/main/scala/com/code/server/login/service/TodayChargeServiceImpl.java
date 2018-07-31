package com.code.server.login.service;
import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.action.DelegateRelataionAction;
import com.code.server.login.util.AgentUtil;
import com.code.server.login.vo.*;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import scala.Char;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
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
    //金额
    public static final int MONEY_TYPE = 0;

    public static final int GOLD_TYPE = 1;

    private static final Logger logger = LoggerFactory.getLogger(TodayChargeServiceImpl.class);

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

        homeChargeVo.setOneLevelGold("" + oneLevelVo.getGold());
        homeChargeVo.setTwoLevelGold("" + twoLevelVo.getGold());
        homeChargeVo.setThreeLevelGold("" + threeLevelVo.getGold());

        homeChargeVo.setOneLevelVoList(oneLevelVo.getList());
        homeChargeVo.setTwoLevelInfoVoList(twoLevelVo.getList());
        homeChargeVo.setThreeLevelInfoVoList(threeLevelVo.getList());

        double total = oneLevelVo.getMoney() + twoLevelVo.getMoney() + threeLevelVo.getMoney();
        homeChargeVo.setTotal("" + total);

        double totalGold = oneLevelVo.getGold() + twoLevelVo.getGold() + threeLevelVo.getGold();
        homeChargeVo.setTotalGold("" + totalGold);

        homeChargeVo.setStart(startStr);
        homeChargeVo.setEnd(endStr);
        return homeChargeVo;
    }

    @Override
    public List<WaterRecordVo> waterRecords(long agentId) {

        Date start = DateUtil.getThisYearStart();
        Date end = new Date();
        List<Charge> list = getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndRecharge_sourceIs(Arrays.asList(agentId),start, end, 1, CHARGE_TYPE_CASH);

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

        //金额
        double total = 0d;
        //金币
        double goldTotal = 0d;
        List<OneLevelInfoVo> oneLevelInfoVoList = new ArrayList<>();
        //获取自己和自己直接玩家的所有list
        List<Long> aList = new ArrayList<>();
        aList.add(agentBean.getId());
        aList.addAll(agentBean.getChildList());

        //查一下手下玩家
        for (Long uid : aList){

            //不要代理 只要玩家
//            if (uid != agentId && RedisManager.getAgentRedisService().isExit(uid)) continue;
            if ( RedisManager.getAgentRedisService().isExit(uid)){
                if (uid != agentId){
                    continue;
                }
            }

            User user = userDao.getUserById(uid);
            if (user == null){
                continue;
            }

            List<Charge> list = getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(Arrays.asList(uid), start, end, 1, Arrays.asList(MONEY_TYPE, GOLD_TYPE));

            double totalMoney = 0d;
            double totalGold = 0d;
            for (Charge charge : list){
                if (charge.getChargeType() == MONEY_TYPE){
                    totalMoney += charge.getMoney();
                }else {
                    totalGold += charge.getMoney();
                }
            }

            total += totalMoney;
            goldTotal += totalGold;

            OneLevelInfoVo oneLevelInfoVo = new OneLevelInfoVo();
            oneLevelInfoVo.setUid(user.getId());
            oneLevelInfoVo.setGold(goldTotal +"");
            oneLevelInfoVo.setImage(user.getImage() + "/96");
            oneLevelInfoVo.setUsername(user.getUsername());
            oneLevelInfoVo.setMoney("" + totalMoney);
            oneLevelInfoVoList.add(oneLevelInfoVo);
        }

        oneLevelVo.setMoney(total);
        oneLevelVo.setGold(goldTotal);
        oneLevelVo.setList(oneLevelInfoVoList);

        return oneLevelVo;
    }

    @Override
    public TwoLevelVo twoLevelCharges(Date start, Date end, long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        TwoLevelVo twoLevelVo = new TwoLevelVo();

        //获取所有二级代理的id
        List<Long> aList = new ArrayList<>();
        for (Long id : agentBean.getChildList()){
            if (RedisManager.getAgentRedisService().isExit(id)){
                aList.add(id);
            }
        }

        logger.info("==========agentId{}===aList:{}", agentId, aList);

        double total = 0d;
        double goldTotal = 0d;

        //手下所有二级代理
        for (Long delegateId : aList){

            User user = userDao.getUserById(delegateId);
            if (user == null) continue;

            List<Charge> list = getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(Arrays.asList(delegateId), start, end, 1, Arrays.asList(MONEY_TYPE, GOLD_TYPE));
            TwoLevelInfoVo twoLevelInfoVo = new TwoLevelInfoVo();

            //计算金额
            double totalMoney = 0d;
            //计算金币
            double totalGold = 0d;
            for (Charge charge : list){
                if (charge.getChargeType() == MONEY_TYPE){
                    totalMoney += charge.getMoney();
                }else {
                    totalGold += charge.getMoney();
                }
            }

            twoLevelInfoVo.setMoney("" + totalMoney);
            twoLevelInfoVo.setGold("" + totalGold);
            twoLevelInfoVo.setUid(user.getId());
            twoLevelInfoVo.setImage(user.getImage() + "/96");
            twoLevelInfoVo.setUsername(user.getUsername());
            twoLevelVo.getList().add(twoLevelInfoVo);

            total += totalMoney;
            goldTotal += totalGold;

            //二级代理手下直接用户
            AgentBean twoLevelAgentBean = RedisManager.getAgentRedisService().getAgentBean(delegateId);
            if (twoLevelAgentBean == null){
                logger.info("异常数据:{}", delegateId);
                continue;
            }
            for (Long uid : twoLevelAgentBean.getChildList()){

                if (RedisManager.getAgentRedisService().isExit(uid)) continue;

                User twoLevelUser = userDao.getUserById(uid);
                if (twoLevelUser == null) continue;

                TwoLevelInfoVo infoVo = new TwoLevelInfoVo();
                infoVo.setUsername(twoLevelUser.getUsername());
                infoVo.setImage(twoLevelUser.getImage() + "/96");

                List<Charge> twoLevelChargeList = getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(Arrays.asList(uid), start, end, 1, Arrays.asList(MONEY_TYPE, GOLD_TYPE));
                double twoLevelUserTotal = 0;
                double twoLevelUserGoldTotal = 0;
                for (Charge charge : twoLevelChargeList){
                    if (charge.getChargeType() == MONEY_TYPE){
                        twoLevelUserTotal += charge.getMoney();
                    }else {
                        twoLevelUserGoldTotal += charge.getMoney();
                    }
                }
                infoVo.setMoney("" + twoLevelUserTotal);
                twoLevelVo.getList().add(infoVo);

                total += twoLevelUserTotal;
                goldTotal += twoLevelUserGoldTotal;


            }
        }

        twoLevelVo.setMoney(total);
        twoLevelVo.setGold(goldTotal);
        return twoLevelVo;
    }

    @Override
    public ThreeLevelVo threeLevelCharges(Date start, Date end, long agentId) {

        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        ThreeLevelVo threeLevelVo = new ThreeLevelVo();

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
                AgentBean agent3Bean = RedisManager.getAgentRedisService().getAgentBean(uid);
                bList.addAll(agent3Bean.getChildList());
            }
        }

        double total = 0d;
        double goldTotal = 0d;
        for (Long uid : bList){

            User user = userDao.getUserById(uid);
            if (user == null) continue;
            List<Charge> chargeList = getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(Arrays.asList(uid), start, end, 1, Arrays.asList(MONEY_TYPE, GOLD_TYPE));
            ThreeLevelInfoVo threeLevelInfoVo = new ThreeLevelInfoVo();
            threeLevelInfoVo.setUsername(user.getUsername());
            threeLevelInfoVo.setImage(user.getImage() + "/96");
            threeLevelInfoVo.setUid(user.getId());

            double totalMoney = 0;
            double totalGold = 0;
            for (Charge charge : chargeList){
                if (charge.getChargeType() == MONEY_TYPE){
                    totalMoney += charge.getMoney();
                }else {
                    totalGold += charge.getMoney();
                }
            }
            threeLevelInfoVo.setMoney("" + totalMoney);
            threeLevelInfoVo.setGold("" + totalGold);
            threeLevelVo.getList().add(threeLevelInfoVo);

            goldTotal += totalGold;
            total += totalMoney;
        }

        threeLevelVo.setMoney(total);
        threeLevelVo.setGold(goldTotal);
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

    public List<Charge> getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndChargeTypeIn(List<Long> users, Date start, Date end, int status, List<Integer> list){

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.between(root.get("createtime").as(Date.class), start, end));
                predicateList.add(cb.equal(root.get("status").as(Integer.class), status));
                predicateList.add(root.get("chargeType").as(Integer.class).in(list));
                predicateList.add(root.get("userid").as(Integer.class).in(users));
                Predicate[] p = new Predicate[predicateList.size()];
                query.where(cb.and(predicateList.toArray(p)));
                return query.getRestriction();
            }
        };
        List<Charge> chargeList = chargeDao.findAll(specification);
        return chargeList;
    }

    public List<Charge> getChargesByUseridInAndCreatetimeBetweenAndStatusIsAndRecharge_sourceIs(List<Long> users, Date start, Date end, int status, String sourceType){

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.between(root.get("createtime").as(Date.class), start, end));
                predicateList.add(cb.equal(root.get("status").as(Integer.class), status));
                predicateList.add(cb.equal(root.get("recharge_source").as(String.class), sourceType));
                predicateList.add(root.get("userid").as(Integer.class).in(users));
                Predicate[] p = new Predicate[predicateList.size()];
                query.where(cb.and(predicateList.toArray(p)));
                return query.getRestriction();
            }
        };
        List<Charge> chargeList = chargeDao.findAll(specification);
        return chargeList;
    }



}
