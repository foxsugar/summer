package com.code.server.center.action;

import com.code.server.db.Service.ClubRecordService;
import com.code.server.db.model.ClubRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2019-05-24.
 */
@RestController
@EnableAutoConfiguration
public class RoomRecordAction {

    @Autowired
    ClubRecordService clubRecordService;

    @RequestMapping("/getRoomUserScore")
    public Object getRoomUserScore(String clubId, String date) {

        String key = clubId + "|" + date;
        ClubRecord clubRecord = clubRecordService.getClubRecordDao().getClubRecordById(key);
        Map<Long, Double> userScore = new HashMap<>();

//        RoomRecord roomRecord =
        for (int i = 0; i < clubRecord.getRecords().size(); i++) {
            Map map = (Map) clubRecord.getRecords().get(i);
            List list = (List)map.get("records");
            for(int j=0;j<list.size();j++){
                Map rm = (Map) list.get(j);
                long userId = Integer.valueOf(rm.get("userId").toString());


                double score = Double.parseDouble(rm.get("score").toString());
                double fs = score + userScore.getOrDefault(userId, 0D);
                userScore.put(userId, fs);
            }
            System.out.println(list);
        }
//        clubRecord.getRecords().forEach(roomRecord -> {
//            System.out.println(roomRecord);
//        });
//        for (List<RoomRecord> roomRecords : clubRecord.getRecords()) {
//            for (UserRecord userRecord : roomRecord.getRecords()) {
//
//                long userId = userRecord.getUserId();
//                double score = userRecord.getScore() + userScore.getOrDefault(userId, 0D);
//                userScore.put(userId, score);
//            }
//        }
        return userScore;
    }

}
