package com.code.server.login.service;

import com.code.server.constant.db.AgentInfo;
import com.code.server.constant.db.ChildCost;
import com.code.server.constant.game.AgentBean;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IGameAgentDao;
import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.Charge;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.User;
import com.code.server.login.action.AgentAction;
import com.code.server.login.vo.HomeChargeVo;
import com.code.server.login.vo.HomePageVo;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import scala.Char;

import javax.persistence.criteria.*;
import java.awt.print.Pageable;
import java.util.*;

/**
 * Created by dajuejinxian on 2018/5/8.
 */
@Service
public class HomeServiceImpl implements HomeService{

    public static final int MONEY_TYPE = 0;

    public static final int GOLD_TYPE = 1;

    @Autowired
    private TodayChargeService todayChargeService;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IGameAgentDao gameAgentDao;

    @Autowired
    private IChargeDao chargeDao;

    private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);
    @Override
    public HomePageVo showHomePage(long agentId) {
        AgentBean agentBean = RedisManager.getAgentRedisService().getAgentBean(agentId);
        HomePageVo homePageVo = new HomePageVo();
        homePageVo.setRebate( agentBean.getRebate());
        homePageVo.setInvitationCode("" + agentId);
        HomeChargeVo homeChargeVo = todayChargeService.showCharge(agentId);
        String total = homeChargeVo.getTotal();
        homePageVo.setTotalMoney((Double.parseDouble(total)));
        //收益
        AgentInfo agentInfo = agentBean.getAgentInfo();
        String today = DateUtil.convert2DayString(new Date());
        Map<String, ChildCost> everyDayCos = agentInfo.getEveryDayCost();
        ChildCost childCost = everyDayCos.get(today);
        if (childCost != null){
            homePageVo.setFirstLevel(childCost.getFirstLevel()* 0.01 * 0.2);
            homePageVo.setSecondLevel(childCost.getSecondLevel()* 0.01 * 0.1);
            homePageVo.setThirdLevel(childCost.getThirdLevel()* 0.01 * 0.1);
            homePageVo.setAllCost(childCost.getFirstLevel() * 0.01 * 0.2 + childCost.getSecondLevel()* 0.01 * 0.1 + childCost.getThirdLevel() * 0.01 * 0.1);
        }
        return homePageVo;
    }

    public Long timeQueryCount(List<Date> listA, List<Date> listB){
        Specification<com.code.server.db.model.User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Expression<Date> registerTimeCol = root.get("registDate");
//                Expression<Date> lastLoginDateCol = root.get("lastLoginDate");
                List<Predicate> predicates = new ArrayList<>();
                if (listA != null && listA.size() != 0){
                    predicates.add(cb.between(root.get("registDate"), listA.get(0), listA.get(1)));
                }
                if (listB != null && listB.size() != 0){
                    predicates.add(cb.between(root.get("lastLoginDate"), listB.get(0), listB.get(1)));
                }
                return query.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };

        return userDao.count(specification);
    }

    @Override
    public Page<GameAgent> findDelegates(org.springframework.data.domain.Pageable pageable) {

        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 0);
                return predicate;
            }
        };

        Page<GameAgent> page = gameAgentDao.findAll(specification, pageable);
        return page;
    }

    public Long  delegatesCount() {

        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 0);
                return predicate;
            }
        };

        return gameAgentDao.count(specification);
    }

    @Override
    public Long partnerCount() {
        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 1);
                return predicate;
            }
        };

        return gameAgentDao.count(specification);
    }

    @Override
    public GameAgent findOneDelegate(long userId) {

        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 0);
                return predicate;
            }
        };
        return gameAgentDao.findOne(userId);
    }

    @Override
    public GameAgent findOnePartner(long userId) {

        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 1);
                return predicate;
            }
        };
        return gameAgentDao.findOne(userId);
    }

    @Override
    public Page<GameAgent> findPartner(org.springframework.data.domain.Pageable pageable) {

        Specification<GameAgent> specification = new Specification<GameAgent>() {
            @Override
            public Predicate toPredicate(Root<GameAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("isPartner");
                Predicate predicate = cb.equal(path, 1);
                return predicate;
            }
        };

        Page<GameAgent> page = gameAgentDao.findAll(specification, pageable);
        return page;
    }

    @Override
    public Page<Charge> findCharges(org.springframework.data.domain.Pageable pageable) {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("chargeType");
                Predicate predicate = path.as(Integer.class).in(Arrays.asList(MONEY_TYPE, GOLD_TYPE));
                return predicate;
            }
        };
        return chargeDao.findAll(specification ,pageable);
    }

    @Override
    public Charge findChargeByUserId(long userId) {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path path = root.get("userid");
                Predicate predicate = path.as(Long.class).in(Arrays.asList(userId));
                return predicate;
            }
        };

       Charge charge = chargeDao.findOne(specification);

        return charge;
    }

    @Override
    public Charge findChargeByOrderId(long oId) {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path path = root.get("orderId");
                Predicate predicate = path.as(Long.class).in(Arrays.asList(oId));
                return predicate;
            }
        };

        Charge charge = chargeDao.findOne(specification);

        return charge;
    }

    @Override
    public Page<Charge> timeSearchCharges(List<Date> listA, org.springframework.data.domain.Pageable pageable) {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (listA != null && listA.size() != 0){
                    predicates.add(cb.between(root.get("createtime"), listA.get(0), listA.get(1)));
                }

                Predicate[] pre = new Predicate[predicates.size()];
                return query.where(predicates.toArray(pre)).getRestriction();
            }
        };

        Page<Charge> page = chargeDao.findAll(specification, pageable);
        return page;
    }

    @Override
    public Long chargesCount() {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path path = root.get("chargeType");
                Predicate predicate = path.as(Integer.class).in(Arrays.asList(MONEY_TYPE, GOLD_TYPE));
                return predicate;
            }
        };

        return chargeDao.count(specification);
    }

    @Override
    public Long timeSearchChargesCount(List<Date> listA) {

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (listA != null && listA.size() != 0){
                    predicates.add(cb.between(root.get("createtime"), listA.get(0), listA.get(1)));
                }

                Predicate[] pre = new Predicate[predicates.size()];
                return query.where(predicates.toArray(pre)).getRestriction();
            }
        };

        return chargeDao.count(specification);
    }

    @Override
    public Page<User> timeQuery(List<Date> listA, List<Date> listB, org.springframework.data.domain.Pageable pageable) {
        Specification<com.code.server.db.model.User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (listA != null && listA.size() != 0){
                    predicates.add(cb.between(root.get("registDate"), listA.get(0), listA.get(1)));
                }
                if (listB != null && listB.size() != 0){
                    predicates.add(cb.between(root.get("lastLoginDate"), listB.get(0), listB.get(1)));
                }
                return query.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };

        Page<User> page = userDao.findAll(specification, pageable);
        return page;
    }

}
