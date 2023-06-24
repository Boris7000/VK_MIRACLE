package com.vkontakte.miracle.network.api;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.message_fields;
import static com.vkontakte.miracle.network.Constants.users_all_fields;
import static com.vkontakte.miracle.network.Creator.users;

public class Users {

    public static Call<JSONObject> get(String user_ids, String fields, String access_token){
        return users().get(user_ids, fields, access_token, latest_api_v);
    }
    public static Call<JSONObject> get(String user_ids, String access_token){
        return users().get(user_ids, users_all_fields, access_token,latest_api_v);
    }
    public static Call<JSONObject> getWithMessageFields(String user_ids, String access_token){
        return users().get(user_ids, message_fields, access_token,latest_api_v);
    }

}
