package com.vkontakte.miracle.fragment.photos;

import static com.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.adapter.photos.PhotoAlbumAdapter;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;

public class FragmentPhotoAlbum extends BaseRecyclerFragment {

    private String photoAlbumId;
    private String ownerId;
    private String photoAlbumTitle;

    public void setPhotoAlbumId(String photoAlbumId) {
        this.photoAlbumId = photoAlbumId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setPhotoAlbumTitle(String photoAlbumTitle) {
        this.photoAlbumTitle = photoAlbumTitle;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new PhotoAlbumAdapter(photoAlbumId, ownerId);
    }

    @Override
    public String requestTitleText() {
        if(photoAlbumTitle!=null) {
            return photoAlbumTitle;
        }
        return super.requestTitleText();
    }

    @CallSuper
    @Override
    public void onRecyclerAdapterStateChange(int state) {
        if (state == SATE_FIRST_LOADING_COMPLETE) {
            if(photoAlbumTitle==null||photoAlbumTitle.isEmpty()) {
                RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
                if(adapter instanceof PhotoAlbumAdapter){
                    PhotoAlbumAdapter photoAlbumAdapter = (PhotoAlbumAdapter) adapter;
                    PhotoAlbumItem photoAlbumItem = photoAlbumAdapter.getPhotoAlbumItem();
                    if(photoAlbumItem!=null) {
                        photoAlbumTitle = photoAlbumAdapter.getPhotoAlbumItem().getTitle();
                    }
                }
            }
        }
        super.onRecyclerAdapterStateChange(state);
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("photoAlbumId");
        if (key!=null) {
            photoAlbumId = key;
            savedInstanceState.remove("photoAlbumId");
        }
        key = savedInstanceState.getString("ownerId");
        if (key!=null) {
            ownerId = key;
            savedInstanceState.remove("ownerId");
        }
        key = savedInstanceState.getString("photoAlbumTitle");
        if (key!=null) {
            photoAlbumTitle = key;
            savedInstanceState.remove("photoAlbumTitle");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("photoAlbumId");
        savedInstanceState.remove("ownerId");
        savedInstanceState.remove("photoAlbumTitle");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoAlbumId!=null) {
            outState.putString("photoAlbumId", photoAlbumId);
        }
        if(ownerId!=null) {
            outState.putString("ownerId", ownerId);
        }
        if(photoAlbumTitle!=null) {
            outState.putString("photoAlbumTitle", photoAlbumTitle);
        }
    }
}