package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.adapter.catalog.holders.actions.CatalogGroupItemActions.resolveOnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.groups.GroupItem;

public class WrappedGroupViewHolderHorizontal extends GroupViewHolderHorizontal {

    private DataItemWrap<?,?> itemWrap;

    public WrappedGroupViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?, ?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof GroupItem){
            super.bind((GroupItem) item);
        }
    }

    @Override
    public void onClick(View v) {
        resolveOnClick(itemWrap, getContext());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedGroupViewHolderHorizontal(
                    inflater.inflate(R.layout.view_group_item_horizontal, viewGroup, false));
        }
    }
}
