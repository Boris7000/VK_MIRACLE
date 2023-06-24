package com.vkontakte.miracle.model.photos;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PHOTO_ALBUM;

import androidx.collection.ArrayMap;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.fields.Size;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotoAlbumItem implements ItemDataHolder, PhotoItemWC {

    private final String id;
    private final String ownerId;
    private final String title;
    private final int size;
    private ArrayMap<String, Size> sizes;
    private final ArrayList<ItemDataHolder> photos = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public ArrayMap<String, Size> getSizes() {
        return sizes;
    }

    @Override
    public ArrayList<ItemDataHolder> getPhotoItems() {
        return photos;
    }

    public PhotoAlbumItem(JSONObject jsonObject) throws JSONException{
        id = jsonObject.getString("id");
        ownerId = jsonObject.getString("owner_id");
        title = jsonObject.getString("title");
        size = jsonObject.getInt("size");
        if(jsonObject.has("sizes")){
            sizes = new ArrayMap<>();
            JSONArray jSizes = jsonObject.getJSONArray("sizes");
            for(int i=0;i<jSizes.length();i++){
                Size size = new Size(jSizes.getJSONObject(i));
                sizes.put(size.getType(),size);
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PHOTO_ALBUM;
    }
}
