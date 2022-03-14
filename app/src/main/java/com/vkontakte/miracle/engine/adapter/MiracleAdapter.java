package com.vkontakte.miracle.engine.adapter;

import android.view.LayoutInflater;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.engine.fragment.ListMiracleFragment;
import com.vkontakte.miracle.model.users.ProfileItem;

public abstract class MiracleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String LOG_TAG = "MiracleAdapter";

    private int currentPosition = 0;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private ListMiracleFragment miracleFragment;
    private MiracleActivity miracleActivity;
    private MiracleApp miracleApp;
    private ProfileItem userItem;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public void iniFromFragment(ListMiracleFragment miracleFragment){
        this.miracleFragment = miracleFragment;
        miracleActivity = miracleFragment.getMiracleActivity();
        miracleApp = miracleFragment.getMiracleApp();
        userItem = miracleActivity.getUserItem();
    }

    public void iniFromActivity(MiracleActivity miracleActivity){
        this.miracleActivity = miracleActivity;
        miracleApp = miracleActivity.getMiracleApp();
        userItem = miracleActivity.getUserItem();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        inflater = LayoutInflater.from(this.recyclerView.getContext());
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
    public MiracleApp getMiracleApp() {
        return miracleApp;
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
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    public int getCurrentPosition() {
        return currentPosition;
    }
    //////////////////////////////////////////////////
    public RecyclerView.RecycledViewPool getRecycledViewPool() {
        if(recycledViewPool==null){
            recycledViewPool = new RecyclerView.RecycledViewPool();
        }
        return recycledViewPool;
    }
    //////////////////////////////////////////////////

    public abstract void ini();

    public abstract void load();

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.currentPosition = holder.getBindingAdapterPosition();
    }

}
