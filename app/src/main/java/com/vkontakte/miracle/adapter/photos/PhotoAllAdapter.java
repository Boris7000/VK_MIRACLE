package com.vkontakte.miracle.adapter.photos;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.photos.PhotoAlbumsHolder;
import com.vkontakte.miracle.model.photos.StackedPhotosItem;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.ViewHolderTypes;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWF;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Photos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class PhotoAllAdapter extends MiracleAsyncLoadAdapter {

    private final int rowLength = 3;
    private final String ownerId;
    private Attachments attachments;

    public PhotoAllAdapter(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void onLoading() throws Exception {

        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        if(!loaded()){
            Response<JSONObject> response =  Photos.getAlbums(ownerId, 12,
                    0, user.getAccessToken()).execute();

            JSONObject jsonObject = validateBody(response).getJSONObject("response");

            JSONArray jsonArray = jsonObject.getJSONArray("items");
            ArrayList<PhotoAlbumItem> arrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length();i++) {
                arrayList.add(new PhotoAlbumItem(jsonArray.getJSONObject(i)));
            }

            holders.add(new PhotoAlbumsHolder(arrayList,jsonObject.getInt("count")));
            attachments = new Attachments();
        }

        Response<JSONObject> response = Photos.getAll(ownerId, getStep(),
                getLoadedCount(), user.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response).getJSONObject("response");

        setTotalCount(jsonObject.getInt("count"));

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        setLoadedCount(getLoadedCount() + jsonArray.length());
        setAddedCount(jsonArray.length() / rowLength);

        for (int i = 0; i < jsonArray.length(); ) {

            ArrayList<ItemDataHolder> arrayList = new ArrayList<>();

            for (int j = 0; j < rowLength && i < jsonArray.length(); j++) {
                PhotoItem photoItem = new PhotoItem(jsonArray.getJSONObject(i));
                DataItemWrap<PhotoItem, PhotoItemWC> wrap = new PhotoItemWF().create(photoItem, attachments);
                attachments.getPhotoItems().add(wrap);
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
    public void ini(){
        super.ini();
        setStep(rowLength*15);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return ViewHolderTypes.getPhotoFabrics();
    }
}
