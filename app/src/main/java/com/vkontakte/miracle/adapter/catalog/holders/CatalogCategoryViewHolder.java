package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;
import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;
import static com.vkontakte.miracle.engine.util.ImageUtil.drawableFromBitmap;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.button.TextViewButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.CatalogLink;
import com.vkontakte.miracle.model.catalog.fields.Image;

public class CatalogCategoryViewHolder extends MiracleViewHolder {

    private CatalogLink catalogLink;
    private final TextViewButton textViewButton;
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            textViewButton.setIconStart(drawableFromBitmap(itemView.getResources(),bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    public CatalogCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewButton = (TextViewButton) itemView;
        itemView.setOnClickListener(view -> hardResolveVKURL(catalogLink.getUrl(), getMiracleActivity()));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogLink = (CatalogLink) itemDataHolder;
        textViewButton.setText(catalogLink.getTitle());

        Log.d("iejifiejfef",catalogLink.getUrl());

        int size = (int) dpToPx(itemView.getContext(), 28);

        Image image = getOptimalSize(catalogLink.getImages(), size, size);

        Picasso.get().cancelRequest(target);

        if(image!=null) {
            Picasso.get().load(image.getUrl()).into(target);
        } else {
            textViewButton.setIconStart(null);
        }
    }


    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogCategoryViewHolder(inflater.inflate(R.layout.catalog_category, viewGroup, false));
        }
    }

}
