package com.code.server.db.Service;

import com.code.server.db.dao.IConstantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by win7 on 2017/3/13.
 */
@Service("constantService")
public class ConstantService {

    @Autowired
    public IConstantDao constantDao;
}
