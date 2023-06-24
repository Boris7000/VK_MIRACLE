package com.miracle.engine.fragment.refresh;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.engine.R;
import com.miracle.engine.fragment.MiracleFragmentController;
import com.miracle.engine.util.FragmentUtil;

public abstract class RefreshFragmentController<T extends Fragment & IRefreshFragment> extends MiracleFragmentController<T> {

    private SwipeRefreshLayout swipeRefreshLayout;

    protected RefreshFragmentController(T fragment) {
        super(fragment);
    }

    public void findViews(@NonNull View rootView){
        swipeRefreshLayout = rootView.findViewById(R.id.refreshLayout);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        if(swipeRefreshLayout!=null) {
            FragmentUtil.setDefaultSwipeRefreshLayoutStyle(
                    swipeRefreshLayout, swipeRefreshLayout.getContext());
            swipeRefreshLayout.setOnRefreshListener(getFragment().requestOnRefreshListener());
        }
    }

    @CallSuper
    public void show(){
        if (swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////

    public final SwipeRefreshLayout getSwipeRefreshLayout(){
        return swipeRefreshLayout;
    }

}
