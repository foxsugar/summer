package com.code.server.util.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by sunxianping on 2015/8/10.
 */
public class GameTimer {

    private GameTimer(){}

    private static final Logger logger = LoggerFactory.getLogger(GameTimer.class);
    private static GameTimer instance;


    public static GameTimer getInstance() {
        if (instance == null) {
            instance = new GameTimer();
        }
        return instance;
    }

    private PriorityQueue<TimerNode> queue = new PriorityQueue<>(100, new Comparator<TimerNode>() {
        @Override
        public int compare(TimerNode o1, TimerNode o2) {
            if (o1.getNextTriggerTime() > o2.getNextTriggerTime()) {
                return 1;
            }else if (o1.getNextTriggerTime() < o2.getNextTriggerTime()){
                return -1;
            }
            return 0;
        }
    });

    public void handle(){
        long nowTime = System.currentTimeMillis();
        TimerNode node = queue.peek();
        if(node != null && node.getNextTriggerTime() <= nowTime) {
            try {
                queue.poll().fire();
            } catch (Exception e) {
                logger.error("timer handle error ",e);
            }
            if (node.isPeroid()) {
                queue.add(node);
            }
        }
    }
    public void fire() {
        while (true){
            long nowTime = System.currentTimeMillis();
            TimerNode node = queue.peek();
            if(node != null && node.getNextTriggerTime() <= nowTime){
                try {
                    queue.poll().fire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(node.isPeroid()){
                    queue.add(node);
                }
            }else{
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addTimerNode(TimerNode node) {
        getInstance().queue.add(node);
    }

    public static void addTimerNode(long interval, boolean isPeroid, ITimeHandler timeHandler){
        TimerNode node = new TimerNode(System.currentTimeMillis(), interval, isPeroid, timeHandler);
        addTimerNode(node);
    }

    public static void removeNode(TimerNode node) {
        getInstance().queue.remove(node);
    }



}
