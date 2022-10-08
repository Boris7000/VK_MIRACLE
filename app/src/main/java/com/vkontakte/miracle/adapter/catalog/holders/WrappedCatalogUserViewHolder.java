package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.users.ProfileItem;

public class WrappedCatalogUserViewHolder extends CatalogUserViewHolder{

    private DataItemWrap<?,?> itemWrap;

    public WrappedCatalogUserViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> resolveItemClickListener(itemWrap, getContext()));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?, ?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof CatalogUser){
            super.bind((CatalogUser) item);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedCatalogUserViewHolder(inflater.inflate(R.layout.view_profile_item, viewGroup, false));
        }
    }

    public static void resolveItemClickListener(DataItemWrap<?,?> itemWrap, Context context){
        CatalogUser catalogUser = (CatalogUser) itemWrap.getItem();
        ProfileItem profileItem = catalogUser.getProfileItem();
        goToProfile(profileItem, context);
    }

}
