package com.vkontakte.miracle.fragment.wall;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.WallAdapter;

public class FragmentWall extends BaseRecyclerFragment {

    private String postId;
    private String ownerId;

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new WallAdapter(postId, ownerId);
    }

    @Override
    public String requestTitleText() {
        Context context = getContext();
        if(context!=null){
            return context.getString(R.string.wall);
        }
        return super.requestTitleText();
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("postId");
        if(key!=null){
            postId = key;
            savedInstanceState.remove("postId");
        }
        key = savedInstanceState.getString("ownerId");
        if(key!=null){
            ownerId = key;
            savedInstanceState.remove("ownerId");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("postId");
        savedInstanceState.remove("ownerId");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(postId !=null){
            outState.putString("postId", postId);
        }
        if(ownerId !=null){
            outState.putString("ownerId", ownerId);
        }
        super.onSaveInstanceState(outState);
    }


}
