package com.vkontakte.miracle.engine.adapter;

import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.ListMiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

import java.util.ArrayList;

public abstract class MiracleLoadableAdapter extends MiracleUniversalAdapter implements ILoadableAdapter {

    private final ArrayList<Task> tasks = new ArrayList<>();
    private boolean finallyLoaded = false;
    private boolean hasError = false;
    private String error = "";
    private String nextFrom = "";
    private long timeStump = 0;
    private int totalCount = 0;
    private int loadedCount = 0;
    private int step = 25;

    private AsyncExecutor<Boolean> dataLoader;

    @Override
    public void load() {
        if(attached()){
            if(hasData()&&!isShowed()){
                show(false);
            } else {
                if(!isLoading()){
                    (dataLoader = new AsyncExecutor<Boolean>() {
                        @Override
                        public Boolean inBackground() {
                            try {
                                MiracleLoadableAdapter.this.onLoading();
                                error = "";
                                return true;
                            } catch (Exception e) {
                                error = e.toString();
                                return false;
                            }
                        }
                        @Override
                        public void onExecute(Boolean success) {
                            if(attached()){
                                if(success){
                                    Log.d(LOG_TAG, "successful loading");
                                    hasError = false;
                                    if(attached()) {
                                        MiracleLoadableAdapter.this.onComplete();
                                    }
                                } else {
                                    Log.d(LOG_TAG, error);
                                    hasError = true;
                                    finallyLoaded = true;
                                    if (!hasData()) {
                                        getItemDataHolders().add(new ErrorDataHolder(error));
                                        setAddedCount(1);
                                        setHasData(true);
                                        notifyData();
                                        if (!isShowed()) {
                                            show(true);
                                            ListMiracleFragment miracleFragment = getMiracleFragment();
                                            if (miracleFragment != null) {
                                                if (miracleFragment instanceof SimpleMiracleFragment) {
                                                    ((SimpleMiracleFragment) miracleFragment).setTitleText("Ошибка");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }
        }
    }

    public boolean isLoading() {
        return dataLoader!=null&&!dataLoader.workIsDone();
    }
    //////////////////////////////////////////////////
    public void setFinallyLoaded(boolean finallyLoaded) {
        this.finallyLoaded = finallyLoaded;
    }
    public boolean finallyLoaded() {
        return finallyLoaded;
    }
    //////////////////////////////////////////////////
    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
    public boolean hasError() {
        return hasError;
    }
    //////////////////////////////////////////////////
    public void setError(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }
    //////////////////////////////////////////////////
    public void setNextFrom(String nextFrom) {
        this.nextFrom = nextFrom;
    }
    public String getNextFrom() {
        return nextFrom;
    }
    //////////////////////////////////////////////////
    public void setLoadedCount(int loadedCount) {
        this.loadedCount = loadedCount;
    }
    public int getLoadedCount() {
        return loadedCount;
    }
    //////////////////////////////////////////////////
    public void setStep(int step) {
        this.step = step;
    }
    public int getStep() {
        return step;
    }
    //////////////////////////////////////////////////
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    //////////////////////////////////////////////////
    public void setTimeStump(long timeStump) {
        this.timeStump = timeStump;
    }
    public long getTimeStump() {
        return timeStump;
    }
    //////////////////////////////////////////////////
    @Override
    public void resetToInitialState(){
        super.resetToInitialState();
        tasks.clear();
        finallyLoaded = false;
        hasError = false;
        error = "";
        nextFrom = "";
        timeStump = 0;
        totalCount = 0;
        loadedCount = 0;
    }
    //////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        if(!hasData()){
            return 0;
        }else {
            if(finallyLoaded){
                return getItemDataHolders().size();
            }else {
                return getItemDataHolders().size()+1;
            }
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int red = (getItemCount()>1)?2:1;
        if (position>=getItemCount()-red && !finallyLoaded && attached()) {
            load();
        }
    }

    @Override
    public void onComplete() {
        if (!hasData()) {
            setHasData(true);
            getRecyclerView().setItemAnimator(null);
            notifyData();
            if(isReloading()){
                setReloading(false);
                getRecyclerView().scrollToPosition(0);
            }
            getRecyclerView().setItemAnimator(new DefaultItemAnimator());
            if(!isShowed()){
                show(true);
            }
            if (!tasks.isEmpty()) {
                tasks.get(0).func();
            }
        }else {
            notifyData();
        }
    }

    @Override
    public void notifyData(){
        super.notifyData();
        if(finallyLoaded){
            notifyItemRemoved(getItemDataHolders().size());
        }
    }

    public void addTask(Task task){
        tasks.add(task);
        if(hasData()) {
            if (tasks.size() == 1) {
                task.func();
            }
        }
    }

    public abstract class Task{
        public abstract void func();
        public void onComplete(){
            tasks.remove(this);
            if(!tasks.isEmpty()){
                Task task = tasks.get(0);
                if(task!=null&&task!=this) {
                    task.func();
                }
            }
        }
    }
}
