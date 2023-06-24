package com.vkontakte.miracle.adapter.catalog.holders.actions;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToGroup;

import android.content.Context;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.groups.GroupItem;

public class CatalogGroupItemActions {
    public static void resolveOnClick(DataItemWrap<?,?> itemWrap, Context context){
        GroupItem groupItem = (GroupItem) itemWrap.getItem();
        goToGroup(groupItem, context);
    }
}
