package com.vkontakte.miracle.engine.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.engine.fragment.ListMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.users.ProfileItem;

public abstract class MiracleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String LOG_TAG = "MiracleAdapter";

    private boolean stateSaved = false;
    private String savedStateKey = "";
    private boolean showed = false;
    private boolean hasData = false;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private ListMiracleFragment miracleFragment;
    private MiracleActivity miracleActivity;
    private ProfileItem userItem;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private final ArrayMap<Integer, MiracleViewRecycler> viewRecyclerMap = new ArrayMap<>();
    private final ArrayMap<Integer, RecyclerView.RecycledViewPool> nestedRecycledViewPoolMap = new ArrayMap<>();

    public void iniFromFragment(ListMiracleFragment miracleFragment){
        this.miracleFragment = miracleFragment;
        miracleActivity = miracleFragment.getMiracleActivity();
        userItem = miracleActivity.getUserItem();
    }

    public void iniFromActivity(MiracleActivity miracleActivity){
        this.miracleActivity = miracleActivity;
        userItem = miracleActivity.getUserItem();
    }

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

    public ListMiracleFragment getMiracleFragment() {
        return miracleFragment;
    }
    //////////////////////////////////////////////////
    public MiracleActivity getMiracleActivity(){
        return miracleActivity;
    }
    //////////////////////////////////////////////////
    public ProfileItem getUserItem() {
        return userItem;
    }
    //////////////////////////////////////////////////
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    //////////////////////////////////////////////////
    public LayoutInflater getInflater() {
        return inflater;
    }
    //////////////////////////////////////////////////
    public RecyclerView.RecycledViewPool getRecycledViewPool() {
        if(recycledViewPool==null){
            if(getRecyclerView()!=null){
                recycledViewPool = getRecyclerView().getRecycledViewPool();
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
    //////////////////////////////////////////////////
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
    public boolean hasData() {
        return hasData;
    }
    //////////////////////////////////////////////////
    public void setShowed(boolean showed) {
        this.showed = showed;
    }
    public boolean isShowed() {
        return showed;
    }
    public void show(boolean animate){
        if(attached()){
            setShowed(true);
            ListMiracleFragment miracleFragment = getMiracleFragment();
            if (miracleFragment != null) {
                miracleFragment.show(animate);
            }
        }
    }
    public void hide(boolean animate){
        if(attached()) {
            setShowed(false);
            ListMiracleFragment miracleFragment = getMiracleFragment();
            if (miracleFragment != null) {
                miracleFragment.hide(animate);
            }
        }
    }
    //////////////////////////////////////////////////
    public boolean attached(){
        return recyclerView!=null;
    }
    //////////////////////////////////////////////////
    public void setStateSaved(boolean stateSaved) {
        this.stateSaved = stateSaved;
    }
    public boolean isStateSaved() {
        return stateSaved;
    }
    public void setSavedStateKey(String savedStateKey) {
        this.savedStateKey = savedStateKey;
    }
    public String getSavedStateKey() {
        return savedStateKey;
    }
    public void saveState(@NonNull Bundle outState){
        if (hasData()) {
            if(isStateSaved()){
                outState.putString("Adapter", getSavedStateKey());
            } else {
                String key = LargeDataStorage.get().storeLargeData(this);
                setStateSaved(true);
                setSavedStateKey(key);
                outState.putString("Adapter", key);
            }
        }
    }
    //////////////////////////////////////////////////
    public void resetToInitialState(){
        setHasData(false);
    }
    //////////////////////////////////////////////////

    public abstract void load();

    public void ini(){}

}
