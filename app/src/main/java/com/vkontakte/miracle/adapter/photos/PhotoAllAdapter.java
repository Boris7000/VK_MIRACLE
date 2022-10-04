package com.vkontakte.miracle.adapter.photos;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.photos.holders.PhotoAlbumsHolder;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotosItem;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Photos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class PhotoAllAdapter extends MiracleAsyncLoadAdapter {

    private final int rowLength = 3;
    private final String ownerId;

    public PhotoAllAdapter(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        if(!loaded()){
            Response<JSONObject> response =  Photos.getAlbums(ownerId, 12,
                    0, profileItem.getAccessToken()).execute();

            JSONObject jsonObject = validateBody(response).getJSONObject("response");

            JSONArray jsonArray = jsonObject.getJSONArray("items");
            ArrayList<PhotoAlbumItem> arrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length();i++) {
                arrayList.add(new PhotoAlbumItem(jsonArray.getJSONObject(i)));
            }

            holders.add(new PhotoAlbumsHolder(arrayList,jsonObject.getInt("count")));
        }

        Response<JSONObject> response = Photos.getAll(ownerId, getStep(),
                getLoadedCount(), profileItem.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response).getJSONObject("response");

        setTotalCount(jsonObject.getInt("count"));

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        setLoadedCount(getLoadedCount() + jsonArray.length());
        setAddedCount(jsonArray.length() / rowLength);

        for (int i = 0; i < jsonArray.length(); ) {

            ArrayList<ItemDataHolder> arrayList = new ArrayList<>();

            for (int j = 0; j < rowLength && i < jsonArray.length(); j++) {
                arrayList.add(new PhotoItem(jsonArray.getJSONObject(i)));
                i++;
            }
            holders.add(new StackedPhotosItem(arrayList, rowLength));
        }

        if (holders.size() == getTotalCount() || jsonArray.length() < getStep()) {
            setFinallyLoaded(true);
        }
    }

    @Override
    public void ini(){
        super.ini();
        setStep(rowLength*15);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return ViewHolderTypes.getPhotoFabrics();
    }
}
