package com.code.server.constant.response;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ErrorCode {
	
	public static final int DATE_ERROR_PLEASE_REFRESH = 100000;
	public static final int YOU_HAVE_NOT_LOGIN = 100001;
	public static final int CAN_NOT_FIND_OPT = 100002;
	public static final int ROLE_ACCOUNT_OR_PASSWORD_ERROR = 100003;
	public static final int CANNOT_CREATE_ROOM_ROLE_HAS_IN_ROOM = 100004;
	public static final int CANNOT_JOIN_ROOM_NOT_EXIST = 100005;
	public static final int CANNOT_JOIN_ROOM_IS_FULL = 100006;
	public static final int CANNOT_QUIT_ROOM_NOT_EXIST = 100007;
	public static final int CANNOT_QUIT_ROOM_NOT_IN_ROOM = 100008;
	public static final int LOOKER_ACCOUNT_OR_PASSWORD_ERROR = 100009;
	public static final int CANNOT_FIND_THIS_USER = 1000010;
	public static final int THIS_USER_HAVE_BEEN_GETREADY = 1000011;
	public static final int THE_DISSOLUTION_HAVE_BEEN_DEAL = 1000012;
	public static final int CANNOT_JOIN_ROOM_NO_MONEY = 1000013;
	public static final int CANNOT_JOIN_ROOM_IS_IN_GAME = 1000014;
	public static final int CANNOT_QUIT_ROOM_IS_IN_GAME = 1000015;
	public static final int CANNOT_CREATE_ROOM_MONEY = 1000016;
	public static final int REDIS_NO_TOKEN = 1000017;
	public static final int USERNAME_NOT_COMPLIE = 200001;
	public static final int ACCOUNT_NOT_COMPLIE = 200002;
	public static final int PASSWORD_NOT_COMPLIE = 200003;

	//userId获取不到Role
	public static final int USERID_ERROR = 110001;
	
	//userId获取不到Role
	public static final int OPENID_ERROR = 110002;
	
	//用户已在房间
	public static final int CANNOT_CREATE_ROOM_USER_HAS_IN_ROOM = 120001;
	
	//创建房间参数有误
	public static final int CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR = 130001;
	
	//创建房间参数有误
	public static final int SEND_MESSAGE_USERID_IS_ERROR = 140001;
	


	//麻将异常参数=====================================================================================
	//传入用户参数错误
	public static final int MJ_SEND_MESSAGE_USERID_IS_ERROR = 150001;
	//传入操作参数错误
	public static final int MJ_SEND_MESSAGE_OPERATE_IS_ERROR = 150002;
	//当前人没有操作权限
	public static final int MJ_CURRENTUSER_IS_NOT_THE_USER = 150003;


	public static final int REQUEST_PARAM_ERROR = 190000;
	public static final int CAN_NOT_JIAO_TURN = 200000;
	public static final int CAN_NOT_QIANG_TURN = 200001;
	public static final int CAN_NOT_NO_ROOM = 200002;
	public static final int CAN_NOT_NO_GAME = 200003;
	public static final int CAN_NOT_PLAY = 200004;
	public static final int CAN_NOT_JIAO_SCORE = 200005;

	public static final int CAN_NOT_BING_REFERRER = 200010;


	public static final int CAN_NOT_JOIN_PARAM_ERROR = 200020;


	public static final int REFERRER_NOT_EXIST = 250000;
	public static final int REFERRER_ALERADY_BIND = 250001;
	public static final int CHARGE_NO_BIND = 250002;
	
	public static final int BALANCE_INSUFFICIENT = 300000; //余额不足
	public static final int NOT_HAVE_THIS_GIVER = 300001;
	public static final int NOT_HAVE_THIS_ACCEPTER = 300002;
	public static final int NOT_HAVE_MORE_MONEY = 300003;
	public static final int ORDER_WAS_PAID = 300011;//订单已支付
	public static final int ORDER_WAS_CLOSED = 300012; //订单已关闭
	public static final int ID_TOKEN_NOT_MATCH = 400001;

	static class ErrorCodeRepeatChecker {
    	public void check() {
    		Set<Integer> set = new HashSet<Integer>();
    		for(Field field : ErrorCode.class.getFields()) {
    			try {
					int code = (Integer)field.get(null);
					if(set.contains(code)) {
						System.err.println("have repeat errorCode:" + field.getName() + "," + code);
					}
					set.add(code);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    public static void checkRepeat() {
    	ErrorCodeRepeatChecker checker = new ErrorCodeRepeatChecker();
    	checker.check();
    }
}



