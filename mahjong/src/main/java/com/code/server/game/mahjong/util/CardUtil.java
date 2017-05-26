/**
 * @Title: Card.java
 * @Package com.milan.majiang
 * @Description:
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0
 */
package com.code.server.game.mahjong.util;

import java.util.*;

/**
 * ClassName: Card
 * 
 * @Description:
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0
 * 
 */
public class CardUtil {

	// 发牌
	public static HashMap<String, ArrayList<String>> getMahjong() {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		// 全套牌
		ArrayList<String> mahjong = new ArrayList<String>();

		// 发出后剩余的牌
		ArrayList<String> rest = new ArrayList<String>();

		// 手牌13张
		ArrayList<String> shouOfEast = new ArrayList<String>();
		ArrayList<String> shouOfSouth = new ArrayList<String>();
		ArrayList<String> shouOfWest = new ArrayList<String>();
		ArrayList<String> shouOfNorth = new ArrayList<String>();

		for (int i = 0; i < 136; i++) {
			if (i < 36) {
				if (i < 10) {
					mahjong.add("00" + i + "wan");
				} else {
					mahjong.add("0" + i + "wan");
				}
			} else if (i >= 36 && i < 72) {
				mahjong.add("0" + i + "tiao");
			} else if (i >= 72 && i < 108) {
				if (i < 100) {
					mahjong.add("0" + i + "tong");
				} else {
					mahjong.add(i + "tong");
				}
			} else if (i >= 108 && i < 112) {
				mahjong.add(i + "dong");
			} else if (i >= 112 && i < 116) {
				mahjong.add(i + "nan");
			} else if (i >= 116 && i < 120) {
				mahjong.add(i + "xi");
			} else if (i >= 120 && i < 124) {
				mahjong.add(i + "bei");
			} else if (i >= 124 && i < 128) {
				mahjong.add(i + "zhong");
			} else if (i >= 128 && i < 132) {
				mahjong.add(i + "fa");
			} else if (i >= 132 && i < 136) {
				mahjong.add(i + "bai");
			}
		}
		int j = 1;
		for (int i = 0; i < 108; i += 4) {
			mahjong.set(i, mahjong.get(i) + j);
			mahjong.set(i + 1, mahjong.get(i + 1) + j);
			mahjong.set(i + 2, mahjong.get(i + 2) + j);
			mahjong.set(i + 3, mahjong.get(i + 3) + j);
			j++;
		}
		Collections.shuffle(mahjong);
		for (int i = 0; i < 13; i++) {
			shouOfEast.add(mahjong.get(i * 4));
			shouOfSouth.add(mahjong.get(i * 4 + 1));
			shouOfWest.add(mahjong.get(i * 4 + 2));
			shouOfNorth.add(mahjong.get(i * 4 + 3));
		}
		for (int i = 52; i < 136; i++) {
			rest.add(mahjong.get(i));
		}

		map.put("rest", rest);
		map.put("shouOfEast", shouOfEast);
		map.put("shouOfSouth", shouOfSouth);
		map.put("shouOfWest", shouOfWest);
		map.put("shouOfNorth", shouOfNorth);

		return map;
	}

	// 发牌(new)
	public static HashMap<String, ArrayList<String>> getMahjongNew() {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		// 全套牌
		ArrayList<String> mahjong = new ArrayList<String>();

		// 发出后剩余的牌
		ArrayList<String> rest = new ArrayList<String>();

		// 手牌13张
		ArrayList<String> shouOfEast = new ArrayList<String>();
		ArrayList<String> shouOfSouth = new ArrayList<String>();
		ArrayList<String> shouOfWest = new ArrayList<String>();
		ArrayList<String> shouOfNorth = new ArrayList<String>();

		for (int i = 0; i < 136; i++) {
			if (i < 36) {
				if (i < 10) {
					mahjong.add("00" + i);
				} else {
					mahjong.add("0" + i);
				}
			} else if (i >= 36 && i < 72) {
				mahjong.add("0" + i);
			} else if (i >= 72 && i < 108) {
				if (i < 100) {
					mahjong.add("0" + i);
				} else {
					mahjong.add(i + "");
				}
			} else if (i >= 108 && i < 112) {
				mahjong.add(i + "");
			} else if (i >= 112 && i < 116) {
				mahjong.add(i + "");
			} else if (i >= 116 && i < 120) {
				mahjong.add(i + "");
			} else if (i >= 120 && i < 124) {
				mahjong.add(i + "");
			} else if (i >= 124 && i < 128) {
				mahjong.add(i + "");
			} else if (i >= 128 && i < 132) {
				mahjong.add(i + "");
			} else if (i >= 132 && i < 136) {
				mahjong.add(i + "");
			}
		}
		int j = 1;
		for (int i = 0; i < 108; i += 4) {
			mahjong.set(i, mahjong.get(i) + j);
			mahjong.set(i + 1, mahjong.get(i + 1) + j);
			mahjong.set(i + 2, mahjong.get(i + 2) + j);
			mahjong.set(i + 3, mahjong.get(i + 3) + j);
			j++;
		}
		Collections.shuffle(mahjong);
		for (int i = 0; i < 13; i++) {
			shouOfEast.add(mahjong.get(i * 4));
			shouOfSouth.add(mahjong.get(i * 4 + 1));
			shouOfWest.add(mahjong.get(i * 4 + 2));
			shouOfNorth.add(mahjong.get(i * 4 + 3));
		}
		for (int i = 52; i < 136; i++) {
			rest.add(mahjong.get(i));
		}

		map.put("rest", rest);
		map.put("shouOfEast", shouOfEast);
		map.put("shouOfSouth", shouOfSouth);
		map.put("shouOfWest", shouOfWest);
		map.put("shouOfNorth", shouOfNorth);

		return map;
	}

	// 发牌不带风
	public static HashMap<String, ArrayList<String>> getMahjongNotFeng() {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		// 全套牌
		ArrayList<String> mahjong = new ArrayList<String>();

		// 发出后剩余的牌
		ArrayList<String> rest = new ArrayList<String>();

		// 手牌13张
		ArrayList<String> shouOfEast = new ArrayList<String>();
		ArrayList<String> shouOfSouth = new ArrayList<String>();
		ArrayList<String> shouOfWest = new ArrayList<String>();
		ArrayList<String> shouOfNorth = new ArrayList<String>();

		for (int i = 0; i < 136; i++) {
			if (i < 36) {
				if (i < 10) {
					mahjong.add("00" + i + "wan");
				} else {
					mahjong.add("0" + i + "wan");
				}
			} else if (i >= 36 && i < 72) {
				mahjong.add("0" + i + "tiao");
			} else if (i >= 72 && i < 108) {
				if (i < 100) {
					mahjong.add("0" + i + "tong");
				} else {
					mahjong.add(i + "tong");
				}
			}
			// else if (i >= 108 && i < 112) {
			// mahjong.add(i + "dong");
			// } else if (i >= 112 && i < 116) {
			// mahjong.add(i + "nan");
			// } else if (i >= 116 && i < 120) {
			// mahjong.add(i + "xi");
			// } else if (i >= 120 && i < 124) {
			// mahjong.add(i + "bei");
			// } else if (i >= 124 && i < 128) {
			// mahjong.add(i + "zhong");
			// } else if (i >= 128 && i < 132) {
			// mahjong.add(i + "fa");
			// } else if (i >= 132 && i < 136) {
			// mahjong.add(i + "bai");
			// }
		}
		int j = 1;
		for (int i = 0; i < 108; i += 4) {
			mahjong.set(i, mahjong.get(i) + j);
			mahjong.set(i + 1, mahjong.get(i + 1) + j);
			mahjong.set(i + 2, mahjong.get(i + 2) + j);
			mahjong.set(i + 3, mahjong.get(i + 3) + j);
			j++;
		}
		Collections.shuffle(mahjong);
		for (int i = 0; i < 13; i++) {
			shouOfEast.add(mahjong.get(i * 4));
			shouOfSouth.add(mahjong.get(i * 4 + 1));
			shouOfWest.add(mahjong.get(i * 4 + 2));
			shouOfNorth.add(mahjong.get(i * 4 + 3));
		}
		for (int i = 52; i < 108; i++) {
			rest.add(mahjong.get(i));
		}

		map.put("rest", rest);
		map.put("shouOfEast", shouOfEast);
		map.put("shouOfSouth", shouOfSouth);
		map.put("shouOfWest", shouOfWest);
		map.put("shouOfNorth", shouOfNorth);

		return map;
	}

