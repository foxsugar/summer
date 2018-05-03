package com.code.server.game.poker.zhaguzi;

import com.code.server.game.poker.doudizhu.CardUtil;
import com.code.server.game.poker.pullmice.IfCard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dajuejinxian on 2018/5/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CardUtilsTest {
    @Test
    public void getCardDict() throws Exception {

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

}