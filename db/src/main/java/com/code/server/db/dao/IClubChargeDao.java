package com.code.server.db.dao;

import com.code.server.db.model.ClubCharge;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sunxianping on 2018/2/27.
 */
public interface IClubChargeDao extends PagingAndSortingRepository<ClubCharge, Long> {
    List<ClubCharge> getClubChargesByClubId(String clubId);
}
