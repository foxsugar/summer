package com.code.server.db.Service;


import com.code.server.db.dao.IServerInfoDao;
import com.code.server.db.dao.ITestDao;
import com.code.server.db.model.ServerInfo;
import com.code.server.db.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by win7 on 2017/3/8.
 */
@Service("serverService")
public class ServerService {

    @Autowired
    public IServerInfoDao serverInfoDao;


    @Autowired
    public ITestDao testDao;

    public List<ServerInfo> getAllServerInfo(){
        return serverInfoDao.findAll();
    }


}
