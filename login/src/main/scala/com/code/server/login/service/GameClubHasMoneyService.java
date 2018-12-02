package com.code.server.login.service;

import com.code.server.constant.club.ClubMember;
import com.code.server.constant.club.UpScoreItem;
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
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * 设置合伙人
     * @param msgKey
     * @param clubId
     * @param userId
     * @param partnerId
     * @return
     */
    public int setPartner(KafkaMsgKey msgKey, String clubId, long userId, long partnerId) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        if (!club.getClubInfo().getPartner().contains(partnerId)) {
            club.getClubInfo().getPartner().add(partnerId);
        }

        sendMsg(msgKey, new ResponseVo("clubService", "setPartner", "ok"));
        return 0;
    }


    public int changePartner(KafkaMsgKey msgKey, String clubId, long userId, long newPartner, long changeUser){
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }
        ClubMember clubMember = club.getClubInfo().getMember().get("" + changeUser);
        clubMember.setReferrer(newPartner);

        sendMsg(msgKey, new ResponseVo("clubService", "changePartner", "ok"));
        return 0;
    }


    /**
     * 上下分
     * @param msgKey
     * @param clubId
     * @param userId
     * @param toUser
     * @param num
     * @return
     */
    public int upScore(KafkaMsgKey msgKey, String clubId, long userId, long toUser, int num) {
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club == null) {
            return ErrorCode.CLUB_NO_THIS;
        }

        ClubMember clubMember = club.getClubInfo().getMember().get("" + userId);
        RedisManager.getClubRedisService().addClubUserMoney(clubId, toUser, num);
        //上下分记录
        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(4);
        List<UpScoreItem> list = club.getUpScoreInfo().getInfo().getOrDefault(date.toString(),new ArrayList<>());
        int type = num>=0?1:0;
        UpScoreItem upScoreItem = new UpScoreItem().setSrcUserId(userId).setDesUserId(toUser).setNum(num)
                .setTime(System.currentTimeMillis()).setType(type).setName(clubMember.getName());

        //添加一条记录
        list.add(upScoreItem);
        club.getUpScoreInfo().getInfo().put(date.toString(), list);
        club.getUpScoreInfo().getInfo().remove(dateBefore.toString());

        sendMsg(msgKey, new ResponseVo("clubService", "upScore", "ok"));
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
