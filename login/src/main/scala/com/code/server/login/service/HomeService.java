package com.code.server.login.service;

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
}
