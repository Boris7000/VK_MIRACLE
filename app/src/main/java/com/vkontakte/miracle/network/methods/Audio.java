package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.current_api_v;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.audio;

import java.util.Locale;

public class Audio {

    public static Call<JSONObject> getByID(String owner_id, String id, String v, String access_token){
        return audio().getByID(owner_id + "_" + id, access_token, v);
    }

    public static Call<JSONObject> followPlaylist(String id, String owner_id, String access_key, String access_token){
        return audio().followPlaylist(id,owner_id,access_key,access_token, latest_api_v);
    }

    public static Call<JSONObject> deletePlaylist(String id, String owner_id, String access_key, String access_token){
        return audio().deletePlaylist(id,owner_id,access_key,access_token, latest_api_v);
    }

    public static Call<JSONObject> getCatalog(String query, String access_token) {
        return audio().getCatalog(query, "", 1, access_token, latest_api_v);
    }

    public static Call<JSONObject> get(String owner_id, String playlist_id,
                                       String access_key, int extended, int count,
                                       int offset, String access_token){
        return audio().get(owner_id, playlist_id, count, offset, access_key, extended, access_token, current_api_v);
    }

    public static Call<JSONObject> get(String owner_id, String playlist_id,
                                       String access_key, int extended, int count,
                                       int offset, int shuffle, int shuffle_seed, String access_token){
        return audio().get(owner_id, playlist_id, count, offset,
                access_key, extended, shuffle, shuffle_seed, access_token, current_api_v);
    }

    public static Call<JSONObject> add(String owner_id, String audio_id, String access_token){
        return audio().add(owner_id, audio_id, access_token, current_api_v);
    }

    public static Call<JSONObject> delete(String owner_id, String audio_id, String access_token){
        return audio().delete(owner_id, audio_id, access_token, current_api_v);
    }

    public static Call<JSONObject> trackEventsAudioPlay(String id, String owner_id, int duration, String access_token){
        String format = "[{\"e\":\"audio_play\",\"audio_id\":\"%s\",\"source\":\"%s\",\"uuid\":%s,\"duration\":%d,\"start_time\":%d}]";
        return audio().trackEvents(String.format(Locale.getDefault(),format,
                owner_id + "_" + id, "my", System.nanoTime(), duration,
                System.currentTimeMillis() / 1000),
                access_token, latest_api_v);
    }
}
