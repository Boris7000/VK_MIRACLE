package com.vkontakte.miracle.fragment.base;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.ConversationsAdapter;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

public class FragmentDialogs extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), getMiracleActivity());
        setProgressBar(rootView.findViewById(R.id.progressCircle));
        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), ()-> setAdapter(new ConversationsAdapter()));
        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new ConversationsAdapter());
        }

        return rootView;
    }


    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentDialogs();
        }
    }
}
