package com.code.server.db.Service;

import com.code.server.db.dao.IRebateDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2019-06-21.
 */
@Service("rebateDetailService")
public class RebateDetailService {


    @Autowired
    public IRebateDetailDao rebateDetailDao;


}
