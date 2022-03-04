package com.vkontakte.miracle.adapter.wall;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.getWallFabrics;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.google.android.exoplayer2.util.Log;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class FeedAdapter extends MiracleLoadableAdapter {

    @Override
    public void onLoading() throws Exception {
        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();

        Response<JSONObject> response = Execute.getNewsfeedSmart(false,
                getNextFrom(), getStep(), profileItem.getAccessToken()).execute();

        JSONObject jo_response = validateBody(response).getJSONObject("response");

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        JSONArray items = jo_response.getJSONArray("items");
        ArrayList<PostItem> postItems = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject postObject = items.getJSONObject(i);
            if(postObject.has("post_type")){
                JSONObject jo_item = items.getJSONObject(i);
                PostItem postItem = new PostItem(jo_item, catalogExtendedArrays);
                postItems.add(postItem);
            }
        }
        holders.addAll(postItems);

        setLoadedCount(getLoadedCount() + items.length());
        setAddedCount(holders.size() - previous);

        setNextFrom(jo_response.getString("next_from"));

        Log.d("sijfijifje",jo_response.toString());

    }

    @Override
    public void ini() {
        super.ini();
        setStep(25);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return getWallFabrics();
    }
}
