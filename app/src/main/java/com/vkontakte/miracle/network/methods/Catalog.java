package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.catalog;


public class Catalog {

    public static Call<JSONObject> getFriends(String user_id, int need_blocks, String access_token){
        return catalog().getFriends(user_id, need_blocks, access_token, latest_api_v);
    }

    public static Call<JSONObject> getAudio(String owner_id, String access_token){
        return catalog().getAudio(owner_id, 0, access_token, latest_api_v);
    }

    public static Call<JSONObject> getAudioArtist(String artist_id, int need_blocks, String access_token){
        return catalog().getAudioArtist(artist_id, need_blocks, access_token, latest_api_v);
    }

    public static Call<JSONObject> getGroups(String user_id, String access_token){
        return catalog().getGroups(user_id, 0, access_token, latest_api_v);
    }

    public static Call<JSONObject> getSection(String section_id, String start_from, String access_token){
        return catalog().getSection(section_id, start_from, access_token, latest_api_v);
    }

    public static Call<JSONObject> getBlockItems(String block_id, String start_from, String access_token){
        return catalog().getBlockItems(block_id, start_from, access_token, latest_api_v);
    }
}
