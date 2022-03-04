package com.vkontakte.miracle.engine.async;

@FunctionalInterface
public interface IAsyncExecutor<T> {
    void onFinish(T object);
}
