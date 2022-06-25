package com.vkontakte.miracle.fragment.base;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.FeedAdapter;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

public class FragmentFeed  extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), getMiracleActivity());
        setProgressBar(rootView.findViewById(R.id.progressCircle));
        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new FeedAdapter());
        }
        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        return rootView;
    }


    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentFeed();
        }
    }
}
