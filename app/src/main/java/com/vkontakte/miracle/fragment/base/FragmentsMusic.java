package com.vkontakte.miracle.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.TabsMiracleFragment;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

public class FragmentsMusic  extends TabsMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setViewPager(rootView.findViewById(R.id.viewPager));
        setTabLayout(rootView.findViewById(R.id.tabLayout));
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        ProfileItem profileItem = miracleActivity.getUserItem();

        if(nullSavedAdapter(savedInstanceState)) {
            loadCatalogSections(Catalog.getAudio(profileItem.getId(), profileItem.getAccessToken()));
        }

        return rootView;
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentsMusic();
        }
    }
}
