package com.code.server.game.mahjong.util;

import java.util.ArrayList;
import java.util.Arrays;

public class MajiangUtil {

	/*
	 * // List去掉前缀 public static ArrayList<String>
	 * deletePrefix(ArrayList<String> majiang) { ArrayList<String>
	 * deletePrefixArrayList = new ArrayList<String>(); if (!majiang.isEmpty())
	 * { for (String string : majiang) {
	 * deletePrefixArrayList.add(string.substring(3)); } } return
	 * deletePrefixArrayList; }
	 * 
	 * // String去掉前缀 public static String deletePrefix(String majiang) { return
	 * majiang.substring(3); }
	 * 
	 * // String去掉后缀 public static ArrayList<String>
	 * deleteSuffix(ArrayList<String> majiang) { ArrayList<String>
	 * deleteSuffixArrayList = new ArrayList<String>(); if (!majiang.isEmpty())
	 * { for (String string : majiang) {
	 * deleteSuffixArrayList.add(string.substring(0, 3)); } } return
	 * deleteSuffixArrayList; }
	 * 
	 * // List加上后缀 public static ArrayList<String> addSuffix(ArrayList<String>
	 * majiang) { ArrayList<String> addSuffixArrayList = new
	 * ArrayList<String>(); for (String string : majiang) {
	 * addSuffixArrayList.add(MajiangUtil.addSuffix(string)); } return
	 * addSuffixArrayList; }
	 * 
	 * // String加上后缀 public static String addSuffix(String majiang) { int i =
	 * Integer.parseInt(majiang); if (i < 36) { majiang += "wan"; } else if (i
	 * >= 36 && i < 72) { majiang += "tiao"; } else if (i >= 72 && i < 108) {
	 * majiang += "tong"; } else if (i >= 108 && i < 112) { majiang += "dong"; }
	 * else if (i >= 112 && i < 116) { majiang += "nan"; } else if (i >= 116 &&
	 * i < 120) { majiang += "xi"; } else if (i >= 120 && i < 124) { majiang +=
	 * "bei"; } else if (i >= 124 && i < 128) { majiang += "zhong"; } else if (i
	 * >= 128 && i < 132) { majiang += "fa"; } else if (i >= 132 && i < 136) {
	 * majiang += "bai"; } if (i < 108) { majiang += (i % 9 + 1); } return
	 * majiang; }
	 * 
	 * // List加上后缀,去掉前缀 public static ArrayList<String>
	 * recoverSuffix(ArrayList<String> majiang) { ArrayList<String>
	 * addSuffixArrayList = new ArrayList<String>(); for (String string :
	 * majiang) { addSuffixArrayList.add(MajiangUtil.deletePrefix(MajiangUtil
	 * .addSuffix(string))); } return addSuffixArrayList; }
	 * 
	 * // string加上前缀，去掉后缀 public static String recoverSuffix(String majiang) {
	 * return MajiangUtil.deletePrefix(MajiangUtil.addSuffix(majiang)); }
	 */

	// =======================================================================================================================================

	/**
	 * 前缀变后缀 003 ==> 003wan4
	 * 
	 * @param majiang
	 * @return
	 */
	public static String perfixToAll(String majiang) {
		int i = Integer.parseInt(majiang.substring(0,3));
		if (i < 36) {
			majiang += "wan";
		} else if (i >= 36 && i < 72) {
			majiang += "tiao";
		} else if (i >= 72 && i < 108) {
			majiang += "tong";
		} else if (i >= 108 && i < 112) {
			majiang += "dong";
		} else if (i >= 112 && i < 116) {
			majiang += "nan";
		} else if (i >= 116 && i < 120) {
			majiang += "xi";
		} else if (i >= 120 && i < 124) {
			majiang += "bei";
		} else if (i >= 124 && i < 128) {
			majiang += "zhong";
		} else if (i >= 128 && i < 132) {
			majiang += "fa";
		} else if (i >= 132 && i < 136) {
			majiang += "bai";
		}
		if (i < 108) {
			// majiang += (i % 9 + 1);
			if (i < 36) {
				majiang += (i / 4 + 1);
			} else if (i >= 36 && i < 72) {
				majiang += ((i - 36) / 4 + 1);
			} else if (i >= 72 && i < 108) {
				majiang += ((i - 72) / 4 + 1);
			}
		}
		return majiang;
	}

	/**
	 * 前缀变后缀 003 ==> wan4
	 * 
	 * @param majiang
	 * @return
	 */
	public static String perfixToSuffix(String majiang) {
		int i = Integer.parseInt(majiang.substring(0,3));
		if (i < 36) {
			majiang += "wan";
		} else if (i >= 36 && i < 72) {
			majiang += "tiao";
		} else if (i >= 72 && i < 108) {
			majiang += "tong";
		} else if (i >= 108 && i < 112) {
			majiang += "dong";
		} else if (i >= 112 && i < 116) {
			majiang += "nan";
		} else if (i >= 116 && i < 120) {
			majiang += "xi";
		} else if (i >= 120 && i < 124) {
			majiang += "bei";
		} else if (i >= 124 && i < 128) {
			majiang += "zhong";
		} else if (i >= 128 && i < 132) {
			majiang += "fa";
		} else if (i >= 132 && i < 136) {
			majiang += "bai";
		}
		if (i < 108) {
			// majiang += (i % 9 + 1);
			if (i < 36) {
				majiang += (i / 4 + 1);
			} else if (i >= 36 && i < 72) {
				majiang += ((i - 36) / 4 + 1);
			} else if (i >= 72 && i < 108) {
				majiang += ((i - 72) / 4 + 1);
			}
		}
		return majiang.substring(3);
	}

