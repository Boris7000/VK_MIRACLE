package com.vkontakte.miracle.executors.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONObject;

import retrofit2.Response;

public class LoadCatalogBlock extends AsyncExecutor<CatalogBlock> {

    private final String blockId;
    private final User user;

    public LoadCatalogBlock(String blockId) {
        this.blockId = blockId;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public CatalogBlock inBackground() {
        try {
            if(user !=null) {
                Response<JSONObject> response = Catalog.getBlockItems(blockId,
                        "", user.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");
                JSONObject block = jo_response.getJSONObject("block");

                ExtendedArrays extendedArrays = new ExtendedArrays(jo_response);

                return new CatalogBlock(block);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
