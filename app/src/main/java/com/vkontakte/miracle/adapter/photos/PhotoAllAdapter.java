package com.vkontakte.miracle.adapter.photos;

import com.vkontakte.miracle.adapter.photos.holders.HorizontalListPhotoAlbumItem;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotosItem;
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

import android.util.ArrayMap;

public class PhotoAllAdapter extends MiracleLoadableAdapter {

    private boolean albumsLoaded = false;
    private final int rowLength = 3;

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        if(!albumsLoaded){
            Response<JSONObject> response =  Photos.getAlbums(profileItem.getId(), 12,
                    0, profileItem.getAccessToken()).execute();

            JSONObject jsonObject = validateBody(response).getJSONObject("response");

            JSONArray jsonArray = jsonObject.getJSONArray("items");
            ArrayList<PhotoAlbumItem> arrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length();i++) {
                arrayList.add(new PhotoAlbumItem(jsonArray.getJSONObject(i)));
            }

            holders.add(new HorizontalListPhotoAlbumItem(arrayList,jsonObject.getInt("count")));
            albumsLoaded = true;
        }

        Response<JSONObject> response = Photos.getAll(profileItem.getId(), getStep(),
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
    public void resetToInitialState() {
        super.resetToInitialState();
        albumsLoaded = false;
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
