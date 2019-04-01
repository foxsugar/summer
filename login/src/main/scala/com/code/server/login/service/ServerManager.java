package com.code.server.login.service;

import com.code.server.constant.db.OtherConstant;
import com.code.server.db.Service.ConstantService;
import com.code.server.db.model.Constant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

import static com.code.server.constant.game.IGameConstant.*;

/**
 * Created by sunxianping on 2017/6/19.
 */
public class ServerManager {
    public static Constant constant;

    public static void init(){
        ConstantService constantService = SpringUtil.getBean(ConstantService.class);
        constant = constantService.constantDao.findOne(1L);
        if (constant == null) {
            Constant constant1 = new Constant();
            constant1.setAppleCheck(1).setDownload("0").setInitMoney(100).setMarquee("welcome").setVersionOfAndroid("0").setVersionOfIos("0").setDownload2("0");
            constantService.constantDao.save(constant1);
            constant = constant1;

        }
        writeConstant2Redis();
    }


    /**
     * 把一些数据写到redis
     */
    public static void writeConstant2Redis(){
        Map<String, Object> data = new HashMap<>();
        if (constant.getOther() == null) {
            constant.setOther(new OtherConstant());
        }
        if (constant.getOther().getRebateData() == null) {
            constant.getOther().setRebateData(new HashMap<>());
        }

        data.put(PAIJIU_BET, constant.getOther().getRebateData().getOrDefault("bet",5));
        data.put(PAIJIU_REBATE100, constant.getOther().getRebateData().getOrDefault("bet",2.5));
        data.put(PAIJIU_REBATE4, constant.getOther().getRebateData().getOrDefault("bet",2));
        data.put(PAIJIU_PAY_ONE, constant.getOther().getRebateData().getOrDefault("bet",10));
        data.put(PAIJIU_PAY_AA, constant.getOther().getRebateData().getOrDefault("bet",3));

        RedisManager.getConstantRedisService().updateConstant(data);
    }



}
