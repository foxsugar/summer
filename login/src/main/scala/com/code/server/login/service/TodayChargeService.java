package com.code.server.login.service;

import com.code.server.login.vo.*;

import java.util.Date;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/14.
 */
public interface TodayChargeService {

   //显示今日充值
   HomeChargeVo showCharge(long agentId);
   //显示充值
   HomeChargeVo showCharge(Date start, Date end, long agentId);
   //流水记录
   List<WaterRecordVo> waterRecords(long agentId);
   //一级代理手下的充值记录
   OneLevelVo oneLevelCharges(Date start, Date end, long agentId);
   //一级代理手下的充值记录
   OneLevelVo oneLevelCharges(long agentId);
   //二级代理充值记录
   TwoLevelVo twoLevelCharges(Date start, Date end, long agnetId);
   //二级代理充值记录
   TwoLevelVo twoLevelCharges(long agentId);
   //三级代理充值
   ThreeLevelVo threeLevelCharges(Date start, Date end, long agentId);
   //待机代理充值记录
   ThreeLevelVo threeLevelCharges(long agentId);
}
