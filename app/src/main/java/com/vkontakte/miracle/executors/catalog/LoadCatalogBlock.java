package com.vkontakte.miracle.executors.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONObject;

import retrofit2.Response;

public class LoadCatalogBlock extends AsyncExecutor<CatalogBlock> {

    private final String blockId;
    private final ProfileItem profileItem;

    public LoadCatalogBlock(String blockId) {
        this.blockId = blockId;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public CatalogBlock inBackground() {
        try {
            if(profileItem!=null) {
                Response<JSONObject> response = Catalog.getBlockItems(blockId,
                        "", profileItem.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");
                JSONObject block = jo_response.getJSONObject("block");

                CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

                return new CatalogBlock(block, catalogExtendedArrays);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
