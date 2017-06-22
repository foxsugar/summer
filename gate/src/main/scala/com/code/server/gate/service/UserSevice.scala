package com.code.server.gate.service

import java.net.InetSocketAddress

import com.code.server.constant.game.UserBean
import com.code.server.constant.response.{Notice, ResponseVo}
import com.code.server.gate.config.ServerConfig
import com.code.server.redis.service.{RedisManager, UserRedisService}
import com.code.server.util.SpringUtil
import io.netty.channel.ChannelHandlerContext

/**
  * Created by sunxianping on 2017/5/27.
  */
object UserSevice {


  def sendExit(userId: Long):Unit ={
    val notice = new Notice
    notice.setMessage("notice exit")
    GateManager.sendMsg(new ResponseVo("userService","noticeExit",notice), userId)
  }

  def doLogin(userId:Long, ctx:ChannelHandlerContext):UserBean = {
    val insocket = ctx.channel.remoteAddress.asInstanceOf[InetSocketAddress]
    val clientIP = insocket.getAddress.getHostAddress

    val userRedisService = RedisManager.getUserRedisService
    val userBean = userRedisService.getUserBean(userId)
    userBean.setIpConfig(clientIP)

    //加入
    ctx.channel.attr(GateManager.attributeKey).set(userId)
    //加入map
    GateManager.putUserNettyCtx(userId, ctx)

    userRedisService.updateUserBean(userId,userBean)
    //gate 映射
    val gateId = SpringUtil.getBean(classOf[ServerConfig]).getServerId
    userRedisService.setGateId(userId,gateId.toString)

    userBean


  }
}
