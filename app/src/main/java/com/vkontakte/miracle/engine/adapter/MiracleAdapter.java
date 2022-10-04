package com.vkontakte.miracle.engine.adapter;

import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_LOADING_COMPLETE;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_LOADING_ERROR;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_RELOADING;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;

import java.util.ArrayList;

public abstract class MiracleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ILoadableAdapter {

    private OnAdapterStateChangeListener stateChangeListener;

    private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    public ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap;

    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private final androidx.collection.ArrayMap<Integer, MiracleViewRecycler> viewRecyclerMap = new androidx.collection.ArrayMap<>();
    private final androidx.collection.ArrayMap<Integer, RecyclerView.RecycledViewPool> nestedRecycledViewPoolMap = new androidx.collection.ArrayMap<>();

    private IMiracleFragment iMiracleFragment;
    private MainActivity mainActivity;

    /////////////////////////////////
    private String error = "";
    private boolean loaded = false;
    private boolean initialized = false;
    private int addedCount = 0;
    private DiffUtil.DiffResult diffResult;
    /////////////////////////////////

    public void iniFromFragment(IMiracleFragment iMiracleFragment){
        this.iMiracleFragment = iMiracleFragment;
        mainActivity = iMiracleFragment.getMiracleActivity();
    }

    public void iniFromActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /////////////////////////////////////////////////////////////////////////

    public abstract void load();

    public void reload(){
        onReload();
    }

    @Override
    public void onComplete() {
        if(attached()){
            if(loaded){
                if(diffResult==null) {
                    notifyData();
                } else {
                    diffResult.dispatchUpdatesTo(this);
                    diffResult = null;
                }
            } else {
                recyclerView.setItemAnimator(null);
                if(diffResult==null) {
                    notifyData();
                    recyclerView.scrollToPosition(0);
                } else {
                    diffResult.dispatchUpdatesTo(this);
                    diffResult = null;
                }
                loaded = true;
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                pushState(SATE_FIRST_LOADING_COMPLETE);
            }
            pushState(SATE_LOADING_COMPLETE);
        }
    }

    @Override
    public void onError() {
        if(attached()) {
            if(!loaded) {
                loaded = true;
                itemDataHolders.clear();
                itemDataHolders.add(new ErrorDataHolder(error));
                notifyItemInserted(0);
            } else {
                int oldSize = itemDataHolders.size();
                itemDataHolders.clear();
                itemDataHolders.add(new ErrorDataHolder(error));

                if (oldSize > 0) {
                    notifyItemChanged(0);
                    if (oldSize > 1) {
                        notifyItemRangeRemoved(1, oldSize - 1);
                    }
                } else {
                    notifyItemInserted(0);
                }
            }
            pushState(SATE_LOADING_ERROR);
        }
    }

    @Override
    public void onReload() {
        if(attached()) {
            restoreState();
            int oldSize = itemDataHolders.size();
            itemDataHolders.clear();
            if (oldSize > 0) {
                if (oldSize > 1) {
                    notifyItemRangeRemoved(0, oldSize - 1);
                } else {
                    notifyItemRemoved(0);
                }
            }
            pushState(SATE_RELOADING);
            load();
        }
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
        addedCount = 0;
    }

    @CallSuper
    public void restoreState(){
        diffResult = null;
        addedCount = 0;
        error = "";
        loaded = false;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    public boolean loaded() {
        return loaded;
    }

    public boolean initialized() {
        return initialized;
    }

    public boolean attached(){
        return recyclerView!=null;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        if(!loaded){
            return 0;
        }else {
            return itemDataHolders.size();
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
            return viewHolderFabric.create(inflater, parent);
        }else {
            return new ErrorViewHolder.Fabric().create(inflater, parent);
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MiracleViewHolder){
            if(position<itemDataHolders.size()){
                ((MiracleViewHolder)holder).bind(itemDataHolders.get(position));
            }
        }
    }

    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap(){
        return ViewHolderTypes.getVerticalFabrics();
    }

    @CallSuper
    public void ini(){
        viewHolderFabricMap = getViewHolderFabricMap();
        initialized = true;
    }


    //////////////////////////////////////////////////////////////////////////////////////
    public void pushState(int state){
        if(stateChangeListener!=null){
            stateChangeListener.onStateChanged(state);
        }
    }
    public void setStateChangeListener(OnAdapterStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }
    public OnAdapterStateChangeListener getStateChangeListener() {
        return stateChangeListener;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setError(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public int getItemsSize(){
        return itemDataHolders.size();
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }
    public int getAddedCount() {
        return addedCount;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public void setItemDataHolders(ArrayList<ItemDataHolder> itemDataHolders) {
        this.itemDataHolders = itemDataHolders;
    }
    public ArrayList<ItemDataHolder> getItemDataHolders() {
        return itemDataHolders;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public void setDiffResult(DiffUtil.DiffResult diffResult) {
        this.diffResult = diffResult;
    }
    public DiffUtil.DiffResult getDiffResult() {
        return diffResult;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public void setRecyclerView(RecyclerView recyclerView) {
        if(this.recyclerView!=null) {
            if(recyclerView!=null) {
                if (this.recyclerView.getContext() != recyclerView.getContext()) {
                    nestedRecycledViewPoolMap.clear();
                    viewRecyclerMap.clear();
                    inflater = LayoutInflater.from(recyclerView.getContext());
                }
                recycledViewPool = recyclerView.getRecycledViewPool();
            } else {
                recycledViewPool = null;
                nestedRecycledViewPoolMap.clear();
                viewRecyclerMap.clear();
                inflater = null;
            }
        } else {
            if(recyclerView!=null) {
                inflater = LayoutInflater.from(recyclerView.getContext());
                recycledViewPool = recyclerView.getRecycledViewPool();
            }
        }
        this.recyclerView = recyclerView;
    }
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public LayoutInflater getInflater() {
        return inflater;
    }
    public RecyclerView.RecycledViewPool getRecycledViewPool() {
        if(recycledViewPool==null){
            if(recyclerView!=null){
                recycledViewPool = recyclerView.getRecycledViewPool();
            }
        }
        return recycledViewPool;
    }
    public MiracleViewRecycler getMiracleViewRecycler(int type) {
        MiracleViewRecycler miracleViewRecycler = viewRecyclerMap.get(type);
        if(miracleViewRecycler==null){
            viewRecyclerMap.put(type, miracleViewRecycler = new MiracleViewRecycler());
        }
        return miracleViewRecycler;
    }
    public RecyclerView.RecycledViewPool getNestedRecycledViewPool(int type) {
        RecyclerView.RecycledViewPool recycledViewPool = nestedRecycledViewPoolMap.get(type);
        if(recycledViewPool==null){
            nestedRecycledViewPoolMap.put(type, recycledViewPool = new RecyclerView.RecycledViewPool());
        }
        return recycledViewPool;
    }
    public IMiracleFragment getMiracleFragment() {
        return iMiracleFragment;
    }
    public MainActivity getMiracleActivity(){
        return mainActivity;
    }
    /////////////////////////////////////////////////////////////////////////////////////
}
