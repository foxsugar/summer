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
            constant1.setAppleCheck(1).setDownload("0").setInitMoney(100).setMarquee("welcome").setVersionOfAndroid("0").setVersionOfIos("0").setDownload2("0").setOther(new OtherConstant());
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

        data.put(PAIJIU_BET, constant.getOther().getRebateData().getOrDefault(PAIJIU_BET,"5"));
        data.put(PAIJIU_REBATE100, constant.getOther().getRebateData().getOrDefault(PAIJIU_REBATE100,"2.5"));
        data.put(PAIJIU_REBATE4, constant.getOther().getRebateData().getOrDefault(PAIJIU_REBATE4,"2"));
        data.put(PAIJIU_PAY_ONE, constant.getOther().getRebateData().getOrDefault(PAIJIU_PAY_ONE,"10"));
        data.put(PAIJIU_PAY_AA, constant.getOther().getRebateData().getOrDefault(PAIJIU_PAY_AA,"3"));

        data.put(FIRST_LEVEL, constant.getOther().getRebateData().getOrDefault(FIRST_LEVEL,"1.2"));
        data.put(SECOND_LEVEL, constant.getOther().getRebateData().getOrDefault(SECOND_LEVEL,"0.6"));
        data.put(THIRD_LEVEL, constant.getOther().getRebateData().getOrDefault(THIRD_LEVEL,"0.2"));

        data.put(FIRST_LEVEL_100, constant.getOther().getRebateData().getOrDefault(FIRST_LEVEL_100,"1.5"));
        data.put(SECOND_LEVEL_100, constant.getOther().getRebateData().getOrDefault(SECOND_LEVEL_100,"0.8"));
        data.put(THIRD_LEVEL_100, constant.getOther().getRebateData().getOrDefault(THIRD_LEVEL_100,"0.2"));

        RedisManager.getConstantRedisService().updateConstant(data);
    }



}
