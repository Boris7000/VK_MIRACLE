package com.vkontakte.miracle.adapter.photos.holders;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToPhotoAlbum;
import static com.vkontakte.miracle.engine.util.StringsUtil.getPhotosDeclensions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.photos.fields.Size;

public class PhotoAlbumViewHolder extends MiracleViewHolder {

    private final TextView title;
    private final TextView subtitle;
    private final ImageView image;

    public PhotoAlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        image = itemView.findViewById(R.id.photo);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        PhotoAlbumItem photoAlbumItem = (PhotoAlbumItem) itemDataHolder;
        title.setText(photoAlbumItem.getTitle());
        subtitle.setText(getPhotosDeclensions(itemView.getContext(), photoAlbumItem.getSize()));

        ArrayMap<String, Size> sizes = photoAlbumItem.getSizes();
        Size size = sizes.get("x");
        if(size!=null) {
            Picasso.get().load(size.getUrl()).into(image);
        }else (image).setImageDrawable(null);

        itemView.setOnClickListener(view -> goToPhotoAlbum(photoAlbumItem, getMiracleActivity()));

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PhotoAlbumViewHolder(inflater.inflate(R.layout.view_horizontal_list_item, viewGroup, false));
        }
    }

}
