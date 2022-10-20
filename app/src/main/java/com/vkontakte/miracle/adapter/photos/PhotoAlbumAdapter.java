package com.vkontakte.miracle.adapter.photos;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotosItem;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWF;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Photos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class PhotoAlbumAdapter extends MiracleAsyncLoadAdapter {

    private final String photoAlbumId;
    private final String ownerId;

    private PhotoAlbumItem photoAlbumItem;
    private final int rowLength = 3;

    public PhotoAlbumAdapter(String photoAlbumId, String ownerId) {
        this.photoAlbumId = photoAlbumId;
        this.ownerId = ownerId;
    }

    public PhotoAlbumItem getPhotoAlbumItem() {
        return photoAlbumItem;
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        Response<JSONObject> response;
        if(!loaded()) {
            response =  Photos.getAlbums(ownerId, photoAlbumId, profileItem.getAccessToken()).execute();
            JSONObject jsonObject = validateBody(response);

            if (jsonObject.has("response")){
                jsonObject = jsonObject.getJSONObject("response");
            } else {
                holders.add(new ErrorDataHolder(R.string.album_missing));
                setAddedCount(1);
                setFinallyLoaded(true);
                return;
            }

            photoAlbumItem = new PhotoAlbumItem(jsonObject.getJSONArray("items").getJSONObject(0));
        }

        response = Photos.get(ownerId, photoAlbumId, getStep(),
                getLoadedCount(), profileItem.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response);

        if (jsonObject.has("response")){
            jsonObject = jsonObject.getJSONObject("response");
        } else {
            holders.add(new ErrorDataHolder(R.string.album_missing));
            setAddedCount(1);
            setFinallyLoaded(true);
            return;
        }

        setTotalCount(jsonObject.getInt("count"));

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        setLoadedCount(getLoadedCount() + jsonArray.length());
        setAddedCount(jsonArray.length() / rowLength);

        for (int i = 0; i < jsonArray.length(); ) {
            ArrayList<ItemDataHolder> arrayList = new ArrayList<>();
            for (int j = 0; j < rowLength && i < jsonArray.length(); j++) {
                PhotoItem photoItem = new PhotoItem(jsonArray.getJSONObject(i));
                DataItemWrap<PhotoItem, PhotoItemWC> wrap = new PhotoItemWF().create(photoItem, photoAlbumItem);
                photoAlbumItem.getPhotoItems().add(wrap);
                arrayList.add(wrap);
                i++;
            }
            holders.add(new StackedPhotosItem(arrayList, rowLength));
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
