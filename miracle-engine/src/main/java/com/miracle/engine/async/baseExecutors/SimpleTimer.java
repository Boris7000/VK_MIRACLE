package com.miracle.engine.async.baseExecutors;

import com.miracle.engine.async.AsyncExecutor;

public class SimpleTimer extends AsyncExecutor<Boolean> {

    private final long total;
    private final long tick;
    private long remaining;

    public SimpleTimer(long total, long tick){
        this.total = total;
        this.tick = tick;
        remaining = total;
    }

    public void reset(){
        remaining = total;
    }

    @Override
    public Boolean inBackground() {
        while (remaining>0){
            try {
                Thread.sleep(Math.min(remaining, tick));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            remaining-=tick;

        }
        return true;
    }
}