package com.code.server.game.mahjong.util;

import java.util.List;
import java.util.Random;

public class RandomUtil {
	
	private static ThreadLocal<Random> randoms = new ThreadLocal<Random>();
	
	private static Random getR(){
	    Random random = randoms.get();
	    if(random == null){
	    	random = new Random();
	        randoms.set(random);
	    }
        return random;
	}
	
	public static int getRandomNumber(int min, int max) {
		int result = getR().nextInt((max - min + 1) << 5);
		return (result >> 5) + min;
	}
	
	public static int getSingleDigitRandomNumber() {
		int result = getR().nextInt((9 - 0 + 1) << 5);
		return (result >> 5) + 0;
	}
	
	public static String getRoomId(List<String> allRoomIdList) {
		String roomId = "";
		for(int i = 0; i < 6; i++) {
			String oneRoomId = String.valueOf(getSingleDigitRandomNumber());
			roomId += oneRoomId;
		}
		
		for(String existRoomId : allRoomIdList) {
			if(roomId.equals(existRoomId)) {
				return getRoomId(allRoomIdList);
			}
		}
		
		return roomId;
	}
}
