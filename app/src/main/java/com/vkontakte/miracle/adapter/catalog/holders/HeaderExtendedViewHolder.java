package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.fields.CatalogLayout;
import com.vkontakte.miracle.model.catalog.fields.TopTitle;

public class HeaderExtendedViewHolder extends HeaderViewHolder {

    private final LinearLayout extension;
    private final TextView topTitleText;
    private final ImageView topTitleIcon;

    public HeaderExtendedViewHolder(@NonNull View itemView) {
        super(itemView);
        extension = itemView.findViewById(R.id.extension);
        topTitleText = extension.findViewById(R.id.top_title);
        topTitleIcon = extension.findViewById(R.id.top_title_icon);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);
        CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
        CatalogLayout catalogLayout = catalogBlock.getLayout();

        if(catalogLayout.getTopTitle()!=null){
            TopTitle topTitle = catalogLayout.getTopTitle();
            topTitleText.setText(topTitle.getText());
            Picasso.get().cancelRequest(topTitleIcon);
            Picasso.get().load(topTitle.getIcon()).into(topTitleIcon);
        } else {
            extension.setVisibility(View.GONE);
        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new HeaderExtendedViewHolder(
                    inflater.inflate(R.layout.catalog_header_extended, viewGroup, false));
        }
    }

}
