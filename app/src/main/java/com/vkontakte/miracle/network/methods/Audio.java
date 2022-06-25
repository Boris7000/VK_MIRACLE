package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.base_fields;
import static com.vkontakte.miracle.network.Constants.current_api_v;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.audio;

import java.util.Locale;

public class Audio {

    public static Call<JSONObject> getByID(String owner_id, String id, String v, String access_token){
        return audio().getByID(owner_id + "_" + id, access_token, v);
    }

    public static Call<JSONObject> getPlaylists(int count,int offset, String owner_id, String access_token){
        return audio().getPlaylists(owner_id,count,offset,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> getPlaylistById(String owner_id, String id, String access_token, String access_key){
        return audio().getPlaylistById(id,owner_id,access_key,1,access_token, current_api_v);
    }

    public static Call<JSONObject> followPlaylist(String id, String owner_id, String access_key, String access_token){
        return audio().followPlaylist(id,owner_id,access_key,access_token, latest_api_v);
    }

    public static Call<JSONObject> deletePlaylist(String id, String owner_id, String access_key, String access_token){
        return audio().deletePlaylist(id,owner_id,access_key,access_token, latest_api_v);
    }

    public static Call<JSONObject> get(String owner_id, String playlist_id, String access_key, int extended, int count, int offset, String access_token){
        return audio().get(owner_id, playlist_id, count, offset, access_key, extended, access_token, current_api_v);
    }

    public static Call<JSONObject> getCatalogBlockById(String block_id, int count, String access_token){
        return audio().getCatalogBlockById(block_id, count,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> getCatalogBlockById(String block_id, int count, String start_from, String access_token){
        return audio().getCatalogBlockById(block_id, count, start_from,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> getCatalog(String q, String access_token){
        return audio().getCatalog(q, base_fields,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> getCatalog(String access_token){
        return audio().getCatalog(base_fields,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> getArtistCatalog(String artist_id, String access_token){
        return audio().getArtistCatalog(artist_id,1, access_token, latest_api_v);
    }

    public static Call<JSONObject> trackEventsAudioPlay(String id, String owner_id, int duration, String access_token){
        String format = "[{\"e\":\"audio_play\",\"audio_id\":\"%s\",\"source\":\"%s\",\"uuid\":%s,\"duration\":%d,\"start_time\":%d}]";
        return audio().trackEvents(String.format(Locale.getDefault(),format,
                owner_id + "_" + id, "my", System.nanoTime(), duration,
                System.currentTimeMillis() / 1000),
                access_token, latest_api_v);
    }
}
