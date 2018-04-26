package com.code.server.game.mahjong.util;

import java.util.HashMap;
import java.util.Map;

public class MahjongCode {

	//表示为庄家
	public static final String MAHJONG_IF_OR_NOT_BANKER = "BANKER";
	
	public static final String COLOR_WAN = "wan";
	public static final String COLOR_TIAO = "tiao";
	public static final String COLOR_TONG = "tong";
	
	public static final String DONG = "dong";
	public static final String NAN = "nan";
	public static final String XI = "xi";
	public static final String BEI = "bei";
	public static final String[] SEAT = {DONG,NAN,XI,BEI};
	
	public static final String ZHONG = "zhong";
	public static final String FA = "fa";
	public static final String BAI = "bai";
	
	
	public static final String DONGS = "1";
	public static final String NANS = "2";
	public static final String XIS = "3";
	public static final String BEIS = "4";
	
	
	//明杠3，暗杠6，
	public static final String PUTONGHU="201";			//点炮3，自摸6
	public static final String QIXIAODUI="222";			//9*3
	public static final String QINGYISE="235";			//9*3
	public static final String YITIAOLONG="244";		//9*3
	public static final String HAOHUAQIXIAODUI="253";	//18*3
	public static final String SHUANGHAOHUAQIXIAODUI="254";	//18*3
	public static final String SHISANYAO="266";			//27*3
	public static final String QINGLONG="288";			//拐三角点炮胡专用
	//分数
	public static final Map<String, Integer> HUTOSCORE = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
	        put("201", 3);
	        put("222", 27);
	        put("235", 27);
	        put("244", 27);
	        put("253", 54);
	        put("266", 81);
	        put("288", 81);
	    }
	};

	//全民算分
	public static final Map<String, Integer> HUTOSCORE4QUANMIN = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
			put("201", 9);
			put("222", 27);
			put("235", 27);
			put("244", 27);
			put("253", 54);
			put("266", 81);
			put("288", 81);
		}
	};

	//分数 龙七2018-01-11
	public static final Map<String, Integer> HUTOSCOREFORLQ2 = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
			put("201", 6);
			put("222", 27);
			put("235", 27);
			put("244", 27);
			put("253", 54);
			put("266", 81);
			put("288", 81);
		}
	};

	//龙七点炮3分
	public static final Map<String, Integer> HUTOSCORE4LQ2 = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
			put("201", 3);
			put("222", 9);
			put("235", 9);
			put("244", 9);
			put("253", 18);
			put("266", 27);
			put("288", 27);
		}
	};

	//分数
	public static final Map<String, Integer> HUTOSCOREFORJD = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
			put("201", 2);
		    put("222", 9);
		    put("235", 9);
		    put("244", 9);
		    put("253", 18);
		    put("266", 27);
		    put("288", 27);
		}
	};
	
	//進城分数
	public static final Map<String, Integer> HUTOSCOREFORJC = new HashMap<String, Integer>() {
		{
			put("201", 3);
			put("222", 9);
			put("235", 9);
			put("244", 9);
			put("253", 18);
			put("254", 18);
			put("288", 18);
		}
	};
	
	//進城分数124
		public static final Map<String, Integer> HUTOSCOREFORHM = new HashMap<String, Integer>() {
			{
				put("201", 1);
				put("222", 3);
				put("235", 3);
				put("244", 3);
				put("253", 4);
				put("288", 4);
			}
		};

	//侯马
	public static final Map<String, Integer> HUTOSCOREFORJC124 = new HashMap<String, Integer>() {
		{
			put("201", 1);
			put("222", 3);
			put("235", 3);
			put("244", 3);
			put("253", 4);
			put("288", 4);
		}
	};
	
	//盛世分数
	public static final Map<String, Integer> HUTOSCOREFORSS = new HashMap<String, Integer>() {
			{
				put("201", 2);
				put("222", 9);
				put("235", 9);
				put("244", 9);
				put("253", 18);
				put("254", 18);
				put("266", 27);
			}
	};
	
	//点炮胡分数
	public static final Map<String, Integer> HUTOSCOREDPH = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
		        put("201", 3);
		        put("235", 9);
		        put("244", 9);
		        put("288", 18);
		    }
		};
	
	//欢乐棋牌分数
	public static final Map<String, Integer> HUTOSCOREFORLQ = new HashMap<String, Integer>() {
		{
				put("201", 3);
				put("222", 9);
				put("235", 9);
				put("244", 9);
				put("253", 18);
				put("254", 18);
				put("266", 18);
				put("288", 27);

		}
	};


	//胡同分数
	public static final Map<String, Integer> HUTOSCOREFORHT = new HashMap<String, Integer>() {
		{
			put("201", 3);
			put("222", 9);
			put("235", 9);
			put("244", 9);
			put("253", 18);
			put("266", 18);
		}
	};
		
	//番数
	public static final Map<String, Integer> HUTOTURN = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("201", 3);
		    put("222", 3);
		    put("235", 6);
		    put("244", 6);
		    put("253", 8);
		    put("266", 9);
	    }
	};

	//分数 XXPB 2018-04-26
	public static final Map<String, Integer> HUTOSCOREFORXXPB = new HashMap<String, Integer>() {
		//private static final long serialVersionUID = 1L;
		{
			put("201", 5);
			put("222", 12*3);
			put("235", 24*3);
			put("244", 24*3);
			put("253", 24*3);
			put("266", 32*3);
		}
	};
}
