package com.miracle.engine.async;

public interface OnExecuteListener<T> {
    void onExecute(AsyncExecutor<T> asyncExecutor);
}
