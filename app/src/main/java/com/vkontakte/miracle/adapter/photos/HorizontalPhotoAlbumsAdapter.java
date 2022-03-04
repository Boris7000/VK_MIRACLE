package com.vkontakte.miracle.adapter.photos;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.photos.holders.PhotoAlbumViewHolder;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;

import java.util.ArrayList;

public class HorizontalPhotoAlbumsAdapter extends RecyclerView.Adapter<PhotoAlbumViewHolder> {

    private final ArrayList<PhotoAlbumItem> photoAlbumItems;
    private final LayoutInflater inflater;
    public HorizontalPhotoAlbumsAdapter(ArrayList<PhotoAlbumItem> photoAlbumItems, LayoutInflater inflater){
        this.photoAlbumItems = photoAlbumItems;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public PhotoAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (PhotoAlbumViewHolder) new PhotoAlbumViewHolder.Fabric().create(inflater,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAlbumViewHolder holder, int position) {
        holder.bind(photoAlbumItems.get(position));
    }

    @Override
    public int getItemCount() {
        return photoAlbumItems.size();
    }

}
