package com.vkontakte.miracle.service.player.loader;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class CatalogBlockLoader implements AudioItemWCLoader{

    private CatalogBlock catalogBlock;
    private String catalogBlockId;
    private String nextFrom;

    public CatalogBlockLoader(String catalogBlockId) {
        this.catalogBlockId = catalogBlockId;
        this.nextFrom = "";
    }

    public CatalogBlockLoader(CatalogBlock catalogBlock) {
        this.catalogBlock = catalogBlock;
        catalogBlockId = catalogBlock.getId();
        nextFrom = catalogBlock.getNextFrom();
    }

    @Override
    public ArrayList<ItemDataHolder> load() throws Exception {

        User user = StorageUtil.get().currentUser();

        Response<JSONObject> response = Catalog.getBlockItems(
                catalogBlockId,
                nextFrom,
                user.getAccessToken()).execute();

        JSONObject jo_response = validateBody(response).getJSONObject("response");
        JSONObject block = jo_response.getJSONObject("block");

        ExtendedArrays extendedArrays = new ExtendedArrays(jo_response);

        if(catalogBlock==null){
            catalogBlock = new CatalogBlock(block);
            catalogBlockId = catalogBlock.getId();
        }

        ArrayList<ItemDataHolder> itemDataHolders = extendedArrays.extractForBlock(catalogBlock, block);

        if(block.has("next_from")){
            nextFrom = block.getString("next_from");
        } else {
            nextFrom = "";
        }

        return itemDataHolders;

    }

    @Override
    public AudioItemWC getContainer() {
        return catalogBlock;
    }

    @Override
    public boolean canLoadMore() {
        return !nextFrom.isEmpty();
    }
}
