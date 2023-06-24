package com.miracle.engine.adapter;

public interface ILoadableAdapter {
    void onLoading() throws Exception;
    void onComplete();
    void onError();
    void onReload();
}
