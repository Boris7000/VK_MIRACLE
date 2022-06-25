package com.vkontakte.miracle.fragment.wall;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.ProfileAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.users.ProfileItem;

public class FragmentProfile extends SimpleMiracleFragment {

    private ProfileItem profileItem;

    public void setProfileItem(ProfileItem profileItem) {
        this.profileItem = profileItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();
        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), getMiracleActivity());
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("profileItem");
            if(key!=null){
                profileItem = (ProfileItem) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("profileItem");
            }
        }

        if(profileItem !=null){
            setTitleText(profileItem.getFullName());
            if(nullSavedAdapter(savedInstanceState)){
                setAdapter(new ProfileAdapter(profileItem));
            }
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(profileItem !=null){
            outState.putString("profileItem", LargeDataStorage.get().storeLargeData(profileItem));
        }

        super.onSaveInstanceState(outState);
    }
}
