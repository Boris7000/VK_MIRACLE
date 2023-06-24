package com.vkontakte.miracle.adapter.catalog.holders.actions;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToProfile;

import android.content.Context;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.users.ProfileItem;

public class CatalogUserItemActions {
    public static void resolveOnClick(DataItemWrap<?,?> itemWrap, Context context){
        CatalogUser catalogUser = (CatalogUser) itemWrap.getItem();
        ProfileItem profileItem = catalogUser.getProfileItem();
        goToProfile(profileItem, context);
    }
}
