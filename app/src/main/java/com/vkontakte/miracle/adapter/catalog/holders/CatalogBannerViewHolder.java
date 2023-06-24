package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.adapter.catalog.holders.actions.CatalogActionItemActions.resolveOpenSection;
import static com.vkontakte.miracle.adapter.catalog.holders.actions.CatalogActionItemActions.resolveOpenUrl;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.model.catalog.CatalogBanner;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.Image;

import java.util.ArrayList;

public class CatalogBannerViewHolder extends MiracleViewHolder {

    private final ImageView photo;
    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private int height = 0;
    private int width = 0;

    private final ATarget target = new ATarget() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            int x = 0;
            int y = 0;
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();

            float scale = (float)bHeight/(float)height;

            int scaledWidth = (int) (width*scale);

            if (bWidth>scaledWidth){
                x = bitmap.getWidth()-scaledWidth;
                bWidth = scaledWidth;
            }

            if(bHeight>height){
                bHeight = height;
            }

            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, x, y, bWidth, bHeight);
            setBitmap(photo, photo.getContext(), bitmap1);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            photo.setImageDrawable(null);
        }
    };

    public CatalogBannerViewHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        CatalogBanner catalogBanner = (CatalogBanner) itemDataHolder;

        if(width==0||height==0){
            photo.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    width = photo.getWidth();
                    height = photo.getHeight();
                    photo.removeOnLayoutChangeListener(this);
                    updateTarget(catalogBanner.getImages());
                }
            });
        } else {
            updateTarget(catalogBanner.getImages());
        }

        title.setText(catalogBanner.getTitle());
        subtitle.setText(catalogBanner.getText());
        subtitle2.setText(catalogBanner.getSubtext());

        if(catalogBanner.getClickAction()!=null){
            itemView.setOnClickListener(view -> {
                CatalogAction catalogAction = catalogBanner.getClickAction();
                switch (catalogAction.getType()) {
                    case "open_url": {
                        resolveOpenUrl(catalogAction, getContext());
                        break;
                    }
                    case "open_section": {
                        resolveOpenSection(catalogAction, getContext());
                        break;
                    }
                }
            });
        }
    }

    private void updateTarget( ArrayList<Image> images){
        Image image = getOptimalSize(images, width, height);
        Picasso.get().cancelRequest(target);
        if(image!=null) {
            Picasso.get().load(image.getUrl()).into(target);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogBannerViewHolder(
                    inflater.inflate(R.layout.catalog_banner_vertical, viewGroup, false));
        }
    }

    public static class FabricHorizontal implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogBannerViewHolder(
                    inflater.inflate(R.layout.catalog_banner_horizontal, viewGroup, false));
        }
    }
}
