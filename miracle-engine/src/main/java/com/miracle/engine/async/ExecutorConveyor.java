package com.miracle.engine.async;

import java.util.ArrayList;

public class ExecutorConveyor <T> {
    private final ArrayList<AsyncExecutor<T>> asyncExecutors = new ArrayList<>();

    public ArrayList<AsyncExecutor<T>> getAsyncExecutors() {
        return new ArrayList<>(asyncExecutors);
    }

    public void addAsyncExecutor(AsyncExecutor<T> asyncExecutor){
        asyncExecutors.add(asyncExecutor);
        asyncExecutor.addOnExecuteListener(asyncExecutor1 -> {
            asyncExecutors.remove(asyncExecutor1);
            if(!asyncExecutors.isEmpty()){
                asyncExecutors.get(0).start();
            }
        });
        if(asyncExecutors.size()==1){
            asyncExecutor.start();
        }
    }

    public void release(){
        if(!asyncExecutors.isEmpty()){
            asyncExecutors.get(0).interrupt();
            asyncExecutors.clear();
        }
    }
}
