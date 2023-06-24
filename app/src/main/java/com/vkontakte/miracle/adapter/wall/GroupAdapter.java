package com.vkontakte.miracle.adapter.wall;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.getWallFabrics;

import android.util.ArrayMap;
import android.util.Log;

import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.network.api.Execute;
import com.vkontakte.miracle.network.api.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class GroupAdapter extends MiracleAsyncLoadAdapter {

    private final String groupId;

    private GroupItem groupItem;

    public GroupAdapter(String groupId){
        this.groupId = groupId;
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    @Override
    public void onLoading() throws Exception {
        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();
        Response<JSONObject> response;
        if(!loaded()) {

            /*
            response =  Groups.get(groupId.substring(1),user.getAccessToken()).execute();

            JSONObject jo_response = validateBody(response).getJSONObject("response");

            JSONArray ja_groups = jo_response.getJSONArray("groups");

            groupItem = new GroupItem(ja_groups.getJSONObject(0));

            holders.add(groupItem);

             */

            //////////////////////////////////////////////////////////////

            response =  Execute.getFullGroupNewNew(groupId.substring(1),user.getAccessToken()).execute();

            JSONObject jo_response = validateBody(response).getJSONObject("response");

            groupItem = new GroupItem(jo_response);

            holders.add(groupItem);

            Log.d("okfoekfokef",jo_response.toString());

        }

        response = Wall.get(groupId,getNextFrom(), getStep(), user.getAccessToken()).execute();

        JSONObject jo_response = validateBody(response).getJSONObject("response");

        setTotalCount(jo_response.getInt("count"));

        ExtendedArrays extendedArrays = new ExtendedArrays(jo_response);

        JSONArray items = jo_response.getJSONArray("items");
        if(items.length()>0) {
            ArrayList<PostItem> postItems = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject postObject = items.getJSONObject(i);
                if (postObject.has("post_type")) {
                    PostItem postItem = new PostItem(postObject, extendedArrays);
                    postItems.add(postItem);
                }
            }
            holders.addAll(postItems);

            setLoadedCount(getLoadedCount() + items.length());

            setNextFrom(jo_response.getString("next_from"));
        } else {
            if(getLoadedCount()==0) {
                holders.add(new ErrorDataHolder(R.string.wall_is_empty));
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
