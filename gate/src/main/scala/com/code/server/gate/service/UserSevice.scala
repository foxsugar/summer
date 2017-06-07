package com.code.server.gate.service

import java.net.InetSocketAddress

import com.code.server.constant.game.UserBean
import com.code.server.constant.response.Notice
import com.code.server.redis.service.UserRedisService
import com.code.server.util.SpringUtil
import io.netty.channel.ChannelHandlerContext

/**
  * Created by sunxianping on 2017/5/27.
  */
object UserSevice {


  def sendExit(userId: Long):Unit ={
    val notice = new Notice
    notice.setMessage("notice exit")
    GateManager.sendMsg(notice, userId)
  }

  def doLogin(userId:Long, ctx:ChannelHandlerContext):UserBean = {
    val insocket = ctx.channel.remoteAddress.asInstanceOf[InetSocketAddress]
    val clientIP = insocket.getAddress.getHostAddress

    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val userBean = userRedisService.getUserBean(userId)
    userBean.setIpConfig(clientIP)

    //加入
    ctx.channel.attr(GateManager.attributeKey).set(userId)
    //加入map
    GateManager.putUserNettyCtx(userId, ctx)

    userRedisService.updateUserBean(userId,userBean)

    userBean


  }
}
