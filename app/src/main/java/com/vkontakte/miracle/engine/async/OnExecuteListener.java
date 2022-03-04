package com.vkontakte.miracle.engine.async;

interface OnExecuteListener<T> {
    void onExecute(AsyncExecutor<T> asyncExecutor);
}