	// 测试专用方法
	public static HashMap<String, ArrayList<String>> getMahjongTEST() {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		// 全套牌
		ArrayList<String> mahjong = new ArrayList<String>();

		// 发出后剩余的牌
		ArrayList<String> rest = new ArrayList<String>();

		// 手牌13张
		ArrayList<String> shouOfEast = new ArrayList<String>();
		ArrayList<String> shouOfSouth = new ArrayList<String>();
		ArrayList<String> shouOfWest = new ArrayList<String>();
		ArrayList<String> shouOfNorth = new ArrayList<String>();

		for (int i = 0; i < 136; i++) {
			if (i < 36) {
				if (i < 10) {
					mahjong.add("00" + i + "wan");
				} else {
					mahjong.add("0" + i + "wan");
				}
			} else if (i >= 36 && i < 72) {
				mahjong.add("0" + i + "tiao");
			} else if (i >= 72 && i < 108) {
				if (i < 100) {
					mahjong.add("0" + i + "tong");
				} else {
					mahjong.add(i + "tong");
				}
			} else if (i >= 108 && i < 112) {
				mahjong.add(i + "dong");
			} else if (i >= 112 && i < 116) {
				mahjong.add(i + "nan");
			} else if (i >= 116 && i < 120) {
				mahjong.add(i + "xi");
			} else if (i >= 120 && i < 124) {
				mahjong.add(i + "bei");
			} else if (i >= 124 && i < 128) {
				mahjong.add(i + "zhong");
			} else if (i >= 128 && i < 132) {
				mahjong.add(i + "fa");
			} else if (i >= 132 && i < 136) {
				mahjong.add(i + "bai");
			}
		}
		int j = 1;
		for (int i = 0; i < 108; i += 4) {
			mahjong.set(i, mahjong.get(i) + j);
			mahjong.set(i + 1, mahjong.get(i + 1) + j);
			mahjong.set(i + 2, mahjong.get(i + 2) + j);
			mahjong.set(i + 3, mahjong.get(i + 3) + j);
			j++;
		}
		Collections.shuffle(mahjong);
		for (int i = 0; i < 13; i++) {
			shouOfEast.add(mahjong.get(i * 4));
			shouOfSouth.add(mahjong.get(i * 4 + 1));
			shouOfWest.add(mahjong.get(i * 4 + 2));
			shouOfNorth.add(mahjong.get(i * 4 + 3));
		}
		for (int i = 52; i < 136; i++) {
			rest.add(mahjong.get(i));
		}

		// TODO 测试模拟控制发牌
		shouOfEast.set(0, "036tiao1");
		shouOfEast.set(1, "037tiao1");
		shouOfEast.set(2, "038tiao1");
		shouOfEast.set(3, "000wan1");
		shouOfEast.set(4, "001wan1");
		shouOfEast.set(5, "004wan2");
		shouOfEast.set(6, "005wan2");
		shouOfEast.set(7, "008wan3");
		shouOfEast.set(8, "009wan3");
		shouOfEast.set(9, "012wan4");
		shouOfEast.set(10, "013wan4");
		shouOfEast.set(11, "016wan5 ");
		shouOfEast.set(12, "017wan5 ");

		rest.set(1, "039tiao1");
		// rest.set(2, "047tiao3");
		// rest.set(3, "051tiao4");

		map.put("rest", rest);
		map.put("shouOfEast", shouOfEast);
		map.put("shouOfSouth", shouOfSouth);
		map.put("shouOfWest", shouOfWest);
		map.put("shouOfNorth", shouOfNorth);

		// 016wan5,017wan5,018wan5,019wan5 020wan6,021wan6,022wan6,023wan6

		return map;
	}

