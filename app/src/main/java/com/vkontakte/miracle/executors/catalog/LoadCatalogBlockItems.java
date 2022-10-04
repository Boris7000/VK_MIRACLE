package com.vkontakte.miracle.executors.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class LoadCatalogBlockItems extends AsyncExecutor<CatalogBlock> {

    private final CatalogBlock catalogBlock;
    private final ProfileItem profileItem;

    public LoadCatalogBlockItems(CatalogBlock catalogBlock) {
        this.catalogBlock = catalogBlock;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public CatalogBlock inBackground() {
        try {
            if(profileItem!=null) {
                Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                        catalogBlock.getNextFrom(), profileItem.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");
                JSONObject block = jo_response.getJSONObject("block");

                CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

                ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.findItems(block, catalogExtendedArrays);

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
