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
import com.vkontakte.miracle.model.wall.StoriesHolder;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class FeedAdapter extends MiracleAsyncLoadAdapter {

    @Override
    public void onLoading() throws Exception {
        ProfileItem profileItem = StorageUtil.get().currentUser();
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
                PostItem postItem = new PostItem(postObject, catalogExtendedArrays);
                postItems.add(postItem);
            }
        }

        if(getLoadedCount()==0){
            holders.add(new StoriesHolder());
        }

        holders.addAll(postItems);

        setLoadedCount(getLoadedCount() + items.length());

        setAddedCount(holders.size() - previous);

        setNextFrom(jo_response.getString("next_from"));

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
