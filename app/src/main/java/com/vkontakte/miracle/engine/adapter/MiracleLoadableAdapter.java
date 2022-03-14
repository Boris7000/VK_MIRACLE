package com.vkontakte.miracle.engine.adapter;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;

import android.util.ArrayMap;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

import java.util.ArrayList;

public abstract class MiracleLoadableAdapter extends MiracleAdapter implements ILoadableAdapter {

    private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    public  ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap;
    private final ArrayList<Task> tasks = new ArrayList<>();
    private boolean finallyLoaded = false;
    private boolean hasData = false;
    private boolean hasError = false;
    private boolean detached = false;
    private String error = "";
    private String nextFrom = "";
    private long timeStump = 0;
    private int addedCount = 0;
    private int totalCount = 0;
    private int loadedCount = 0;
    private int step = 25;

    private AsyncExecutor<Boolean> dataLoader;

    @Override
    public void load() {

        if(detached){
            detached = false;
            if(hasData) {
                getMiracleFragment().show(false);
            }
        }else{
            if(!loading()){
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
                    public void onExecute(Boolean object) {
                        if(object){
                            Log.d(LOG_TAG, "successful loading");
                            hasError = false;
                            MiracleLoadableAdapter.this.onComplete();
                        }else {
                            if (!detached) {
                                Log.d(LOG_TAG, error);

                                hasError = true;
                                finallyLoaded = true;

                                if (!hasData) {
                                    itemDataHolders.add(new ErrorDataHolder(error));
                                    hasData = true;
                                    notifyData();
                                    getMiracleFragment().show();
                                    if(getMiracleFragment() instanceof SimpleMiracleFragment) {
                                        ((SimpleMiracleFragment) getMiracleFragment()).setTitleText("Ошибка");
                                    }
                                }
                            }
                        }
                    }
                }).start();
            }
        }
    }

    public boolean loading() {
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
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
    public boolean hasData() {
        return hasData;
    }
    //////////////////////////////////////////////////
    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }
    public int getAddedCount() {
        return addedCount;
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
    public ArrayList<ItemDataHolder> getItemDataHolders() {
        return itemDataHolders;
    }
    public void setItemDataHolders(ArrayList<ItemDataHolder> itemDataHolders) {
        this.itemDataHolders = itemDataHolders;
    }
    //////////////////////////////////////////////////

    public void setDetached(boolean detached) {
        this.detached = detached;
    }

    @Override
    public int getItemCount() {
        if(!hasData){
            return 0;
        }else {
            if(finallyLoaded){
                return itemDataHolders.size();
            }else {
                return itemDataHolders.size()+1;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position<itemDataHolders.size()){
            return itemDataHolders.get(position).getViewHolderType();
        } else {
            return TYPE_LOADING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolderFabric viewHolderFabric = viewHolderFabricMap.get(viewType);
        if(viewHolderFabric !=null){
            return viewHolderFabric.create(getInflater(),parent);
        }else {
            return new ErrorViewHolder.Fabric().create(getInflater(),parent);
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        super.onBindViewHolder(holder, position);

        if(holder instanceof MiracleViewHolder){
            if(position<itemDataHolders.size()){
                ((MiracleViewHolder)holder).bind(itemDataHolders.get(position));
            }
        }

        int red = (getItemCount()>1)?2:1;

        if (position==getItemCount()-red && !finallyLoaded && !detached) {
            load();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        detached = true;
        super.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public void onComplete() {
        if (!hasData) {
            hasData = true;
            notifyData();
            getMiracleFragment().show();

            if (!tasks.isEmpty()) {
                tasks.get(0).func();
            }

        }else {
            notifyData();
        }
    }

    @CallSuper
    @Override
    public void ini() {
        viewHolderFabricMap = getViewHolderFabricMap();
    }

    public void notifyData(){
        int lastItemPosition = itemDataHolders.size();

        if(addedCount>0){
            notifyItemRangeInserted(lastItemPosition - addedCount, addedCount);
        }else {
            if (addedCount<0) {
                notifyItemRangeRemoved(lastItemPosition + addedCount, -addedCount);
            }
        }

        if(finallyLoaded){
            notifyItemRemoved(lastItemPosition);
        }
    }

    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap(){
        return ViewHolderTypes.getCatalogFabrics();
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
