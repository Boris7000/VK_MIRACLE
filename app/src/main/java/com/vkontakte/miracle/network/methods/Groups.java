package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.groups_all_fields;
import static com.vkontakte.miracle.network.Constants.groups_min_fields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.message_fields;
import static com.vkontakte.miracle.network.Creator.groups;

public class Groups {

    public static Call<JSONObject> getWithMessageFields(String user_ids, String access_token){
        return groups().get(user_ids, message_fields, access_token, latest_api_v);
    }

    public static Call<JSONObject> get(String user_ids, String access_token){
        return groups().get(user_ids, groups_all_fields, access_token, latest_api_v);
    }

    public static Call<JSONObject> get(int count, int offset, String user_id, String access_token){
        return groups().get(count,offset,user_id,1, groups_min_fields, access_token, latest_api_v);
    }
}
