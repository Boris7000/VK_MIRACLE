package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.util.DimensionsUtil;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.catalog.CatalogLink;
import com.vkontakte.miracle.model.catalog.fields.Image;

public class CatalogLinkViewHolderHorizontal extends CatalogClickableViewHolder {

    private final ImageView photo;
    private final TextView title;
    private CatalogLink catalogLink;

    public CatalogLinkViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogLink = (CatalogLink) itemDataHolder;

        title.setText(catalogLink.getTitle());

        int size = (int) DimensionsUtil.dpToPx(itemView.getContext(), 70);

        Image image = getOptimalSize(catalogLink.getImages(), size, size);

        Picasso.get().cancelRequest(photo);

        if(image!=null) {
            Picasso.get().load(image.getUrl()).into(photo);
        }
    }

    @Override
    public void onClick(View view) {
        hardResolveVKURL(catalogLink.getUrl(), getContext());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogLinkViewHolderHorizontal(
                    inflater.inflate(R.layout.catalog_link_horizontal, viewGroup, false));
        }
    }
}
