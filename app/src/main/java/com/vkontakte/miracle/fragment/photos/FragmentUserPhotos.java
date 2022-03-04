package com.vkontakte.miracle.fragment.photos;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.PhotoAllAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

public class FragmentUserPhotos extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setBackClick(rootView.findViewById(R.id.backButton));
        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));
        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), ()-> setAdapter(new PhotoAllAdapter()));
        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new PhotoAllAdapter());
        }

        setTitleText(miracleActivity.getString(R.string.photos));

        return rootView;
    }

}
