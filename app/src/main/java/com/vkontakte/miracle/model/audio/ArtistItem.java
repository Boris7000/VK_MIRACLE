package com.vkontakte.miracle.model.audio;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArtistItem implements ItemDataHolder{

    private final String id;
    private final String name;
    private final boolean isAlbumCover;
    private final ArrayMap<Integer, String> photoSizes = new ArrayMap<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAlbumCover() {
        return isAlbumCover;
    }

    public ArrayMap<Integer, String> getPhotoSizes() {
        return photoSizes;
    }

    public ArtistItem(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        name = jsonObject.getString("name");
        isAlbumCover = jsonObject.getBoolean("is_album_cover");

        if(jsonObject.has("photo")){
            JSONArray ja_photo = jsonObject.getJSONArray("photo");
            for(int i=0;i<ja_photo.length();i++){
                JSONObject jo_photo = ja_photo.getJSONObject(i);
                photoSizes.put(jo_photo.getInt("width"),jo_photo.getString("url"));
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return 0;
    }
}
