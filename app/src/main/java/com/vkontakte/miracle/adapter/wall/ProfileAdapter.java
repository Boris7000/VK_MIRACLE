package com.vkontakte.miracle.adapter.wall;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.getWallFabrics;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.network.methods.Users;
import com.vkontakte.miracle.network.methods.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class ProfileAdapter extends MiracleLoadableAdapter {

    private ProfileItem profileItem;
    public ProfileAdapter(ProfileItem profileItem){
        this.profileItem = profileItem;
    }

    @Override
    public void onLoading() throws Exception {
        ProfileItem userItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();
        Response<JSONObject> response;
        if(!hasData()) {

            response =  Users.get(profileItem.getId(),userItem.getAccessToken()).execute();

            JSONArray ja_response = validateBody(response).getJSONArray("response");

            profileItem = new ProfileItem(ja_response.getJSONObject(0));

            holders.add(profileItem);
        }

        response = Wall.get(profileItem.getId(),getNextFrom(), getStep(), userItem.getAccessToken()).execute();
        JSONObject jo_response = validateBody(response).getJSONObject("response");

        setTotalCount(jo_response.getInt("count"));

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        JSONArray items = jo_response.getJSONArray("items");
        if(items.length()>0) {
            ArrayList<PostItem> postItems = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject postObject = items.getJSONObject(i);
                if (postObject.has("post_type")) {
                    JSONObject jo_item = items.getJSONObject(i);
                    PostItem postItem = new PostItem(jo_item, catalogExtendedArrays);
                    postItems.add(postItem);
                }
            }
            holders.addAll(postItems);

            setLoadedCount(getLoadedCount() + items.length());

            setNextFrom(jo_response.getString("next_from"));
        } else {
            if(getLoadedCount()==0) {
                holders.add(new ErrorDataHolder("Здесь пока нет записей"));
            }
        }

        setAddedCount(holders.size() - previous);

        if (getLoadedCount() >= getTotalCount()) {
            setFinallyLoaded(true);
        }

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
