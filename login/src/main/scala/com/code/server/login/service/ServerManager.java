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
    }
}
