package com.code.server.login.service;

import com.code.server.db.model.AgentRecords;
import com.code.server.db.model.Charge;
import com.code.server.db.model.GameAgent;
import com.code.server.db.model.User;
import com.code.server.grpc.idl.Game;
import com.code.server.login.vo.HomePageVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/8.
 */

public interface HomeService {

    HomePageVo showHomePage(long agentId);

    Page<User> timeQuery(List<Date> listA, List<Date> listB, Pageable pageable);

    Long timeQueryCount(List<Date> listA, List<Date> listB);

    Page<GameAgent> findDelegates(Pageable pageable);

    Long delegatesCount();

    Long partnerCount();

    GameAgent findOneDelegate(long userId);

    GameAgent findOnePartner(long userId);

    Page<GameAgent> findPartner(Pageable pageable);

    Page<Charge> findCharges(Pageable pageable);

    Charge findChargeByUserId(long userId);

    List<Charge> findChargesByUserId(long userId);

    Charge findChargeByOrderId(long oId);

    Page<Charge> timeSearchCharges(List<Date> listA, org.springframework.data.domain.Pageable pageable);

    Long chargesCount();

    Long timeSearchChargesCount(List<Date> listA);

    Page<AgentRecords> findAllAgentRecords(int agentId , List<String> listA, Pageable pageable);

}
