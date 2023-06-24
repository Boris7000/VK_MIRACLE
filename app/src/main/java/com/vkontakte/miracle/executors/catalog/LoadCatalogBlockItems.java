package com.vkontakte.miracle.executors.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class LoadCatalogBlockItems extends AsyncExecutor<CatalogBlock> {

    private final CatalogBlock catalogBlock;
    private final User user;

    public LoadCatalogBlockItems(CatalogBlock catalogBlock) {
        this.catalogBlock = catalogBlock;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public CatalogBlock inBackground() {
        try {
            if(user !=null) {
                Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                        catalogBlock.getNextFrom(), user.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");
                JSONObject block = jo_response.getJSONObject("block");

                ExtendedArrays extendedArrays = new ExtendedArrays(jo_response);

                ArrayList<ItemDataHolder> itemDataHolders = extendedArrays.extractForBlock(catalogBlock, block);

                catalogBlock.getItems().addAll(itemDataHolders);

                if(block.has("next_from")){
                    catalogBlock.setNextFrom(block.getString("next_from"));
                } else {
                    catalogBlock.setNextFrom("");
                }

                return catalogBlock;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
