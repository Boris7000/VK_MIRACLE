package com.vkontakte.miracle.model.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_AUDIO;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.catalog.CatalogBlock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class AudioItem implements ItemDataHolder{

    private final String id;
    private final String ownerId;
    private final String title;
    private final String artist;
    private final String url;
    private final String durationString;
    private final int duration;
    private final boolean isExplicit;
    private final ArrayList<Artist> mainArtists = new ArrayList<>();
    private final ArrayList<Artist> featuredArtists = new ArrayList<>();
    private ArrayList<Artist> artists = new ArrayList<>();
    private Album album;

    private CatalogBlock catalogBlock;
    private PlaylistItem playlistItem;

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public String getDurationString() {
        return durationString;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isExplicit() {
        return isExplicit;
    }

    public ArrayList<Artist> getMainArtists() {
        return mainArtists;
    }

    public ArrayList<Artist> getFeaturedArtists() {
        return featuredArtists;
    }

    public ArrayList<Artist> getArtists(){return artists;}

    public Album getAlbum() {
        return album;
    }

    public CatalogBlock getCatalogBlock() {
        return catalogBlock;
    }

    public PlaylistItem getPlaylistItem() {
        return playlistItem;
    }

    public void setCatalogBlock(CatalogBlock catalogBlock) {
        this.catalogBlock = catalogBlock;
    }

    public void setPlaylistItem(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
    }

    public AudioItem(JSONObject jsonObject) throws JSONException {
        Log.d("ABOBUSsjfjsf", jsonObject.toString());
        id = jsonObject.getString("id");
        ownerId = jsonObject.getString("owner_id");
        title = jsonObject.getString("title");
        artist = jsonObject.getString("artist");
        url = jsonObject.getString("url");
        duration = jsonObject.getInt("duration");
        isExplicit = jsonObject.getBoolean("is_explicit");

        durationString = TimeUtil.getDurationStringSecs(Locale.getDefault(),duration);

        if(jsonObject.has("main_artists")){
            JSONArray ja_artists = jsonObject.getJSONArray("main_artists");
            for (int i=0; i<ja_artists.length();i++){
                try {
                    mainArtists.add(new Artist(ja_artists.getJSONObject(i)));
                } catch (Exception ignore){ }
            }
            artists.addAll(mainArtists);
        }

        if(jsonObject.has("featured_artists")){
            JSONArray ja_artists = jsonObject.getJSONArray("featured_artists");
            for (int i=0; i<ja_artists.length();i++){
                try {
                    featuredArtists.add(new Artist(ja_artists.getJSONObject(i)));
                } catch (Exception ignore){ }
            }
            artists.addAll(featuredArtists);
        }

        if(jsonObject.has("album")){
            album = new Album(jsonObject.getJSONObject("album"));
        }

    }

    @Override
    public int getViewHolderType() {
        return TYPE_AUDIO;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof AudioItem){
                AudioItem audioItem = (AudioItem) obj;
                return audioItem.id.equals(id);
            }
        }
        return false;
    }
}
