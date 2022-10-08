package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.catalog.CatalogBanner;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.Image;

public class CatalogBannerViewHolder extends MiracleViewHolder {

    private final ImageView photo;
    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private final int height;

    public CatalogBannerViewHolder(@NonNull View itemView) {
        super(itemView);
        height = (int) DimensionsUtil.dpToPx(itemView.getContext(),150)*4;
        photo = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogBanner catalogBanner = (CatalogBanner) itemDataHolder;


        Log.d("suwdwokdowkdchushcdus", String.valueOf(height));
        Image image = getOptimalSize(catalogBanner.getImages(), 0, height);

        Picasso.get().cancelRequest(photo);

        if(image!=null) {
            Picasso.get().load(image.getUrl()).into(photo);
        }

        title.setText(catalogBanner.getText());
        subtitle.setText(catalogBanner.getTitle());
        subtitle2.setText(catalogBanner.getSubtext());

        if(catalogBanner.getClickAction()!=null){
            itemView.setOnClickListener(view -> {
                CatalogAction catalogAction = catalogBanner.getClickAction();
                switch (catalogAction.getType()) {
                    case "open_url": {
                        hardResolveVKURL(catalogAction.getUrl(), getContext());
                        break;
                    }
                    case "open_section": {
                        NavigationUtil.goToCatalogSection(catalogAction, getContext());
                        break;
                    }
                    case "clear_recent_groups": {

                        break;
                    }
                    case "select_sorting": {

                        break;
                    }
                    case "friends_lists": {

                        break;
                    }
                }
            });
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {

            return new CatalogBannerViewHolder(inflater.inflate(R.layout.catalog_banner_vertical, viewGroup, false));
        }
    }

    public static class FabricHorizontal implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {

            return new CatalogBannerViewHolder(inflater.inflate(R.layout.catalog_banner_horizontal, viewGroup, false));
        }
    }



}
