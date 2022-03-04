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

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
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
        swipeRefreshLayout.setColorSchemeColors(getColorByAttributeId(getMiracleActivity(), R.attr.colorPrimary));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getColorByAttributeId(getMiracleActivity(), R.attr.swipeRefreshCircleColor));
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
        if(animate){
            recyclerView.animate().alpha(1f).setDuration(200).start();
        }else {
            recyclerView.setAlpha(1f);
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
                hide();
                if (previousAdapter instanceof MiracleLoadableAdapter){
                    ((MiracleLoadableAdapter)previousAdapter).setDetached(true);
                }
                (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                    recyclerView.setAdapter(adapter);
                    adapter.load();
                }, 200);
            } else {
                recyclerView.setAdapter(adapter);
                adapter.load();
            }
        }
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
                if(adapter instanceof MiracleLoadableAdapter){
                    MiracleLoadableAdapter miracleLoadableAdapter = (MiracleLoadableAdapter) adapter;
                    miracleLoadableAdapter.setDetached(true);
                }
            }
        }
        super.onDestroy();
    }

    public boolean nullSavedAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null) {
            if (savedInstanceState.getString("adapter") != null) {
                MiracleLoadableAdapter adapter = (MiracleLoadableAdapter)
                        getMiracleApp().getLargeDataStorage().getLargeData(savedInstanceState.getString("adapter"));
                savedInstanceState.remove(savedInstanceState.getString("adapter"));
                if (adapter != null) {
                    adapter.setDetached(true);
                    setAdapter(adapter);
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
                if (object instanceof MiracleLoadableAdapter) {
                    MiracleLoadableAdapter loadableAdapter = (MiracleLoadableAdapter) object;
                    if (loadableAdapter.hasData()) {
                        LargeDataStorage largeDataStorage = getMiracleApp().getLargeDataStorage();
                        outState.putString("adapter", largeDataStorage.storeLargeData(loadableAdapter, largeDataStorage.createUniqueKey()));
                    }
                }
            }
        }
        super.onSaveInstanceState(outState);
    }
}
