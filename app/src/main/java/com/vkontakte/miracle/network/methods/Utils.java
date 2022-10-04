package com.vkontakte.miracle.network.methods;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.utils;

import org.json.JSONObject;

import retrofit2.Call;

public class Utils {
    public static Call<JSONObject> resolveScreenName(String screen_name, String access_token){
        return utils().resolveScreenName(screen_name, access_token, latest_api_v);
    }
}
