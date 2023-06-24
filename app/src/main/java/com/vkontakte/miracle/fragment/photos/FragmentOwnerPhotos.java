package com.vkontakte.miracle.fragment.photos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.PhotoAllAdapter;

public class FragmentOwnerPhotos extends BaseRecyclerFragment {

    private String ownerId;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new PhotoAllAdapter(ownerId);
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.photos;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("ownerId");
        if (key != null) {
            ownerId = key;
            savedInstanceState.remove("ownerId");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("ownerId");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(ownerId !=null){
            outState.putString("ownerId", ownerId);
        }
        super.onSaveInstanceState(outState);
    }

}
