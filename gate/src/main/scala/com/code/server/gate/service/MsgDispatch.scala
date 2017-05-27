package com.code.server.gate.service

import com.code.server.constant.kafka.IKafaTopic
import com.code.server.constant.response.ErrorCode
import com.code.server.gate.config.ServerConfig
import com.code.server.gate.kafka.MsgProducer
import com.code.server.gate.util.SpringUtil
import com.code.server.redis.service.UserRedisService
import net.sf.json.JSONObject

/**
  * Created by sunxianping on 2017/5/26.
  */
object MsgDispatch {



  def dispatch(msg:Object):Unit = {
    val jsonObject = msg.asInstanceOf[JSONObject]
    val service = jsonObject.getString("service")
    val method = jsonObject.getString("method")
    val params = jsonObject.getJSONObject("params")

    val result =
      service match {
        case "userService" => userService_dispatch(service, method, params)

      }

  }


  def userService_dispatch(service:String, method:String, jSONObject: JSONObject):Unit = {
    method match {
      case "loginGate" =>
    }
  }

  def loginGate(userId:Long, token:String):Int = {
    var result = 0
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val token_redis = userRedisService.getToken(userId)
    if(token != token_redis){
      return ErrorCode.ID_TOKEN_NOT_MATCH
    }
    val loginGateId = userRedisService.getGateId(userId)
    //登录过
    if(loginGateId != null){

      val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
      //两个netty连接在同一个gate上
      if(serverConfig.getServerId == loginGateId){

      }else{

      }
      //发送kick通知
      val kafkaSend = SpringUtil.getBean(classOf[MsgProducer])

      kafkaSend.send(IKafaTopic.GATE_TOPIC,loginGateId,)
    }
    result

  }

  def main(args: Array[String]): Unit = {

    print("9999")
  }
}