	public static String CardListToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String string : list) {
			sb.append(string + ",");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	// 以上为工具类，以下为Demo============================================================================================================================================

	static MyComparator myComparator = new MyComparator();

	// 弃牌13张(dis)
	private static ArrayList<String> discardOfEast = new ArrayList<String>();
	private static ArrayList<String> discardOfSouth = new ArrayList<String>();
	private static ArrayList<String> discardOfWest = new ArrayList<String>();
	private static ArrayList<String> discardOfNorth = new ArrayList<String>();

	// 摸牌(dis)
	public static String mopai(ArrayList<String> rest) {
		String mopai = rest.get(0);
		rest.remove(0);
		return mopai;
	}

	// 吃牌（无此需求）
	public static boolean canChi(ArrayList<String> shoupai, String mopai) {
		boolean b = false;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int mopaiInt = 0;
		if (mopai == "dong" || mopai == "nan" || mopai == "xi"
				|| mopai == "bei" || mopai == "zhong" || mopai == "fa"
				|| mopai == "bai") {
			return b;
		} else if (mopai.contains("wan")) {
			mopaiInt = Integer.parseInt(mopai.substring(mopai.length() - 1,
					mopai.length()));
			for (String string : shoupai) {
				if (string.contains("wan")) {
					temp.add(Integer.parseInt(string.substring(
							string.length() - 1, string.length())));
				}
			}
		} else if (mopai.contains("tiao")) {
			mopaiInt = Integer.parseInt(mopai.substring(mopai.length() - 1,
					mopai.length()));
			for (String string : shoupai) {
				if (string.contains("tiao")) {
					temp.add(Integer.parseInt(string.substring(
							string.length() - 1, string.length())));
				}
			}
		} else if (mopai.contains("tong")) {
			mopaiInt = Integer.parseInt(mopai.substring(mopai.length() - 1,
					mopai.length()));
			for (String string : shoupai) {
				if (string.contains("tong")) {
					temp.add(Integer.parseInt(string.substring(
							string.length() - 1, string.length())));
				}
			}
		} else {
			b = false;
		}
		if (temp.contains(mopaiInt - 2) && temp.contains(mopaiInt - 1)) {
			b = true;
		} else if (temp.contains(mopaiInt + 1) && temp.contains(mopaiInt - 1)) {
			b = true;
		} else if (temp.contains(mopaiInt + 1) && temp.contains(mopaiInt + 2)) {
			b = true;
		} else {
			b = false;
		}
		return b;
	}

	// 碰牌
	public static boolean canPeng(ArrayList<String> shoupai, String mopai) {
		int point = 0;
		boolean b = false;
		for (String string : shoupai) {
			if (string.contains(mopai)) {
				point++;
			}
		}
		if (point == 2) {
			b = true;
		}
		return b;
	}

	// 碰牌（字符串判断）
	public static boolean canPeng(String shoupai, String mopai) {
		int point = 0;
		boolean b = false;
		String[] s = shoupai.split(",");
		for (String string : s) {
			if (MajiangUtil.perfixToSuffix(string).equals(
					MajiangUtil.perfixToSuffix(mopai))) {
				point++;
			}
		}
		if (point >= 2) {
			b = true;
		}
		return b;
	}

	// 碰牌 (得到数组)
	public static String[] canPengToString(String shoupai, String mopai) {
		String[] result = new String[] { "", "", "" };// 0存放手牌(13-2)，1存放摸牌(1)，2存放亮的牌(2)
		String uid = mopai.substring(3);
		mopai = mopai.substring(0, 3);
		result[1] = mopai;
		int point = 0;
		String[] strs = shoupai.split(",");
		for (String string : strs) {
			if (MajiangUtil.perfixToSuffix(string).equals(
					MajiangUtil.perfixToSuffix(mopai))) {
				point++;
			}
		}
		for (String string : strs) {
			if (!MajiangUtil.perfixToSuffix(string).equals(
					MajiangUtil.perfixToSuffix(mopai))) {
				result[0] += (string + ",");
			} else {
				System.out
						.println("result[2]:for================================"
								+ result[2]);
				result[2] += (string + ",");
			}
		}
		System.out.println("result[2]:end================================"
				+ result[2]);
		result[2] = result[2].substring(0, result[2].length() - 1);
		result[0] = result[0].substring(0, result[0].length() - 1);
		if (point == 3) {
			result[0] = result[0] + "," + result[2].split(",")[2];
			result[2] = result[2].split(",")[0] + "," + result[2].split(",")[1];

		}
		String[] str = result[2].split(",");
		str[0] += uid.replace(",", "");
		result[2] = str[0];
		for (int i = 1; i < str.length; i++) {
			result[2] += "," + str[i];
		}

		return result;
	}

	// 杠
	public static boolean canGang(ArrayList<String> shoupai, String mopai) {
		int point = 0;
		boolean b = false;
		for (String string : shoupai) {
			if (string.contains(mopai)) {
				point++;
			}
		}
		if (point == 3) {
			b = true;
		}
		return b;
	}

	// 杠（字符串判断）
	public static boolean canGang(String shoupai, String mopai) {

		String[] strs = shoupai.split(",");
		boolean a = false;
		for (int i = 0; i < strs.length; i++) {
			int point = 0;
			for (int j = 0; j < strs.length; j++) {
				if (MajiangUtil.perfixToSuffix(strs[i]).equals(
						MajiangUtil.perfixToSuffix(strs[j]))) {
					point++;
				}
				if (point == 4) {
					a = true;
				}
			}
		}

		int point = 0;
		boolean b = false;
		String[] s = shoupai.split(",");
		for (String string : s) {
			if (MajiangUtil.perfixToSuffix(string).equals(
					MajiangUtil.perfixToSuffix(mopai))) {
				point++;
			}
		}
		if (point == 3) {
			b = true;
		}
		return b || a;
	}

	// 杠牌 (得到数组)
	public static String[] canGangToString(String shoupai, String mopai) {
		String[] result = new String[] { "", "", "" };// 0存放手牌(13-3)，1存放摸牌(1)，2存放亮的牌(3)
		/*
		 * result[1] = mopai; String[] strs = shoupai.split(","); for (String
		 * string : strs) { if (!MajiangUtil.perfixToSuffix(string).equals(
		 * MajiangUtil.perfixToSuffix(mopai))) { result[0] += (string + ","); }
		 * else { result[2] += (string + ","); } } result[2] =
		 * result[2].substring(0, result[2].length() - 1); result[0] =
		 * result[0].substring(0, result[0].length() - 1);
		 */
		String s = "";
		String uid = mopai.substring(3);
		mopai = mopai.substring(0, 3);
		String[] strs = MajiangUtil.perfixToSuffix(
				(shoupai + "," + mopai).split(",")).split(",");
		for (int i = 0; i < strs.length; i++) {
			int point = 0;
			for (int j = 0; j < strs.length; j++) {
				if (strs[i].equals(strs[j])) {
					point++;
					if (point == 4) {
						s = strs[i];
					}
				}
			}
		}

		String[] allStrs = MajiangUtil.perfixToAllString(
				(mopai + "," + shoupai).split(",")).split(",");
		for (String string : allStrs) {
			if (string.endsWith(s)) {
				result[2] += string.substring(0, 3);
				result[2] += ",";
			} else {
				result[0] += string.substring(0, 3);
				result[0] += ",";
			}
		}
		if (MajiangUtil.perfixToSuffix(mopai).equals(s)) {
			result[1] = mopai;
		}
		result[0] = result[0].substring(0, result[0].length() - 1);
		result[2] = result[2].substring(0, result[2].length() - 1);
		String str[] = result[2].split(",");
		str[0] += uid.replace(",", "");
		result[2] = str[0];
		for (int i = 1; i < str.length; i++) {
			result[2] += "," + str[i];
		}
		return result;
	}

	public static String[] canGangToString(String shoupai, String mopai,
			String show) {
		String[] result = new String[] { "", "", "" };// 0存放手牌(13-3)，1存放摸牌(1)，2存放亮的牌(3)
		/*
		 * result[1] = mopai; String[] strs = shoupai.split(","); for (String
		 * string : strs) { if (!MajiangUtil.perfixToSuffix(string).equals(
		 * MajiangUtil.perfixToSuffix(mopai))) { result[0] += (string + ","); }
		 * else { result[2] += (string + ","); } } result[2] =
		 * result[2].substring(0, result[2].length() - 1); result[0] =
		 * result[0].substring(0, result[0].length() - 1);
		 */
		String s = "";
		String uid = "";
		if (mopai.length() > 3) {
			uid = mopai.substring(3);
		}
		mopai = mopai.substring(0, 3);
		String[] strs = MajiangUtil.perfixToSuffix(
				(shoupai + "," + mopai).split(",")).split(",");
		for (int i = 0; i < strs.length; i++) {
			int point = 0;
			for (int j = 0; j < strs.length; j++) {
				if (strs[i].equals(strs[j])) {
					point++;
					if (point == 4) {
						s = strs[i];
					}
				}
			}
		}

		if (s == "") {
			String showOf[] = show.split("\\|");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < showOf.length; i++) {
				for (String string : showOf[i].split(",")) {
					sb.append(string.substring(0, 3) + ",");
				}
			}
			String shShow = sb.substring(0, sb.length() - 1);
			strs = MajiangUtil.perfixToSuffix(
					(shoupai + "," + mopai + "," + shShow).split(",")).split(
					",");
			for (int i = 0; i < strs.length; i++) {
				int point = 0;
				for (int j = 0; j < strs.length; j++) {
					if (strs[i].equals(strs[j])) {
						point++;
						if (point == 4) {
							s = strs[i];
						}
					}
				}
			}

			String[] allStrs = MajiangUtil.perfixToAllString(
					(mopai + "," + shShow + "," + shoupai).split(",")).split(
					",");
			for (String string : allStrs) {
				if (string.endsWith(s)) {
					result[2] += string.substring(0, 3);
					result[2] += ",";
				} else {
					result[0] += string.substring(0, 3);
					result[0] += ",";
				}
			}
			if (MajiangUtil.perfixToSuffix(mopai).equals(s)) {
				result[1] = mopai;
			}
			result[0] = result[0].substring(0, result[0].length() - 1);
			result[2] = result[2].substring(0, result[2].length() - 1);
			String str[] = result[2].split(",");
			str[0] += uid.replace(",", "");
			result[2] = str[0];
			for (int i = 1; i < str.length; i++) {
				result[2] += "," + str[i];
			}
			if (MajiangUtil.perfixToSuffix(result[2].substring(0, 3)).equals(s)) {
				for (String string : show.split(",")) {
					if (string.length() > 3) {
						result[2] = result[2] + string.substring(3);
					}
				}
			}

			return result;
		}

		String[] allStrs = MajiangUtil.perfixToAllString(
				(mopai + "," + shoupai).split(",")).split(",");
		for (String string : allStrs) {
			if (string.endsWith(s)) {
				result[2] += string.substring(0, 3);
				result[2] += ",";
			} else {
				result[0] += string.substring(0, 3);
				result[0] += ",";
			}
		}
		if (MajiangUtil.perfixToSuffix(mopai).equals(s)) {
			result[1] = mopai;
		}
		result[0] = result[0].substring(0, result[0].length() - 1);
		result[2] = result[2].substring(0, result[2].length() - 1);
		String str[] = result[2].split(",");
		str[0] += uid.replace(",", "");
		result[2] = str[0];
		for (int i = 1; i < str.length; i++) {
			result[2] += "," + str[i];
		}
		return result;
	}

	// 牌型算法===============================================================================================
	// 对子
	public static int duiZi(List<String> shoupai) {
		int b = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String string : shoupai) {
			if (map.get(string) == null) {
				map.put(string, 1);
			} else {
				map.put(string, map.get(string) + 1);
			}
		}
		for (Integer i : map.values()) {
			if (i > 1 && i < 4) {
				b++;
			}
			if (i == 4) {
				b += 2;
			}
		}
		return b;
	}

	// 顺子
	public static int shunZi(List<String> shoupai) {
		Collections.sort(shoupai, myComparator);
		int b = 0;
		List<Integer> wanList = new ArrayList<Integer>();
		List<Integer> tiaoList = new ArrayList<Integer>();
		List<Integer> tongList = new ArrayList<Integer>();
		for (String string : shoupai) {
			if (string.contains("wan")) {
				wanList.add(Integer.parseInt(string.substring(
						string.length() - 1, string.length())));
			}
			if (string.contains("tiao")) {
				tiaoList.add(Integer.parseInt(string.substring(
						string.length() - 1, string.length())));
			}
			if (string.contains("tong")) {
				tongList.add(Integer.parseInt(string.substring(
						string.length() - 1, string.length())));
			}
		}
		for (Integer integer : wanList) {
			if (wanList.contains(integer + 1) && wanList.contains(integer + 2)) {
				b++;
			}
		}
		for (Integer integer : tiaoList) {
			if (tiaoList.contains(integer + 1)
					&& tiaoList.contains(integer + 2)) {
				b++;
			}
		}
		for (Integer integer : tongList) {
			if (tongList.contains(integer + 1)
					&& tongList.contains(integer + 2)) {
				b++;
			}
		}
		return b;
	}

	// 刻子，三个一样的
	public static int keZi(List<String> shoupai) {
		int b = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String string : shoupai) {
			if (map.get(string) == null) {
				map.put(string, 1);
			} else {
				map.put(string, map.get(string) + 1);
			}
		}
		for (Integer i : map.values()) {
			if (i > 2 && i < 4) {
				b++;
			}
		}
		return b;
	}

	// 胡牌算法======================================================================
	// 普通胡
	static boolean jiang = false;

	public static boolean puTongHu(List<String> shoupai) {
		boolean jiang = false;
		if (shoupai.isEmpty()) {
			return true;
		}
		// 刻子
		for (int i = 0; i < shoupai.size() - 1; i++) {
			// int index = Collections.frequency(shoupai, shoupai.get(i));// 张数
			String indexString = shoupai.get(i);// 牌类型

			if (Collections.frequency(shoupai, shoupai.get(i)) >= 3) {
				/*
				 * while (shoupai.remove(indexString)) { }
				 */
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				if (puTongHu(shoupai)) {
					return true;
				} else {
					/* for (int j = 0; j < index; j++) { */
					shoupai.add(indexString);
					shoupai.add(indexString);
					shoupai.add(indexString);
					/* } */
				}
			}
		}

		// 顺子
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("wan" + i)) {
				if (shoupai.contains("wan" + (i + 1))
						&& shoupai.contains("wan" + (i + 2))) {
					shoupai.remove("wan" + i);
					shoupai.remove("wan" + (i + 1));
					shoupai.remove("wan" + (i + 2));

					if (puTongHu(shoupai)) {
						return true;
					} else {
						shoupai.add("wan" + i);
						shoupai.add("wan" + (i + 1));
						shoupai.add("wan" + (i + 2));
					}
				}
			}
		}
		// 条字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tiao" + i)) {
				if (shoupai.contains("tiao" + (i + 1))
						&& shoupai.contains("tiao" + (i + 2))) {
					shoupai.remove("tiao" + i);
					shoupai.remove("tiao" + (i + 1));
					shoupai.remove("tiao" + (i + 2));

					if (puTongHu(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		// 桶字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tong" + i)) {
				if (shoupai.contains("tong" + (i + 1))
						&& shoupai.contains("tong" + (i + 2))) {
					shoupai.remove("tong" + i);
					shoupai.remove("tong" + (i + 1));
					shoupai.remove("tong" + (i + 2));

					if (puTongHu(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}

		// 将
		for (int i = 0; i < shoupai.size() - 1; i++) {
			String indexString = shoupai.get(i);// 牌类型
			if (Collections.frequency(shoupai, shoupai.get(i)) >= 2) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				if (shoupai.isEmpty()) {
					return true;
				} else {
					shoupai.add(indexString);
					shoupai.add(indexString);
					return false;
				}
			}
		}

		return false;
	}

	// 普通胡13+1
	public static boolean puTongHu(ArrayList<String> shoupai, String mopai) {
		boolean b = false;
		shoupai.add(mopai);
		b = CardUtil.puTongHu(shoupai);
		shoupai.remove(mopai);
		return b;
	}

	// 普通听牌算法 13张
	public static boolean canTing(ArrayList<String> shoupai) {
		boolean jiang = false;
		if (shoupai.size() < 3) {
			return true;
		}
		// 刻子
		for (int i = 0; i < shoupai.size() - 1; i++) {
			int index = Collections.frequency(shoupai, shoupai.get(i));// 张数
			String indexString = shoupai.get(i);// 牌类型

			if (Collections.frequency(shoupai, shoupai.get(i)) >= 3) {
				while (shoupai.remove(indexString)) {
				}
				if (canTing(shoupai)) {
					return true;
				} else {
					for (int j = 0; j < index; j++) {
						shoupai.add(indexString);
					}
				}
			}
		}

		// 将
		for (int i = 0; i < shoupai.size() - 1; i++) {
			String indexString = shoupai.get(i);// 牌类型
			if (Collections.frequency(shoupai, shoupai.get(i)) >= 2 && !jiang) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				jiang = true;
				if (canTing(shoupai)) {
					return true;
				} else {
					shoupai.add(indexString);
					shoupai.add(indexString);
					jiang = false;
				}
			}
		}

		// 顺子
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("wan" + i)) {
				if (shoupai.contains("wan" + (i + 1))
						&& shoupai.contains("wan" + (i + 2))) {
					shoupai.remove("wan" + i);
					shoupai.remove("wan" + (i + 1));
					shoupai.remove("wan" + (i + 2));
					if (canTing(shoupai)) {
						return true;
					} else {
						shoupai.add("wan" + i);
						shoupai.add("wan" + (i + 1));
						shoupai.add("wan" + (i + 2));
					}
				}
			}
		}
		// 条字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tiao" + i)) {
				if (shoupai.contains("tiao" + (i + 1))
						&& shoupai.contains("tiao" + (i + 2))) {
					shoupai.remove("tiao" + i);
					shoupai.remove("tiao" + (i + 1));
					shoupai.remove("tiao" + (i + 2));
					if (canTing(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		// 桶字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tong" + i)) {
				if (shoupai.contains("tong" + (i + 1))
						&& shoupai.contains("tong" + (i + 2))) {
					shoupai.remove("tong" + i);
					shoupai.remove("tong" + (i + 1));
					shoupai.remove("tong" + (i + 2));
					if (canTing(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		return false;
	}

	// 扣点听牌算法 14张
	public static boolean canTingKD(ArrayList<String> shoupai) {
		boolean jiang = false;
		if (shoupai.size() <= 3) {
			if (shoupai.size() == 2
					&& (isSixUp(shoupai.get(0)) || isSixUp(shoupai.get(1))))
				return true;
			else {
				ArrayList<String> wan = getWan(shoupai);
				ArrayList<String> tiao = getTiao(shoupai);
				ArrayList<String> tong = getTong(shoupai);
				if (wan.size() == 2) {
					return isDecomKD(Integer.parseInt(wan.get(0).substring(3)),
							Integer.parseInt(wan.get(1).substring(3)));
				} else if (wan.size() == 3) {
					return isDecomKD(Integer.parseInt(wan.get(0).substring(3)),
							Integer.parseInt(wan.get(1).substring(3)))
							|| isDecomKD(
									Integer.parseInt(wan.get(1).substring(3)),
									Integer.parseInt(wan.get(2).substring(3)))
							|| isDecomKD(
									Integer.parseInt(wan.get(0).substring(3)),
									Integer.parseInt(wan.get(2).substring(3)));
				}
				if (tiao.size() == 2) {
					return isDecomKD(
							Integer.parseInt(tiao.get(0).substring(4)),
							Integer.parseInt(tiao.get(1).substring(4)));
				} else if (tiao.size() == 3) {
					return isDecomKD(
							Integer.parseInt(tiao.get(0).substring(4)),
							Integer.parseInt(tiao.get(1).substring(4)))
							|| isDecomKD(
									Integer.parseInt(tiao.get(0).substring(4)),
									Integer.parseInt(tiao.get(2).substring(4)))
							|| isDecomKD(
									Integer.parseInt(tiao.get(1).substring(4)),
									Integer.parseInt(tiao.get(1).substring(4)));
				}
				if (tong.size() == 2) {
					return isDecomKD(
							Integer.parseInt(tong.get(0).substring(4)),
							Integer.parseInt(tong.get(1).substring(4)));
				} else if (tong.size() == 3) {
					return isDecomKD(
							Integer.parseInt(tong.get(0).substring(4)),
							Integer.parseInt(tong.get(1).substring(4)))
							|| isDecomKD(
									Integer.parseInt(tong.get(2).substring(4)),
									Integer.parseInt(tong.get(1).substring(4)))
							|| isDecomKD(
									Integer.parseInt(tong.get(0).substring(4)),
									Integer.parseInt(tong.get(2).substring(4)));
				}
			}
		}
		// 刻子
		for (int i = 0; i < shoupai.size() - 1; i++) {
			int index = Collections.frequency(shoupai, shoupai.get(i));// 张数
			String indexString = shoupai.get(i);// 牌类型

			if (Collections.frequency(shoupai, shoupai.get(i)) >= 3) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				if (canTingKD(shoupai)) {
					return true;
				} else {
					for (int j = 0; j < index; j++) {
						shoupai.add(indexString);
					}
				}
			}
		}

		// 顺子
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("wan" + i)) {
				if (shoupai.contains("wan" + (i + 1))
						&& shoupai.contains("wan" + (i + 2))) {
					shoupai.remove("wan" + i);
					shoupai.remove("wan" + (i + 1));
					shoupai.remove("wan" + (i + 2));
					if (canTingKD(shoupai)) {
						return true;
					} else {
						shoupai.add("wan" + i);
						shoupai.add("wan" + (i + 1));
						shoupai.add("wan" + (i + 2));
					}
				}
			}
		}
		// 条字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tiao" + i)) {
				if (shoupai.contains("tiao" + (i + 1))
						&& shoupai.contains("tiao" + (i + 2))) {
					shoupai.remove("tiao" + i);
					shoupai.remove("tiao" + (i + 1));
					shoupai.remove("tiao" + (i + 2));
					if (canTingKD(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		// 桶字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tong" + i)) {
				if (shoupai.contains("tong" + (i + 1))
						&& shoupai.contains("tong" + (i + 2))) {
					shoupai.remove("tong" + i);
					shoupai.remove("tong" + (i + 1));
					shoupai.remove("tong" + (i + 2));
					if (canTingKD(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}

		// 将
		for (int i = 0; i < shoupai.size() - 1; i++) {
			String indexString = shoupai.get(i);// 牌类型
			if (Collections.frequency(shoupai, shoupai.get(i)) >= 2 && !jiang) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				jiang = true;
				if (canTingKD(shoupai)) {
					return true;
				}
			}
		}
		return false;

	}

	private static ArrayList<String> getTong(ArrayList<String> shoupai) {
		ArrayList<String> arr = new ArrayList<>();
		for (String s : shoupai) {
			if (s.contains("tong")) {
				arr.add(s);
			}
		}
		return arr;
	}

	private static ArrayList<String> getTiao(ArrayList<String> shoupai) {
		ArrayList<String> arr = new ArrayList<>();
		for (String s : shoupai) {
			if (s.contains("tiao")) {
				arr.add(s);
			}
		}
		return arr;
	}

	private static ArrayList<String> getWan(ArrayList<String> shoupai) {
		ArrayList<String> arr = new ArrayList<>();
		for (String s : shoupai) {
			if (s.contains("wan")) {
				arr.add(s);
			}
		}
		return arr;
	}

	private static boolean isDecomKD(int a, int b) {
		if (a >= 6 || b >= 6) {
			if (Math.abs(a - b) == 1 || Math.abs(a - b) == 2)
				return true;
			else
				return false;
		}
		return false;
	}

	// 扣点听牌算法 手牌
	public static ArrayList<String> TingKD(ArrayList<String> shoupai) {
		boolean jiang = false;
		if (shoupai.size() <= 3) {
			if (shoupai.size() == 2
					&& (isSixUp(shoupai.get(0)) || isSixUp(shoupai.get(1)))) {
				ArrayList<String> arr = new ArrayList<>();
				arr.add(shoupai.get(0));
				arr.add(shoupai.get(1));
				return arr;
			} else {
				ArrayList<String> wan = getWan(shoupai);
				ArrayList<String> tiao = getTiao(shoupai);
				ArrayList<String> tong = getTong(shoupai);
				if (wan.size() == 2) {
					if (isDecomKD(Integer.parseInt(wan.get(0).substring(3)),
							Integer.parseInt(wan.get(1).substring(3)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					}
				} else if (wan.size() == 3) {
					if (isDecomKD(Integer.parseInt(wan.get(0).substring(3)),
							Integer.parseInt(wan.get(1).substring(3)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(wan.get(1).substring(3)),
							Integer.parseInt(wan.get(2).substring(3)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(1));
						arr.add(shoupai.get(2));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(wan.get(0).substring(3)),
							Integer.parseInt(wan.get(2).substring(3)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(2));
						return arr;
					}
				}
				if (tong.size() == 2) {
					if (isDecomKD(Integer.parseInt(tong.get(0).substring(4)),
							Integer.parseInt(tong.get(1).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					}
				} else if (tong.size() == 3) {
					if (isDecomKD(Integer.parseInt(tong.get(0).substring(4)),
							Integer.parseInt(tong.get(1).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(tong.get(1).substring(4)),
							Integer.parseInt(tong.get(2).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(1));
						arr.add(shoupai.get(2));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(tong.get(0).substring(4)),
							Integer.parseInt(tong.get(2).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(2));
						return arr;
					}
				}
				if (tiao.size() == 2) {
					if (isDecomKD(Integer.parseInt(tiao.get(0).substring(4)),
							Integer.parseInt(tiao.get(1).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					}
				} else if (tiao.size() == 3) {
					if (isDecomKD(Integer.parseInt(tiao.get(0).substring(4)),
							Integer.parseInt(tiao.get(1).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(1));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(tiao.get(1).substring(4)),
							Integer.parseInt(tiao.get(2).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(1));
						arr.add(shoupai.get(2));
						return arr;
					} else if (isDecomKD(
							Integer.parseInt(tiao.get(0).substring(4)),
							Integer.parseInt(tiao.get(2).substring(4)))) {
						ArrayList<String> arr = new ArrayList<>();
						arr.add(shoupai.get(0));
						arr.add(shoupai.get(2));
						return arr;
					}
				}
			}
		}
		// 刻子
		for (int i = 0; i < shoupai.size() - 1; i++) {
			int index = Collections.frequency(shoupai, shoupai.get(i));// 张数
			String indexString = shoupai.get(i);// 牌类型

			if (Collections.frequency(shoupai, shoupai.get(i)) >= 3) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				ArrayList<String> arr = TingKD(shoupai);
				if (arr.size() > 0) {
					return arr;
				} else {
					for (int j = 0; j < index; j++) {
						shoupai.add(indexString);
					}
				}
			}
		}

		// 将
		for (int i = 0; i < shoupai.size() - 1; i++) {
			String indexString = shoupai.get(i);// 牌类型
			if (Collections.frequency(shoupai, shoupai.get(i)) >= 2 && !jiang) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				jiang = true;
				ArrayList<String> arr = TingKD(shoupai);
				if (arr.size() > 0) {
					return arr;
				} else {
					shoupai.add(indexString);
					shoupai.add(indexString);
					jiang = false;
				}
			}
		}

		// 顺子
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("wan" + i)) {
				if (shoupai.contains("wan" + (i + 1))
						&& shoupai.contains("wan" + (i + 2))) {
					shoupai.remove("wan" + i);
					shoupai.remove("wan" + (i + 1));
					shoupai.remove("wan" + (i + 2));
					ArrayList<String> arr = TingKD(shoupai);
					if (arr.size() > 0) {
						return arr;
					} else {
						shoupai.add("wan" + i);
						shoupai.add("wan" + (i + 1));
						shoupai.add("wan" + (i + 2));
					}
				}
			}
		}
		// 条字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tiao" + i)) {
				if (shoupai.contains("tiao" + (i + 1))
						&& shoupai.contains("tiao" + (i + 2))) {
					shoupai.remove("tiao" + i);
					shoupai.remove("tiao" + (i + 1));
					shoupai.remove("tiao" + (i + 2));
					ArrayList<String> arr = TingKD(shoupai);
					if (arr.size() > 0) {
						return arr;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		// 桶字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tong" + i)) {
				if (shoupai.contains("tong" + (i + 1))
						&& shoupai.contains("tong" + (i + 2))) {
					shoupai.remove("tong" + i);
					shoupai.remove("tong" + (i + 1));
					shoupai.remove("tong" + (i + 2));
					ArrayList<String> arr = TingKD(shoupai);
					if (arr.size() > 0) {
						return arr;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		return new ArrayList<String>();
	}

	// 一条龙
	public static boolean yiTiaoLong(List<String> shoupai, String mopai) {
		boolean b = false;
		List<String> arrayList = shoupai;
		arrayList.add(mopai);
		Collections.sort(arrayList, new MyComparator());
		List<String> tempwan = new ArrayList<>();
		List<String> tempbing = new ArrayList<>();
		List<String> temptiao = new ArrayList<>();
		tempwan = arrayList;
		tempbing = arrayList;
		temptiao = arrayList;
		tempwan.remove("wan1");
		tempwan.remove("wan2");
		tempwan.remove("wan3");
		tempwan.remove("wan4");
		tempwan.remove("wan5");
		tempwan.remove("wan6");
		tempwan.remove("wan7");
		tempwan.remove("wan8");
		tempwan.remove("wan9");
		tempbing.remove("tong1");
		tempbing.remove("tong2");
		tempbing.remove("tong3");
		tempbing.remove("tong4");
		tempbing.remove("tong5");
		tempbing.remove("tong6");
		tempbing.remove("tong7");
		tempbing.remove("tong8");
		tempbing.remove("tong9");
		temptiao.remove("tiao1");
		temptiao.remove("tiao2");
		temptiao.remove("tiao3");
		temptiao.remove("tiao4");
		temptiao.remove("tiao5");
		temptiao.remove("tiao6");
		temptiao.remove("tiao7");
		temptiao.remove("tiao8");
		temptiao.remove("tiao9");
		b = puTongHu(tempwan) || puTongHu(tempbing) || puTongHu(temptiao);
		
		/*if (arrayList.contains("wan1") && arrayList.contains("wan2")
				&& arrayList.contains("wan3") && arrayList.contains("wan4")
				&& arrayList.contains("wan5") && arrayList.contains("wan6")
				&& arrayList.contains("wan7") && arrayList.contains("wan8")
				&& arrayList.contains("wan9")) {
			b = true;
		} else if (arrayList.contains("tiao1") && arrayList.contains("tiao2")
				&& arrayList.contains("tiao3") && arrayList.contains("tiao4")
				&& arrayList.contains("tiao5") && arrayList.contains("tiao6")
				&& arrayList.contains("tiao7") && arrayList.contains("tiao8")
				&& arrayList.contains("tiao9")) {
			b = true;
		} else if (arrayList.contains("tong1") && arrayList.contains("tong2")
				&& arrayList.contains("tong3") && arrayList.contains("tong4")
				&& arrayList.contains("tong5") && arrayList.contains("tong6")
				&& arrayList.contains("tong7") && arrayList.contains("tong8")
				&& arrayList.contains("tong9")) {
			b = true;
		} else {
			b = false;
		}*/
		arrayList.remove(mopai);
		return b;
	}

	// 清一色
	public static boolean qingYiSe(List<String> shoupai, String mopai) {
		if (!shoupai.get(0).contains("wan") && !shoupai.get(0).contains("tiao") && !shoupai.get(0).contains("tong")) {
			shoupai.remove(mopai);
			return false;
		}
		if(mopai!=null && mopai.length()>0){
			shoupai.add(mopai);
			if (shoupai.get(0).contains("wan")) {
				for (String string : shoupai) {
					if (!string.contains("wan")) {
						shoupai.remove(mopai);
						return false;
					}
				}
			}
			else if (shoupai.get(0).contains("tiao")) {
				for (String string : shoupai) {
					if (!string.contains("tiao")) {
						shoupai.remove(mopai);
						return false;
					}
				}
			}
			else if (shoupai.get(0).contains("tong")) {
				for (String string : shoupai) {
					if (!string.contains("tong")) {
						shoupai.remove(mopai);
						return false;
					}
				}
			}
		}else{
			if (shoupai.get(0).contains("wan")) {
				for (String string : shoupai) {
					if (!string.contains("wan")) {
						return false;
					}
				}
			}
			else if (shoupai.get(0).contains("tiao")) {
				for (String string : shoupai) {
					if (!string.contains("tiao")) {
						return false;
					}
				}
			}
			else if (shoupai.get(0).contains("tong")) {
				for (String string : shoupai) {
					if (!string.contains("tong")) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// 清一色
	public static boolean qingYiSeNew(List<String> shoupai,
			List<Integer> typeList) {
		boolean shou = true;
		boolean type = true;
		String shoutemp = MajiangUtil.perfixToSuffix(shoupai.get(0).substring(
				0, MajiangUtil.perfixToSuffix(shoupai.get(0)).length() - 1));
		String typetemp = "";
		if (typeList.get(0) < 9) {
			typetemp = "wan";
		} else if (typeList.get(0) > 8 && typeList.get(0) < 18) {
			typetemp = "tong";
		} else if (typeList.get(0) > 17 && typeList.get(0) < 27) {
			typetemp = "tiao";
		} else {
			type = false;
		}
		for (String s : shoupai) {
			if (!MajiangUtil.perfixToSuffix(s).contains(shoutemp)) {
				shou = false;
			}
		}
		for (int i = 1; i < typeList.size() - 1; i++) {
			if (typeList.get(i) < 9) {
				if (!typetemp.contains("wan")) {
					type = false;
				}
			} else if (typeList.get(i) > 8 && typeList.get(i) < 18) {
				if (!typetemp.contains("tong")) {
					type = false;
				}
			} else if (typeList.get(i) > 17 && typeList.get(i) < 27) {
				if (!typetemp.contains("tiao")) {
					type = false;
				}
			} else {
				type = false;
			}
		}
		return shou && type && shoutemp.contains(typetemp);
	}

	// 十三幺
	public static boolean shiSanYao(List<String> shoupai, String mopai) {
		boolean t = false;
		boolean o = false;
		shoupai.add(mopai);
		if (shoupai.contains("wan1") && shoupai.contains("wan9")
				&& shoupai.contains("tiao1") && shoupai.contains("tiao9")
				&& shoupai.contains("tong1") && shoupai.contains("tong9")
				&& shoupai.contains("dong") && shoupai.contains("nan")
				&& shoupai.contains("xi") && shoupai.contains("bei")
				&& shoupai.contains("zhong") && shoupai.contains("fa")
				&& shoupai.contains("bai")) {
			shoupai.remove("wan1");
			shoupai.remove("wan9");
			shoupai.remove("tiao1");
			shoupai.remove("tiao9");
			shoupai.remove("tong1");
			shoupai.remove("tong9");
			shoupai.remove("dong");
			shoupai.remove("nan");
			shoupai.remove("xi");
			shoupai.remove("bei");
			shoupai.remove("zhong");
			shoupai.remove("fa");
			shoupai.remove("bai");
			t = true;
		}
		if (shoupai.contains("wan1") || shoupai.contains("wan9")
				|| shoupai.contains("tiao1") || shoupai.contains("tiao9")
				|| shoupai.contains("tong1") || shoupai.contains("tong9")
				|| shoupai.contains("dong") || shoupai.contains("nan")
				|| shoupai.contains("xi") || shoupai.contains("bei")
				|| shoupai.contains("zhong") || shoupai.contains("fa")
				|| shoupai.contains("bai")) {
			o = true;
		}
		shoupai.remove(mopai);
		return t && o;
	}

	// 七小对(完成)
	public static boolean qiXiaoDui(List<String> shoupai, String mopai) {
		if(!mopai.isEmpty()){
			shoupai.add(mopai);
		}
		boolean a = false;
		boolean b = false;
		//shoupai.remove(mopai);
		int i = duiZi(shoupai);
		if(shoupai.size()==14){
			a = true;
		}
		if (i == 7) {
			b = true;
		}
		if(!mopai.isEmpty()){
			shoupai.remove(mopai);
		}
		
		return a && b;
	}

	public static void main(String[] args) {
		ArrayList<String> a = new ArrayList<>();
		a.add("wan1");
		a.add("wan1");
		a.add("wan2");
		a.add("wan3");
		a.add("wan2");
		a.add("wan3");
		a.add("wan7");
		a.add("wan7");
		a.add("wan5");
		a.add("wan5");
		a.add("wan6");
		a.add("wan6");
		a.add("wan8");
		System.out.println(qiXiaoDui(a, "wan8"));
	}
	
	// 豪华七小对(完成)
	public static boolean haoHuaQiXiaoDui(List<String> shoupai, String mopai) {
		boolean b = false;
		List<String> arrayList = shoupai;

		if(!mopai.isEmpty()){
			arrayList.add(mopai);
		}
		Collections.sort(arrayList, new MyComparator());
		
		if (arrayList.size() == 14) {
			if (arrayList.get(1).equals(arrayList.get(2))
					|| arrayList.get(3).equals(arrayList.get(4))
					|| arrayList.get(5).equals(arrayList.get(6))
					|| arrayList.get(7).equals(arrayList.get(8))
					|| arrayList.get(9).equals(arrayList.get(10))
					|| arrayList.get(11).equals(arrayList.get(12))) {
				b = true;
			}
		}
		
		boolean a = qiXiaoDui(shoupai, mopai);
		
		/*if(!mopai.isEmpty()){
			arrayList.remove(mopai);
		}*/
		
		return b && a;
	}

	// 摸牌后判断是否可以胡牌（字符串判断）
	public static boolean hu(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);
		return CardUtil.puTongHu(list, mopai)
				|| CardUtil.qiXiaoDui(list, mopai)
				|| CardUtil.haoHuaQiXiaoDui(list, mopai)
				|| CardUtil.yiTiaoLong(list, mopai)
				|| (CardUtil.qingYiSe(list, mopai) && CardUtil.puTongHu(list,
						mopai)) || CardUtil.shiSanYao(list, mopai);
	}

	// 摸牌后判断是否可以胡牌（字符串判断） 自摸
	public static boolean huOfKDZiMo(String shoupai, String mopai, int id,
			String currentStatus) {
		if (isTing(id, currentStatus) == false) {
			return false;
		}
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);
		if (!isThreeUp(mopai)) {
			return false;
		}
		return CardUtil.puTongHu(list, mopai)
				|| CardUtil.qiXiaoDui(list, mopai)
				|| CardUtil.haoHuaQiXiaoDui(list, mopai)
				|| CardUtil.yiTiaoLong(list, mopai)
				|| (CardUtil.qingYiSe(list, mopai) && CardUtil.puTongHu(list,
						mopai)) || CardUtil.shiSanYao(list, mopai);
	}

	// 点炮
	public static boolean huOfKDDianPao(String shoupai, String mopai, int id,
			String currentStatus) {
		if (isTing(id, currentStatus) == false) {
			return false;
		}
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);
		if (!isSixUp(mopai)) {
			return false;
		}
		return CardUtil.puTongHu(list, mopai)
				|| CardUtil.qiXiaoDui(list, mopai)
				|| CardUtil.haoHuaQiXiaoDui(list, mopai)
				|| CardUtil.yiTiaoLong(list, mopai)
				|| (CardUtil.qingYiSe(list, mopai) && CardUtil.puTongHu(list,
						mopai)) || CardUtil.shiSanYao(list, mopai);
	}

	/**
	 * 判断胡的什么牌，返回给玩家
	 * 
	 * @param shoupai
	 * @param mopai
	 * @return
	 */
	public static String huForString(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		if (CardUtil.shiSanYao(list, mopai)) {
			result += MahjongCode.SHISANYAO;
			result += ",";
		}
		if (CardUtil.haoHuaQiXiaoDui(list, mopai)) {
			result += MahjongCode.HAOHUAQIXIAODUI;
			result += ",";
		}
		if (CardUtil.qiXiaoDui(list, mopai)) {
			result += MahjongCode.QIXIAODUI;
			result += ",";
		}
		if (CardUtil.qingYiSe(list, mopai)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (CardUtil.yiTiaoLong(list, mopai)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		return result.substring(0, result.length() - 1);
	}

	/**
	 * 判断胡的什么牌，返回给玩家胡的分数
	 * 
	 * @param shoupai
	 * @param mopai
	 * @return
	 */
	public static int huForScores(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		if (CardUtil.shiSanYao(list, mopai)) {
			result += MahjongCode.SHISANYAO;
			result += ",";
		}
		if (CardUtil.haoHuaQiXiaoDui(list, mopai)) {
			result += MahjongCode.HAOHUAQIXIAODUI;
			result += ",";
		}
		if (CardUtil.qiXiaoDui(list, mopai)) {
			result += MahjongCode.QIXIAODUI;
			result += ",";
		}
		if (CardUtil.qingYiSe(list, mopai)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (CardUtil.yiTiaoLong(list, mopai)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		return score;
	}

	/**
	 * 判断胡的什么牌，返回给玩家胡的分数
	 * 
	 * @return
	 */
	public static int huForScores(List<String> lists,HuCardType huCardType) {
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (huCardType.specialHuList.contains(HuType.hu_十三幺)) {
			result += MahjongCode.SHISANYAO;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_豪华七小对)) {
			result += MahjongCode.HAOHUAQIXIAODUI;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_七小对)) {
			result += MahjongCode.QIXIAODUI;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清龙)) {
			result += MahjongCode.QINGLONG;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清一色)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_一条龙)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		return score;
	}

	/**
	 * winType相加
	 * 
	 * @return
	 */
	public static Set<Integer> huForWinType(List<String> lists) {
		Set<Integer> winType = new HashSet<>();
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (CardUtil.shiSanYao(list, "")) {
			result += HuType.hu_十三幺;
			result += ",";
		}
		if (CardUtil.haoHuaQiXiaoDui(list, "")) {
			result += HuType.hu_豪华七小对;
			result += ",";
		} else if (CardUtil.qiXiaoDui(list, "")) {
			result += HuType.hu_七小对;
			result += ",";
		}
		if (CardUtil.qingYiSe(list, "") && CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_清龙;
			result += ",";
		} else if (CardUtil.qingYiSe(list, "")) {
			result += HuType.hu_清一色;
			result += ",";
		} else if (CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_一条龙;
			result += ",";
		} else {
			result += HuType.hu_普通胡;
			result += ",";
		}
		result= result.substring(0,result.length()-1);
		if(result.contains(",")){
			for (String string : result.split(",")) {
				winType.add(Integer.parseInt(string));
			}
		}else{
			winType.add(Integer.parseInt(result));
		}
		return winType;
	}

	/**
	 * winType相加
	 * 
	 * @return
	 */
	public static Set<Integer> huForWinTypeGSJ(List<String> lists) {
		Set<Integer> winType = new HashSet<>();
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (CardUtil.qingYiSe(list, "") && CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_清龙;
			result += ",";
		} else if (CardUtil.qingYiSe(list, "")) {
			result += HuType.hu_清一色;
			result += ",";
		} else if (CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_一条龙;
			result += ",";
		} else {
			result += HuType.hu_普通胡;
			result += ",";
		}
		result= result.substring(0,result.length()-1);
		if(result.contains(",")){
			for (String string : result.split(",")) {
				winType.add(Integer.parseInt(string));
			}
		}else{
			winType.add(Integer.parseInt(result));
		}
		return winType;
	}
	
	
	/**
	 * 判断胡的什么牌，返回给玩家胡的分数
	 * 
	 * @return
	 */
	public static int huForScoresDPH(List<String> lists,HuCardType huCardType) {
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		if (huCardType.specialHuList.contains(HuType.hu_清龙)) {
			result += MahjongCode.QINGLONG;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_清一色)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (huCardType.specialHuList.contains(HuType.hu_一条龙)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		return score;
	}
	
	
	/**
	 * winType相加
	 * 
	 * @return
	 */
	public static Set<Integer> huForWinTypeDPH(List<String> lists) {
		Set<Integer> winType = new HashSet<>();
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		if (CardUtil.qingYiSe(list, "") && CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_清龙;
			result += ",";
		} else if (CardUtil.qingYiSe(list, "")) {
			result += HuType.hu_清一色;
			result += ",";
		} else if (CardUtil.yiTiaoLong(list, "")) {
			result += HuType.hu_一条龙;
			result += ",";
		} else {
			result += HuType.hu_普通胡;
			result += ",";
		}
		result= result.substring(0,result.length()-1);
		if(result.contains(",")){
			for (String string : result.split(",")) {
				winType.add(Integer.parseInt(string));
			}
		}else{
			winType.add(Integer.parseInt(result));
		}
		return winType;
	}
	
	
	public static List<String> huForString(List<String> lists) {
		ArrayList<String> list = new ArrayList<String>();
		String result = "";
		for (String string : lists) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}

		if (CardUtil.shiSanYao(list, "")) {
			result += MahjongCode.SHISANYAO;
			result += ",";
		}
		if (CardUtil.haoHuaQiXiaoDui(list, "")) {
			result += MahjongCode.HAOHUAQIXIAODUI;
			result += ",";
		}
		if (CardUtil.qiXiaoDui(list, "")) {
			result += MahjongCode.QIXIAODUI;
			result += ",";
		}
		if (CardUtil.qingYiSe(list, "")) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (CardUtil.yiTiaoLong(list, "")) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		List<String> listResult = new ArrayList<>();
		for (String string : result.split(",")) {
			list.add(string);
		}
		return listResult;
	}

	/**
	 * 判断平胡下胡的什么牌，返回分数
	 */
	// ////////
	public static int pingHuForScores(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		// if(CardUtil.shiSanYao(list, mopai)){
		// result += MahjongCode.SHISANYAO;
		// result += ",";
		// }
		// if(CardUtil.haoHuaQiXiaoDui(list, mopai)){
		// result += MahjongCode.HAOHUAQIXIAODUI;
		// result += ",";
		// }
		// if(CardUtil.qiXiaoDui(list, mopai)){
		// result += MahjongCode.QIXIAODUI;
		// result += ",";
		// }
		// if(CardUtil.qingYiSe(list, mopai)){
		// result += MahjongCode.QINGYISE;
		// result += ",";
		// }
		// if(CardUtil.yiTiaoLong(list, mopai)){
		// result += MahjongCode.YITIAOLONG;
		// result += ",";
		// }
		// else{
		// result += MahjongCode.PUTONGHU;
		// result += ",";
		// }
		if (CardUtil.puTongHu(list, mopai)) {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		score = Integer.parseInt(scoreResult);
		// if(scoreResult.contains(",")){
		// for (String string : scoreResult.split(",")) {
		// if(Integer.parseInt(string)>score){
		// }
		// }
		// }
		return score;
	}

	/**
	 * 判断大胡下胡的什么牌，返回分数
	 */
	public static int daHuForScores(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		if (CardUtil.shiSanYao(list, mopai)) {
			result += MahjongCode.SHISANYAO;
			result += ",";
		}
		if (CardUtil.haoHuaQiXiaoDui(list, mopai)) {
			result += MahjongCode.HAOHUAQIXIAODUI;
			result += ",";
		}
		if (CardUtil.qiXiaoDui(list, mopai)) {
			result += MahjongCode.QIXIAODUI;
			result += ",";
		}
		if (CardUtil.qingYiSe(list, mopai)) {
			result += MahjongCode.QINGYISE;
			result += ",";
		}
		if (CardUtil.yiTiaoLong(list, mopai)) {
			result += MahjongCode.YITIAOLONG;
			result += ",";
		} else {
			result += MahjongCode.PUTONGHU;
			result += ",";
		}
		scoreResult = result.substring(0, result.length() - 1);
		if (scoreResult.contains(",")) {
			for (String string : scoreResult.split(",")) {
				if (Integer.parseInt(string) > score) {
					score = Integer.parseInt(string);
				}
			}
		} else {
			score = Integer.parseInt(scoreResult);
		}
		return score;
	}

	/**
	 * 判断扣点下胡的什么牌，返回分数
	 */
	public static int KDForScores(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		try {
			score = Integer.parseInt(mopai.substring(mopai.length() - 1));
		} catch (Exception e) {
			score = 10;
		}

		if (CardUtil.shiSanYao(list, mopai)
				|| CardUtil.haoHuaQiXiaoDui(list, mopai)
				|| (CardUtil.qingYiSe(list, mopai) && CardUtil.yiTiaoLong(list,
						mopai))) {
			score *= 4;
		} else if (CardUtil.qiXiaoDui(list, mopai)) {
			score *= 2;
		}
		return score;
	}

	/**
	 * 判断扣点下胡的什么牌，返回分数
	 */
	public static int KDForScoresDoubleScore(String shoupai, String mopai,HuCardType huCardType) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String result = "";
		String scoreResult = "";
		int score = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		mopai = MajiangUtil.perfixToSuffix(mopai);

		try {
			score = Integer.parseInt(mopai.substring(mopai.length() - 1));
		} catch (Exception e) {
			score = 10;
		}

		if (huCardType.fan > 1) {

			score *= huCardType.fan;
		} else {
			score *= 1;
		}
//		if (CardUtil.shiSanYao(list, mopai)
//				|| CardUtil.haoHuaQiXiaoDui(list, mopai)
//				|| (CardUtil.qingYiSe(list, mopai) && CardUtil.yiTiaoLong(list,
//						mopai))) {
//			score *= 4;
//			System.out.println("=============乘以4");
//		} else if (CardUtil.qiXiaoDui(list, mopai)) {
//			score *= 2;
//			System.out.println("=============乘以2");
//		}
//		if (CardUtil.qingYiSe(list, mopai) || CardUtil.yiTiaoLong(list, mopai)) {
//			score *= 2;
//			System.out.println("=============乘以2");
//		}
		return score;
	}

	/**
	 * 判断扣点杠的什么牌，返回分数
	 */
	public static int KDGangForScores(String mopai) {
		int score = 0;
		mopai = MajiangUtil.perfixToSuffix(mopai);
		try {
			score = Integer.parseInt(mopai.substring(mopai.length() - 1));
		} catch (Exception e) {
			score = 10;
		}

		return score;
	}

	/**
	 * 听牌（字符串判断）
	 * 
	 * @param shoupai
	 * @return
	 */
	public static boolean ting(String shoupai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		return CardUtil.canTing(list);
	}

	public static boolean tingK(String shoupai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		return CardUtil.canTingKD(list);
	}

	public static ArrayList<String> tingKD(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		String[] str = new String[s.length + 1];
		int i = 0;
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
			str[i++] = MajiangUtil.perfixToSuffix(string);
		}
		list.add(MajiangUtil.perfixToSuffix(mopai));
		str[i] = MajiangUtil.perfixToSuffix(mopai);
		ArrayList<String> listreturn = CardUtil.TingKD(list);
		list = new ArrayList<>();
		for (int j = 0; j < listreturn.size(); j++) {
			for (int k = 0; k < str.length; k++) {
				if (k != str.length - 1) {
					if (listreturn.get(j).equals(str[k])
							&& !isSixUp(MajiangUtil.perfixToSuffix(s[k]))) {
						list.add(s[k]);
						break;
					}
				} else {
					if (listreturn.get(j).equals(str[k])
							&& !isSixUp(MajiangUtil.perfixToSuffix(mopai))) {
						list.add(mopai);
						break;
					}
				}
			}
		}

		return list;
	}

	public static boolean tingKDTest(String shoupai, String mopai) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = shoupai.split(",");
		for (String string : s) {
			list.add(MajiangUtil.perfixToSuffix(string));
		}
		list.add(MajiangUtil.perfixToSuffix(mopai));
		return CardUtil.canTingKD(list);
	}

	private static boolean isThreeUp(String pai) {
		boolean flag = false;
		try {
			if (Integer.parseInt(pai.substring(pai.length() - 1)) >= 3) {
				flag = true;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return flag;
	}

	public static boolean isSixUp(String pai) {
		boolean flag = false;
		try {
			if (Integer.parseInt(pai.substring(pai.length() - 1)) >= 6) {
				flag = true;
			}
		} catch (NumberFormatException e) {
			return true;
		}

		return flag;
	}

	public static boolean isTing(int east, String currentStatus) {
		if (currentStatus == null || currentStatus.equals("")) {
			return false;
		}
		if (currentStatus.contains(east + "-7")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param disCard
	 * @return
	 */
	public static String disCardToClient(String disCard) {
		String disCardToClient = "";
		if (disCard.contains("|")) {
			for (String string : disCard.split("\\|")) {
				disCardToClient += "(";
				String[] s = string.split(",");
				for (int i = 0; i < s.length; i++) {
					disCardToClient += s[i].substring(0, 3);
					disCardToClient += ",";
				}
				disCardToClient = disCardToClient.substring(0,
						disCardToClient.length() - 1);
				disCardToClient += "),";
			}
			disCardToClient = disCardToClient.substring(0,
					disCardToClient.length() - 1);
		} else {
			if (disCard.contains("-")) {
				for (String s : disCard.split(",")) {
					disCardToClient += s;
					disCardToClient += ",";
					disCardToClient = disCardToClient.substring(0,
							disCardToClient.length() - 1);
				}
			} else {
				disCardToClient = disCard.substring(0, disCard.length() - 1);
			}
		}
		return disCardToClient.startsWith("null") ? disCardToClient
				.substring(4) : disCardToClient;
	}

	/**
	 * @param disCard
	 * @return
	 */
	public static String disCardToClient2(String disCard) {
		String disCardToClient = "";

		if (disCard.contains("|")) {
			String[] strss = disCard.split("\\|");
			for (String string : strss) {
				if (disCard.contains("-")) {
					disCardToClient += "[";
					String[] strs = string.split(",");
					disCardToClient += strs[0].substring(4);
					disCardToClient += ",";
					if (strs.length == 4) {
						disCardToClient += "10,(";
						disCardToClient += strs[0].substring(0, 3);
						disCardToClient += ",";
						for (int i = 1; i < strs.length; i++) {
							disCardToClient += strs[i];
							disCardToClient += ",";
						}
						disCardToClient = disCardToClient.substring(0,
								disCardToClient.length() - 1);
						disCardToClient += ")]";
					} else if (strs.length == 3) {
						disCardToClient += "4,(";
						disCardToClient += strs[0].substring(0, 3);
						disCardToClient += ",";
						for (int i = 1; i < strs.length; i++) {
							disCardToClient += strs[i];
							disCardToClient += ",";
						}
						disCardToClient = disCardToClient.substring(0,
								disCardToClient.length() - 1);
						disCardToClient += ")]";
					}
				} else {
					disCardToClient += "[ ,11,(";
					for (String s : disCard.split(",")) {
						disCardToClient += s;
						disCardToClient += ",";
					}
					disCardToClient = disCardToClient.substring(0,
							disCardToClient.length() - 1);
					disCardToClient += ")]";
				}
				disCardToClient += ",";
				disCardToClient = disCardToClient.substring(0,
						disCardToClient.length() - 1);
			}
		} else {
			if (disCard.contains("-")) {
				disCardToClient += "[";
				String[] strs = disCard.split(",");
				disCardToClient += strs[0].substring(4);
				disCardToClient += ",";
				if (strs.length == 4) {
					disCardToClient += "10,(";
					disCardToClient += strs[0].substring(0, 3);
					disCardToClient += ",";
					for (int i = 1; i < strs.length; i++) {
						disCardToClient += strs[i];
						disCardToClient += ",";
					}
					disCardToClient = disCardToClient.substring(0,
							disCardToClient.length() - 1);
					disCardToClient += ")]";
				} else if (strs.length == 3) {
					disCardToClient += "4,(";
					disCardToClient += strs[0].substring(0, 3);
					disCardToClient += ",";
					for (int i = 1; i < strs.length; i++) {
						disCardToClient += strs[i];
						disCardToClient += ",";
					}
					disCardToClient = disCardToClient.substring(0,
							disCardToClient.length() - 1);
					disCardToClient += ")]";
				}
			} else {
				disCardToClient += "[ ,11,(";
				for (String s : disCard.split(",")) {
					disCardToClient += s;
					disCardToClient += ",";
				}
				disCardToClient = disCardToClient.substring(0,
						disCardToClient.length() - 1);
				disCardToClient += ")]";
			}
		}

		return disCardToClient;
	}

	// 听牌之后可以杠， 不破坏听牌的牌型
	public static boolean tingGang(String handOfEast, String disCard) {
		if (!canGang(handOfEast, disCard)) {
			return false;
		}
		String[] result = canGangToString(handOfEast, disCard, "");
		if (tingK(result[0]))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @Title: 听牌验证
	 * @Creater: Clark
	 * @Description: 13张验证，扣点
	 * @param @param shoupai
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean canTingOrNot(ArrayList<String> shoupai) {
		boolean jiang = false;
		if (shoupai.size() == 1) {
			if (shoupai.get(0).contains("wan")
					|| shoupai.get(0).contains("tiao")
					|| shoupai.get(0).contains("tong")) {
				if (Integer.parseInt(shoupai.get(0).substring(
						shoupai.get(0).length() - 1)) < 6) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else if (shoupai.size() == 2) {
			if (shoupai.get(0).contains("wan")
					|| shoupai.get(0).contains("tiao")
					|| shoupai.get(0).contains("tong")) {
				if (shoupai.get(0).equals(shoupai.get(1))
						&& (Integer.parseInt(shoupai.get(0).substring(
								shoupai.get(0).length() - 1)) > 5)) {
					return true;
				} else if (!shoupai.get(0).equals(shoupai.get(1))) {
					if (Math.abs(Integer.parseInt(shoupai.get(0).substring(
							shoupai.get(0).length() - 1))
							- Integer.parseInt(shoupai.get(1).substring(
									shoupai.get(1).length() - 1))) == 1) {
						if (Integer.parseInt(shoupai.get(0).substring(
								shoupai.get(0).length() - 1)) > 3
								&& Integer.parseInt(shoupai.get(1).substring(
										shoupai.get(1).length() - 1)) > 3) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				if (shoupai.get(0).equals(shoupai.get(1))) {
					return true;
				} else {
					return false;
				}
			}
		}

		// 刻子
		for (int i = 0; i < shoupai.size() - 1; i++) {
			int index = Collections.frequency(shoupai, shoupai.get(i));// 张数
			String indexString = shoupai.get(i);// 牌类型

			if (Collections.frequency(shoupai, shoupai.get(i)) >= 3) {
				while (shoupai.remove(indexString)) {
				}
				if (canTingOrNot(shoupai)) {
					return true;
				} else {
					for (int j = 0; j < index; j++) {
						shoupai.add(indexString);
					}
				}
			}
		}

		// 顺子
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("wan" + i)) {
				if (shoupai.contains("wan" + (i + 1))
						&& shoupai.contains("wan" + (i + 2))) {
					shoupai.remove("wan" + i);
					shoupai.remove("wan" + (i + 1));
					shoupai.remove("wan" + (i + 2));

					if (canTingOrNot(shoupai)) {
						return true;
					} else {
						shoupai.add("wan" + i);
						shoupai.add("wan" + (i + 1));
						shoupai.add("wan" + (i + 2));
					}
				}
			}
		}
		// 条字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tiao" + i)) {
				if (shoupai.contains("tiao" + (i + 1))
						&& shoupai.contains("tiao" + (i + 2))) {
					shoupai.remove("tiao" + i);
					shoupai.remove("tiao" + (i + 1));
					shoupai.remove("tiao" + (i + 2));

					if (canTingOrNot(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}
		// 桶字
		for (int i = 1; i <= 7; i++) {
			if (shoupai.contains("tong" + i)) {
				if (shoupai.contains("tong" + (i + 1))
						&& shoupai.contains("tong" + (i + 2))) {
					shoupai.remove("tong" + i);
					shoupai.remove("tong" + (i + 1));
					shoupai.remove("tong" + (i + 2));

					if (canTingOrNot(shoupai)) {
						return true;
					} else {
						shoupai.add("tiao" + i);
						shoupai.add("tiao" + (i + 1));
						shoupai.add("tiao" + (i + 2));
					}
				}
			}
		}

		// 将
		for (int i = 0; i < shoupai.size() - 1; i++) {
			String indexString = shoupai.get(i);// 牌类型
			if (Collections.frequency(shoupai, shoupai.get(i)) >= 2 && !jiang) {
				shoupai.remove(indexString);
				shoupai.remove(indexString);
				jiang = true;
				if (canTingOrNot(shoupai)) {
					return true;
				} else {
					shoupai.add(indexString);
					shoupai.add(indexString);
					jiang = false;
				}
			}
		}

		return false;
	}

	/**
	 * @Title: 判读是不是特殊胡的牌
	 * @Creater: Clark
	 * @Description:
	 * @param @param shoupai
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isSpecialHu(List<String> shoupai,
			List<Integer> typelist) {
		return qingYiSe(shoupai, "") || qiXiaoDui(shoupai, "")
				|| haoHuaQiXiaoDui(shoupai, "") || yiTiaoLong(shoupai, "")
				|| (puTongHu(shoupai) && qingYiSeNew(shoupai, typelist));
	}


	/*
	 * public static void main(String[] args) {
	 * System.out.println("============做一副牌============");
	 * System.out.println(Arrays.toString(printMahjong(mahjong).toArray()));
	 * System.out.println("=============洗牌==============");
	 * System.out.println(Arrays.toString(xipai(mahjong).toArray()));
	 * System.out.println("=============发牌=============="); fapai(mahjong);
	 * System.out.println(Arrays.toString(shouOfEast.toArray()));
	 * System.out.println(Arrays.toString(shouOfSouth.toArray()));
	 * System.out.println(Arrays.toString(shouOfWest.toArray()));
	 * System.out.println(Arrays.toString(shouOfNorth.toArray()));
	 * System.out.println("=============排序==============");
	 * 
	 * Collections.sort(shouOfEast, myComparator); Collections.sort(shouOfSouth,
	 * myComparator); Collections.sort(shouOfWest, myComparator);
	 * Collections.sort(shouOfNorth, myComparator);
	 * System.out.println(Arrays.toString(shouOfEast.toArray()));
	 * System.out.println(Arrays.toString(shouOfSouth.toArray()));
	 * System.out.println(Arrays.toString(shouOfWest.toArray()));
	 * System.out.println(Arrays.toString(shouOfNorth.toArray()));
	 * System.out.println(Arrays.toString(rest.toArray()));// 剩余的牌
	 * 
	 * System.out.println(mopai());
	 * 
	 * // 胡牌方法测试 ArrayList<String> a = new ArrayList<String>(); String[] s = {
	 * "wan7", "wan7", "tiao1", "tiao2", "tiao3", "tiao4","tong1", "tong2",
	 * "tong3", "tong7", "tong2", "xi", "xi" }; String[] s1 = { "wan1", "wan2",
	 * "wan3", "wan4", "wan5", "wan6", "wan7","wan8", "wan9", "tong7", "tong2",
	 * "xi", "xi","xi" }; String[] s2 = { "wan1", "wan2", "wan3", "wan4",
	 * "wan5", "wan6", "wan7","wan8", "wan9", "wan7", "wan8","wan9","wan1"};
	 * String[] s3 = { "wan1", "wan9", "tiao1", "tiao9", "tong1", "tong9",
	 * "dong","nan", "xi", "bei", "zhong","fa","bai"}; String[] s4 = { "064",
	 * "009", "038", "128", "006", "087", "019","131", "092", "076",
	 * "000","116","021"}; //[064tiao2, 009wan1, 038tiao3, 128fa, 006wan7,
	 * 087tong7, 019wan2, 131fa, 092tong3, 076tong5, 000wan1, 116xi, 021wan4]
	 * Collections.addAll(a, s4); // System.out.println(qiXiaoDui(a,"xi"));
	 * System.out.println(
	 * "========================================================================================================="
	 * );
	 * 
	 * Collections.sort(a, myComparator); // System.out.println(canPeng(a,
	 * "wan7")); // System.out.println(MajiangUtil.deletePrefix(shouOfNorth));
	 * // System.out.println(MajiangUtil.deletePrefix("083tong3")); //
	 * System.out.println(MajiangUtil.addSuffix("058"));
	 * System.out.println(MajiangUtil.addSuffix(a));
	 * 
	 * System.exit(0); }
	 */

}
