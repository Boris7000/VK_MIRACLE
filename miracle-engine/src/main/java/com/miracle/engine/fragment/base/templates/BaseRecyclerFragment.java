package com.miracle.engine.fragment.base.templates;

import static com.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;
import static com.miracle.engine.adapter.AdapterStates.SATE_LOADING_ERROR;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.fragment.ScrollAndElevate;
import com.miracle.engine.fragment.base.BaseFragmentController;
import com.miracle.engine.fragment.base.IBaseFragment;
import com.miracle.engine.fragment.recycler.templates.RecyclerFragment;

public abstract class BaseRecyclerFragment extends RecyclerFragment implements IBaseFragment {

    private final BaseFragmentController<BaseRecyclerFragment> baseController =
            new BaseFragmentController<BaseRecyclerFragment>(this){};

    @Override
    public BaseFragmentController<?> getBaseFragmentController() {
        return baseController;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_base, container, false);
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getBaseFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);
        getBaseFragmentController().initViews(rootView, savedInstanceState);
        if(scrollAndElevateEnabled()) {
            ScrollAndElevate.scrollAndElevate(getRecyclerView(), getAppBarLayout());
        }
    }

    @Override
    public void onRecyclerAdapterStateChange(int state) {
        switch (state){
            case SATE_LOADING_ERROR:{
                if (getRecyclerFragmentController().adapterErrorWasThrown()) {
                    if(needChangeTitleText()) {
                        Context context = getContext();
                        if(context!=null) {
                            getBaseFragmentController().setTitleText(context.getString(R.string.error));
                        }
                    }
                }
                break;
            }
            case SATE_FIRST_LOADING_COMPLETE:{
                if (!getRecyclerFragmentController().adapterErrorWasThrown()) {
                    if(needChangeTitleText()) {
                        getBaseFragmentController().updateTitleText();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        getBaseFragmentController().expandAppBar();
    }

    @Override
    public void scrollToTop() {
        super.scrollToTop();
        getBaseFragmentController().expandAppBar();
    }

    @Override
    public boolean notTop() {
        return !getBaseFragmentController().appbarExpanded() || super.notTop();
    }

    @CallSuper
    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        getBaseFragmentController().readSavedInstance(savedInstanceState);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        getBaseFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getBaseFragmentController().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
