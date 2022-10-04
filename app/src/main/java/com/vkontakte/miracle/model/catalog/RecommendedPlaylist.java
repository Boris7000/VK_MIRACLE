package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_RECOMMENDATION;

import com.vkontakte.miracle.model.audio.PlaylistItem;

import org.json.JSONException;
import org.json.JSONObject;

public class RecommendedPlaylist extends PlaylistItem {

    private final String percentage;
    private final String percentageTitle;
    private final String color;

    public String getPercentage() {
        return percentage;
    }

    public String getPercentageTitle() {
        return percentageTitle;
    }

    public String getColor() {
        return color;
    }

    public RecommendedPlaylist(JSONObject jsonObject, PlaylistItem playlistItem) throws JSONException {
        super(playlistItem);

        percentage = ((int)(jsonObject.getDouble("percentage")*100))+"%";
        percentageTitle = jsonObject.getString("percentage_title");
        color = jsonObject.getString("color");
    }


    @Override
    public int getViewHolderType() {
        return TYPE_PLAYLIST_RECOMMENDATION;
    }
}
