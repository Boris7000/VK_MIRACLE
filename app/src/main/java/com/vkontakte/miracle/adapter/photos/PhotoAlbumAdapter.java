package com.vkontakte.miracle.adapter.photos;

import android.util.ArrayMap;
import android.util.Log;

import com.vkontakte.miracle.adapter.photos.holders.StackedPhotoItem;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Photos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

public class PhotoAlbumAdapter extends MiracleLoadableAdapter {

    private final PhotoAlbumItem photoAlbumItem;
    private int rowLength = 3;

    public PhotoAlbumAdapter(PhotoAlbumItem photoAlbumItem){
        this.photoAlbumItem = photoAlbumItem;
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        Response<JSONObject> response = Photos.get(photoAlbumItem.getOwnerId(),photoAlbumItem.getId(), getStep(),
                getLoadedCount(), profileItem.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response).getJSONObject("response");

        setTotalCount(jsonObject.getInt("count"));

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        setLoadedCount(getLoadedCount() + jsonArray.length());
        setAddedCount(jsonArray.length() / rowLength);

        for (int i = 0; i < jsonArray.length(); ) {

            ArrayList<PhotoItem> arrayList = new ArrayList<>();

            for (int j = 0; j < rowLength && i < jsonArray.length(); j++) {
                arrayList.add(new PhotoItem(jsonArray.getJSONObject(i)));
                i++;
            }
            holders.add(new StackedPhotoItem(arrayList, rowLength));
        }

        if (holders.size() == getTotalCount() || jsonArray.length() < getStep()) {
            setFinallyLoaded(true);
        }
    }

    @Override
    public void ini() {
        super.ini();
        setStep(rowLength*15);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return ViewHolderTypes.getPhotoFabrics();
    }
}
