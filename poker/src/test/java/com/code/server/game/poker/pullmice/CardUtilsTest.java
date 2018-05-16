package com.code.server.game.poker.pullmice;

import com.code.server.game.poker.doudizhu.CardUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardUtilsTest {

    private IfCard ifCard = new IfCard() {
        @Override
        public Map<Integer, Integer> cardDict() {
            return CardUtils.getCardDict();
        }
    };

    @Test
    public void test(){

        System.out.println("a");

        for (int i = 0; i < 54; i++){

            String str = CardUtils.local2String(i, new IfCard() {
                @Override
                public Map<Integer, Integer> cardDict() {
                    return CardUtils.getCardDict();
                }
            });

            System.out.println(str);
        }
    }

    @Test
    public void test2(){

        PlayerPullMice playerPullMice1 = new PlayerPullMice();
        {
            Integer a = CardUtils.string2Local("黑桃-4", this.ifCard);
            Integer b = CardUtils.string2Local("红桃-4", this.ifCard);
            Integer c = CardUtils.string2Local("梅花-4", this.ifCard);
            Integer d = CardUtils.string2Local("方片-4", this.ifCard);
            Integer e = CardUtils.string2Local("红桃-6", this.ifCard);

            playerPullMice1.getCards().add(a);
            playerPullMice1.getCards().add(b);
            playerPullMice1.getCards().add(c);
            playerPullMice1.getCards().add(d);
            playerPullMice1.getCards().add(e);

        }

        PlayerPullMice playerPullMice2 = new PlayerPullMice();
        {
            Integer a = CardUtils.string2Local("黑桃-6", this.ifCard);
            Integer b = CardUtils.string2Local("红桃-5", this.ifCard);
            Integer c = CardUtils.string2Local("梅花-6", this.ifCard);
            Integer d = CardUtils.string2Local("方片-5", this.ifCard);
            Integer e = CardUtils.string2Local("红桃-7", this.ifCard);

            playerPullMice2.getCards().add(a);
            playerPullMice2.getCards().add(b);
            playerPullMice2.getCards().add(c);
            playerPullMice2.getCards().add(d);
            playerPullMice2.getCards().add(e);
        }

        List<PlayerPullMice> aList = new ArrayList<>();

        aList.add(playerPullMice1);
        aList.add(playerPullMice2);

        PlayerPullMice pullMice = CardUtils.findWinner(aList);

        System.out.println(pullMice);
    }

}