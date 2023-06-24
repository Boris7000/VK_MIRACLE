package com.vkontakte.miracle.adapter.friends;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PROFILE;

import android.util.ArrayMap;

import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.adapter.friends.holders.ProfileViewHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Friends;

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
        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        int previous = holders.size();

        Response<JSONObject> response =
                Friends.getOnline(null, ownerId, user.getAccessToken()).execute();

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
