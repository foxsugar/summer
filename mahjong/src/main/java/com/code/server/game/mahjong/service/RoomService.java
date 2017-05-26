package com.code.server.game.mahjong.service;

import com.code.server.game.mahjong.logic.RoomFactory;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.mahjong.util.ErrorCode;
import com.code.server.game.mahjong.util.RandomUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.ITimeHandler;
import com.code.server.util.timer.TimerNode;
import net.sf.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/5/23.
 */
public class RoomService {

    public int createRoomButNotInRoom(String userId, String modeTotal, String mode, String multiple, String gameNumber, String personNumber, String gameType)  {
        if(modeTotal.equals("1")){
            if(!mode.equals("5")&&!mode.equals("6")){
                return
                        ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
                      
            }
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("5")){
                return 
                        ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
                        
            }
        }
        else if(modeTotal.equals("2")){
            if(!mode.equals("1")&&!mode.equals("2")&&!mode.equals("3")&&!mode.equals("4")){

                return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("3")&&!multiple.equals("4")&&!multiple.equals("5")){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("3") || modeTotal.equals("15")){
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("5")){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("4")){
            if(!mode.equals("303")){
                
                       return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("5")){
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("5")){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("6")){
            if(!mode.equals("0")){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        /**
         256封顶、128封顶、杠呲宝、未上听包三家、杠上开花、三七夹	二进制取,1是0否
         */
        else if(modeTotal.equals("10")){
            if(!multiple.equals("25")&&!multiple.equals("50")&&!multiple.equals("100")&&!multiple.equals("200")){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(Integer.parseInt(mode)>63||Integer.parseInt(mode)<0){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("11")){
            if(!multiple.equals("25")&&!multiple.equals("50")&&!multiple.equals("100")&&!multiple.equals("200")){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(Integer.parseInt(mode)>63||Integer.parseInt(mode)<0){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }
        else if(modeTotal.equals("12")){
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("3")&&!multiple.equals("4")){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(Integer.parseInt(mode)!= 12051314 && Integer.parseInt(mode)!= 12051304 && Integer.parseInt(mode)!= 12050314 && Integer.parseInt(mode)!= 12050304 && Integer.parseInt(mode)!= 12151314 && Integer.parseInt(mode)!= 12151304 && Integer.parseInt(mode)!= 12150314 && Integer.parseInt(mode)!= 12150304){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }

        else if(modeTotal.equals("13")){
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("3")&&!multiple.equals("4")){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(Integer.parseInt(mode)!= 12051314 && Integer.parseInt(mode)!= 12051304 && Integer.parseInt(mode)!= 12050314 && Integer.parseInt(mode)!= 12050304 && Integer.parseInt(mode)!= 12151314 && Integer.parseInt(mode)!= 12151304 && Integer.parseInt(mode)!= 12150314 && Integer.parseInt(mode)!= 12150304){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }

        else if(modeTotal.equals("14")){
            if(!multiple.equals("1")&&!multiple.equals("2")&&!multiple.equals("3")&&!multiple.equals("4")){

                return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
            if(Integer.parseInt(mode)!= 12051314 && Integer.parseInt(mode)!= 12051304 && Integer.parseInt(mode)!= 12050314 && Integer.parseInt(mode)!= 12050304 && Integer.parseInt(mode)!= 12151314 && Integer.parseInt(mode)!= 12151304 && Integer.parseInt(mode)!= 12150314 && Integer.parseInt(mode)!= 12150304){

                return    ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
            }
        }

        //modeTotal 10快听，11慢听  
        //mode 以下2行拼接
        //000 不封顶，128 128封顶 256 256封顶
        //三七夹，未上听，杠开，杠呲宝   0000-1111

        else{

            return   ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR;
        }
		/*if(!gameNumber.equals("4")&&!gameNumber.equals("8")){
			
					ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR,
					"cannot create room parameter is error");
		}*/

        JSONObject result = new JSONObject();

        User user = userDao.getUser(Integer.parseInt(userId));

        Room room = roomDao.createRoom(user.getId());

        List<Room> allRoomList = roomDao.getAllRoom();
        List<String> allRoomIdList = new ArrayList<String>();

        for (Room room4Id : allRoomList) {
            allRoomIdList.add(room4Id.getRoomId());
        }

        final String roomId = RandomUtil.getRoomId(allRoomIdList);

        room.setRoomId(roomId);
        room.setCreateTime(new Timestamp(System.currentTimeMillis()));
        room.setModeTotal(modeTotal);
        room.setMode(mode);
        room.setMultiple(Integer.parseInt(multiple));
        room.setGameNumber(Integer.parseInt(gameNumber));
        room.setPersonNumber(Integer.parseInt(personNumber));
        room.setCreateUser(Integer.parseInt(userId));
        room.setBankerId(0);//记录房间人数
        room.setRoleIds("0");//0表示未开始，1表示游戏已经开始



        if("LQ".equals(gameType)){
            if(8==Integer.parseInt(gameNumber)){
                if(user.getMoney() < 30){
                    ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY,
                            "cannot join room no money");
                }
                user.setMoney(user.getMoney() - 30);
                user.setMarquee(Integer.parseInt(user.getMarquee())+3+"");
            }else if(16==Integer.parseInt(gameNumber)){
                if(user.getMoney() < 60){
                    ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY,
                            "cannot join room no money");
                }
                user.setMoney(user.getMoney() - 60);
                user.setMarquee(Integer.parseInt(user.getMarquee())+6+"");
            }
        }
        userDao.saveUser(user);
        roomDao.saveRoom(room);


        //roomDao.removeRoom(roomId);

        int userIdInt= Integer.parseInt(userId);
        RoomInfo roomInfo = RoomFactory.getRoomInstance(gameType);

        //初始化时，roominfo的bankerId默认为0
        roomInfo.init(roomId,userIdInt,modeTotal, mode, Integer.parseInt(multiple), Integer.parseInt(gameNumber),Integer.parseInt(personNumber),userIdInt,0);
        roomInfo.setRoomDao(roomDao);
        roomInfo.setUserDao(userDao);
        roomInfo.setUserRecodeDao(userRecodeDao);
        roomInfo.setGameDao(gameDao);
        roomInfo.setServerContext(serverContext);
        //roomInfo.joinRoom(userIdInt);
        roomInfo.setEach("2");//1是4个分开付，0是user付,2其他人付

        GameManager.getInstance().addRoom(roomInfo);

        long start = System.currentTimeMillis();
        TimerNode node = new TimerNode(start, ONE_HOUR, false, new ITimeHandler() {
            @Override
            public void fire() {
                try {
                    RoomInfo roomInfo = GameManager.getInstance().getRoom(roomId);
                    if (roomInfo!=null && !roomInfo.isInGame() && roomInfo.getCurGameNumber()==1) {
                        User user = userDao.getUser(roomInfo.getCreateUser());
                        if("LQ".equals(roomInfo.getGameType())){
                            if(8==roomInfo.getGameNumber()){
                                user.setMoney(user.getMoney() + 30);
                                user.setMarquee(Integer.parseInt(user.getMarquee())-3+"");
                            }else if(16==roomInfo.getGameNumber()){
                                user.setMoney(user.getMoney() + 60);
                                user.setMarquee(Integer.parseInt(user.getMarquee())-6+"");
                            }
                        }
                        userDao.saveUser(user);
                        GameManager.getInstance().remove(roomInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.timerNode = node;
        GameTimer.getInstance().addTimerNode(node);
		
		
/*		ArrayList<JSONObject> userJSONObject = new ArrayList<JSONObject>();
		userJSONObject.add(user.toJSONObject());
		room.setUserList(userJSONObject);*/

        result.put("params", roomInfo.toJSONObject());
        result.put("code", "0");
        return result;
    }

}
