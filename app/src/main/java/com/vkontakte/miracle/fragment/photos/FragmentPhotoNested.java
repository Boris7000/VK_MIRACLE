package com.vkontakte.miracle.fragment.photos;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getWindowWidth;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.view.zoomableImageView.ZoomableImageView2;

public class FragmentPhotoNested extends MiracleFragment {

    private PhotoViewerItem photoViewerItem;
    private ZoomableImageView2 image;
    private ProgressBar progressBar;

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            show();
            image.setZoomable(true);
            setBitmap(image, MiracleApp.getInstance(), bitmap);

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            //Not used because there is some delay before the drawable is set.
            /*
            if(placeHolderDrawable!=null) {
                show();
                image.setZoomable(false);
                image.setImageDrawable(placeHolderDrawable);
            }*/
        }
    };

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        FragmentPhotoViewerDialog parentFragment = (FragmentPhotoViewerDialog) getParentFragment();

        if(parentFragment!=null) {
            image.setViewPager2(parentFragment.getViewPager());
            image.setOnPhotoActionListener(parentFragment);
        }

        if(photoViewerItem.getPreview()!=null) {
            image.setZoomable(false);
            image.setImageDrawable(photoViewerItem.getPreview());
            show();
        }

        createTarget(photoViewerItem);

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_photo_nested, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        progressBar = rootView.findViewById(R.id.progressCircle);
        image = rootView.findViewById(R.id.photo);
    }

    public void show(){
        if (progressBar != null){
            progressBar.setVisibility(GONE);
        }
    }

    public void hide(){
        if(progressBar != null){
            progressBar.setVisibility(VISIBLE);
        }
    }


    private void createTarget(PhotoViewerItem photoViewerItem){
        Picasso.get().cancelRequest(target);
        int displayWidth = getWindowWidth(MiracleApp.getInstance());
        String url = photoViewerItem.getMediaItem().
                getSizeForWidth(displayWidth, false).getUrl();
        if(url!=null){
            Picasso.get().load(url).into(target);
        }
    }

    public void setPhotoViewerItem(PhotoViewerItem photoViewerItem) {
        this.photoViewerItem = photoViewerItem;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("photoViewerItem");
        if (key!= null) {
            PhotoViewerItem photoViewerItem = (PhotoViewerItem) LargeDataStorage.get().getLargeData(key);
            savedInstanceState.remove("photoViewerItem");
            if (photoViewerItem != null) {
                setPhotoViewerItem(photoViewerItem);
            }
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("photoViewerItem");
        if (key != null) {
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("photoViewerItem");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoViewerItem !=null) {
            outState.putString("photoViewerItem", LargeDataStorage.get().storeLargeData(photoViewerItem));
        }
    }

    public static class Fabric implements FragmentFabric {

        private final PhotoViewerItem photoViewerItem;

        public Fabric(PhotoViewerItem photoViewerItem1){
            this.photoViewerItem = photoViewerItem1;
        }

        @NonNull
        @Override
        public MiracleFragment createFragment() {
            FragmentPhotoNested fragmentPhotoNested = new FragmentPhotoNested();
            fragmentPhotoNested.setPhotoViewerItem(photoViewerItem);
            return fragmentPhotoNested;
        }
    }

}
