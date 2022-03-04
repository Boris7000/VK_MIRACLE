package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.defaultHeaders;
import static com.vkontakte.miracle.network.Constants.defaultWallFields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.execute;

public class Execute {

    public static Call<JSONObject> getPlaylist(String owner_id, String id, boolean need_playlist,int audio_offset, int audio_count, String access_key, String access_token){
        return execute().getPlaylist(owner_id, id, need_playlist?1:0, need_playlist?1:0, audio_offset, audio_count, access_key, access_token, latest_api_v,defaultHeaders());
    }

    public static Call<JSONObject> getMusicPage(int count, int offset,boolean need_playlists, String owner_id, String access_token){
        if(need_playlists) return  execute().getMusicPage(owner_id, count, offset, 1, 12, access_token, 3, latest_api_v,defaultHeaders());
        else return  execute().getMusicPage(owner_id, count, offset, 0, access_token, 3, latest_api_v,defaultHeaders());
    }

    public static Call<JSONObject> getNewsfeedSmart(boolean need_stories, String start_from, int count, String access_token){
        if (need_stories) return execute().getNewsfeedSmart(start_from,count,defaultWallFields(),access_token,latest_api_v);
        else return execute().newsFeed_get(start_from,count,defaultWallFields(),access_token,latest_api_v);
    }

    public static Call<JSONObject> getFeedLikes(int start_from, String access_token){
        return execute().getFeedLikes(start_from,defaultWallFields(),access_token,latest_api_v);
    }
}
