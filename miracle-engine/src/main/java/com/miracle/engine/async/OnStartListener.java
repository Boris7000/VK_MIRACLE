package com.miracle.engine.async;

public interface OnStartListener <T> {
    void onStart(AsyncExecutor<T> asyncExecutor);
}
