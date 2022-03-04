package com.vkontakte.miracle.fragment.wall;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.GroupAdapter;
import com.vkontakte.miracle.adapter.wall.ProfileAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

public class FragmentGroup  extends SimpleMiracleFragment {

    private View rootView;
    private MiracleActivity miracleActivity;
    private MiracleApp miracleApp;
    private GroupItem groupItem;

    public void setGroupItem(GroupItem groupItem) {
        this.groupItem = groupItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        miracleActivity = getMiracleActivity();
        miracleApp = getMiracleApp();

        rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setBackClick(rootView.findViewById(R.id.backButton));
        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(),miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("groupItem");
            if(key!=null){
                LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
                groupItem = (GroupItem) largeDataStorage.getLargeData(key);
                savedInstanceState.remove("groupItem");
            }
        }

        if(groupItem !=null){
            setTitleText(groupItem.getName());
            setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout),
                    ()-> setAdapter(new GroupAdapter(groupItem)));
            if(nullSavedAdapter(savedInstanceState)){
                setAdapter(new GroupAdapter(groupItem));
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(groupItem !=null){
            LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
            String key = largeDataStorage.createUniqueKey();
            largeDataStorage.storeLargeData(groupItem,key);
            outState.putString("groupItem", key);
        }

        super.onSaveInstanceState(outState);
    }
}