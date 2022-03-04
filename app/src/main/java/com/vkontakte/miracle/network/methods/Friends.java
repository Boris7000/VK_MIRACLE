package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.users_min_fields;
import static com.vkontakte.miracle.network.Creator.friends;

public class Friends {

    public static Call<JSONObject> get(int count, int offset, String order, String user_id, String access_token){
        return friends().get(count, offset, order, user_id, users_min_fields, access_token,latest_api_v);
    }

}
