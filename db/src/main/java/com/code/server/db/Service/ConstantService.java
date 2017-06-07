package com.code.server.db.Service;

import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Constant;
import com.code.server.db.model.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by win7 on 2017/3/13.
 */
@Service("constantService")
public class ConstantService {

    @Autowired
    public IConstantDao constantDao;

}
