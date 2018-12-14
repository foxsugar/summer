package com.code.server.game.mahjong.logic;

import java.util.List;

/**
 * Created by sunxianping on 2018-12-14.
 */
public class PlayerCardsInfoLongxiang extends PlayerCardsInfoMj {

    @Override
    public void init(List<String> cards) {
        super.init(cards);

        specialHuScore.put(hu_十三幺, 1);

        specialHuScore.put(hu_缺一门, 1);
        specialHuScore.put(hu_缺两门, 1);

        specialHuScore.put(hu_中张, 1);
        specialHuScore.put(hu_幺九, 1);
        specialHuScore.put(hu_将对, 1);
        specialHuScore.put(hu_四碰, 1);

        specialHuScore.put(hu_七小对, 1);
        specialHuScore.put(hu_豪华七小对, 1);
        specialHuScore.put(hu_双豪七小对, 1);
        specialHuScore.put(hu_三豪七小对, 1);

        specialHuScore.put(hu_清一色, 1);

        specialHuScore.put(hu_清一色七小对, 1);
        specialHuScore.put(hu_清一色豪华七小对, 1);
        specialHuScore.put(hu_清一色双豪华七小对, 1);

        specialHuScore.put(hu_碰碰胡, 1);
        specialHuScore.put(hu_清一色碰碰胡, 1);


        specialHuScore.put(hu_门清, 1);





        //没有1 9
        int hu_中张 = 200;
        //所有附子里都有1 9
        int hu_幺九= 201;
        //全是2 5 8
        int hu_将对= 202;
        //四个碰
        int hu_四碰 = 203;
    }




}
