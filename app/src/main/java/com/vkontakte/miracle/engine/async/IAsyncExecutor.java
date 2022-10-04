package com.vkontakte.miracle.engine.async;


public interface IAsyncExecutor<T> {
    void onFinish(T object);
    void onStart();
}
