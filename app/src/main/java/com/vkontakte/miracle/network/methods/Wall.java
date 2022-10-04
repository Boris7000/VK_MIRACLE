package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.defaultMessagesFields;
import static com.vkontakte.miracle.network.Constants.defaultWallFields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.wall;

public class Wall {


    public static Call<JSONObject> getComments(String owner_id,String post_id,int count, int offset,String start_comment_id,boolean from_new, String access_token){
        return wall().getComments(owner_id, post_id, from_new?"desc":"asc", start_comment_id,
                10,1, offset, count, defaultMessagesFields(), access_token, latest_api_v);
    }

    public static Call<JSONObject> get(String owner_id, String start_from, int count, String access_token){
        return wall().get(owner_id, start_from, count, defaultWallFields(), access_token, latest_api_v);
    }
    public static Call<JSONObject> getById(String post_ids, String access_token){
        return wall().getById(post_ids, defaultWallFields(), access_token, latest_api_v);
    }

}
