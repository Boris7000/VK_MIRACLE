package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class CatalogSliderAdapter extends MiracleNestedLoadableAdapter {
    private final CatalogBlock catalogBlock;

    public CatalogSliderAdapter(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;

        if(catalogBlock.getItems().size()>0) {
            ArrayList<ItemDataHolder> holders = getItemDataHolders();

            holders.addAll(catalogBlock.getItems());

            setAddedCount(holders.size());

            if(catalogBlock.getNextFrom().isEmpty()){
                setFinallyLoaded(true);
            } else {
                setNextFrom(catalogBlock.getNextFrom());
            }

            onComplete();
        }
    }

    @Override
    public void onLoading() throws Exception {
        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                getNextFrom(), profileItem.getAccessToken()).execute();

        JSONObject jo_response;
        JSONObject block;

        jo_response = validateBody(response).getJSONObject("response");
        block = jo_response.getJSONObject("block");

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.findItems(block,catalogExtendedArrays);

        int previous = holders.size();

        holders.addAll(itemDataHolders);

        setAddedCount(holders.size()-previous);

        if(block.has("next_from")){
            setNextFrom(block.getString("next_from"));
        } else {
            setFinallyLoaded(true);
            setNextFrom("");
        }

    }
}
