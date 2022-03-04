package com.vkontakte.miracle.adapter.photos.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.HorizontalPhotoAlbumsAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;

import java.util.ArrayList;

import static com.vkontakte.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;

public class HorizontalListPhotoAlbumViewHolder extends MiracleViewHolder {

    private final TextView title;
    private TextView badge;
    private final TextView button;
    private final ViewStub badgeStub;
    private RecyclerView recyclerView;

    public HorizontalListPhotoAlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        button = itemView.findViewById(R.id.button);
        badgeStub = itemView.findViewById(R.id.badgeStub);
        recyclerView = itemView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(getHorizontalLayoutManager(getMiracleActivity()));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        HorizontalListPhotoAlbumItem horizontalListPhotoAlbumItem = (HorizontalListPhotoAlbumItem) itemDataHolder;
        ArrayList<PhotoAlbumItem> photoAlbumItems = horizontalListPhotoAlbumItem.getPhotoAlbumItems();
        title.setText(itemView.getContext().getString(R.string.albums));

        if(badge==null) {
            if(badgeStub!=null) {
                badge = (TextView) badgeStub.inflate();
            } else {
                badge = itemView.findViewById(R.id.badge);
            }
        }

        badge.setText(String.valueOf(photoAlbumItems.size()));

        recyclerView.setAdapter(new HorizontalPhotoAlbumsAdapter(photoAlbumItems,LayoutInflater.from(recyclerView.getContext())));

        if(photoAlbumItems.size()<horizontalListPhotoAlbumItem.getCount()){
            if(button.getVisibility()!=View.VISIBLE) {
                button.setVisibility(View.VISIBLE);
            }
            button.setText("Показать все");
        } else {
            if(button.getVisibility()!=View.GONE) {
                button.setVisibility(View.GONE);
            }
        }

    }
    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new HorizontalListPhotoAlbumViewHolder(inflater.inflate(R.layout.view_horizontal_list, viewGroup, false));
        }
    }
}