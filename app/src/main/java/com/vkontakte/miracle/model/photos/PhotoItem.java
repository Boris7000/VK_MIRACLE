package com.vkontakte.miracle.model.photos;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;
import com.vkontakte.miracle.model.photos.fields.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoItem extends MediaItem {

    private final String id;
    private final String albumId;
    private final String ownerId;
    private String postId;
    private long date;

    public String getId() {
        return id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPostId() {
        return postId;
    }

    public long getDate() {
        return date;
    }

    public PhotoItem(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        albumId = jsonObject.getString("album_id");
        ownerId = jsonObject.getString("owner_id");

        if(jsonObject.has("post_id")){
            postId = jsonObject.getString("post_id");
        }
        if(jsonObject.has("date")) {
            date = jsonObject.getLong("date");
        }
        if(jsonObject.has("sizes")){
            ArrayMap <String, Size> sizes = new ArrayMap<>();
            JSONArray jSizes = jsonObject.getJSONArray("sizes");
            for(int i=0;i<jSizes.length();i++){
                Size size = new Size(jSizes.getJSONObject(i));
                sizes.put(size.getType(),size);
            }
            setSizes(sizes);
        }
    }
}
