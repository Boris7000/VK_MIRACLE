package com.vkontakte.miracle.engine.fragment.base;

import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;
import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_LOADING_ERROR;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;
import com.vkontakte.miracle.engine.fragment.recycler.RecyclerFragment;

public abstract class BaseRecyclerFragment extends RecyclerFragment implements IBaseFragment {

    private final BaseFragmentController baseFragmentController = requestBaseFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getBaseFragmentController().onCreateView(rootView, savedInstanceState);

        if(scrollAndElevateEnabled()) {
            ScrollAndElevate.scrollAndElevate(getRecyclerView(), getAppBarLayout());
        }

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getBaseFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        super.initViews();
        getBaseFragmentController().initViews();
    }

    @Override
    public BaseFragmentController requestBaseFragmentController() {
        return new BaseFragmentController(this){};
    }

    @Override
    public BaseFragmentController getBaseFragmentController() {
        return baseFragmentController;
    }

    @Override
    public AppBarLayout getAppBarLayout() {
        return getBaseFragmentController().getAppBarLayout();
    }

    @Override
    public Toolbar getToolBar() {
        return getBaseFragmentController().getToolBar();
    }

    @Override
    public TextView getTitle() {
        return getBaseFragmentController().getTitle();
    }

    @Override
    public boolean scrollAndElevateEnabled() {
        return true;
    }

    @Override
    public boolean needChangeTitleText() {
        return true;
    }

    @Override
    public String requestTitleText() {
        return "";
    }

    @Override
    public void onRecyclerAdapterStateChange(int state) {
        switch (state){
            case SATE_LOADING_ERROR:{
                if (getRecyclerFragmentController().adapterErrorWasThrown()) {
                    if(needChangeTitleText()) {
                        getBaseFragmentController().setTitleText(getMiracleActivity().getString(R.string.error));
                    }
                }
                break;
            }
            case SATE_FIRST_LOADING_COMPLETE:{
                if (!getRecyclerFragmentController().adapterErrorWasThrown()) {
                    if(needChangeTitleText()) {
                        getBaseFragmentController().setTitleText(requestTitleText());
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onHide() {
        getBaseFragmentController().expandAppBar();
    }

    @Override
    public void scrollToTop() {
        super.scrollToTop();
        getBaseFragmentController().expandAppBar();
    }
}
