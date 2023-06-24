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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;
import com.vkontakte.miracle.engine.view.zoomableImageView.ZoomableImageView2;
import com.vkontakte.miracle.model.DataItemWrap;

public class FragmentPhotoNested extends MiracleFragment {

    private PhotoDialogItem photoDialogItem;
    private ZoomableImageView2 image;
    private ProgressBar progressBar;

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            show();
            image.setZoomable(true);
            setBitmap(image, MainApp.getInstance(), bitmap);

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
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_photo_nested, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        progressBar = rootView.findViewById(R.id.progressCircle);
        image = rootView.findViewById(R.id.photo);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        FragmentPhotoViewerDialog parentFragment = (FragmentPhotoViewerDialog) getParentFragment();

        if(parentFragment!=null) {
            image.setViewPager2(parentFragment.getViewPager());
            image.setOnPhotoActionListener(parentFragment);
        }

        if(photoDialogItem.getPreview()!=null) {
            image.setZoomable(false);
            image.setImageDrawable(photoDialogItem.getPreview());
            show();
        }

        createTarget(photoDialogItem);
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


    private void createTarget(PhotoDialogItem photoDialogItem){
        Picasso.get().cancelRequest(target);
        int displayWidth = getWindowWidth(MainApp.getInstance());
        ItemDataHolder mediaItemData = photoDialogItem.getItemDataHolder();
        DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>)mediaItemData;
        MediaItem mediaItem = (MediaItem) dataItemWrap.getItem();
        String url = mediaItem.getSizeForWidth(displayWidth, false).getUrl();
        if(url!=null){
            Picasso.get().load(url).into(target);
        }
    }

    public void setPhotoDialogItem(PhotoDialogItem photoDialogItem) {
        this.photoDialogItem = photoDialogItem;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("photoDialogItem");
        if (key!= null) {
            PhotoDialogItem photoDialogItem = (PhotoDialogItem) LargeDataStorage.get().getLargeData(key);
            savedInstanceState.remove("photoDialogItem");
            if (photoDialogItem != null) {
                setPhotoDialogItem(photoDialogItem);
            }
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        String key = savedInstanceState.getString("photoDialogItem");
        if (key != null) {
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("photoDialogItem");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoDialogItem !=null) {
            outState.putString("photoDialogItem", LargeDataStorage.get().storeLargeData(photoDialogItem));
        }
    }

    public static class Fabric implements FragmentFabric {

        private final PhotoDialogItem photoDialogItem;

        public Fabric(PhotoDialogItem photoDialogItem){
            this.photoDialogItem = photoDialogItem;
        }

        @NonNull
        @Override
        public Fragment createFragment() {
            FragmentPhotoNested fragmentPhotoNested = new FragmentPhotoNested();
            fragmentPhotoNested.setPhotoDialogItem(photoDialogItem);
            return fragmentPhotoNested;
        }
    }

}
