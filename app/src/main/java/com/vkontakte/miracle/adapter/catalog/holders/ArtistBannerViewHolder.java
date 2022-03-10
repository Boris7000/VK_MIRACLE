package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDisplayWidth;
import static com.vkontakte.miracle.engine.util.ImageUtil.fastBlur;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;

import java.util.Map;

public class ArtistBannerViewHolder extends MiracleViewHolder {

    private final ImageView photo;
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                Bitmap blurBitmap;

                @Override
                public Boolean inBackground() {
                    blurBitmap = fastBlur(bitmap, 0.2f, 4);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    setBitmap(photo, getMiracleActivity(), blurBitmap);
                }
            }.start();
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    public ArtistBannerViewHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
        ArtistItem artistItem = (ArtistItem) catalogBlock.getItems().get(0);

        ArrayMap<Integer,String> sizesMap = artistItem.getPhotoSizes();
        int minDifference = 100000;
        int itemViewWidth = getDisplayWidth(itemView.getContext());
        String imgUrl = "";
        for (Map.Entry<Integer,String> entry:sizesMap.entrySet()){
            int difference = Math.abs(itemViewWidth-entry.getKey());
            if(difference<minDifference){
                minDifference = difference;
                imgUrl = entry.getValue();
            }
        }

        Picasso.get().cancelRequest(photo);
        if(!imgUrl.isEmpty()) {
            if(artistItem.isAlbumCover()){
                Picasso.get().load(imgUrl).into(target);
            } else {
                Picasso.get().load(imgUrl).into(photo);
            }

        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ArtistBannerViewHolder(inflater.inflate(R.layout.view_artist_item_banner, viewGroup, false));
        }
    }

}
