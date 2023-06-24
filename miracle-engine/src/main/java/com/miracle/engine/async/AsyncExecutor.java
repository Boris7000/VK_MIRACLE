package com.miracle.engine.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

public abstract class AsyncExecutor<T> extends Thread implements IAsyncExecutor<T> {

    private final ArrayList<OnExecuteListener<T>> executeListeners = new ArrayList<>();
    private final ArrayList<OnStartListener<T>> startListeners = new ArrayList<>();
    private boolean workIsDone = false;
    private final String LOG_TAG = "AsyncExecutor";

    @Override
    public void run() {
        super.run();

        new Handler(Looper.getMainLooper()).post(this::onStart);

        final T object = inBackground();
        workIsDone = true;

        new Handler(Looper.getMainLooper()).post(() -> onFinish(object));

    }

    @Override
    public void interrupt() {
        Log.d(LOG_TAG,"interrupted");
        super.interrupt();
    }

    @Override
    public void onStart() {
        for (OnStartListener<T>listener:startListeners) {
            listener.onStart(this);
        }
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

    public void onExecute(T result){}

    public boolean workIsDone() {
        return workIsDone;
    }

    public void addOnExecuteListener(OnExecuteListener<T> onExecuteListener){
        executeListeners.add(onExecuteListener);
    }

    public void addOnStartListener(OnStartListener<T> onStartListener){
        startListeners.add(onStartListener);
    }

}

