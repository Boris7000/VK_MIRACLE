package com.vkontakte.miracle.engine.fragment.recycler;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_LOADING_ERROR;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_REFRESH_CANCELED;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.MiracleFragmentController;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.engine.util.LargeDataStorage;

public abstract class RecyclerFragmentController extends MiracleFragmentController {

    private final IRecyclerFragment recyclerFragment;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private boolean adapterErrorWasThrown = false;

    protected RecyclerFragmentController(IMiracleFragment miracleFragment) {
        super(miracleFragment);
        recyclerFragment = (IRecyclerFragment) miracleFragment;
    }

    @Override
    public void onCreateView(@NonNull View rootView, Bundle savedInstanceState){
        if(recyclerFragment.saveRecyclerAdapterSate()) {
            if(!hasSavedRecyclerAdapter(savedInstanceState)){
                if(recyclerFragment.autoSetRecyclerAdapter()){
                    setRecyclerAdapter(recyclerFragment.onCreateRecyclerAdapter());
                }
            }
        } else {
            if(recyclerFragment.autoSetRecyclerAdapter()){
                setRecyclerAdapter(recyclerFragment.onCreateRecyclerAdapter());
            }
        }
    }

    @Override
    public void findViews(@NonNull View rootView){
        recyclerView = rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.refreshLayout);
        progressBar = rootView.findViewById(R.id.progressCircle);
    }

    @Override
    public void initViews(){
        if(recyclerView!=null) {
            recyclerView.setLayoutManager(
                    getVerticalLayoutManager(
                            recyclerView.getContext(),
                            recyclerFragment.reverseRecyclerAdapter()));

            recyclerView.setHasFixedSize(true);
        }
        if(swipeRefreshLayout!=null){
            FragmentUtil.setDefaultSwipeRefreshLayoutStyle(
                    swipeRefreshLayout, getMiracleFragment().getMiracleActivity());
            swipeRefreshLayout.setOnRefreshListener(this::reloadRecyclerAdapter);
        }
    }

    public final RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public final SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public final ProgressBar getProgressBar() {
        return progressBar;
    }

    @CallSuper
    public void setRecyclerAdapter(RecyclerView.Adapter<?> adapter){
        if(adapter!=null){
            if(adapter instanceof MiracleAdapter) {
                MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                miracleAdapter.iniFromFragment(getMiracleFragment());
                miracleAdapter.setRecyclerView(recyclerView);
                miracleAdapter.setStateChangeListener(this::onAdapterStateChange);
                if (!miracleAdapter.initialized()) {
                    miracleAdapter.ini();
                }
                RecyclerView.Adapter<?> previousAdapter = recyclerView.getAdapter();
                if (miracleAdapter.loaded()) {
                    if (previousAdapter == null) {
                        recyclerView.setAdapter(miracleAdapter);
                        show(true);
                    } else {
                        hide(true, new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                recyclerView.setAdapter(miracleAdapter);
                                show(true);
                            }
                            @Override
                            public void onAnimationStart(Animator animator) {}
                            @Override
                            public void onAnimationCancel(Animator animator) {}
                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        });
                    }
                } else {
                    hide();
                    recyclerView.setAdapter(miracleAdapter);
                    miracleAdapter.load();
                }
            } else {
                recyclerView.setAdapter(adapter);
                show(true);
            }
        } else {
            hide();
            recyclerView.setAdapter(null);
        }
    }

    @CallSuper
    public void reloadRecyclerAdapter(){
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter != null) {
            if(adapter instanceof MiracleAdapter) {
                MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                if(miracleAdapter.initialized()&&miracleAdapter.loaded()) {
                    if (miracleAdapter instanceof MiracleAsyncLoadAdapter) {
                        MiracleAsyncLoadAdapter asyncLoadAdapter = (MiracleAsyncLoadAdapter) miracleAdapter;
                        if (!asyncLoadAdapter.loading()) {
                            asyncLoadAdapter.setProhibitScrollLoad(true);
                            hide(true, new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    asyncLoadAdapter.reload();
                                    asyncLoadAdapter.setProhibitScrollLoad(false);
                                }
                                @Override
                                public void onAnimationStart(Animator animator) {}
                                @Override
                                public void onAnimationCancel(Animator animator) {}
                                @Override
                                public void onAnimationRepeat(Animator animator) {}
                            });
                            return;
                        }
                    } else {
                        if (miracleAdapter instanceof MiracleInstantLoadAdapter) {
                            MiracleInstantLoadAdapter instantLoadAdapter = (MiracleInstantLoadAdapter) miracleAdapter;
                            instantLoadAdapter.setProhibitScrollLoad(true);
                            hide(true, new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    instantLoadAdapter.reload();
                                    instantLoadAdapter.setProhibitScrollLoad(false);
                                }
                                @Override
                                public void onAnimationStart(Animator animator) {}
                                @Override
                                public void onAnimationCancel(Animator animator) {}
                                @Override
                                public void onAnimationRepeat(Animator animator) {}
                            });
                        }
                        return;
                    }
                }
            }
        }
        onAdapterStateChange(SATE_REFRESH_CANCELED);
    }

    public final boolean hasSavedRecyclerAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()) {
            String key = savedInstanceState.getString("Adapter");
            if (key != null) {
                MiracleAdapter miracleAdapter = (MiracleAdapter) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("Adapter");
                if (miracleAdapter != null) {
                    //miracleAdapter.setSavedStateKey("");
                    setRecyclerAdapter(miracleAdapter);
                    return true;
                }
            }
        }
        return false;
    }

    @CallSuper
    public void onAdapterStateChange(int state){
        switch (state){
            case SATE_LOADING_ERROR:{
                adapterErrorWasThrown = true;
                show(true);
                break;
            }
            case SATE_FIRST_LOADING_COMPLETE:{
                adapterErrorWasThrown = false;
                show(true);
                break;
            }
            case SATE_REFRESH_CANCELED:{
                if(swipeRefreshLayout!=null){
                    swipeRefreshLayout.setRefreshing(false);
                }
                break;
            }
        }
        recyclerFragment.onRecyclerAdapterStateChange(state);
    }

    @CallSuper
    public void show(boolean animate, Animator.AnimatorListener listener){
        recyclerFragment.onShow();
        if(recyclerView!=null) {
            recyclerView.animate().cancel();
            if (animate) {
                recyclerView.animate().alpha(1f).setDuration(500).setListener(listener).start();
            } else {
                recyclerView.setAlpha(1f);
            }
        }
        if (swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(false);
        }
        if(progressBar!=null) {
            progressBar.setVisibility(GONE);
        }
    }

    @CallSuper
    public void show(boolean animate){
        show(animate, null);
    }

    @CallSuper
    public void show() {
        show(false);
    }

    @CallSuper
    public void hide(boolean animate, Animator.AnimatorListener listener){
        recyclerFragment.onHide();
        if(recyclerView!=null) {
            recyclerView.animate().cancel();
            if (animate) {
                recyclerView.animate().alpha(0f).setDuration(200).setListener(listener).start();
            } else {
                recyclerView.setAlpha(0f);
            }
        }
        if(progressBar!=null) {
            if (swipeRefreshLayout != null) {
                if(!swipeRefreshLayout.isRefreshing()){
                    progressBar.setVisibility(VISIBLE);
                }
            } else {
                progressBar.setVisibility(VISIBLE);
            }
        }
    }

    @CallSuper
    public void hide(boolean animate) {
        hide(animate, null);
    }

    @CallSuper
    public void hide() {
        hide(false);
    }

    public boolean adapterErrorWasThrown() {
        return adapterErrorWasThrown;
    }

    @CallSuper
    public boolean notTop(){
        if(recyclerView!=null) {
            if(recyclerFragment.reverseRecyclerAdapter()){
                return recyclerView.canScrollVertically(1);
            } else {
                return recyclerView.canScrollVertically(-1);
            }

        }
        return false;
    }

    @CallSuper
    public void scrollToTop() {
        if(recyclerView!=null) {
            if(recyclerFragment.reverseRecyclerAdapter()){
                if(recyclerView.canScrollVertically(1)) {
                    recyclerView.scrollToPosition(0);
                }
            } else {
                if(recyclerView.canScrollVertically(-1)) {
                    recyclerView.scrollToPosition(0);
                }
            }

        }
    }

    @CallSuper
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState){
        String key = savedInstanceState.getString("Adapter");
        if(key!=null){
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("Adapter");
        }
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(recyclerFragment.saveRecyclerAdapterSate()) {
            if (recyclerView != null) {
                Object object = recyclerView.getAdapter();
                if (object != null) {
                    outState.putString("Adapter", LargeDataStorage.get().storeLargeData(object));
                    //if (object instanceof MiracleAdapter) {
                        //MiracleAdapter miracleAdapter = (MiracleAdapter) object;
                        //miracleAdapter.saveState(outState);
                    //}
                }
            }
        }
    }

    @CallSuper
    public void onDestroy() {
        if(recyclerView!=null) {
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if(adapter!=null){
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    if(miracleAdapter.getMiracleFragment()==recyclerFragment) {
                        miracleAdapter.setRecyclerView(null);
                    }
                }
            }
        }
    }
}
