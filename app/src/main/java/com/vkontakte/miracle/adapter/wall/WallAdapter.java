package com.vkontakte.miracle.adapter.wall;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.getWallFabrics;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.network.methods.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class WallAdapter extends MiracleAsyncLoadAdapter {

    private final String postId;
    private final String ownerId;

    public WallAdapter(String postId, String ownerId) {
        this.postId = postId;
        this.ownerId = ownerId;
    }

    @Override
    public void onLoading() throws Exception {
        ProfileItem userItem = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();
        Response<JSONObject> response;
        if(!loaded()) {

            response =  Wall.getById(ownerId+"_"+postId, userItem.getAccessToken()).execute();
            JSONObject jo_response = validateBody(response).getJSONObject("response");

            CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

            JSONArray items = jo_response.getJSONArray("items");

            PostItem postItem = new PostItem(items.getJSONObject(0),catalogExtendedArrays);

            holders.add(postItem);

            setAddedCount(holders.size() - previous);

            setFinallyLoaded(true);
        }

    }
    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return getWallFabrics();
    }
}
