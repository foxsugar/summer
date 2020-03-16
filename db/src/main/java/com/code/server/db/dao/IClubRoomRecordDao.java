package com.code.server.db.dao;

import com.code.server.db.model.ClubRoomRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IClubRoomRecordDao extends PagingAndSortingRepository<ClubRoomRecord, Long> {
    @Query(value="select * from club_room_records where club_id=?1 and floor=?2 and DATE_FORMAT(record_date,'%Y-%m-%d')=?3", nativeQuery = true)
    List<ClubRoomRecord> getAllByClubIdAndFloor(String clubId, int floor, String date);

    @Query(value="select * from club_room_records where club_id=?1 and DATE_FORMAT(record_date,'%Y-%m-%d')=?2", nativeQuery = true)
    List<ClubRoomRecord> getAllByClubIdAndDate(String clubId, String date);
}
