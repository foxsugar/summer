package com.code.server.login.service;
import com.code.server.db.dao.IChargeDao;
import com.code.server.db.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PayServiceImpl implements PayService{
    @Autowired
    private IChargeDao dao;
    @Override
    public Charge create(Integer money) {
        Charge aCharge = new Charge();
        aCharge.setCreatetime(new Date());
        aCharge.setMoney(money);
        aCharge.setSign("0");
        Charge charge = dao.save(aCharge);
        return charge;
    }
}
