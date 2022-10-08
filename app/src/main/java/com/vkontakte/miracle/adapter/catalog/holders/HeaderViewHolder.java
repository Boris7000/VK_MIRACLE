package com.vkontakte.miracle.adapter.catalog.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.view.catalog.CatalogHeaderButton;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.CatalogLayout;

public class HeaderViewHolder extends MiracleViewHolder {
    private final TextView title;
    private TextView badge;
    private final CatalogHeaderButton button;
    private final ViewStub badgeStub;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        button = itemView.findViewById(R.id.button);
        badgeStub = itemView.findViewById(R.id.badgeStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
        CatalogLayout catalogLayout = catalogBlock.getLayout();
        title.setText(catalogLayout.getTitle());

        if(catalogBlock.getBadge()!=null){
            if(badge==null) {
                if(badgeStub!=null) {
                    badge = (TextView) badgeStub.inflate();
                } else {
                    badge = itemView.findViewById(R.id.badge);
                }
            }
            if(badge.getVisibility()!=VISIBLE) {
                badge.setVisibility(VISIBLE);
            }
            badge.setText(catalogBlock.getBadge().getText());
        } else {
            if(badge!=null&&badge.getVisibility()!=GONE){
                badge.setVisibility(GONE);
            }
        }

        if(catalogBlock.getActions()!=null&&!catalogBlock.getActions().isEmpty()){
            if(button.getVisibility()!=VISIBLE){
                button.setVisibility(VISIBLE);
            }

            CatalogAction catalogAction = (CatalogAction) catalogBlock.getActions().get(0);

            button.setUpWithCatalogAction(catalogAction);

            button.setOnClickListener(view -> {
                switch (catalogAction.getType()) {
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
        } else {
            if(button.getVisibility()!=GONE){
                button.setVisibility(GONE);
            }
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new HeaderViewHolder(inflater.inflate(R.layout.catalog_header_light, viewGroup, false));
        }
    }

}
