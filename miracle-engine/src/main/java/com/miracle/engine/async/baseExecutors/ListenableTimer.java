package com.miracle.engine.async.baseExecutors;

import android.os.Handler;
import android.os.Looper;

import com.miracle.engine.async.AsyncExecutor;

public class ListenableTimer extends AsyncExecutor<Boolean> {

    private final long total;
    private final long tick;
    private long remaining;
    private final Handler handler;

    private OnTickListener onTickListener;

    public void setOnTickListener(OnTickListener onTickListener) {
        this.onTickListener = onTickListener;
    }

    public ListenableTimer(long total, long tick){
        this.total = total;
        this.tick = tick;
        remaining = total;
        handler = new Handler(Looper.getMainLooper());
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
                return false;
            }
            remaining-=tick;

            if(onTickListener!=null){
                handler.post(() -> onTickListener.onTick(remaining, total));
            }
        }
        return true;
    }

    public interface OnTickListener {
        void onTick(long remaining, long total);
    }

}