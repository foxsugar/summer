package com.code.server.login.service;

import com.code.server.constant.game.UserBean;
import com.code.server.db.Service.GameRecordService;
import com.code.server.db.Service.RebateDetailService;
import com.code.server.db.model.RebateDetail;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by sunxianping on 2018-10-10.
 */
@Component
public class ScheduledService {
    /**
     * 0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
     0 0/30 9-17 * * ?   朝九晚五工作时间内每半小时
     0 0 12 ? * WED 表示每个星期三中午12点 
     "0 0 12 * * ?" 每天中午12点触发 
     "0 15 10 ? * *" 每天上午10:15触发 
     "0 15 10 * * ?" 每天上午10:15触发 
     "0 15 10 * * ? *" 每天上午10:15触发 
     "0 15 10 * * ? 2005" 2005年的每天上午10:15触发 
     "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 
     "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 
     "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 
     "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 
     "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 
     "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 
     "0 15 10 15 * ?" 每月15日上午10:15触发 
     "0 15 10 L * ?" 每月最后一日的上午10:15触发 
     "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发 
     "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发 
     "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
     */

    Logger logger = LoggerFactory.getLogger(ScheduledService.class);

    @Autowired
    GameRecordService gameRecordService;

    @Autowired
    RebateDetailService rebateDetailService;

    /**
     * 每天5点 定时删除游戏记录
     */
    @Scheduled(cron = "0 0 5 ? * *")
    public void scheduled(){
        if (SpringUtil.getBean(ServerConfig.class).getDeleteRecordTask() == 1) {
            logger.info("=====>>>>>定时任务 删除游戏记录  {}",System.currentTimeMillis());

            LocalDate date = LocalDate.now();
            date = date.minusDays(7);

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = date.atStartOfDay(zoneId);

            Date d = Date.from(zdt.toInstant());
            gameRecordService.gameRecordDao.deleteAllByDateBefore(d);
            gameRecordService.replayDao.deleteAllByDateBefore(d);
        }

        if (SpringUtil.getBean(ServerConfig.class).getLoadAllUser() == 1) {
            LocalDate yestoday = LocalDate.now().minusDays(1);
            String ys = yestoday.toString();
//            ys = LocalDate.now().toString();
            for(String uid : RedisManager.getUserRedisService().getAllUserId()){
                long userId = Long.valueOf(uid);
                UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
                if (userBean.getVip() != 0 && userBean.getReferee() != 0) {

                    Double lastDayRebate = rebateDetailService.rebateDetailDao.getRebateByDate(userId, ys);
                    if (lastDayRebate != null) {

                        double re = lastDayRebate /10;

                        UserBean parent = RedisManager.getUserRedisService().getUserBean(userBean.getReferee());
                        parent.getUserInfo().setAllRebate(parent.getUserInfo().getAllRebate() + re);
                        RedisManager.getUserRedisService().updateUserBean(parent.getId(), parent);

                        //返利记录
                        RebateDetail rebateDetail = new RebateDetail();
                        rebateDetail.setNum(re);
                        rebateDetail.setUserId(userBean.getId());
                        rebateDetail.setAgentId(parent.getId());
                        rebateDetail.setDate(new Date());
                        rebateDetail.setType(1);

                        rebateDetailService.rebateDetailDao.save(rebateDetail);
                    }
                }
            }
//            rebateDetailService.rebateDetailDao.findAllByAgentId()
        }

    }




    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        date = date.minusDays(7);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = date.atStartOfDay(zoneId);

        Date d = Date.from(zdt.toInstant());
        System.out.println(date.toString());
    }
}
