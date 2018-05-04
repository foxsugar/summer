package com.code.server.game.poker;

import com.code.server.game.poker.pullmice.CardUtils;
import com.code.server.game.poker.pullmice.IfCard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class PokerApplicationTests {


	public static void main(String[] args) {

		Integer i = CardUtils.string2Client("大王");
		Integer j = CardUtils.string2Client("小王");

		List<String> list = new ArrayList<>();
		list.add("♥-A");
		list.add("♥-4");
		list.add("♥-2");
		list.add("♥-5");
		list.add("♥-3");


		List<Integer> lll = CardUtils.strings2Local(list, new IfCard() {
			@Override
			public Map<Integer, Integer> cardDict() {
				return CardUtils.getCardDict();
			}
		});

		List<String> kkkk = CardUtils.localsToString(lll, new IfCard() {
			@Override
			public Map<Integer, Integer> cardDict() {
				return CardUtils.getCardDict();
			}
		});

		boolean ret = CardUtils.is12345(lll);

		System.out.println(ret);
	}

}
