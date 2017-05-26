package com.code.server.util.timer;

/**
 * Created by sunxianping on 2015/8/10.
 */
public class TimerNode {
    private long start;
    private long interval;
    private boolean isPeroid;
    private ITimeHandler timeHandler ;
    private int triggerCount;


    public TimerNode(long start, long interval, boolean isPeroid, ITimeHandler timeHandler) {
        this.start = start;
        this.interval = interval;
        this.isPeroid = isPeroid;
        this.timeHandler = timeHandler;
        this.triggerCount = 0;
    }

    public long getStart() {
        return start;
    }

    public long getInterval() {
        return interval;
    }

    public boolean isPeroid() {
        return isPeroid;
    }

    public ITimeHandler getTimeHandler() {
        return timeHandler;
    }

    public long getNextTriggerTime() {
        return start + interval * (triggerCount + 1);
    }

    public void fire(){
        timeHandler.fire();
        this.triggerCount++;
    }

}
