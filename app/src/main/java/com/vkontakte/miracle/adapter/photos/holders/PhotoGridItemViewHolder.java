package com.vkontakte.miracle.adapter.photos.holders;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridItem;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridPosition;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoViewerDialog;
import com.vkontakte.miracle.fragment.photos.PhotoViewerItem;
import com.vkontakte.miracle.model.photos.fields.Size;

import java.util.ArrayList;

public class PhotoGridItemViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private Size size;

    public PhotoGridItemViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        PhotoGridItem photoGridItem = (PhotoGridItem) itemDataHolder;
        MediaItem mediaItem = photoGridItem.mediaItem;
        PhotoGridPosition photoGridPosition = photoGridItem.gridPosition;
        Size newSize = mediaItem.getSizeForWidth(photoGridPosition.sizeX, false);

        if(newSize==null){
            size = null;
            Picasso.get().cancelRequest(imageView);
            imageView.setImageDrawable(null);
        } else {
            if(size == null || !size.getUrl().equals(newSize.getUrl())){
                size = newSize;
                Picasso.get().cancelRequest(imageView);
                Picasso.get().load(newSize.getUrl()).into(imageView);
            }
        }
    }

    public Drawable getDrawable(){
        return imageView.getDrawable();
    }

    /*
    public static void resolveItemClick(){
        ArrayList<PhotoViewerItem> photoViewerItems = new ArrayList<>();
        for(int j=0; j<buffer.size(); j++){
            RecyclerView.ViewHolder viewHolder = buffer.get(j);
            if(viewHolder instanceof PhotoGridItemViewHolder){
                PhotoGridItem photoGridItem = photoGridItems.get(j);
                PhotoGridPosition gridPosition = photoGridItem.getGridPosition();
                PhotoGridItemViewHolder wrappedPhotoGridItemViewHolder = (PhotoGridItemViewHolder) viewHolder;
                View child = wrappedPhotoGridItemViewHolder.itemView;
                int[]location = new int[2];
                child.getLocationInWindow(location);
                int rawX = location[0];
                int rawY = location[1];
                Drawable drawable = wrappedPhotoGridItemViewHolder.getDrawable();
                Drawable drwNewCopy = drawable==null?null:drawable
                        .getConstantState().newDrawable().mutate();
                photoViewerItems.add(new PhotoViewerItem(photoGridItem.mediaItem, drwNewCopy,
                        rawX, rawY, gridPosition.sizeX,gridPosition.sizeY));
            }
        }
        FragmentPhotoViewerDialog.PhotoViewerData photoViewerData =
                new FragmentPhotoViewerDialog.PhotoViewerData(photoViewerItems, finalI);
        new FragmentPhotoViewerDialog(photoViewerData).show(((AppCompatActivity)getContext())
                .getSupportFragmentManager(),"");
    }
     */

    public static class Fabric implements ViewHolderFabric {
        @Override
        public RecyclerView.ViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PhotoGridItemViewHolder(inflater.inflate(R.layout.view_photo_grid_item, viewGroup, false));
        }
    }
}
