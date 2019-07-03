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
	public static final int CANNOT_SHARE = 1000018;
	public static final int BLACK_LIST = 1000019;
	public static final int ROOM_CAN_NOT_CHANGE = 1000020;
	public static final int CANNOT_JOIN_ROOM_WATCH = 1000021;
	public static final int CANNOT_JOIN_ROOM_NO_GOLD = 1000022;



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
	public static final int JOIN_ROOM_USERID_IS_0= 140002;



	//麻将异常参数=====================================================================================
	//传入用户参数错误
	public static final int MJ_SEND_MESSAGE_USERID_IS_ERROR = 150001;
	//传入操作参数错误
	public static final int MJ_SEND_MESSAGE_OPERATE_IS_ERROR = 150002;
	//当前人没有操作权限
	public static final int MJ_CURRENTUSER_IS_NOT_THE_USER = 150003;

	public static final int CLUB_CANNOT_MONEY = 1500000;
	public static final int CLUB_CANNOT_NUM = 1500001;
	public static final int CLUB_NO_THIS = 1500002;
	public static final int CLUB_CANNOT_JOIN = 1500003;
	public static final int CLUB_CANNOT_QUIT = 1500004;
	public static final int CLUB_CANNOT_NO_PRESIDENT = 1500005;
	public static final int CLUB_NOT_PRESIDENT = 1500006;
	public static final int CLUB_NOT_MODEL_LIMIT = 1500007;
	public static final int CLUB_PARAM_ERROR = 1500008;
	public static final int CLUB_NOT_MEMBER = 1500009;
	public static final int CLUB_NOT_TRANSFER = 1500010;
	public static final int CLUB_NOT_CHANGE_USER = 1500011;
	public static final int CLUB_NO_USER = 1500012;


	public static final int REQUEST_PARAM_ERROR = 190000;
	public static final int CAN_NOT_JIAO_TURN = 200000;
	public static final int CAN_NOT_QIANG_TURN = 200001;
	public static final int CAN_NOT_NO_ROOM = 200002;
	public static final int CAN_NOT_NO_GAME = 200003;
	public static final int CAN_NOT_PLAY = 200004;
	public static final int CAN_NOT_JIAO_SCORE = 200005;

	public static final int CAN_NOT_BING_REFERRER = 200010;

	public static final int REPLAY_NOT_EXIST = 200011;
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
	public static final int GAMESERVER_NOT_OPEN = 500000;

	public static final int ROOM_START_NOT_CREATEUSER = 500010;
	public static final int ROOM_START_CAN_NOT = 500011;

	public static final int NO_USER = 500020;
	public static final int ALREADY_BET = 500021;
	public static final int BET_PARAM_ERROR = 500022;
	public static final int OPEN_PARAM_ERROR = 500023;
	public static final int NOT_BANKER = 500024;
	public static final int ALREADY_FIGHT_FOR_BANKER = 500025;
	public static final int BANKER_SET_SCORE_GAMENUM_ERROR = 500026;
	public static final int BANKER_BREAK_GAMENUM_ERROR = 500027;
	public static final int READY_NUM_ERROR = 500028;
	public static final int CRAP_PARAM_ERROR = 500029;
	public static final int CRAP_ALREADY_BANKER = 500030;
	public static final int BET_PARAM_LIMIT = 500031;
	public static final int BET_PARAM_NO_MONEY = 500032;
	public static final int ALREADY_OPEN = 500033;

	//扎金花
	public static final int NOT_GET_MEMPAI = 600001;
	public static final int NOT_YOU_TURN = 600002;
	public static final int BET_WRONG = 600003;
	public static final int NOT_KILL = 600004;


	public static final int NO_THIS_ROOM = 700001;

	public static final int NO_ACCESSCODE = 750000;

	//红绿车
	public static final int STATE_ERROR = 800001;
	public static final int SET_SCORE_ERROR = 800002;
	public static final int CAN_NOT_MOVE = 800003;

	//扎股子
	public static final int CARDS_ERROR = 900001;
	public static final int CAN_NOT_DISCARD = 900002;//管不了
	public static final int ALREADY_TALK = 900003;//已经说完话话了
	public static final int OPERATOR_ERROR = 900004;//操作错误
	public static final int MUST_HONGTAO_FIVE = 900005;//必须出红桃5
	public static final int GOLD_NOT_ENOUGH = 900006;//必须出红桃5
	public static final int BET_ERR = 900007;
	//宣起起
	public static final int MUST_PLAY_MaxCard = 1000001;//有大的牌没出的错误码

	//打七
	public static final int ERROR_CARD = 1100001;//牌不对
	public static final int ERROR_CARD_MAX = 1100002;//牌不对
	public static final int ERROR_CANNOT_FANZHU = 1100003;//牌不对
	//填大坑

	public static final int CANNOT_BET = 1110000;//不能下注
	public static final int CANNOT_KICK = 1110001;//不能踢
	public static final int CANNOT_OPEN = 1110002;//不能踢
	public static final int CANNOT_KICK_USER = 1110003;//不能踢

	public static final int CANNOT_BET_LIMIT = 1110010;//不能下注
	public static final int CANNOT_NUO = 1110011;//不能下注
	public static final int CANNOT_CRAP = 1110012;//不能下注
	public static final int CANNOT_SET_BANKER = 1110013;//不能下注

	public static final int CANNOT_GET_COUPON_ERROR = 1110014;//不能下注
	public static final int CANNOT_GOOD_EXCHANGE_ERROR = 1110015;//不能下注
	public static final int CANNOT_GOLD_NOT_ENOUGH = 1110016;//返利不够

	public static final int CANNOT_ALREADY_SET= 1110020;//





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



