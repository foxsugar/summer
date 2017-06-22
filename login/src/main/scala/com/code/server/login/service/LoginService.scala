package com.code.server.login.service

import java.util.Comparator
import java.util.stream.Collectors

import com.code.server.redis.config.ServerInfo
import com.code.server.redis.service.RedisManager

import scala.util.Random

/**
  * Created by sunxianping on 2017/6/19.
  */
object LoginService {

  /**
    * 通过服务器类型获得一个服务器实例
    * @param serverType
    * @return
    */
  def getSortedServer(serverType: String="GATE"): ServerInfo = {

    val servers = RedisManager.getGateRedisService.getAllServer.stream().filter(
      serverInfo => serverInfo.getStatus == 0 && serverInfo.getServerType == serverType
    ).collect(Collectors.toList())
    //排序
    sort(servers)
    val size = servers.size()
    if (size == 0) {
      return null
    }
    val rand = new Random()
    val randId = rand.nextInt(size)
    servers.get(randId)
  }

  private def sort(servers: java.util.List[ServerInfo]): Unit = {
    val com = new Comparator[ServerInfo] {
      override def compare(o1: ServerInfo, o2: ServerInfo): Int = {
        if (o1.getSort > o2.getSort) {
          -1
        } else {
          1
        }
      }
    }
    servers.sort(com)
  }
}
