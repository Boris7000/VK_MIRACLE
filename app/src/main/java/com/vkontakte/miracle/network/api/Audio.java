package com.vkontakte.miracle.network.api;

import static com.vkontakte.miracle.network.Constants.current_api_v;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.audio;
import static com.vkontakte.miracle.network.Creator.stats;

import com.vkontakte.miracle.service.player.statistics.AudioPlaybackStatisticsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import retrofit2.Call;

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

    public static Call<JSONObject> sendPlaybackStatistics(AudioPlaybackStatisticsData statisticsData,
                                                          String access_token) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("e", "audio_play");
        jsonObject.put("audio_id",statisticsData.getAudioId());
        jsonObject.put("uuid", Integer.valueOf(UUID.randomUUID().hashCode()));
        jsonObject.put("start_time", statisticsData.getStartTime());
        jsonObject.put("duration", statisticsData.getDuration());

        if(statisticsData.getSource().equals("user_playlists")){
            jsonObject.put("playlist_id", statisticsData.getPlayListId());
        }

        jsonArray.put(jsonObject);

        return stats().trackEvents(jsonArray.toString(), access_token, latest_api_v);
    }

}
