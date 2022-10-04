package com.vkontakte.miracle.adapter.friends;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PROFILE;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.friends.holders.ProfileViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Friends;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class OnlineFriendsAdapter extends MiracleAsyncLoadAdapter {

    private final String ownerId;

    public OnlineFriendsAdapter(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void onLoading() throws Exception {
        ProfileItem userItem = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        int previous = holders.size();

        Response<JSONObject> response =
                Friends.getOnline(null, ownerId, userItem.getAccessToken()).execute();

        JSONObject jo_response = validateBody(response).getJSONObject("response");

        if(!loaded()) {
            setTotalCount(jo_response.getInt("count"));
        }

        JSONArray items = jo_response.getJSONArray("items");

        ArrayList<ItemDataHolder> profileItems = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            ProfileItem profileItem = new ProfileItem(items.getJSONObject(i));
            profileItems.add(profileItem);
        }

        holders.addAll(profileItems);

        setLoadedCount(getLoadedCount() + profileItems.size());
        setAddedCount(holders.size() - previous);

        if (getLoadedCount() >= getTotalCount()) {
            setFinallyLoaded(true);
        }
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_PROFILE, new ProfileViewHolder.Fabric());
        return arrayMap;
    }

}
