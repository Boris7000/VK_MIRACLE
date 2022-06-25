package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_TRIPLE_STACKED;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import java.util.ArrayList;

public class CatalogTripleStack implements ItemDataHolder {

    private final ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();

    public void addItem(ItemDataHolder itemDataHolder){
        itemDataHolders.add(itemDataHolder);
    }

    public ArrayList<ItemDataHolder> getItems() {
        return itemDataHolders;
    }

    public int getSize(){
        return itemDataHolders.size();
    }

    @Override
    public int getViewHolderType() {
        return TYPE_TRIPLE_STACKED;
    }
}
