package com.code.server.login.service;

import com.code.server.db.Service.ConstantService;
import com.code.server.db.model.Constant;
import com.code.server.util.SpringUtil;

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
    }
}
