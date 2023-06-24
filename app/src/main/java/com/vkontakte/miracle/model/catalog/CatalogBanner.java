package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_BANNER;

import android.util.Log;

import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogBanner implements ItemDataHolder {

    private final String id;
    private String text;
    private String title;
    private String subtext;
    private String imageMode;
    private final ArrayList<Image> images = new ArrayList<>();
    private CatalogAction clickAction;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtext() {
        return subtext;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public CatalogAction getClickAction() {
        return clickAction;
    }

    public CatalogBanner(JSONObject jsonObject) throws JSONException {

        Log.d("dpdvdpvkdpkvdv",jsonObject.toString());

        id = jsonObject.getString("id");

        if(jsonObject.has("text")) {
            text = jsonObject.getString("text");
        }
        if(jsonObject.has("title")) {
            title = jsonObject.getString("title");
        }
        if(jsonObject.has("subtext")) {
            subtext = jsonObject.getString("subtext");
        }

        if(jsonObject.has("image_mode")){
            imageMode = jsonObject.getString("image_mode");
        }

        if(jsonObject.has("click_action")){
            clickAction = new CatalogAction(jsonObject.getJSONObject("click_action"));
        }

        if(jsonObject.has("images")){
            JSONArray ja_images = jsonObject.getJSONArray("images");
            for(int i=0; i<ja_images.length(); i++){
                images.add(new Image(ja_images.getJSONObject(i)));
            }
        }

    }

    @Override
    public int getViewHolderType() {
        return TYPE_CATALOG_BANNER;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof CatalogBanner){
                CatalogBanner catalogBanner = (CatalogBanner) obj;
                return catalogBanner.id.equals(id);
            }
        }
        return false;
    }

    public boolean equalsContent(@Nullable Object obj){
        if(obj!=null){
            if(obj instanceof CatalogBanner){
                CatalogBanner catalogBanner = (CatalogBanner) obj;
                return catalogBanner.text.equals(text)&&catalogBanner.title.equals(title)&&
                        catalogBanner.subtext.equals(subtext);
            }
        }
        return false;
    }

}
