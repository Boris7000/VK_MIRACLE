package com.vkontakte.miracle.network.api;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.likes;

import org.json.JSONObject;

import retrofit2.Call;

public class Likes {
    public static Call<JSONObject> add(String type, String item_id, String owner_id, String access_token){
        return likes().add(type, item_id, owner_id, access_token, latest_api_v);
    }
    public static Call<JSONObject> delete(String type, String item_id, String owner_id, String access_token){
        return likes().delete(type, item_id, owner_id, access_token, latest_api_v);
    }
}
