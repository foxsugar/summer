package com.code.server.db.Service;

import com.code.server.db.dao.IGoodsExchangeRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by sunxianping on 2019-01-28.
 */
@Service("goodExchangeService")
public class GoodExchangeService {


    @PersistenceContext
    public EntityManager em;

    @Autowired
    public IGoodsExchangeRecordDao goodsExchangeRecordDao;



}
