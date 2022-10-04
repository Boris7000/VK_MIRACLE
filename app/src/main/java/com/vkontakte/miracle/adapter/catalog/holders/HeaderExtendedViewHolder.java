package com.vkontakte.miracle.adapter.catalog.holders;

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
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.fields.CatalogLayout;
import com.vkontakte.miracle.model.catalog.fields.TopTitle;

public class HeaderExtendedViewHolder extends MiracleViewHolder {

    private final TextView title;
    private final TextView topTitleText;
    private final ImageView topTitleIcon;

    public HeaderExtendedViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        topTitleText = itemView.findViewById(R.id.top_title);
        topTitleIcon = itemView.findViewById(R.id.top_title_icon);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
        CatalogLayout catalogLayout = catalogBlock.getLayout();
        title.setText(catalogLayout.getTitle());

        if(catalogLayout.getTopTitle()!=null){
            TopTitle topTitle = catalogLayout.getTopTitle();
            topTitleText.setText(topTitle.getText());

            Picasso.get().cancelRequest(topTitleIcon);
            Picasso.get().load(topTitle.getIcon()).into(topTitleIcon);

        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new HeaderExtendedViewHolder(inflater.inflate(R.layout.catalog_header_extended, viewGroup, false));
        }
    }

}
