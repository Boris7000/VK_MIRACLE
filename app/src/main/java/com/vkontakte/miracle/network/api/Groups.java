package com.vkontakte.miracle.network.api;

import static com.vkontakte.miracle.network.Constants.groups_all_fields;
import static com.vkontakte.miracle.network.Constants.groups_min_fields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.message_fields;
import static com.vkontakte.miracle.network.Creator.groups;
import static com.vkontakte.miracle.network.Creator.stats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;

public class Groups {

    public static Call<JSONObject> getWithMessageFields(String user_ids, String access_token) {
        return groups().get(user_ids, message_fields, access_token, latest_api_v);
    }

    public static Call<JSONObject> get(String user_ids, String access_token) {
        return groups().get(user_ids, groups_all_fields, access_token, latest_api_v);
    }

    public static Call<JSONObject> get(int count, int offset, String user_id, String access_token) {
        return groups().get(count, offset, user_id, 1, groups_min_fields, access_token, latest_api_v);
    }

    /*
    not_sure
    Опциональный параметр, учитываемый,
    если group_id принадлежит встрече.
    1 — Возможно пойду. 0 — Точно пойду. По умолчанию 0.
     */

    public static Call<JSONObject> join(String groupId, int notSure, String access_token) {
        return groups().join(groupId, notSure, access_token, latest_api_v);
    }

    public static Call<JSONObject> leave(String groupId, String access_token) {
        return groups().leave(groupId, access_token, latest_api_v);
    }

    public static Call<JSONObject> removeRecents(String access_token) {
        return groups().removeRecents(access_token, latest_api_v);
    }

    public static Call<JSONObject> sendGroupStatistics(String groupId, String access_token) throws JSONException, IOException, NoSuchAlgorithmException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("e", "open_group");
        JSONArray groupIds = new JSONArray();
        groupIds.put(-Long.parseLong(groupId));
        jsonObject.put("group_ids",groupIds);

        jsonArray.put(jsonObject);


        return stats().trackEvents(jsonArray.toString(), access_token, latest_api_v);
    }

}
