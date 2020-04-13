package com.code.server.db.model;

import com.code.server.constant.game.RoomRecord;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName RoomRecord
 * @Description TODO
 * @Author sunxp
 * @Date 2020/3/16 9:51
 **/
@DynamicUpdate
@Entity
@Table(name = "club_room_records",
        indexes = {@Index(name = "id", columnList = "id")})
public class ClubRoomRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String clubId;
    private Date recordDate;
    private String roomId;
    private String roomModelId;
    private int floor;

    @Type(type = "json")
    @Lob
    @Column(columnDefinition = "json")
    private RoomRecord roomRecord;

    public long getId() {
        return id;
    }

    public ClubRoomRecord setId(long id) {
        this.id = id;
        return this;
    }

    public String getClubId() {
        return clubId;
    }

    public ClubRoomRecord setClubId(String clubId) {
        this.clubId = clubId;
        return this;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public ClubRoomRecord setRecordDate(Date date) {
        this.recordDate = date;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public ClubRoomRecord setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getRoomModelId() {
        return roomModelId;
    }

    public ClubRoomRecord setRoomModelId(String roomModelId) {
        this.roomModelId = roomModelId;
        return this;
    }

    public int getFloor() {
        return floor;
    }

    public ClubRoomRecord setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    public RoomRecord getRoomRecord() {
        return roomRecord;
    }

    public ClubRoomRecord setRoomRecord(RoomRecord roomRecord) {
        this.roomRecord = roomRecord;
        return this;
    }
}
