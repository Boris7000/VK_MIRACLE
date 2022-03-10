package com.vkontakte.miracle.adapter.catalog;

import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.other.CatalogTripleStack;

import java.util.ArrayList;

public class CatalogTripleStackedSliderAdapter  extends MiracleNestedLoadableAdapter {

    public CatalogTripleStackedSliderAdapter(CatalogBlock catalogBlock){

        ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.getItems();
        if(itemDataHolders.size()>0) {
            ArrayList<ItemDataHolder> holders = getItemDataHolders();

            ArrayList<ItemDataHolder> tripleStackedHolders = new ArrayList<>();
            for(int i=0;i<itemDataHolders.size();){
                CatalogTripleStack catalogTripleStack = new CatalogTripleStack();
                for(int j=0;j<3&&i+j<itemDataHolders.size();j++){
                    catalogTripleStack.addItem(itemDataHolders.get(i+j));
                }
                i+=catalogTripleStack.getItemDataHolders().size();
                tripleStackedHolders.add(catalogTripleStack);
            }

            holders.addAll(tripleStackedHolders);

            setAddedCount(holders.size());

            setFinallyLoaded(true);

            onComplete();
        }
    }

    @Override
    public void onLoading() throws Exception { }

}
