package com.code.server.game.poker.hitgoldflower;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Demo {


    public void func1() {

        ArrayList<PokerItem> list = PokerItem.createPokers();
        PokerItem.shufflePokers(list);
        PokerItem.sortPokers(list);
        PokerItem.showPokers(list);
    }


    // 判断是否是豹子
    public void func2() {

        ArrayList<PokerItem> list = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            PokerItem item = PokerItem.createItem(i);
            list.add(item);
        }
        PokerItem.showPokers(list);
        boolean ret = PokerItem.BaoZi(list);
        System.out.println(ret);
    }


    // 判断是否是顺子
    public void func3() {

        ArrayList<PokerItem> list = new ArrayList<>();
        int[] arr = {2, 6, 10};
        for (int i = 0; i < arr.length; i++) {
            PokerItem item = PokerItem.createItem(arr[i]);
            list.add(item);
        }
        PokerItem.showPokers(list);
        boolean ret = PokerItem.ShunZi(list);
        System.out.println(ret);
    }


    // 判断是否是金花
    public void func4() {

        ArrayList<PokerItem> list = new ArrayList<>();
        int[] arr = {2, 6, 10};
        for (int i = 0; i < arr.length; i++) {
            PokerItem item = PokerItem.createItem(arr[i]);
            list.add(item);
        }
        PokerItem.showPokers(list);
        boolean ret = PokerItem.JinHua(list);
        System.out.println(ret);
    }


    // 判断是否是金花
    public void func5() {

        System.out.println("===================");
        ArrayList<PokerItem> list = new ArrayList<>();
        int[] arr = {2, 6, 10};
        for (int i = 0; i < arr.length; i++) {
            PokerItem item = PokerItem.createItem(arr[i]);
            list.add(item);
        }
        PokerItem.showPokers(list);
        boolean ret = PokerItem.ShunJin(list);
        System.out.println(ret);
    }


    // 判断是否是对子
    public void func6() {

        System.out.println("===================");
        ArrayList<PokerItem> list = new ArrayList<>();
        int[] arr = {2, 3, 10};
        for (int i = 0; i < arr.length; i++) {
            PokerItem item = PokerItem.createItem(arr[i]);
            list.add(item);
        }
        PokerItem.showPokers(list);
        boolean ret = PokerItem.DuiZi(list);
        System.out.println(ret);
    }


    // 判断牌的类型
    public void func7() {

        // Scanner sc = new Scanner(System.in);
        // while(true){
        //
        // System.out.println("请输入第一张牌");
        // int a = sc.nextInt();
        // System.out.println("请输入第二张牌");
        // int b = sc.nextInt();
        // System.out.println("请输入第三张牌");
        // int c = sc.nextInt();
        //
        // Player p1 = new Player();
        // ArrayList<PokerItem> list = new ArrayList<>();
        // int[] arr = {0, 0, 0};
        // arr[0] = a;
        // arr[1] = b;
        // arr[2] = c;
        // for(int i = 0; i < arr.length; i++){
        // PokerItem item = PokerItem.createItem(arr[i]);
        // list.add(item);
        // }
        // PokerItem.showPokers(list);
        //
        // p1.setPokers(list);
        // System.out.println(p1);
        // }

    }


    // 判断牌的类型
    public void func8() {

        Scanner sc = new Scanner(System.in);
        while (true) {

            System.out.println("1请输入第一张牌");
            int a = sc.nextInt();
            System.out.println("1请输入第二张牌");
            int b = sc.nextInt();
            System.out.println("1请输入第三张牌");
            int c = sc.nextInt();

            System.out.println("2请输入第一张牌");
            int d = sc.nextInt();
            System.out.println("2请输入第二张牌");
            int e = sc.nextInt();
            System.out.println("2请输入第三张牌");
            int f = sc.nextInt();

            Player p1 = new Player();
            ArrayList<PokerItem> list = new ArrayList<>();
            int[] arr = {0, 0, 0};
            arr[0] = a;
            arr[1] = b;
            arr[2] = c;
            for (int i = 0; i < arr.length; i++) {
                PokerItem item = PokerItem.createItem(arr[i]);
                list.add(item);
            }
            p1.setPokers(list);
            System.out.println(p1);

            Player p2 = new Player();
            ArrayList<PokerItem> list2 = new ArrayList<>();
            int[] arr2 = {0, 0, 0};
            arr2[0] = d;
            arr2[1] = e;
            arr2[2] = f;
            for (int i = 0; i < arr2.length; i++) {
                PokerItem item = PokerItem.createItem(arr2[i]);
                list2.add(item);
            }

            System.out.println("==============");
            System.out.println(list2);
            p2.setPokers(list2);
            System.out.println(p2);

            // 比较两张牌的大小

            int ret = p1.comparePlayer(p2);
            System.out.println("比较结果是 :" + ret);
        }
    }


    // 查找获胜者
    public void func9() {

        Scanner sc = new Scanner(System.in);
        while (true) {

            System.out.println("1请输入第一张牌");
            int a = sc.nextInt();
            System.out.println("1请输入第二张牌");
            int b = sc.nextInt();
            System.out.println("1请输入第三张牌");
            int c = sc.nextInt();

            System.out.println("2请输入第一张牌");
            int d = sc.nextInt();
            System.out.println("2请输入第二张牌");
            int e = sc.nextInt();
            System.out.println("2请输入第三张牌");
            int f = sc.nextInt();

            Player p1 = new Player();
            ArrayList<PokerItem> list = new ArrayList<>();
            int[] arr = {0, 0, 0};
            arr[0] = a;
            arr[1] = b;
            arr[2] = c;
            for (int i = 0; i < arr.length; i++) {
                PokerItem item = PokerItem.createItem(arr[i]);
                list.add(item);
            }
            p1.setPokers(list);
            System.out.println(p1);

            Player p2 = new Player();
            ArrayList<PokerItem> list2 = new ArrayList<>();
            int[] arr2 = {0, 0, 0};
            arr2[0] = d;
            arr2[1] = e;
            arr2[2] = f;
            for (int i = 0; i < arr2.length; i++) {
                PokerItem item = PokerItem.createItem(arr2[i]);
                list2.add(item);
            }

            System.out.println(list2);
            p2.setPokers(list2);
            System.out.println(p2);

            // 比较两张牌的大小

            List<Player> winner = Player.findWinners(p1, p2);
            System.out.println(winner);
        }
    }


    public void func10() {
        Player p1 = new Player();
        p1.setUid(1l);
        ArrayList<PokerItem> pokerlist1 = new ArrayList<>();
        PokerItem item1 = PokerItem.createItem(4);// 2-54
        PokerItem item2 = PokerItem.createItem(6);
        PokerItem item3 = PokerItem.createItem(10);
        pokerlist1.add(item1);
        pokerlist1.add(item2);
        pokerlist1.add(item3);
        p1.setPokers(pokerlist1);

        Player p2 = new Player();
        p1.setUid(2l);
        ArrayList<PokerItem> pokerlist2 = new ArrayList<>();
        PokerItem item21 = PokerItem.createItem(2);// 2-54
        PokerItem item22 = PokerItem.createItem(3);
        PokerItem item23 = PokerItem.createItem(5);
        pokerlist2.add(item21);
        pokerlist2.add(item22);
        pokerlist2.add(item23);
        p2.setPokers(pokerlist2);

        Player p3 = new Player();
        p1.setUid(3l);
        ArrayList<PokerItem> pokerlist31 = new ArrayList<>();
        PokerItem item31 = PokerItem.createItem(7);// 2-54
        PokerItem item32 = PokerItem.createItem(8);
        PokerItem item33 = PokerItem.createItem(19);
        pokerlist31.add(item31);
        pokerlist31.add(item32);
        pokerlist31.add(item33);
        p3.setPokers(pokerlist31);

        ArrayList<Player> li = Player.findWinners(p1, p2, p3);
        for (Player p : li) {
            System.out.println(p);
        }
    }


    public void func11() {

        Player p1 = new Player(1l, 18, 19, 20);
        Player p2 = new Player(2l, 2, 3, 5);
        Player p3 = new Player(3l, 11, 12, 13);

        System.out.println(p1.comparePlayer(p2));
        System.out.println(p2.comparePlayer(p3));
        System.out.println(p1.comparePlayer(p3));
        System.out.println("=================");
        ArrayList<Player> li = Player.findWinners(p1, p2, p3);

        for (Player p : li) {
            System.out.println(p);
        }
    }


    public void func12() {

//		Player p1 = new Player(1, "HEI", "A", "HONG", "A", "PIAN", "A");
//		Player p2 = new Player(2, "HONG", "6", "PIAN", "6", "HUA", "6");
//		Player p3 = new Player(3, "HONG", "7", "PIAN", "7", "HUA", "7");

        Player p1 = new Player(1l, "HEI", "A", "HEI", "A", "HEI", "A");
        Player p2 = new Player(2l, "HONG", "2", "HONG", "3", "HONG", "4");
//		Player p3 = new Player(3, "HUA", "2", "HUA", "3", "PIAN", "5");
        Player p4 = new Player(4l, "HUA", "7", "HUA", "7", "PIAN", "7");

        ArrayList<Player> li = Player.findWinners(p1, p2, p4);

        for (Player p : li) {
            System.out.println(p);
        }

        System.out.println("====================");
        System.out.println(p1.getCategory());
        System.out.println(p1.getCategory().toString());
    }

    public static void main(String[] args) {
//		Player p1 = new Player(1l, "HEI", "J", "HEI", "A", "HEI", "A");
//		Player p2 = new Player(2l, "HONG", "2", "HONG", "3", "HONG", "4");
////		Player p3 = new Player(3, "HUA", "2", "HUA", "3", "PIAN", "5");
//		Player p4 = new Player(4l, "HUA", "7", "HUA", "7", "PIAN", "7");
//
//		ArrayList<Player> li = Player.findWinners(p1, p2, p4);
//
//		for(Player p : li){
//			System.out.println(p);
//		}
//
//		System.out.println("====================");
//		System.out.println(p1.getCategory());
//		System.out.println(p1.getCategory().toString());


//        Player p1 = new Player(3l, "HEI", "A", "HONG", "3", "HUA", "2");
//        Player p2 = new Player(2l, "HUA", "5", "PIAN", "4", "HEI", "3");


//        Player p2 = new Player(3l, "HEI", "A", "HONG", "3", "HUA", "2");
//        Player p1 = new Player(2l, "HEI", "10", "HUA", "9", "HUA", "8");
        Player p1 = new Player(3l, "HEI", "A", "HUA", "3", "HUA", "2");
        Player p2 = new Player(2l, "HEI", "10", "HUA", "9", "HUA", "8");
//        Player p2 = new Player(2l, "HUA", "A", "PIAN", "K", "HEI", "Q");

//		p1.rules_ = Player.Rules.HuanLe;
//		p2.rules_ = Player.Rules.HuanLe;
        p1.rules_ = Player.Rules.XiaoYao;
        p2.rules_ = Player.Rules.XiaoYao;
        ArrayList<Player> aList = Player.findWinners(p1, p2);
        System.err.println(aList);

//		Player asker = new Player(222L, ListUtils.cardCode.get(20), ListUtils.cardCode.get(16), ListUtils.cardCode.get(44));
//		Player accepter = new Player(333L, ListUtils.cardCode.get(40), ListUtils.cardCode.get(24), ListUtils.cardCode.get(36));
//
//		ArrayList<Player> winnerList = Player.findWinners(asker,accepter);
//
//		Long winnerId = winnerList.size()==1?winnerList.get(0).getUid():winnerList.get(1).getUid();
//		System.out.println("winnerId====="+winnerList.toString());
//		System.out.println("winnerId====="+winnerId);
//		System.out.println("===="+asker.getCategory());
    }

}
