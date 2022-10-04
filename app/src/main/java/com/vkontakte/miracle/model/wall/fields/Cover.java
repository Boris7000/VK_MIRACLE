package com.vkontakte.miracle.model.wall.fields;

import com.vkontakte.miracle.model.catalog.fields.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Cover implements Serializable {

    private final boolean enabled;
    private final ArrayList<Image> images = new ArrayList<>();

    public ArrayList<Image> getImages() {
        return images;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Cover(JSONObject jsonObject)throws JSONException {

        enabled = jsonObject.getInt("enabled")==1;

        if (jsonObject.has("images")){
            JSONArray ja_images = jsonObject.getJSONArray("images");
            for(int i=0; i<ja_images.length(); i++){
                images.add(new Image(ja_images.getJSONObject(i)));
            }
        }

    }
}
