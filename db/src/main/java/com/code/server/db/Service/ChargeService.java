package com.code.server.db.Service;

import com.code.server.db.dao.IChargeDao;
import com.code.server.db.dao.IConstantDao;
import com.code.server.db.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by win7 on 2017/3/13.
 */
@Service("chargeService")
public class ChargeService {

    @Autowired
    public IChargeDao chargeDao;

    public Charge getChargeByOrderid(String orderid){
        return chargeDao.getChargeByOrderId(orderid);
    }

    public Charge save(Charge charge){
        return chargeDao.save(charge);
    }

    public int update(Charge charge){
        return chargeDao.update(charge.getStatus(),charge.getOrderId());
    }

}
