package com.miracle.engine.adapter;

import static com.miracle.engine.adapter.AdapterStates.SATE_LOADING;
import static com.miracle.engine.adapter.AdapterStates.SATE_LOADING_ENDED;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MiracleInstantLoadAdapter extends MiracleAdapter {

    /////////////////////////////////
    private boolean finallyLoaded = false;
    private boolean prohibitScrollLoad = false;
    private int totalCount = 0;
    private int loadedCount = 0;
    private int step = 25;
    /////////////////////////////////

    @Override
    public void load() {
        setError("");
        try {
            pushState(SATE_LOADING);
            onLoading();
            onComplete();
            if(finallyLoaded()){
                pushState(SATE_LOADING_ENDED);
            }
        } catch (Exception e) {
            if(e.getMessage()==null){
                setError(e.toString());
            } else {
                setError(e.getMessage());
            }
            setFinallyLoaded(true);
            onError();
            pushState(SATE_LOADING_ENDED);
            e.printStackTrace();
        }
    }

    @Override
    public void restoreState() {
        setFinallyLoaded(false);
        setTotalCount(0);
        setLoadedCount(0);
        super.restoreState();
    }

    @Override
    public int getItemCount() {
        if(!loaded()){
            return 0;
        }else {
            if(finallyLoaded()){
                return getItemsSize();
            }else {
                return getItemsSize()+1;
            }
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int red = (getItemCount()>1)?2:1;
        if (position>=getItemCount()-red && !finallyLoaded() && attached() && !prohibitScrollLoad()) {
            load();
        }
    }

    @Override
    public void notifyData(){
        super.notifyData();
        if(loaded() && finallyLoaded()){
            notifyItemRemoved(getItemDataHolders().size());
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setProhibitScrollLoad(boolean prohibitScrollLoad) {
        this.prohibitScrollLoad = prohibitScrollLoad;
    }
    public boolean prohibitScrollLoad() {
        return prohibitScrollLoad;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setFinallyLoaded(boolean finallyLoaded) {
        this.finallyLoaded = finallyLoaded;
    }
    public boolean finallyLoaded() {
        return finallyLoaded;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setLoadedCount(int loadedCount) {
        this.loadedCount = loadedCount;
    }
    public int getLoadedCount() {
        return loadedCount;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setStep(int step) {
        this.step = step;
    }
    public int getStep() {
        return step;
    }
}
