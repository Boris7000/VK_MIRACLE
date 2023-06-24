package com.vkontakte.miracle.adapter.catalog.holders;

import static com.miracle.engine.util.ImageUtil.drawableFromBitmap;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_LEFT;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.util.DimensionsUtil;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.model.catalog.CatalogLink;
import com.vkontakte.miracle.model.catalog.fields.Image;

public class CatalogCategoryViewHolder extends CatalogClickableViewHolder {

    private CatalogLink catalogLink;
    private final ExtendedMaterialButton button;
    private final Target target = new ATarget() {
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            button.setIcon(placeHolderDrawable, ICON_POS_LEFT);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            button.setIcon(drawableFromBitmap(itemView.getResources(),bitmap), ICON_POS_LEFT);
        }
    };

    public CatalogCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        button = (ExtendedMaterialButton) itemView;
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogLink = (CatalogLink) itemDataHolder;
        button.setText(catalogLink.getTitle());

        int size = (int) DimensionsUtil.dpToPx(itemView.getContext(), 28);

        Image image = getOptimalSize(catalogLink.getImages(), size, size);

        Picasso.get().cancelRequest(target);

        if(image!=null) {
            Picasso.get().load(image.getUrl())
                    .placeholder(R.drawable.catalog_category_icon_placeholder).into(target);
        }
    }

    @Override
    public void onClick(View v) {
        hardResolveVKURL(catalogLink.getUrl(), getContext());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogCategoryViewHolder(
                    inflater.inflate(R.layout.catalog_category, viewGroup, false));
        }
    }
}
