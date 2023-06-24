package com.vkontakte.miracle.adapter.wall;

import static com.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WALL_COUNTER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WALL_COUNTERS;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.getWallFabrics;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleAdapter;
import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.model.wall.fields.Counter;
import com.vkontakte.miracle.model.wall.fields.Counters;
import com.vkontakte.miracle.network.api.Users;
import com.vkontakte.miracle.network.api.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class ProfileAdapter extends MiracleAsyncLoadAdapter {

    private final String profileId;
    private ProfileItem profileItem;

    public ProfileAdapter(String profileId){
        this.profileId = profileId;
    }

    public ProfileItem getProfileItem() {
        return profileItem;
    }

    @Override
    public void onLoading() throws Exception {
        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();
        Response<JSONObject> response;
        if(!loaded()) {

            response =  Users.get(profileId,user.getAccessToken()).execute();

            JSONArray ja_response = validateBody(response).getJSONArray("response");
            profileItem = new ProfileItem(ja_response.getJSONObject(0));

            holders.add(profileItem);

            //TODO WHAT THE HELL WITH COUNTERS?
            if(profileItem.getCounters()!=null){
                Counters counters = profileItem.getCounters();
                ArrayList<ItemDataHolder> counterArrayList = counters.getCounters();
                if(!counterArrayList.isEmpty()){
                    for (ItemDataHolder idh: counterArrayList){
                        ((Counter)idh).setOwnerId(profileItem.getId());
                    }
                }
                holders.add(profileItem.getCounters());
            }
        }

        response = Wall.get(profileId,getNextFrom(), getStep(), user.getAccessToken()).execute();

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
        ArrayMap<Integer, ViewHolderFabric> arrayMap = getWallFabrics();
        arrayMap.put(TYPE_WALL_COUNTERS, new WallCountersSliderViewHolderFabric());
        return arrayMap;
    }

    public class WallCountersSliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;

        public WallCountersSliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool = getNestedRecycledViewPool(TYPE_WALL_COUNTERS);
            recycledViewPool.setMaxRecycledViews(TYPE_WALL_COUNTER, 7);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            Counters counters = (Counters) itemDataHolder;

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            CountersAdapter countersAdapter;
            if(adapter instanceof CountersAdapter){
                countersAdapter = ((CountersAdapter)recyclerView.getAdapter());
                countersAdapter.iniFromFragment(getFragment());
                countersAdapter.setNewCounters(counters);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                countersAdapter = new CountersAdapter(counters);
                countersAdapter.iniFromFragment(getFragment());
                countersAdapter.setRecyclerView(recyclerView);
                countersAdapter.ini();
                if(recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(false);
                }
                recyclerView.setAdapter(countersAdapter);
                if(!recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(true);
                }
            }
            countersAdapter.load();
        }
    }

    public class WallCountersSliderViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WallCountersSliderViewHolder(inflater.inflate(R.layout.view_wall_counters_slider,
                    viewGroup, false));
        }
    }

}
