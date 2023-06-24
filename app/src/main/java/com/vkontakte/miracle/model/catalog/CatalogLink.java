package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_LINK;

import android.util.Log;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.fields.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogLink implements ItemDataHolder {

    private final String id;
    private final String url;
    private String title;
    private String subtitle;
    private final ArrayList<Image> images = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public CatalogLink(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");

        url = jsonObject.getString("url");

        if(jsonObject.has("title")){
            title = jsonObject.getString("title");
        }

        if(jsonObject.has("subtitle")){
            subtitle = jsonObject.getString("subtitle");
        }

        if(jsonObject.has("image")){
            JSONArray ja_images = jsonObject.getJSONArray("image");
            for(int i=0; i<ja_images.length(); i++){
                images.add(new Image(ja_images.getJSONObject(i)));
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_CATALOG_LINK;
    }
}
