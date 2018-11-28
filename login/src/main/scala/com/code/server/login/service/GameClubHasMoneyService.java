package com.code.server.login.service;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ClubChargeService;
import com.code.server.db.Service.ClubRecordService;
import com.code.server.db.Service.ClubService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Club;
import com.code.server.kafka.MsgProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2018-11-28.
 */
@Service
public class GameClubHasMoneyService {


    @Autowired
    private ClubService clubService;

    @Autowired
    private MsgProducer kafkaMsgProducer;

    @Autowired
    private ClubRecordService clubRecordService;

    @Autowired
    private ClubChargeService clubChargeService;

    @Autowired
    private UserService userService;



    /**
     * 转让俱乐部
     * @param clubId
     * @param userId
     * @param toUser
     * @return
     */
    public int transfer(KafkaMsgKey msgKey, String clubId, long userId, long toUser) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + toUser);
        if(clubMember == null){
            return ErrorCode.CLUB_NOT_TRANSFER;
        }
        club.setImage(clubMember.getImage());
        club.setPresidentName(clubMember.getName());
        club.setPresident(clubMember.getUserId());
        club.setPresidentWx("");

        sendMsg(msgKey, new ResponseVo("clubService", "transfer", "ok"));
        return 0;
    }



    /**
     * 发送消息
     *
     * @param msgKey
     * @param msg
     */
    private void sendMsg(KafkaMsgKey msgKey, Object msg) {
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(), "" + msgKey.getUserId(), msg);
    }
}
