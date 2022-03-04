package com.vkontakte.miracle.model.wall;

import com.vkontakte.miracle.model.photos.PhotoItem;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkItem {

    private final String url;
    private final String title;
    private final String caption;
    private final String description;
    private PhotoItem photo;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public PhotoItem getPhoto() {
        return photo;
    }

    public LinkItem(JSONObject jsonObject) throws JSONException {
        url = jsonObject.getString("url");

        if(jsonObject.has("title")){
            title = jsonObject.getString("title");
        } else {
            title = "";
        }

        if(jsonObject.has("caption")){
            caption = jsonObject.getString("caption");
        } else {

            String customCaption = url;
            int substringIndex = customCaption.indexOf("://");
            if(substringIndex>-1){
                customCaption = customCaption.substring(substringIndex+3);
            }
            substringIndex = customCaption.indexOf("/");
            if(substringIndex>-1){
                customCaption = customCaption.substring(0,substringIndex);
            }

            caption = customCaption;
        }

        if(jsonObject.has("description")){
            description = jsonObject.getString("description");
        } else {
            description = "";
        }

        if(jsonObject.has("photo")){
            photo = new PhotoItem(jsonObject.getJSONObject("photo"));
        }
    }
}
