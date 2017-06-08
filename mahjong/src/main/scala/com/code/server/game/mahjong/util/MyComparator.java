package com.code.server.game.mahjong.util;

import java.util.Comparator;

public class MyComparator implements Comparator<String> {
	// 重定義排序方法
	@Override
	// 1 小到大
	public int compare(String s1, String s2) {
		if (s1.startsWith("wan")) {
			if (s2.startsWith("wan")) {
				if (Integer
						.parseInt(s1.substring(s1.length() - 1, s1.length())) > Integer
						.parseInt(s2.substring(s2.length() - 1, s2.length()))) {
					return 1;
				} else {
					return -1;
				}
			} else if (s2.startsWith("tiao")) {
				return -1;
			} else if (s2.startsWith("tong")) {
				return -1;
			} else if (s2.startsWith("dong")) {
				return -1;
			} else if (s2.startsWith("nan")) {
				return -1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("tiao")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				if (Integer
						.parseInt(s1.substring(s1.length() - 1, s1.length())) > Integer
						.parseInt(s2.substring(s2.length() - 1, s2.length()))) {
					return 1;
				} else {
					return -1;
				}
			} else if (s2.startsWith("tong")) {
				return -1;
			} else if (s2.startsWith("dong")) {
				return -1;
			} else if (s2.startsWith("nan")) {
				return -1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("tong")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				if (Integer
						.parseInt(s1.substring(s1.length() - 1, s1.length())) > Integer
						.parseInt(s2.substring(s2.length() - 1, s2.length()))) {
					return 1;
				} else {
					return -1;
				}
			} else if (s2.startsWith("dong")) {
				return -1;
			} else if (s2.startsWith("nan")) {
				return -1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("dong")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return -1;
			} else if (s2.startsWith("nan")) {
				return -1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("nan")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return -1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("xi")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return 1;
			} else if (s2.startsWith("xi")) {
				return -1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("bei")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return 1;
			} else if (s2.startsWith("xi")) {
				return 1;
			} else if (s2.startsWith("bei")) {
				return -1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("zhong")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return 1;
			} else if (s2.startsWith("xi")) {
				return 1;
			} else if (s2.startsWith("bei")) {
				return 1;
			} else if (s2.startsWith("zhong")) {
				return -1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("fa")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return 1;
			} else if (s2.startsWith("xi")) {
				return 1;
			} else if (s2.startsWith("bei")) {
				return 1;
			} else if (s2.startsWith("zhong")) {
				return 1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else if (s1.startsWith("bai")) {
			if (s2.startsWith("wan")) {
				return 1;
			} else if (s2.startsWith("tiao")) {
				return 1;
			} else if (s2.startsWith("tong")) {
				return 1;
			} else if (s2.startsWith("dong")) {
				return 1;
			} else if (s2.startsWith("nan")) {
				return 1;
			} else if (s2.startsWith("xi")) {
				return 1;
			} else if (s2.startsWith("bei")) {
				return 1;
			} else if (s2.startsWith("zhong")) {
				return 1;
			} else if (s2.startsWith("fa")) {
				return -1;
			} else if (s2.startsWith("bai")) {
				return -1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}