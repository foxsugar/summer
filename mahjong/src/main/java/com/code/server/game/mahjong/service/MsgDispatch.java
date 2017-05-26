package com.code.server.game.mahjong.service;

import com.code.server.game.mahjong.kafka.MsgProducer;
import com.code.server.game.mahjong.response.ResponseVo;
import com.code.server.game.mahjong.util.ErrorCode;
import com.code.server.game.mahjong.util.SpringUtil;
import com.code.server.util.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by sunxianping on 2017/5/23.
 */
public class MsgDispatch {

    public void dispatch(ConsumerRecord<?, ?> record){


        JSONObject jSONObject = (JSONObject) record.value();
        String[] keys = ((String)record.key()).split(",");
        int partitionId = Integer.valueOf(keys[0]);
        String userId = keys[1];


        String service = jSONObject.getString("service");
        String method = jSONObject.getString("method");
        JSONObject params = jSONObject.getJSONObject("params");


        int code = dispatchAllMsg(service, method, params);
        //客户端要的方法返回
        if (code != 0) {
            ResponseVo vo = new ResponseVo(service, method, code);
            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
            msgProducer.send("gate",partitionId,userId, JsonUtil.toJson(vo));
        }

    }

    private static int dispatchAllMsg(String service, String method, JSONObject params) {
        switch (service) {

            case "roomService":
                return dispatchRoomService(service,method, params);
            case "GameLogicService":{
                return GameLogicService.dispatch(method, params);


            }

            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }

    private static int dispatchRoomService(String service, String method, JSONObject params){
        switch (method) {

        }
        return 0;
    }


}
