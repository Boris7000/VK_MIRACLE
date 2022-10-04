package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.current_api_v;
import static com.vkontakte.miracle.network.Constants.defaultMessagesFields;
import static com.vkontakte.miracle.network.Constants.defaultWallFields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.message_fields;
import static com.vkontakte.miracle.network.Creator.execute;

import java.util.HashMap;

public class Execute {

    public static Call<JSONObject> getPlaylist(String code, String access_token){
        return execute().execute(code, access_token, current_api_v);
    }

    public static Call<JSONObject> getPlaylist(String owner_id, String id,
                                               boolean need_playlist,int audio_offset,
                                               int audio_count, String access_key, String access_token){
        return execute().getPlaylist(owner_id, id, need_playlist?1:0, need_playlist?1:0, audio_offset, audio_count, access_key, 1,access_token,  current_api_v);
    }

    public static Call<JSONObject> getNewsfeedSmart(boolean need_stories, String start_from, int count, String access_token){
        if (need_stories) return execute().getNewsfeedSmart(start_from,count,defaultWallFields(),access_token,latest_api_v);
        else return execute().newsFeed_get(start_from,count,defaultWallFields(),access_token,latest_api_v);
    }

    public static Call<JSONObject> getFeedLikes(int start_from, String access_token){
        return execute().getFeedLikes(start_from,defaultWallFields(),access_token,latest_api_v);
    }

    public static Call<JSONObject> getMutualFriendsExtended(String target_id, String access_token){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fields", message_fields);
        return execute().getMutualFriendsExtended(target_id, map,access_token,latest_api_v);
    }

}
