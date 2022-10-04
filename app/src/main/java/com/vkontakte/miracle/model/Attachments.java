package com.vkontakte.miracle.model;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PHOTO;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.photos.PhotoItem;
import com.vkontakte.miracle.model.photos.PhotoWrapContainer;
import com.vkontakte.miracle.model.wall.LinkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Attachments implements AudioWrapContainer, PhotoWrapContainer {

    private final ArrayList<ItemDataHolder> audios = new ArrayList<>();

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return audios;
    }

    private final ArrayList<LinkItem> links = new ArrayList<>();

    public ArrayList<LinkItem> getLinks() {
        return links;
    }

    private final ArrayList<ItemDataHolder> mediaItems = new ArrayList<>();

    public ArrayList<ItemDataHolder> getMediaItems() {
        return mediaItems;
    }

    private final ArrayList<ItemDataHolder> photos = new ArrayList<>();

    @Override
    public ArrayList<ItemDataHolder> getPhotoItems() {
        return photos;
    }

    public Attachments(JSONArray jsonArray) throws JSONException {
        for (int i=0; i<jsonArray.length();i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String type = jsonObject.getString("type");
            switch (type){
                case "photo":{
                    PhotoItem photoItem = new PhotoItem(jsonObject.getJSONObject("photo"));
                    DataItemWrap<PhotoItem, PhotoWrapContainer> dataItemWrap =
                            new DataItemWrap<PhotoItem, PhotoWrapContainer>(photoItem, this) {
                                @Override
                                public int getViewHolderType() {
                                    return TYPE_WRAPPED_PHOTO;
                                }
                            };
                    photos.add(photoItem);
                    mediaItems.add(photoItem);
                    break;
                }
                case "link":{
                    links.add(new LinkItem(jsonObject.getJSONObject("link")));
                    break;
                }
                case "audio":{
                    AudioItem audioItem = new AudioItem(jsonObject.getJSONObject("audio"));
                    DataItemWrap<AudioItem, AudioWrapContainer> dataItemWrap =
                            new DataItemWrap<AudioItem, AudioWrapContainer>(audioItem, this) {
                                @Override
                                public int getViewHolderType() {
                                    return TYPE_WRAPPED_AUDIO;
                                }
                            };
                    audios.add(dataItemWrap);
                    break;
                }
            }

        }
    }


}
