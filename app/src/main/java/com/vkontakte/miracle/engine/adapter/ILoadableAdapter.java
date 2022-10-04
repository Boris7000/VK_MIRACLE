package com.vkontakte.miracle.engine.adapter;

public interface ILoadableAdapter {
    void onLoading() throws Exception;
    void onComplete();
    void onError();
    void onReload();
}
