package com.code.server.game.poker.guess;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class Demo {
    public static void main(String[] args) {
        int temp = 10;
        new Thread(new Runnable() {//设置倒计时
            @Override
            public void run() {
                try{
                    Thread.sleep(10000);
                    System.out.print("倒计时结束");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        while(temp>0){
            System.out.print(temp);
            temp--;
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
