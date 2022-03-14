package com.vkontakte.miracle.engine.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

public abstract class AsyncExecutor<T> extends Thread implements IAsyncExecutor<T>{

    private final ArrayList<OnExecuteListener<T>> executeListeners = new ArrayList<>();
    private boolean workIsDone = false;
    private final String LOG_TAG = "AsyncExecutor";

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();

        final T object = inBackground();
        workIsDone = true;
        //if(object!=null) {
            new Handler(Looper.getMainLooper()).post(() -> onFinish(object));
        //}else {
            //throw new RuntimeException("invalid executed object in "+Thread.currentThread().getName());
        //}
    }

    @Override
    public void interrupt() {
        Log.d(LOG_TAG,"interrupted");
        super.interrupt();
    }

    @Override
    public void onFinish(T object) {
        Log.d(LOG_TAG,"executed");
        AsyncExecutor.this.onExecute(object);
        for (OnExecuteListener<T>listener:executeListeners) {
            listener.onExecute(this);
        }
        executeListeners.clear();
    }

    public abstract T inBackground();

    public abstract void onExecute(T object);

    public boolean workIsDone() {
        return workIsDone;
    }

    public void addOnExecuteListener(OnExecuteListener<T> onExecuteListener){
        executeListeners.add(onExecuteListener);
    }
}

