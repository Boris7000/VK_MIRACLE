package com.vkontakte.miracle.model;

import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.wall.LinkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Attachments {

    private final ArrayList<LinkItem> links = new ArrayList<>();

    public ArrayList<LinkItem> getLinks() {
        return links;
    }

    private final ArrayList<MediaItem> mediaItems = new ArrayList<>();

    public ArrayList<MediaItem> getMediaItems() {
        return mediaItems;
    }

    private final ArrayList<PhotoItem> photos = new ArrayList<>();

    public ArrayList<PhotoItem> getPhotos() {
        return photos;
    }

    public Attachments(JSONArray jsonArray) throws JSONException {
        for (int i=0; i<jsonArray.length();i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String type = jsonObject.getString("type");
            switch (type){
                case "photo":{
                    PhotoItem photoItem = new PhotoItem(jsonObject.getJSONObject("photo"));
                    photos.add(photoItem);
                    mediaItems.add(photoItem);
                    break;
                }
                case "link":{
                    links.add(new LinkItem(jsonObject.getJSONObject("link")));
                    break;
                }
            }

        }
    }
}
