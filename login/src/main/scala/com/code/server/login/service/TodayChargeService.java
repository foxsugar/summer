package com.code.server.login.service;

import com.code.server.login.vo.OneLevelVo;
import com.code.server.login.vo.TodayChargeVo;
import com.code.server.login.vo.TwoLevelVo;
import com.code.server.login.vo.WaterRecordVo;
import java.util.List;

/**
 * Created by dajuejinxian on 2018/5/14.
 */
public interface TodayChargeService {

   //显示今日充值
   TodayChargeVo showTodayCharge();
   //流水记录
   List<WaterRecordVo> waterRecords();
   //一级代理手下的充值记录
   OneLevelVo oneLevelCharges();
   //二级代理充值记录
   TwoLevelVo twoLevelCharges();
   //三级代理充值

}
