package com.vkontakte.miracle.adapter.catalog.holders.actions;

import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;

import android.content.Context;

import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;

public class CatalogActionItemActions {
    public static void resolveOpenUrl(CatalogAction catalogAction, Context context){
        hardResolveVKURL(catalogAction.getUrl(), context);
    }

    public static void resolveOpenSection(CatalogAction catalogAction, Context context){
        NavigationUtil.goToCatalogSection(catalogAction, context);
    }
}
