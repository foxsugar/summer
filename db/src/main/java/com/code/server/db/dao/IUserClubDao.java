package com.code.server.db.dao;

import com.code.server.db.model.UserClub;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sunxianping on 2018/1/16.
 */
public interface IUserClubDao extends PagingAndSortingRepository<UserClub, Long> {

    UserClub getUserClubById(int id);
}
