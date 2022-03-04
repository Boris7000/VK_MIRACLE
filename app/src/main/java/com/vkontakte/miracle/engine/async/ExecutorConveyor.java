package com.vkontakte.miracle.engine.async;

import java.util.ArrayList;

public class ExecutorConveyor <T> {
    private final ArrayList<AsyncExecutor<T>> asyncExecutors = new ArrayList<>();

    public void addAsyncExecutor(AsyncExecutor<T> asyncExecutor){
        asyncExecutor.addOnExecuteListener(asyncExecutor1 -> {
            asyncExecutors.remove(asyncExecutor1);
            if(!asyncExecutors.isEmpty()){
                asyncExecutors.get(0).start();
            }
        });
        asyncExecutors.add(asyncExecutor);
        if(asyncExecutors.size()==1){
            asyncExecutor.start();
        }
    }
}
