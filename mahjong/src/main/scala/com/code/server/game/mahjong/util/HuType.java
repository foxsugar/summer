package com.code.server.game.mahjong.util;

/**
 * Created by win7 on 2016/12/15.
 */
public interface HuType {

	int hu_普通胡 = 0;//普通胡
    int hu_七小对 = 1;//七小对
    int hu_十三幺 = 2;//十三幺
    int hu_豪华七小对 = 3;//豪华七小对
    int hu_双豪七小对_山西 = 4;//双豪七小对_山西 双豪华和三豪华都是双豪华
    int hu_清七对 = 5;//双豪七小对
    int hu_普通七小对 = 6;//普通七小对
    int hu_三豪七小对 = 7;//三豪华七小对
    int hu_双豪七小对 = 8;//双豪七小对_山西
    int hu_十三不靠 = 9;//十三不靠

    int hu_缺一门 = 10;
    int hu_缺两门 = 11;
    int hu_孤将 = 12;
    int hu_一张赢 = 13;
    int hu_门清 = 14;
    int hu_三风一副 = 15;
    int hu_三风两副 = 16;
    int hu_三风三副 = 17;
    int hu_三元一副 = 18;//三元一副
    int hu_三元一副自摸 = 19;//三元一副
    int hu_三元两副 = 20;//三元两副
    int hu_三元两副自摸 = 21;//三元两副
    int hu_三元三副 = 22;//三元两副
    int hu_三元三副自摸 = 23;//三元两副自摸

    int hu_清一色 = 30;//清一色
    int hu_一条龙 = 31;//一条龙
    int hu_字一色 = 32;
    int hu_清龙 = 33;

    
    int hu_夹张 = 40;
    int hu_边张 = 41;
    int hu_吊张 = 42;
    
    int hu_砍胡 = 43;
    int hu_边张_乾安 = 44;


    int hu_杠上开花 = 50;
    int hu_杠上开宝 = 51;
    int hu_飘胡 = 52;
    int hu_夹五 = 53;
    int hu_摸宝 = 54;
    int hu_直对 = 55;
    int hu_海底捞 = 56;


    int hu_吊将 = 60;

    int hu_混吊 = 65;

    int hu_捉五 = 70;
    int hu_混儿吊捉五 = 71;

    int hu_龙 = 80;
    int hu_本混龙 = 81;

    int hu_捉五龙 = 90;
    int hu_本混捉五龙 = 91;

    int hu_混儿吊龙 = 100;
    int hu_混儿吊本混龙 = 101;

    int hu_混儿吊捉五龙 = 110;
    int hu_混儿吊捉五本混龙 = 111;

    int hu_素本混龙 = 120;
    int hu_素本混捉五龙 = 130;

    int hu_杠开 = 150;
    int hu_素 = 151;

    int hu_天胡 = 160;

    int hu_金杠 = 165;

    int hu_断幺 = 170;//不能有字，1，9
    int hu_混一色 = 171;//同一色加字

    int hu_三碰 = 172;//XXPB专用
    int hu_四个红中 = 173;//XXPB专用

}
