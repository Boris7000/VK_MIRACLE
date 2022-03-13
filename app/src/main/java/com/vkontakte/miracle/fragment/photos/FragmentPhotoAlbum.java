package com.vkontakte.miracle.fragment.photos;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.PhotoAlbumAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;

public class FragmentPhotoAlbum extends SimpleMiracleFragment {

    private PhotoAlbumItem photoAlbumItem;

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
            String key = savedInstanceState.getString("photoAlbumItem");
            if(key!=null){
                photoAlbumItem = (PhotoAlbumItem) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("photoAlbumItem");
            }
        } else {
            Bundle arguments = getArguments();
            if(arguments!=null&&!arguments.isEmpty()){
                String key = arguments.getString("photoAlbumItem");
                if(key!=null){
                    photoAlbumItem = (PhotoAlbumItem) LargeDataStorage.get().getLargeData(key);
                    arguments.remove("photoAlbumItem");
                }
            }
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), ()-> setAdapter(new PhotoAlbumAdapter(photoAlbumItem)));
        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new PhotoAlbumAdapter(photoAlbumItem));
        }

        setTitleText(photoAlbumItem.getTitle());

        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(photoAlbumItem!=null){
            outState.putString("photoAlbumItem", LargeDataStorage.get().storeLargeData(photoAlbumItem));
        }
        super.onSaveInstanceState(outState);
    }
}