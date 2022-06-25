package com.vkontakte.miracle.engine.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;
import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleUniversalAdapter;
import com.vkontakte.miracle.engine.util.LargeDataStorage;

public class ListMiracleFragment extends MiracleFragment{
    private NestedScrollView scrollView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    public void setScrollView(NestedScrollView scrollView){
        this.scrollView = scrollView;
    }

    public NestedScrollView getScrollView() {
        return scrollView;
    }

    //////////////////////////////////////////////////

    public void setRecyclerView(RecyclerView recyclerView){
        setRecyclerView(recyclerView, getVerticalLayoutManager(getMiracleActivity()));
    }

    public void setRecyclerView(RecyclerView recyclerView ,RecyclerView.LayoutManager layoutManager){
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        this.recyclerView = recyclerView;
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    //////////////////////////////////////////////////

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        setSwipeRefreshLayout(swipeRefreshLayout,null);
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        MiracleActivity miracleActivity = getMiracleActivity();
        swipeRefreshLayout.setColorSchemeColors(getColorByAttributeId(miracleActivity,R.attr.colorPrimary));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getColorByAttributeId(miracleActivity,R.attr.swipeRefreshCircleColor));
        swipeRefreshLayout.setProgressViewOffset(true,0,1);
        swipeRefreshLayout.setProgressViewEndTarget(true,64);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout()
    {
        return swipeRefreshLayout;
    }

    //////////////////////////////////////////////////

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    //////////////////////////////////////////////////

    public void show(boolean animate){
        if(recyclerView!=null) {
            if (animate) {
                recyclerView.animate().alpha(1f).setDuration(200).start();
            } else {
                recyclerView.setAlpha(1f);
            }
        }
        if (progressBar != null){
            progressBar.setVisibility(GONE);
        }
        if (swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void show() {
        show(true);
    }

    public void hide(boolean animate) {
        if(animate){
            recyclerView.animate().alpha(0f).setDuration(200).start();
        }else {
            recyclerView.setAlpha(0f);
        }
        if(progressBar != null&&swipeRefreshLayout!=null&&!swipeRefreshLayout.isRefreshing()){
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void hide() {
        hide(true);
    }

    public void setAdapter(MiracleAdapter adapter){
        if(adapter!=null) {
            adapter.iniFromFragment(this);
            adapter.setRecyclerView(recyclerView);
            adapter.ini();
            RecyclerView.Adapter<?> previousAdapter = recyclerView.getAdapter();
            if (previousAdapter != null) {
                if(previousAdapter != adapter) {
                    if (previousAdapter instanceof MiracleAdapter) {
                        MiracleAdapter miracleAdapter = (MiracleAdapter) previousAdapter;
                        miracleAdapter.setRecyclerView(null);
                    }
                    hide();
                    (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                        recyclerView.setAdapter(adapter);
                        adapter.load();
                    }, 200);
                } else {
                    hide();
                    (new Handler(Looper.getMainLooper())).postDelayed(adapter::load, 200);
                }
            } else {
                recyclerView.setAdapter(adapter);
                adapter.load();
            }
        }
    }

    public void reloadAdapter(){
        RecyclerView.Adapter<?> previousAdapter = getRecyclerView().getAdapter();
        if (previousAdapter != null) {
            if(previousAdapter instanceof MiracleUniversalAdapter) {
                if(previousAdapter instanceof MiracleLoadableAdapter){
                    MiracleLoadableAdapter miracleLoadableAdapter = (MiracleLoadableAdapter) previousAdapter;
                    if(!miracleLoadableAdapter.isLoading()){
                        miracleLoadableAdapter.reload();
                        return;
                    }
                } else {
                    MiracleUniversalAdapter universalAdapter = (MiracleUniversalAdapter) previousAdapter;
                    universalAdapter.reload();
                    return;
                }
            }
        }
        getSwipeRefreshLayout().setRefreshing(false);
    }

    @Override
    public boolean notTop(){
        if(recyclerView!=null) {
            return recyclerView.canScrollVertically(-1);
        } else {
            if (scrollView != null) {
                return scrollView.canScrollVertically(-1);
            }
        }

        return false;
    }

    @Override
    public void scrollToTop() {
        if(recyclerView!=null) {
            if(recyclerView.canScrollVertically(-1)) {
                recyclerView.scrollToPosition(0);
            }
        } else {
            if (scrollView != null) {
                if(scrollView.canScrollVertically(-1)) {
                    scrollView.scrollTo(0, 0);
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        if(recyclerView!=null) {
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if(adapter!=null){
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    if(miracleAdapter.getMiracleFragment()==this) {
                        miracleAdapter.setRecyclerView(null);
                    }
                }
            }
        }
        super.onDestroy();
    }

    public boolean nullSavedAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null) {
            String key = savedInstanceState.getString("Adapter");
            if (key != null) {
                MiracleAdapter miracleAdapter = (MiracleAdapter) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove(key);
                if (miracleAdapter != null) {
                    miracleAdapter.setStateSaved(false);
                    miracleAdapter.setSavedStateKey("");
                    miracleAdapter.setShowed(false);
                    setAdapter(miracleAdapter);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(recyclerView!=null) {
            Object object = recyclerView.getAdapter();
            if (object != null){
                if (object instanceof MiracleAdapter) {
                    MiracleAdapter miracleAdapter = (MiracleAdapter) object;
                    miracleAdapter.saveState(outState);
                }
            }
        }
        super.onSaveInstanceState(outState);
    }
}
