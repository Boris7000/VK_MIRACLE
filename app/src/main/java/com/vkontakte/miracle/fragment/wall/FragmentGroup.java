package com.vkontakte.miracle.fragment.wall;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.GroupAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.groups.GroupItem;

public class FragmentGroup  extends SimpleMiracleFragment {

    private GroupItem groupItem;

    public void setGroupItem(GroupItem groupItem) {
        this.groupItem = groupItem;
    }

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

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("groupItem");
            if(key!=null){
                groupItem = (GroupItem) LargeDataStorage.get().getLargeData(key);
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
            outState.putString("groupItem", LargeDataStorage.get().storeLargeData(groupItem));
        }
        super.onSaveInstanceState(outState);
    }
}