	/**
	 * 前缀数组变后缀数组 003,004 ==> wan4,wan5
	 * 
	 * @param majiang
	 * @return
	 */
	public static String perfixToSuffix(String[] majiang) {
		String s = "";
		for (String string : majiang) {
			s += (MajiangUtil.perfixToSuffix(string) + ",");
		}
		return s.substring(0, s.length() - 1);
	}

	/**
	 * 前缀数组变后缀数组 003,004 ==> 003wan4,004wan5
	 * 
	 * @param majiang
	 * @return
	 */
	public static String perfixToAllString(String[] majiang) {
		String s = "";
		for (String string : majiang) {
			s += (MajiangUtil.perfixToAll(string) + ",");
		}
		return s.substring(0, s.length() - 1);
	}

	// 杠
	public static String gang(String[] majiang) {
		String s = "";
		String[] strs = MajiangUtil.perfixToSuffix(majiang).split(",");
		for (int i = 0; i < strs.length; i++) {
			int point = 0;
			for (int j = 1; j < strs.length; j++) {
				if (strs[i].equals(strs[j])) {
					point++;
				}
				if (point == 3) {
					s = strs[i];
				}
			}
		}
		return s;
	}

	// list转String
	public static String deleteSuffixArrayListToString(ArrayList<String> majiang) {
		String s = "";
		for (String string : majiang) {
			s += (string.substring(0, 3) + ",");
		}
		return s.substring(0, s.length() - 1);
	}

	/**
	 * 牌组中删除一张
	 * 
	 * @param card
	 * @return
	 */
	public static String deleteOneCard(String shoupai, String card) {
		StringBuffer sb = new StringBuffer();
		if (shoupai.contains(",")) {
			for (String s : shoupai.split(",")) {
				if (!s.equals(card)) {
					sb.append(s);
					sb.append(",");
				}
			}
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	// 房间号中牌局数自动增加(倒数第二位为牌局数) 12345601→12345611
	public static String upNumber(String up) {
		StringBuffer sb = new StringBuffer();
		String upp = "";
		String end = "";
		int number = 0;
		if (!up.isEmpty()) {
			upp = up.substring(0, 6);
			end = up.substring(7);
			number = Integer.parseInt(up.substring(6, 7));
			number += 1;
			sb.append(upp);
			sb.append(number);
			sb.append(end);
		}
		return sb.toString();
	}

	//取牌局数，默认为当前局数-1
	public static int getGameNumber(String up) {
		return Integer.parseInt(up.substring(6, 7));
	}

	public static void main(String[] args) {

		System.out.println(MajiangUtil.upNumber("12345611"));

		// String s ;
		// for(int i=0;i<136;i++)
		// {
		// s = String.format("%03d", i);
		// System.out.print(MajiangUtil.perfixToSuffix(s)+ " " +s + " ");
		// }
		String[] str;
		String s = "107";
		str = s.split(",");
		Arrays.sort(str);
		for (int i = 0; i < str.length; i++) {
			System.out.print(str[i] + ",");
		}
		System.out.println(MajiangUtil.perfixToSuffix(str));
		// System.out.println(MajiangUtil.perfixToSuffix("065,037,068,013,042,097,092,100,067,053,039,031,047".split(",")));
		// System.out.println(CardUtil.canPengToString("000,001,002,123",
		// "003"));
		// System.out.println(CardUtil.canPengToString("000,009,026,123",
		// "018"));
		// System.out.println(MajiangUtil.deleteOneCard("080,098,059,021,135,000,113,015,060,067,058,050,074","025"));
		// ArrayList<String> a = CardUtil.getMahjong().get("rest");
		// System.out.println(MajiangUtil.deleteSuffixArrayListToString(CardUtil.getMahjong().get("rest")));

		// String s = "001,002,000,003,042,097,092,100,067,053,039,031,047";
		// String[] strs = s.split(",");
		// boolean b = false;
		// for (int i = 0; i < strs.length; i++) {
		// int point = 0;
		// for (int j = 1; j < strs.length; j++) {
		// if (MajiangUtil.perfixToSuffix(strs[i]).equals(
		// MajiangUtil.perfixToSuffix(strs[j]))) {
		// point++;
		// }
		// if(point==3){
		// b = true;
		// }
		// }
		// }
		//
		// System.out.println(MajiangUtil.gang("001,002,000,003,042,097,092,100,067,053,039,031,047".split(",")));
		//
		//
		// String[] str =
		// CardUtil.canGangToString("001,002,000,003,042,097,092,100,067,053,039,031,047",
		// "023");
		// for (String string : str) {
		// System.out.println(string);
		// }
	}

	/**
	 * 二进制数组转int
	 * 
	 * @param b
	 * @param start
	 * @param len
	 * @return
	 */
	public static int bytes2Int(byte[] b, int start, int len) {
		int sum = 0;
		int end = start + len;
		for (int i = start; i < end; i++) {
			int n = ((int) b[i]) & 0xff;
			n <<= (--len) * 8;
			sum += n;
		}
		return sum;
	}
}
