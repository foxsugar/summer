package com.code.server.login.rpc;


import com.code.server.constant.game.IChargeType;
import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.ChargeService;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Charge;
import com.code.server.db.model.User;
import com.code.server.login.service.ServerManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.redis.service.UserRedisService;
import com.code.server.rpc.idl.*;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sunxianping on 2017/3/29.
 */
public class GameRpcNewHandler implements GameRPCNew.AsyncIface {

    private static final Logger logger = LoggerFactory.getLogger(GameRpcNewHandler.class);

    @Override
    public void charge(Order order, AsyncMethodCallback<Integer> resultHandler) throws TException {
        long userId = order.getUserId();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        UserService userService = SpringUtil.getBean(UserService.class);
        User user = userService.getUserByUserId(userId);
        String name = "";
        if (userBean == null) {
            if (user != null) {
                if (order.getType() == ChargeType.money.getValue()) {
                    user.setMoney(user.getMoney() + order.getNum());
                } else if (order.getType() == ChargeType.gold.getValue()) {
                    user.setGold(user.getGold() + order.getNum());
                }
                userService.save(user);
                name = user.getUsername();
            } else {
                resultHandler.onComplete(RPCError.NO_USER.getValue());
                return;
            }

        } else {//在redis里
            name = userBean.getUsername();
            if (order.getType() == ChargeType.money.getValue()) {
                double nowMoney = RedisManager.getUserRedisService().addUserMoney(userId, order.getNum());
                user.setMoney(nowMoney);
                userService.save(user);
            } else if (order.getType() == ChargeType.gold.getValue()) {
                double nowGold = RedisManager.getUserRedisService().addUserMoney(userId, order.getNum());
                user.setGold(nowGold);
                userService.save(user);
            }
        }
        //充值记录
        if (order.getAgentId() != 0 && order.getType() == ChargeType.money.getValue()) {
            Charge charge = new Charge();
            charge.setOrderId("" + IdWorker.getDefaultInstance().nextId());
            charge.setUserid(order.getUserId());
            charge.setUsername(name);
            charge.setCreatetime(new Date());
            charge.setCallbacktime(new Date());
            charge.setOrigin(order.getAgentId());
            charge.setMoney(order.getNum());
            charge.setMoney_point(order.getNum());
            charge.setRecharge_source("" + IChargeType.AGENT);
            charge.setStatus(1);
            SpringUtil.getBean(ChargeService.class).save(charge);

        }
        resultHandler.onComplete(0);
    }

    @Override
    public void getUserInfo(long userId, AsyncMethodCallback<com.code.server.rpc.idl.User> resultHandler) throws TException {
        com.code.server.rpc.idl.User userRep = new com.code.server.rpc.idl.User();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            User user = userService.getUserByUserId(userId);
            if (user != null) {
                userRep.setId(userId);
                userRep.setGold(user.getGold());
                userRep.setMoney(user.getMoney());
                userRep.setUsername(user.getUsername());
            }
        } else {
            userRep.setId(userBean.getId());
            userRep.setGold(userBean.getGold());
            userRep.setMoney(userBean.getMoney());
            userRep.setUsername(userBean.getUsername());
        }


        resultHandler.onComplete(userRep);
    }

    @Override
    public void exchange(Order order, AsyncMethodCallback<Integer> resultHandler) throws TException {
        long userId = order.getUserId();
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            UserService userService = SpringUtil.getBean(UserService.class);
            User user = userService.getUserByUserId(userId);
            user.setMoney(user.getMoney() - order.getNum());
            user.setGold(user.getGold() + order.getNum());
            userService.save(user);
        } else {
            UserRedisService userRedisService = RedisManager.getUserRedisService();
            userRedisService.addUserMoney(userId, -order.getNum());
            RedisManager.addGold(userId, order.getNum());
        }
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyMarquee(String str, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setMarquee(str);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyDownload(String str, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setDownload(str);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyAndroidVersion(String str, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setVersionOfAndroid(str);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyIOSVersion(String str, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setVersionOfIos(str);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void shutdown(AsyncMethodCallback<Integer> resultHandler) throws TException {
//        Shutdown.shutdown();
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyInitMoney(int money, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setInitMoney(money);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyAppleCheck(int status, AsyncMethodCallback<Integer> resultHandler) throws TException {
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setAppleCheck(status);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void modifyDownload2(String str, AsyncMethodCallback<Integer> resultHandler) throws TException {

        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        ServerManager.constant.setDownload2(str);
        constantService.constantDao.save(ServerManager.constant);
        resultHandler.onComplete(0);
    }

    @Override
    public void addBlackList(long userId, AsyncMethodCallback<Integer> resultHandler) throws TException {

    }

    @Override
    public void removeBlackList(long userId, AsyncMethodCallback<Integer> resultHandler) throws TException {

    }

    @Override
    public void getBlackList(AsyncMethodCallback<Set<Long>> resultHandler) throws TException {
        ServerManager.init();
        Set<Long> set = ServerManager.constant.getBlackList().stream().mapToLong(Long::valueOf).boxed().collect(Collectors.toSet());
        resultHandler.onComplete(set);
    }

    @Override
    public void getOnlineUser(AsyncMethodCallback<OnlineNum> resultHandler) throws TException {

    }

    @Override
    public void bindReferee(long userId, int referee, AsyncMethodCallback<Integer> resultHandler) throws TException {
        logger.info("绑定邀请码 userId : " + userId);
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean != null) {
            userBean.setReferee(referee);
            RedisManager.getUserRedisService().updateUserBean(userBean.getId(), userBean);
        } else {
            UserService userService = SpringUtil.getBean(UserService.class);
            User user = userService.getUserByUserId(userId);
            user.setReferee(referee);
            userService.save(user);
        }
        resultHandler.onComplete(0);
    }


}
