package com.miracle.engine.adapter;

import static com.miracle.engine.adapter.AdapterStates.SATE_LOADING;
import static com.miracle.engine.adapter.AdapterStates.SATE_LOADING_ENDED;

import androidx.annotation.CallSuper;

import com.miracle.engine.async.AsyncExecutor;

public abstract class MiracleAsyncLoadAdapter extends MiracleInstantLoadAdapter {

    private AsyncExecutor<Boolean> dataLoader;
    /////////////////////////////////
    private String nextFrom = "";
    private long timeStump = 0;
    private boolean instantLoading = false;
    /////////////////////////////////

    public void instantLoad(){
        instantLoading = true;
        super.load();
        instantLoading = false;
    }

    @Override
    public void load() {
        setError("");
        if(!loading()){
            dataLoader = new AsyncExecutor<Boolean>() {
                @Override
                public Boolean inBackground() {
                    try {
                        onLoading();
                        return true;
                    } catch (Exception e) {
                        if(e.getMessage()==null){
                            setError(e.toString());
                        } else {
                            setError(e.getMessage());
                        }
                        e.printStackTrace();
                    }
                    return false;
                }
                @Override
                public void onExecute(Boolean result) {
                    if(this==dataLoader) {
                        if (result) {
                            onComplete();
                            if(finallyLoaded()){
                                pushState(SATE_LOADING_ENDED);
                            }
                        } else {
                            setFinallyLoaded(true);
                            onError();
                        }
                    }
                }
            };
            pushState(SATE_LOADING);
            dataLoader.start();
        }
    }

    public boolean loading() {
        return dataLoader!=null&&!dataLoader.workIsDone();
    }

    @CallSuper
    @Override
    public void restoreState() {
        setNextFrom("");
        setTimeStump(0);
        if(dataLoader!=null){
            if(!dataLoader.workIsDone()){
                dataLoader.interrupt();
            }
            dataLoader = null;
        }
        super.restoreState();
    }

    //////////////////////////////////////////////////
    public boolean instantLoading() {
        return instantLoading;
    }
    //////////////////////////////////////////////////
    public void setNextFrom(String nextFrom) {
        this.nextFrom = nextFrom;
    }
    public String getNextFrom() {
        return nextFrom;
    }
    //////////////////////////////////////////////////
    public void setTimeStump(long timeStump) {
        this.timeStump = timeStump;
    }
    public long getTimeStump() {
        return timeStump;
    }
    //////////////////////////////////////////////////

}
