package com.miracle.engine.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.R;
import com.miracle.engine.fragment.MiracleFragmentController;
import com.miracle.engine.fragment.ScrollAndElevate;
import com.miracle.engine.util.NavigationUtil;

public abstract class BaseFragmentController<T extends Fragment & IBaseFragment> extends MiracleFragmentController<T> {

    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private TextView title;
    private boolean canBackClick;

    protected BaseFragmentController(T fragment) {
        super(fragment);
    }

    @Override
    public void findViews(@NonNull View rootView){
        appBarLayout = rootView.findViewById(R.id.appbarLayout);
        if(appBarLayout!=null){
            toolBar = appBarLayout.findViewById(R.id.toolbar);
            title = appBarLayout.findViewById(R.id.title);
        }
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){

        T fragment = getFragment();

        if(fragment.needChangeTitleText()) {
            updateTitleText();
        }

        setupOptionsMenu();
    }

    public void updateTitleText(){
        T fragment = getFragment();
        int resId = fragment.requestTitleTextResId();
        if(resId!=0){
            setTitleTextResId(resId);
        } else {
            setTitleText(fragment.requestTitleText());
        }
    }

    public void setTitleText(String titleText){
        if(titleText==null){
            titleText = "";
        }
        if(title!=null) {
            title.setText(titleText);
        } else {
            if(toolBar!=null){
                toolBar.setTitle(titleText);
            }
        }
    }

    public void setTitleTextResId(@StringRes int resId){
        if(resId!=0){
            Context context = getContext();
            if(context!=null){
                setTitleText(context.getString(resId));
            }
        }
    }

    public void setupOptionsMenu() {
        if(toolBar!=null){
            T fragment = getFragment();
            toolBar.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    fragment.onCreateOptionsMenu(menu, menuInflater);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    return fragment.onOptionsItemSelected(menuItem);
                }

                @Override
                public void onPrepareMenu(@NonNull Menu menu) {
                    fragment.onPrepareOptionsMenu(menu);
                }
            }, fragment, Lifecycle.State.RESUMED);

            if(canBackClick) {
                toolBar.setNavigationIcon(R.drawable.ic_back_28);
                toolBar.setNavigationContentDescription(R.string.back);
                toolBar.setNavigationOnClickListener(v -> NavigationUtil.back(getContext()));
            }
        }
    }

    public boolean appbarExpanded(){
        if(appBarLayout!=null){
            return appBarLayout.getHeight()-appBarLayout.getBottom()==0;
        }
        return true;
    }

    public void expandAppBar(){
        if (appBarLayout!=null) {
            appBarLayout.setExpanded(true);
            if(getFragment().scrollAndElevateEnabled()) {
                ScrollAndElevate.appBarLand(appBarLayout);
            }
        }
    }

    boolean canBackClick(){
        return canBackClick;
    }

    void setCanBackClick(boolean canBackClick){
        this.canBackClick = canBackClick;
    }

    /////////////////////////////////////////////////////////////////

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }

    public Toolbar getToolBar() {
        return toolBar;
    }

    public TextView getTitle(){
        return title;
    }

    /////////////////////////////////////////////////////////////////

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        canBackClick = savedInstanceState.getBoolean("canBackClick", false);
        savedInstanceState.remove("canBackClick");
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        savedInstanceState.remove("canBackClick");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("canBackClick", canBackClick);
    }

}
