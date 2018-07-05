package com.code.server.db.Service;

import com.code.server.db.dao.IChargeDao;
import com.code.server.db.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import scala.Char;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by win7 on 2017/3/13.
 */
@Service("chargeService")
public class ChargeService {

    @Autowired
    public IChargeDao chargeDao;

    public Charge getChargeByOrderid(String oId){
//        return chargeDao.getChargeByOrderId(orderid);

        Specification<Charge> specification = new Specification<Charge>() {
            @Override
            public Predicate toPredicate(Root<Charge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

//                Predicate p2=cb.equal(root.get("lastName").as(String.class), "sd"
                Predicate predicate = cb.equal(root.get("orderId").as(String.class), oId);
                return predicate;
            }
        };
        return chargeDao.findOne(specification);

    }

    public Charge save(Charge charge){
        return chargeDao.save(charge);
    }


}
