package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

public class FragmentCatalogFriends extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();

        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        ProfileItem profileItem = miracleActivity.getUserItem();

        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new CatalogSectionAdapter(Catalog.getFriends(profileItem.getId(), 1, profileItem.getAccessToken())));
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        setTitleText(miracleActivity.getString(R.string.friends));

        return rootView;
    }

}