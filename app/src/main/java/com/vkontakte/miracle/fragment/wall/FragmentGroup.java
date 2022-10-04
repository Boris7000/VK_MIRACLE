package com.vkontakte.miracle.fragment.wall;

import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.GroupAdapter;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;
import com.vkontakte.miracle.model.groups.GroupItem;

public class FragmentGroup extends SideRecyclerFragment {

    private String groupId;
    private String groupName;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_dark, container, false);
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new GroupAdapter(groupId);
    }

    @Override
    public String requestTitleText() {
        if(groupName!=null){
            return groupName;
        }
        return super.requestTitleText();
    }

    @CallSuper
    @Override
    public void onRecyclerAdapterStateChange(int state) {
        if (state == SATE_FIRST_LOADING_COMPLETE) {
            if(groupName==null||groupName.isEmpty()) {
                RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
                if(adapter instanceof GroupAdapter){
                    GroupAdapter groupAdapter = (GroupAdapter) adapter;
                    GroupItem groupItem = groupAdapter.getGroupItem();
                    if(groupItem!=null){
                        groupName = groupItem.getName();
                    }
                }
            }
        }
        super.onRecyclerAdapterStateChange(state);
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("groupId");
        if(key!=null){
            groupId = key;
            savedInstanceState.remove("groupId");
        }
        key = savedInstanceState.getString("groupName");
        if(key!=null){
            groupName = key;
            savedInstanceState.remove("groupName");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("groupId");
        savedInstanceState.remove("groupName");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(groupId !=null){
            outState.putString("groupId", groupId);
        }
        if(groupName !=null){
            outState.putString("groupName", groupName);
        }
        super.onSaveInstanceState(outState);
    }
}